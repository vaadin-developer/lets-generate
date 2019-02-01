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
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vgu.generator.annotation.VaadinDataBeans;
import org.rapidpm.vgu.generator.codegenerator.VaadinDataProviderGenerator;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.google.auto.service.AutoService;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class VaadinDataBeanAnnotationProcessor extends AbstractDataBeanProcessor
    implements HasLogger {
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
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
        logger().severe("Failure proccessing", e);
        error("Failure proccessing dataBean", annotation);
      }
    }
    return true;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportedAnnotations =
        new HashSet<>(Arrays.asList(VaadinDataBeans.class.getName()));
    return supportedAnnotations;
  }

  @Override
  public void write(TypeElement typeElement, DataBeanModel dataBeanModel) {
    try {
      VaadinDataProviderGenerator generator = new VaadinDataProviderGenerator();
      generator.writeCode(processingEnv.getFiler(), dataBeanModel);
    } catch (IOException e1) {
      logger().severe("Failrue writing code", e1);
      error("Failure writing code", typeElement);
    }
  }
}
