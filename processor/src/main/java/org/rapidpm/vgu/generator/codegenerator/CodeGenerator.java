package org.rapidpm.vgu.generator.codegenerator;

import java.io.IOException;
import javax.annotation.processing.Filer;
import org.rapidpm.vgu.generator.model.DataBeanModel;

@FunctionalInterface
public interface CodeGenerator {
  public void writeCode(Filer filer, DataBeanModel model) throws IOException;
}
