package de.generator.beans.vaadin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class TestI18N {

  @Test
  void test() {
    I18N i18n = new I18N();

    assertThat(i18n.getTranslation("de.generator.beans.Address.id.caption", Locale.ENGLISH),
        not(isEmptyOrNullString()));
  }

}
