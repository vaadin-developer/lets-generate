package org.rapidpm.vgu.generator.codegenerator;

import static org.rapidpm.vgu.generator.codegenerator.ClassNameUtils.toEnumName;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class SortPropertyGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    if (!model.getSortProperties().isEmpty()) {

      Builder enumBuilder = TypeSpec.enumBuilder(enumName(model)).addModifiers(Modifier.PUBLIC);

      model.getSortProperties().forEach(p -> {
        enumBuilder.addEnumConstant(toEnumName(p.getName()));
      });
      if (model.getIdProperty().isPresent()) {
        enumBuilder.addEnumConstant(toEnumName(model.getIdProperty().get().getName()));
      }

      enumBuilder.addMethod(fromPropertyMethod(model));
      TypeSpec typeSpec = enumBuilder.build();


      writeClass(filer, model, typeSpec);
    }
  }

  private String enumName(DataBeanModel model) {
    return model.getName() + classSuffix();
  }

  private MethodSpec fromPropertyMethod(DataBeanModel model) {
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("fromProperty").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get(packageName(model), enumName(model)));
    builder.addParameter(String.class, "property");
    builder.beginControlFlow("switch(property)");
    model.getSortProperties().forEach(sortProperty -> {
      builder.addCode("case $S:\n", sortProperty.getName());
      builder.addStatement("$>return $L$<", toEnumName(sortProperty.getName()));
    });
    builder.addCode("default:\n");
    builder.addStatement("throw new $T( $S + property)", IllegalArgumentException.class,
        "Unknown property: ");
    builder.endControlFlow();
    return builder.build();
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
