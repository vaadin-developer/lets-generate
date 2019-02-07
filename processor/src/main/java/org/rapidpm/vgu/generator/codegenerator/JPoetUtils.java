package org.rapidpm.vgu.generator.codegenerator;

import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;

public class JPoetUtils {
  private JPoetUtils() {
    throw new IllegalAccessError("Utility class");
  }

  public static ClassName getFilterClassName(DataBeanModel model) {
    return ClassName.get(model.getPkg() + ".filter", model.getName() + "Filter");
  }

  public static ClassName getBaseQueriesClassName(DataBeanModel model) {
    return ClassName.get(model.getPkg() + ".repo", model.getName() + "BaseQueries");
  }

  public static ClassName getBeanClassName(DataBeanModel model) {
    return ClassName.get(model.getPkg(), model.getName());
  }

  public static ClassName getSortPropertyClassName(DataBeanModel model) {
    return ClassName.get(ClassNameUtils.getFilterPackage(model),
        model.getName() + "SortFields");
  }
}
