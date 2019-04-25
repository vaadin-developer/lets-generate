package org.rapidpm.vgu.generator.model;

import java.util.Optional;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.rapidpm.vgu.generator.annotation.DataBean;
import org.rapidpm.vgu.generator.annotation.DisplayReadOnly;
import net.vergien.beanautoutils.annotation.Bean;

@Bean
public class PropertyModel {
  private final TypeMirror type;
  private final String name;
  private final boolean displayReadOnly;
  private final Optional<VariableElement> variableElement;
  private final boolean dataBean;

  public PropertyModel(VariableElement variableElement) {
    super();
    this.type = variableElement.asType();
    this.name = variableElement.getSimpleName().toString();
    this.variableElement = Optional.ofNullable(variableElement);
    this.displayReadOnly = variableElement.getAnnotation(DisplayReadOnly.class) != null;
    this.dataBean = type.getKind().equals(TypeKind.DECLARED)
        && ((DeclaredType) type).asElement().getAnnotation(DataBean.class) != null;
  }

  public PropertyModel(String name, TypeMirror type) {
    super();
    this.type = type;
    this.name = name;
    this.variableElement = Optional.empty();
    this.displayReadOnly = false;
    this.dataBean = type.getKind().equals(TypeKind.DECLARED)
        && ((DeclaredType) type).asElement().getAnnotation(DataBean.class) != null;
  }

  public TypeMirror getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public Optional<VariableElement> getVariableElement() {
    return variableElement;
  }

  public boolean isDisplayReadOnly() {
    return displayReadOnly;
  }

  public boolean isDataBean() {
    return dataBean;
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

  public String getTranslation() {
    return name;
  }
}
