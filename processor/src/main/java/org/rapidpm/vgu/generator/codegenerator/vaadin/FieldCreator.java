package org.rapidpm.vgu.generator.codegenerator.vaadin;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

public interface FieldCreator {
  public boolean isResponsibleFor(TypeName typeName);

  public int getPriority();

  public void createAndReturnField(MethodSpec.Builder builder);

  public ClassName getFieldType();
}
