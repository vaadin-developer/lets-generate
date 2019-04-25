package org.rapidpm.vgu.generator.codegenerator;

import java.io.IOException;
import javax.annotation.processing.ProcessingEnvironment;
import org.rapidpm.vgu.generator.model.DataBeanModel;

@FunctionalInterface
public interface CodeGenerator {
  public void writeCode(ProcessingEnvironment processingEnvironment, DataBeanModel model) throws IOException;
}
