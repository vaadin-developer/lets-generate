package org.rapidpm.vgu.generator.codegenerator;

import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.TypeSpec;

public class VaadinDataProviderGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    TypeSpec dataProviderClass = TypeSpec.classBuilder(model.getName() + classSuffix())
        .addModifiers(Modifier.PUBLIC).build();
    writeClass(filer, model, dataProviderClass);
  }

  @Override
  public String packageSuffix() {
    return "vaadin";
  }

  @Override
  public String classSuffix() {
    return "DataProvider";
  }

}
