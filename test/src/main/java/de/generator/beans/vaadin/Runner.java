package de.generator.beans.vaadin;

import org.apache.meecrowave.Meecrowave;

public class Runner {
  private Runner() {
    throw new IllegalAccessError("Utility class");
  }

  public static void main(String[] args) throws Exception {
    new Meecrowave(new Meecrowave.Builder() {
      {
        setHttpPort(8080);
        setTomcatScanning(true);
        setTomcatAutoSetup(true);
        setHttp2(true);

      }
    }).bake().await();
  }
}
