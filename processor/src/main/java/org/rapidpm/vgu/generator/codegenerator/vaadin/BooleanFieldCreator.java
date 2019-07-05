package org.rapidpm.vgu.generator.codegenerator.vaadin;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;

@AutoService(FieldCreator.class)
public class BooleanFieldCreator implements FieldCreator {

  @Override
  public boolean isResponsibleFor(TypeName typeName) {
    return TypeName.BOOLEAN.equals(typeName) || TypeName.BOOLEAN.box().equals(typeName);
  }

  @Override
  public int getPriority(FieldType fieldType) {
    return 0;
  }

  @Override
  public void createAndReturnFormField(Builder builder) {
    ClassName className = getFormFieldClassName();
    String fieldName = "field";
    builder.addStatement("$T $L = new $T()", className, fieldName, className);
    builder.addStatement("return $L", fieldName);
  }

  @Override
  public void createAndReturnFilterField(Builder builder) {
    TypeName className = getFilterFieldClassName();

    String fieldName = "field";
    builder.addStatement("$T $L = new $T()", className, fieldName, className);
    builder.addStatement("$L.setItems(true, false)", fieldName);
    builder.addStatement("return $L", fieldName);
  }

  @Override
  public TypeName getFilterFieldClassName() {
    return ParameterizedTypeName.get(ClassName.get(ComboBox.class), TypeName.get(Boolean.class));
  }

  @Override
  public ClassName getFormFieldClassName() {
    return ClassName.get(Checkbox.class);
  }

  @Override
  public TypeName getFieldType() {
    return ClassName.BOOLEAN.box();
  }

  @Override
  public boolean allowsRequiered() {
    return false;
  }
}
