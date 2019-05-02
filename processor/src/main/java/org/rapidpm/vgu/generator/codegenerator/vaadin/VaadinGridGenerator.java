package org.rapidpm.vgu.generator.codegenerator.vaadin;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import org.rapidpm.vgu.generator.codegenerator.ClassNameUtils;
import org.rapidpm.vgu.generator.codegenerator.JPoetUtils;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import org.rapidpm.vgu.generator.model.PropertyModel;
import org.rapidpm.vgu.vaadin.FilterGrid;
import org.rapidpm.vgu.vaadin.FilterGrid.FilterBuilder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.binder.HasFilterableDataProvider;

public class VaadinGridGenerator extends AbstractVaadinCodeGenerator {
  @Override
  public void writeCode(ProcessingEnvironment processingEnvironment, DataBeanModel model)
      throws IOException {
    setProccesingEnviroment(processingEnvironment);
    TypeSpec.Builder builder = TypeSpec.classBuilder(model.getName() + classSuffix())
        .addJavadoc("Generated grid component for {@link $T}", JPoetUtils.getBeanClassName(model))
        .superclass(ParameterizedTypeName.get(ClassName.get(Composite.class),
            ClassName.get(Component.class)))
        .addSuperinterface(HasSize.class).addMethod(constructor(model)).addModifiers(PUBLIC)
        .addType(i18Wrapper(model)).addType(filterBuilder(model, model.getFilterProperties()))
        .addField(FieldSpec.builder(ClassName.bestGuess("I18NWrapper"), "wrapper", PRIVATE).build())
        .addField(gridType(model), "grid", PRIVATE).addMethod(getGrid(model))
        .addField(FieldSpec.builder(HeaderRow.class, "filterRow", PRIVATE).build())
        .addMethod(setBaseQueries(model)).addMethod(getContent());

    for (PropertyModel property : model.getProperties()) {
      builder.addField(columnType(model), columnName(property), PRIVATE);
      builder.addMethod(createColumnMethod(model, property));
    }

    for (PropertyModel property : model.getFilterProperties()) {
      builder.addField(Component.class, filterComponentName(property), PROTECTED);
      builder.addMethod(createFilterFieldMethod(model, property));
      builder.addMethod(createFilterFieldGetter(model, property));
    }

    Map<TypeMirror, List<PropertyModel>> dependendDataBeans = model.getFilterProperties().stream()
        .filter(PropertyModel::isDataBean).collect(Collectors.groupingBy(PropertyModel::getType));
    for (TypeMirror type : dependendDataBeans.keySet()) {
      builder.addMethod(createDepenedDataBeanSetBaseQueries(type, dependendDataBeans.get(type),
          pro -> filterComponentName(pro)));
    }
    writeClass(processingEnvironment.getFiler(), model, builder.build());
  }

  private MethodSpec createDepenedDataBeanSetBaseQueries(TypeMirror type,
      List<PropertyModel> properties, Function<PropertyModel, String> fieldName) {

    MethodSpec.Builder builder = MethodSpec
        .methodBuilder("set" + JPoetUtils.getBaseQueriesClassName(type).simpleName())
        .addModifiers(PUBLIC).addParameter(
            ParameterSpec.builder(JPoetUtils.getBaseQueriesClassName(type), "baseQueries").build());

    for (PropertyModel property : properties) {
      builder.addStatement("(($T)$L).setDataProvider(new $T(baseQueries))",
          ParameterizedTypeName.get(ClassName.get(HasFilterableDataProvider.class),
              ClassName.get(type), ClassName.get(String.class)),
          fieldName.apply(property), VaadinUtils.getStringDataProviderClassName(type));
    }
    return builder.build();
  }

  private MethodSpec setBaseQueries(DataBeanModel model) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder("setBaseQueries").addModifiers(PUBLIC)
        .addParameter(
            ParameterSpec.builder(JPoetUtils.getBaseQueriesClassName(model), "baseQueries").build())
        .addStatement("grid.setFilterableDataProvider(new $T(baseQueries))",
            VaadinUtils.getDataProviderClassName(model));
    return builder.build();
  }

  private ParameterizedTypeName columnType(DataBeanModel model) {
    return ParameterizedTypeName.get(ClassName.get(Column.class),
        JPoetUtils.getBeanClassName(model));
  }

  private MethodSpec createColumnMethod(DataBeanModel model, PropertyModel property) {

    MethodSpec.Builder builder = MethodSpec.methodBuilder(createColumnMethodName(property))
        .returns(columnType(model)).addModifiers(PROTECTED);

    boolean isSortable = model.getSortProperties().contains(property);
    if (isSortable) {
      builder.addStatement("return grid.addColumn($T::$L).setSortable(true).setSortProperty($S)",
          JPoetUtils.getBeanClassName(model),
          ClassNameUtils.prefixCamelCase("get", property.getName()), property.getName());
    } else {
      builder.addStatement("return grid.addColumn($T::$L)", JPoetUtils.getBeanClassName(model),
          ClassNameUtils.prefixCamelCase("get", property.getName()));
    }
    return builder.build();
  }

  private MethodSpec createFilterFieldMethod(DataBeanModel model, PropertyModel property) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(createFilterFieldMethodName(property))
        .returns(Component.class).addModifiers(PROTECTED);
    getFieldCreator(property).createAndReturnFilterField(builder);
    return builder.build();
  }

  private MethodSpec createFilterFieldGetter(DataBeanModel model, PropertyModel property) {
    TypeName hasValueType = hasValueType(property);
    MethodSpec.Builder builder = MethodSpec.methodBuilder(createFilterFieldGetterName(property))
        .returns(hasValueType).addModifiers(PUBLIC);
    builder.addStatement("return ($T) $L", hasValueType, filterComponentName(property));
    return builder.build();
  }

  private TypeName hasValueType(PropertyModel property) {
    TypeName hasValueType = ParameterizedTypeName.get(ClassName.get(HasValue.class),
        ParameterizedTypeName.get(ClassName.get(ValueChangeEvent.class),
            JPoetUtils.getPropertyClassName(property).box()),
        JPoetUtils.getPropertyClassName(property).box());
    return hasValueType;
  }

  private MethodSpec constructor(DataBeanModel model) {
    Builder builder = MethodSpec.constructorBuilder().addModifiers(PUBLIC)
        .addStatement("this.grid = new $T<>()", ClassName.get(FilterGrid.class))
        .addStatement("this.grid.setFilterBuilder(new " + model.getName() + "FilterBuilder())")
        .addStatement("this.filterRow = grid.appendHeaderRow()");

    for (PropertyModel property : model.getProperties()) {
      builder.addStatement("this.$L = $L()", columnName(property),
          createColumnMethodName(property));
      if (model.getFilterProperties().contains(property)) {
        builder.addStatement("this.$L = $L()", filterComponentName(property),
            createFilterFieldMethodName(property));
        builder.addStatement("this.grid.addFilter($L, $L)", columnName(property),
            filterComponentName(property));
      }

    }
    builder.addStatement("this.wrapper = new I18NWrapper(grid)");
    return builder.build();
  }

  private String createColumnMethodName(PropertyModel property) {
    return ClassNameUtils.prefixCamelCase("create", property.getName()) + "Column";
  }

  private String createFilterFieldMethodName(PropertyModel property) {
    return ClassNameUtils.prefixCamelCase("create", property.getName()) + "FilterField";
  }

  private String createFilterFieldGetterName(PropertyModel property) {
    return ClassNameUtils.prefixCamelCase("get", property.getName()) + "FilterField";
  }

  private String columnName(PropertyModel property) {
    return property.getName() + "Column";
  }

  private String filterComponentName(PropertyModel property) {
    return property.getName() + "FilterField";
  }

  private MethodSpec getGrid(DataBeanModel model) {
    return MethodSpec.methodBuilder("getGrid").returns(gridType(model)).addStatement("return grid")
        .addModifiers(PUBLIC).addJavadoc("Access to the encapsulated grid").build();
  }

  private ParameterizedTypeName gridType(DataBeanModel model) {
    return ParameterizedTypeName.get(ClassName.get(FilterGrid.class),
        JPoetUtils.getBeanClassName(model), JPoetUtils.getFilterClassName(model));
  }

  private TypeSpec i18Wrapper(DataBeanModel model) {
    return VaadinUtils.i18Wrapper(localeChangeMethodBuilder -> {
      for (PropertyModel propertyModel : model.getProperties()) {
        String fieldName = columnName(propertyModel);
        localeChangeMethodBuilder.addStatement("$T.this.$L.setHeader(getTranslation($S))",
            ClassName.get(packageName(model), model.getName() + classSuffix()), fieldName,
            TranslationUtil.captionKey(model, propertyModel));
      }
    });
  }

  private TypeSpec filterBuilder(DataBeanModel model, Set<PropertyModel> filterProperties) {
    TypeSpec.Builder builder =
        TypeSpec.classBuilder(JPoetUtils.getFilterClassName(model).simpleName() + "Builder")
            .addSuperinterface(ParameterizedTypeName.get(ClassName.get(FilterBuilder.class),
                JPoetUtils.getFilterClassName(model)))
            .addModifiers(PUBLIC);
    Builder methodBuilder = MethodSpec.methodBuilder("buildFilter").addAnnotation(Override.class)
        .addModifiers(PUBLIC).returns(JPoetUtils.getFilterClassName(model));

    methodBuilder.addStatement("$T filter = new $T()", JPoetUtils.getFilterClassName(model),
        JPoetUtils.getFilterClassName(model));

    for (PropertyModel filterProperty : filterProperties) {
      methodBuilder.addStatement(
          "filter.set" + ClassNameUtils.capitalizeFirstLetter(filterProperty.getName()) + "("
              + "(($T)" + filterComponentName(filterProperty) + ")" + ".getValue())",
          ParameterizedTypeName.get(ClassName.get(HasValue.class),
              WildcardTypeName.subtypeOf(ClassName.get(ValueChangeEvent.class)),
              JPoetUtils.getPropertyClassName(filterProperty).box()));
    }
    methodBuilder.addStatement("return filter");
    builder.addMethod(methodBuilder.build());
    return builder.build();
  }

  private MethodSpec getContent() {
    return MethodSpec.methodBuilder("getContent").addAnnotation(Override.class).addModifiers(PUBLIC)
        .addStatement("return $L", "wrapper").returns(Component.class).build();
  }

  @Override
  public String packageSuffix() {
    return "vaadin";
  }

  @Override
  public String classSuffix() {
    return "Grid";
  }

}
