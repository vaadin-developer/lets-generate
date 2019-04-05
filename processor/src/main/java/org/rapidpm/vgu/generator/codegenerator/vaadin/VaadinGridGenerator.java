package org.rapidpm.vgu.generator.codegenerator.vaadin;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import java.io.IOException;
import javax.annotation.processing.Filer;
import org.rapidpm.vgu.generator.codegenerator.AbstractCodeGenerator;
import org.rapidpm.vgu.generator.codegenerator.ClassNameUtils;
import org.rapidpm.vgu.generator.codegenerator.JPoetUtils;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import org.rapidpm.vgu.generator.model.PropertyModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;

public class VaadinGridGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    TypeSpec.Builder builder = TypeSpec.classBuilder(model.getName() + classSuffix())
        .superclass(ParameterizedTypeName.get(ClassName.get(Composite.class),
            ClassName.get(Component.class)))
        .addSuperinterface(HasSize.class).addMethod(constructor(model)).addModifiers(PUBLIC)
        .addType(i18Wrapper(model))
        .addField(FieldSpec.builder(ClassName.bestGuess("I18NWrapper"), "wrapper", PRIVATE).build())
        .addField(gridType(model), "grid", PRIVATE).addMethod(getGrid(model))
        .addMethod(setBaseQueries(model)).addMethod(getContent());

    for (PropertyModel property : model.getProperties()) {
      builder.addField(columnType(model), columnName(property), PRIVATE);
      builder.addMethod(createColumnMethod(model, property));
    }
    writeClass(filer, model, builder.build());
  }

  private MethodSpec setBaseQueries(DataBeanModel model) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder("setBaseQueries").addModifiers(PUBLIC)
        .addParameter(
            ParameterSpec.builder(JPoetUtils.getBaseQueriesClassName(model), "baseQueries").build())
        .addStatement("grid.setDataProvider(new $T(baseQueries))",
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

  private MethodSpec constructor(DataBeanModel model) {
    Builder builder = MethodSpec.constructorBuilder().addModifiers(PUBLIC)
        .addStatement("this.grid = new $T<>()", ClassName.get(Grid.class));

    for (PropertyModel property : model.getProperties()) {
      builder.addStatement("this.$L = $L()", columnName(property),
          createColumnMethodName(property));
    }
    builder.addStatement("this.wrapper = new I18NWrapper(grid)");
    return builder.build();
  }

  private String createColumnMethodName(PropertyModel property) {
    return ClassNameUtils.prefixCamelCase("create", property.getName()) + "Column";
  }

  private String columnName(PropertyModel property) {
    return property.getName() + "Column";
  }

  private MethodSpec getGrid(DataBeanModel model) {
    return MethodSpec.methodBuilder("getGrid").returns(gridType(model)).addStatement("return grid")
        .addModifiers(PUBLIC).addJavadoc("Access to the encapsulated grid").build();
  }

  private ParameterizedTypeName gridType(DataBeanModel model) {
    return ParameterizedTypeName.get(ClassName.get(Grid.class), JPoetUtils.getBeanClassName(model));
  }

  private TypeSpec i18Wrapper(DataBeanModel model) {
    return VaadinUtils.i18Wrapper(localeChangeMethodBuilder -> {
      for (PropertyModel propertyModel : model.getProperties()) {
        String fieldName = columnName(propertyModel);
        localeChangeMethodBuilder.addStatement("$L.this.$L.setHeader(getTranslation($S))",
            packageName(model) + "." + model.getName() + classSuffix(), fieldName,
            TranslationUtil.captionKey(model, propertyModel));
      }
    });
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
