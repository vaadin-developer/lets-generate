package org.rapidpm.vgu.generator.codegenerator.vaadin;

import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;

public class VaadinClassNameUtils {
  private VaadinClassNameUtils() {
    throw new IllegalAccessError("Utility class");
  }

  public static ClassName getItemLabelGeneratorClassName(DataBeanModel model) {
    return ClassName.get(model.getPkg() + ".vaadin", model.getName() + "ItemLabelGenerator");
  }
}
