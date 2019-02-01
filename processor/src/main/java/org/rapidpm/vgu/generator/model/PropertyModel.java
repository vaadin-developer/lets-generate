package org.rapidpm.vgu.generator.model;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import net.vergien.beanautoutils.annotation.Bean;

@Bean
public class PropertyModel {
  private final TypeMirror type;
  private final String name;

  public PropertyModel(VariableElement variableElement) {
    super();
    this.type = variableElement.asType();
    this.name = variableElement.getSimpleName().toString();
  }

  public PropertyModel(String name, TypeMirror type) {
    super();
    this.type = type;
    this.name = name;
  }

  public TypeMirror getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return PropertyModelBeanUtil.doToString(this);
  }

  @Override
  public int hashCode() {
    return PropertyModelBeanUtil.doToHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return PropertyModelBeanUtil.doEquals(this, obj);
  }
}
