package org.rapidpm.vgu.generator.codegenerator.vaadin;

import org.rapidpm.vgu.generator.model.DataBeanModel;
import org.rapidpm.vgu.generator.model.PropertyModel;

public class TranslationUtil {
  private TranslationUtil() {
    throw new IllegalAccessError("Utitility class");
  }

  public static String captionKey(DataBeanModel bean, PropertyModel property) {
    return bean.getFqnNAme() + "." + property.getName() + ".caption";
  }

  public static String captionKey(DataBeanModel bean) {
    return bean.getFqnNAme() + ".caption";
  }
}
