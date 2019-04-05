package org.rapidpm.vgu.generator.codegenerator.vaadin;

import java.io.IOException;
import java.util.stream.Stream;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import org.infinitenature.commons.pagination.OffsetRequest;
import org.infinitenature.commons.pagination.SortOrder;
import org.infinitenature.commons.pagination.impl.OffsetRequestImpl;
import org.rapidpm.vgu.generator.codegenerator.AbstractCodeGenerator;
import org.rapidpm.vgu.generator.codegenerator.ClassNameUtils;
import org.rapidpm.vgu.generator.codegenerator.JPoetUtils;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.vaadin.flow.data.provider.SortDirection;

public class VaadinDataProviderGenerator extends AbstractCodeGenerator {

  private static final String BASE_QUERIES_FIELD = "baseQueries";

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    TypeSpec dataProviderClass =
        TypeSpec.classBuilder(model.getName() + classSuffix()).addModifiers(Modifier.PUBLIC)
            .superclass(ParameterizedTypeName.get(
                ClassName.get("com.vaadin.flow.data.provider", "AbstractBackEndDataProvider"),
                JPoetUtils.getBeanClassName(model), filterClassName(model)))
            .addField(JPoetUtils.getBaseQueriesClassName(model), BASE_QUERIES_FIELD,
                Modifier.PRIVATE)
            .addMethod(constructor(model)).addMethod(fetch(model)).addMethod(size(model)).build();
    writeClass(filer, model, dataProviderClass);
  }

  private MethodSpec size(DataBeanModel model) {
    return MethodSpec.methodBuilder("sizeInBackEnd").addAnnotation(Override.class)
        .returns(int.class).addModifiers(Modifier.PROTECTED)
        .addParameter(
            ParameterizedTypeName.get(ClassName.get("com.vaadin.flow.data.provider", "Query"),
                JPoetUtils.getBeanClassName(model), filterClassName(model)),
            "query")
        .addStatement("return (int) $N.count($N.getFilter().orElse(new $T()))", BASE_QUERIES_FIELD,
            "query", filterClassName(model))
        .build();
  }

  protected ClassName filterClassName(DataBeanModel model) {
    return JPoetUtils.getFilterClassName(model);
  }

  private MethodSpec fetch(DataBeanModel model) {
    ClassName sortOderClassName = ClassName.get(SortOrder.class);
    ClassName sortDirectionClassName = ClassName.get(SortDirection.class);
    ClassName sortPropertyClassName = JPoetUtils.getSortPropertyClassName(model);
    return MethodSpec.methodBuilder("fetchFromBackEnd").addAnnotation(Override.class)
        .returns(ParameterizedTypeName
            .get(ClassName.get(Stream.class), JPoetUtils.getBeanClassName(model)))
        .addModifiers(Modifier.PROTECTED)
        .addParameter(
            ParameterizedTypeName.get(ClassName.get("com.vaadin.flow.data.provider", "Query"),
                JPoetUtils.getBeanClassName(model), filterClassName(model)),
            "query")
        .addStatement(
            "$T order = $N.getSortOrders().isEmpty() ? $T.ASC : query.getSortOrders().get(0).getDirection() == $T.ASCENDING ? $T.ASC : $T.DESC",
            sortOderClassName, "query", sortOderClassName, sortDirectionClassName,
            sortOderClassName, sortOderClassName)
        .addStatement(
            "$T property = query.getSortOrders().isEmpty() ?  $T.$N : $T.fromProperty(query.getSortOrders().get(0).getSorted())",
            sortPropertyClassName, sortPropertyClassName,
            ClassNameUtils.toEnumName(model.getDefaultSortProperty().get().getName()),
            sortPropertyClassName)
        .addStatement(
            "$T offsetRequest = new $T(query.getOffset(), query.getLimit(), order, property)",
            ClassName.get(OffsetRequest.class), ClassName.get(OffsetRequestImpl.class))
        .addStatement(
            "$T filter = query.getFilter().isPresent() ? query.getFilter().get() : new $T()",
            filterClassName(model), filterClassName(model))
        .addStatement("return baseQueries.find(filter, offsetRequest).getContent().stream()")
        .build();
  }

  private MethodSpec constructor(DataBeanModel model) {
    return MethodSpec.constructorBuilder()
        .addParameter(ParameterSpec
            .builder(JPoetUtils.getBaseQueriesClassName(model), BASE_QUERIES_FIELD).build())
        .addModifiers(Modifier.PUBLIC).addStatement("super()")
        .addStatement("this.baseQueries = baseQueries").build();
  }

  @Override
  public String packageSuffix() {
    return "vaadin";
  }

  @Override
  public String classSuffix() {
    return "DataProvider";
  }
}
