package org.rapidpm.vgu.generator.codegenerator.vaadin;

import java.io.IOException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import org.rapidpm.vgu.generator.codegenerator.AbstractCodeGenerator;
import org.rapidpm.vgu.generator.codegenerator.JPoetUtils;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.vaadin.flow.component.combobox.ComboBox;

public class VaadinComboBoxGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(ProcessingEnvironment processingEnvironment, DataBeanModel model)
      throws IOException {
    TypeSpec comboBoxClass =
        TypeSpec.classBuilder(model.getName() + classSuffix()).addModifiers(Modifier.PUBLIC)
            .superclass(ParameterizedTypeName.get(ClassName.get(ComboBox.class),
                JPoetUtils.getBeanClassName(model)))
            .addInitializerBlock(CodeBlock.of("setItemLabelGenerator(new $T());",
                VaadinClassNameUtils.getItemLabelGeneratorClassName(model)))
            .build();
    writeClass(processingEnvironment.getFiler(), model, comboBoxClass);
  }

  @Override
  public String packageSuffix() {
    return "vaadin";
  }

  @Override
  public String classSuffix() {
    return "ComboBox";
  }

}
