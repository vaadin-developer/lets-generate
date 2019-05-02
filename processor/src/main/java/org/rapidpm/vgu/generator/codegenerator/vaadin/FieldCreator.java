package org.rapidpm.vgu.generator.codegenerator.vaadin;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

public interface FieldCreator {
  public boolean isResponsibleFor(TypeName typeName);

  public int getPriority(FieldType fieldType);

  public void createAndReturnFormField(MethodSpec.Builder builder);

  public ClassName getFormFieldClassName();

  public default void createAndReturnFilterField(MethodSpec.Builder builder) {
    createAndReturnFormField(builder);
  };

  public default ClassName getFilterFieldClassName() {
    return getFormFieldClassName();
  }
}
