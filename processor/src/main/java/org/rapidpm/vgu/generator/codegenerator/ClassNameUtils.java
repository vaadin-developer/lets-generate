package org.rapidpm.vgu.generator.codegenerator;

import org.apache.commons.lang3.StringUtils;
import org.rapidpm.vgu.generator.model.DataBeanModel;

public class ClassNameUtils {
  private ClassNameUtils() {
    throw new IllegalAccessError("Utility class");
  }

  public static String prefixCamelCase(String prefix, String suffix) {
    return prefix + capitalizeFirstLetter(suffix);
  }

  public static String capitalizeFirstLetter(String original) {
    if (original == null || original.length() == 0) {
      return original;
    }
    return original.substring(0, 1).toUpperCase() + original.substring(1);
  }

  public static String getPackageName(String qualifiedName) {
    return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
  }

  public static String getSimpleName(String qualifiedName) {
    return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
  }

  public static String appendSubPackage(String qualifiedName, String subPackageName) {
    return StringUtils.join(
        new String[] {getPackageName(qualifiedName), subPackageName, getSimpleName(qualifiedName)},
        ".");
  }

  public static String toEnumName(String camelCasedString) {
    if (camelCasedString == null || camelCasedString.isEmpty())
      return camelCasedString;

    StringBuilder result = new StringBuilder();
    // result.append(camelCasedString.charAt(0));
    for (int i = 0; i < camelCasedString.length(); i++) {
      if (Character.isUpperCase(camelCasedString.charAt(i)))
        result.append("_");
      result.append(Character.toUpperCase(camelCasedString.charAt(i)));
    }
    return result.toString();
  }

  public static String getFilterPackage(DataBeanModel model) {
    return model.getPackage() + ".filter";
  }
}
