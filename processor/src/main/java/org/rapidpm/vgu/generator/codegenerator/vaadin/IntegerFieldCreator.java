package org.rapidpm.vgu.generator.codegenerator.vaadin;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;
import com.vaadin.flow.component.textfield.TextField;

@AutoService(FieldCreator.class)
public class IntegerFieldCreator implements FieldCreator {

  @Override
  public boolean isResponsibleFor(TypeName typeName) {
    return TypeName.INT.equals(typeName) || TypeName.INT.box().equals(typeName);
  }

  @Override
  public int getPriority(FieldType fieldType) {
    return 0;
  }

  @Override
  public void createAndReturnFormField(Builder builder) {
    String fieldName = "field";
    builder.addStatement("$T $L = new $T()", getFormFieldClassName(), fieldName,
        ClassName.get(TextField.class));
    builder.addStatement("$L.setPattern($S)", fieldName, "[0-9]*");
    builder.addStatement("$L.setPreventInvalidInput(true)", fieldName);
    builder.addStatement("return $L", fieldName);
  }

  @Override
  public ClassName getFormFieldClassName() {
    return ClassName.get(TextField.class);
  }
}
