package de.generator.beans.vaadin;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.rapidpm.dependencies.core.logger.HasLogger;
import com.vaadin.flow.i18n.I18NProvider;

public class I18N implements I18NProvider, HasLogger {

  private static final String BUNDLE_NAME = "Translation";

  @Override
  public List<Locale> getProvidedLocales() {
    return Arrays.asList(Locale.GERMAN, Locale.ENGLISH);
  }

  @Override
  public String getTranslation(String key, Locale locale, Object... params) {
    try {
      ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
      String string = bundle.getString(key);
      return new MessageFormat(string, locale).format(params);
    } catch (MissingResourceException e) {
      return defaultString(key, locale);
    }


  }

  private String defaultString(String key, Locale locale) {
    logger().severe("The key \"{}\" is not in the resource bundel for the locale \"{}\".", key,
        locale);
    return '!' + key + '!';
  }

}
