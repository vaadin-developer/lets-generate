package de.generator.beans.vaadin;

import static java.lang.System.setProperty;
import org.apache.meecrowave.Meecrowave;

public class Runner {
  private Runner() {
    throw new IllegalAccessError("Utility class");
  }

  public static void main(String[] args) throws Exception {
    new Meecrowave(new Meecrowave.Builder() {
      {
        setProperty("vaadin.i18n.provider", I18N.class.getName());
        setHttpPort(8080);
        setTomcatScanning(true);
        setTomcatAutoSetup(true);
        setHttp2(true);

      }
    }).bake().await();
  }
}
