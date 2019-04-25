package org.rapidpm.vgu.generator.codegenerator.vaadin;

import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;

public class VaadinClassNameUtils {
  private VaadinClassNameUtils() {
    throw new IllegalAccessError("Utility class");
  }

  public static ClassName getItemLabelGeneratorClassName(DataBeanModel model) {
    return ClassName.get(model.getPkg() + ".vaadin", model.getName() + "ItemLabelGenerator");
  }

  public static ClassName getComboboxClassName(DataBeanModel model) {
    return getComboboxClassName(model.getPkg(), model.getName());
  }

  private static ClassName getComboboxClassName(String packageName, String className) {
    return ClassName.get(packageName + ".vaadin", className + "ComboBox");
  }

  public static ClassName getComboboxClassName(TypeMirror type) {
    DeclaredType dt = (DeclaredType) type;
    String simpleName = ((QualifiedNameable) dt.asElement()).getSimpleName().toString();
    String fullName = ((QualifiedNameable) dt.asElement()).getQualifiedName().toString();
    String packageName = fullName.substring(0, fullName.length() - simpleName.length() - 1);

    return getComboboxClassName(packageName, simpleName);
  }
}
