package org.rapidpm.vgu.generator.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class APLogger {
  private final ProcessingEnvironment processingEnvironment;

  public APLogger(ProcessingEnvironment processingEnvironment) {
    super();
    this.processingEnvironment = processingEnvironment;
  }

  public void error(String msg, Element e, Throwable t) {
    String stackTrace = t == null ? null : ExceptionUtils.getStackTrace(t);
    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR,
        StringUtils.join(msg, "\n", stackTrace), e);
  }

  public void warn(String msg, Element e) {
    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, e);
  }

  public void info(String msg, Element e) {
    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, msg, e);
  }

  public void info(String msg) {
    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
  }
}
