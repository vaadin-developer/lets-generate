package org.rapidpm.vgu.generator.codegenerator.vaadin;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

public interface FieldCreator {
  public boolean isResponsibleFor(TypeName typeName);

  public int getPriority(FieldType fieldType);

  public void createAndReturnFormField(MethodSpec.Builder builder);

  public TypeName getFormFieldClassName();

  public TypeName getFieldType();

  public default void createAndReturnFilterField(MethodSpec.Builder builder) {
    createAndReturnFormField(builder);
  }

  public default TypeName getFilterFieldClassName() {
    return getFormFieldClassName();
  }

  public default ClassName converter() {
    return null;
  }

  public default boolean allowsRequiered() {
    return true;
  }
}
