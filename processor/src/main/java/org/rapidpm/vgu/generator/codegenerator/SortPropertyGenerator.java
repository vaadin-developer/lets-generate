package org.rapidpm.vgu.generator.codegenerator;

import static org.rapidpm.vgu.generator.codegenerator.ClassNameUtils.toEnumName;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class SortPropertyGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    if (!model.getSortProperties().isEmpty()) {

      Builder enumBuilder =
          TypeSpec.enumBuilder(model.getName() + classSuffix()).addModifiers(Modifier.PUBLIC);

      model.getSortProperties().forEach(p -> {
        enumBuilder.addEnumConstant(toEnumName(p.getName()));
      });
      if (model.getIdProperty().isPresent()) {
        enumBuilder.addEnumConstant(toEnumName(model.getIdProperty().get().getName()));
      }
      TypeSpec typeSpec = enumBuilder.build();


      writeClass(filer, model, typeSpec);
    }
  }

  @Override
  public String packageSuffix() {
    return "filter";
  }

  @Override
  public String classSuffix() {
    return "SortFields";
  }

}
