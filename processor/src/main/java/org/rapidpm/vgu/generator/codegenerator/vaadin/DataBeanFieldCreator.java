package org.rapidpm.vgu.generator.codegenerator.vaadin;

import org.rapidpm.vgu.generator.codegenerator.JPoetUtils;
import org.rapidpm.vgu.generator.model.PropertyModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

public class DataBeanFieldCreator implements FieldCreator {

  private final PropertyModel propertyModel;

  public DataBeanFieldCreator(PropertyModel propertyModel) {
    this.propertyModel = propertyModel;
  }

  @Override
  public boolean isResponsibleFor(TypeName typeName) {
    return typeName.equals(JPoetUtils.getPropertyClassName(propertyModel));
  }

  @Override
  public int getPriority() {
    return 0;
  }

  @Override
  public void createAndReturnField(Builder builder) {
    String fieldName = "field";
    builder.addStatement("$T $L = new $T()", getFieldType(), fieldName, getFieldType());
    builder.addStatement("return $L", fieldName);
  }

  @Override
  public ClassName getFieldType() {
    return VaadinClassNameUtils.getComboboxClassName(propertyModel);
  }

}
