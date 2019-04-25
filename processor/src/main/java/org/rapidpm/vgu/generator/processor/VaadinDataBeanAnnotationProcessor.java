package org.rapidpm.vgu.generator.processor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.rapidpm.vgu.generator.annotation.VaadinDataBeans;
import org.rapidpm.vgu.generator.codegenerator.CodeGenerator;
import org.rapidpm.vgu.generator.codegenerator.StringFilterDataProviderGenerator;
import org.rapidpm.vgu.generator.codegenerator.vaadin.VaadinComboBoxGenerator;
import org.rapidpm.vgu.generator.codegenerator.vaadin.VaadinDataProviderGenerator;
import org.rapidpm.vgu.generator.codegenerator.vaadin.VaadinFormGenerator;
import org.rapidpm.vgu.generator.codegenerator.vaadin.VaadinGridGenerator;
import org.rapidpm.vgu.generator.codegenerator.vaadin.VaadinItemLabelGeneratorGenerator;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.google.auto.service.AutoService;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class VaadinDataBeanAnnotationProcessor extends AbstractDataBeanProcessor {

  private CodeGenerator[] generators =
      {new VaadinDataProviderGenerator(), new StringFilterDataProviderGenerator(),
          new VaadinFormGenerator(), new VaadinComboBoxGenerator(),
          new VaadinItemLabelGeneratorGenerator(), new VaadinGridGenerator()};

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!roundEnv.processingOver()) {
      for (TypeElement annotation : annotations) {
        try {
          Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
          for (Element e : annotatedElements) {

            TypeElement typeElement = (TypeElement) e;
            VaadinDataBeansPrism prisim = VaadinDataBeansPrism.getInstanceOn(typeElement);
            List<TypeMirror> mirrors = prisim.value();
            for (TypeMirror typeMirror : mirrors) {
              Element ee = processingEnv.getTypeUtils().asElement(typeMirror);
              DataBeanModel model = process((TypeElement) ee);
              write(typeElement, model);
            }
          }
        } catch (Exception e) {
          error("Failure proccessing dataBean", annotation, e);
        }
      }

    }
    return false;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportedAnnotations =
        new HashSet<>(Arrays.asList(VaadinDataBeans.class.getName()));
    return supportedAnnotations;
  }

  @Override
  public void write(TypeElement typeElement, DataBeanModel dataBeanModel) {
    for (CodeGenerator generator : generators) {
      try {
        generator.writeCode(processingEnv, dataBeanModel);
      } catch (IOException e1) {
        error("Failure writing code", typeElement, e1);
      }
    }
  }
}
