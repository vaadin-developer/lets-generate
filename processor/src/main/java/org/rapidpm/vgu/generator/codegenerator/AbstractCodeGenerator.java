package org.rapidpm.vgu.generator.codegenerator;

import static org.rapidpm.vgu.generator.codegenerator.ClassNameUtils.appendSubPackage;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import org.rapidpm.vgu.generator.processor.APLogger;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

public abstract class AbstractCodeGenerator implements CodeGenerator, HasLogger {
  protected APLogger apLogger = null;
  protected ProcessingEnvironment processingEnvironment;

  protected void setProccesingEnviroment(ProcessingEnvironment processingEnvironment) {
    this.processingEnvironment = processingEnvironment;
    this.apLogger = new APLogger(processingEnvironment);
  }

  public void writeClass(Filer filer, DataBeanModel model, TypeSpec typeSpec) throws IOException {
    String packageSuffix = packageSuffix();

    JavaFile sourceFile = JavaFile
        .builder(StringUtils.join(new String[] {model.getPackage(), packageSuffix}, "."), typeSpec)
        .build();
    try (Writer writer =
        filer.createSourceFile(appendSubPackage(model.getFqnNAme() + classSuffix(), packageSuffix))
            .openWriter()) {
      sourceFile.writeTo(writer);
      logger().info("Wrote file: {}.{}", sourceFile.packageName, sourceFile.typeSpec.name);
    }
  }

  protected String packageName(DataBeanModel model) {
    return StringUtils.join(new String[] {model.getPackage(), packageSuffix()}, ".");
  }

  public abstract String packageSuffix();

  public abstract String classSuffix();
}
