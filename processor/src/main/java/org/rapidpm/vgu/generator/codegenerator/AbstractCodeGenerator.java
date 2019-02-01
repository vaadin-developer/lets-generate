package org.rapidpm.vgu.generator.codegenerator;

import static org.rapidpm.vgu.generator.codegenerator.ClassNameUtils.appendSubPackage;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.processing.Filer;
import org.apache.commons.lang3.StringUtils;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

public abstract class AbstractCodeGenerator implements CodeGenerator {
  public void writeClass(Filer filer, DataBeanModel model, TypeSpec typeSpec) throws IOException {
    String packageSuffix = packageSuffix();

    JavaFile sourceFile = JavaFile
        .builder(StringUtils.join(new String[] {model.getPackage(), packageSuffix}, "."), typeSpec)
        .build();
    try (Writer writer =
        filer.createSourceFile(appendSubPackage(model.getFqnNAme() + classSuffix(), packageSuffix))
            .openWriter()) {
      sourceFile.writeTo(writer);
    }
  }

  public abstract String packageSuffix();

  public abstract String classSuffix();
}
