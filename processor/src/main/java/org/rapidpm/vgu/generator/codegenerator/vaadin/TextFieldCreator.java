package org.rapidpm.vgu.generator.codegenerator.vaadin;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;
import com.vaadin.flow.component.textfield.TextField;

@AutoService(FieldCreator.class)
public class TextFieldCreator implements FieldCreator {

  @Override
  public boolean isResponsibleFor(TypeName typeName) {
    return ClassName.get(String.class).equals(typeName);
  }

  @Override
  public int getPriority(FieldType fieldType) {
    return 0;
  }

  @Override
  public TypeName getFieldType() {
    return ClassName.get(String.class);
  }

  @Override
  public void createAndReturnFormField(Builder builder) {
    String fieldName = "field";
    builder.addStatement("$T $L = new $T()", getFormFieldClassName(), fieldName,
        ClassName.get(TextField.class));
    builder.addStatement("return $L", fieldName);
  }

  @Override
  public ClassName getFormFieldClassName() {
    return ClassName.get(TextField.class);
  }
}
