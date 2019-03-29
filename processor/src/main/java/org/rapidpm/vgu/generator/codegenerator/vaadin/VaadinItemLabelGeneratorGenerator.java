package org.rapidpm.vgu.generator.codegenerator.vaadin;

import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import org.rapidpm.vgu.generator.codegenerator.AbstractCodeGenerator;
import org.rapidpm.vgu.generator.codegenerator.JPoetUtils;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vaadin.flow.component.ItemLabelGenerator;

public class VaadinItemLabelGeneratorGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    TypeSpec comboBoxClass = TypeSpec.classBuilder(model.getName() + classSuffix())
        .addModifiers(Modifier.PUBLIC).addSuperinterface(ParameterizedTypeName
            .get(ClassName.get(ItemLabelGenerator.class), JPoetUtils.getBeanClassName(model)))
        .addMethod(apply(model)).build();
    writeClass(filer, model, comboBoxClass);
  }

  private MethodSpec apply(DataBeanModel model) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder("apply")
        .addParameter(JPoetUtils.getBeanClassName(model), "item").addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC).returns(TypeName.get(String.class));
    if (model.getCaptionMethod().isPresent()) {
      builder.addStatement("return item." + model.getCaptionMethod().get().getSimpleName() + "()");
    } else {
      builder.addStatement("return item.toString()");
    }
    return builder.build();
  }

  @Override
  public String packageSuffix() {
    return "vaadin";
  }

  @Override
  public String classSuffix() {
    return "ItemLabelGenerator";
  }

}
