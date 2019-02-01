package org.rapidpm.vgu.generator.processor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vgu.generator.annotation.DataBean;
import org.rapidpm.vgu.generator.annotation.DataBeanType;
import org.rapidpm.vgu.generator.annotation.FilterProperty;
import org.rapidpm.vgu.generator.annotation.SortProperty;
import org.rapidpm.vgu.generator.codegenerator.FilterGenerator;
import org.rapidpm.vgu.generator.codegenerator.QueryInterfaceGenerator;
import org.rapidpm.vgu.generator.codegenerator.SortPropertyGenerator;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import org.rapidpm.vgu.generator.model.PropertyModel;
import com.google.auto.service.AutoService;


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class VaadinGeneratorUtilsAnnotationProccessor extends AbstractProcessor
    implements HasLogger {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      try {
        logger().info("Process anotation {}", annotation);
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
        for (Element e : annotatedElements) {
          logger().info("Process element {} of type {} with annotaded with {}", e,
              e.getClass().getName(), annotation);

          TypeElement typeElement = (TypeElement) e;

          DataBeanModel dataBeanModel = process(typeElement);
          logger().info("DataBeanModel: {}", dataBeanModel);
          write(typeElement, dataBeanModel);
        }
      } catch (Exception e) {
        logger().severe("Failure proccessing", e);
        error("Failure proccessing dataBean", annotation);
      }
    }
    return true;
  }

  private void write(TypeElement typeElement, DataBeanModel dataBeanModel) {
    try {
      FilterGenerator filterGenerator = new FilterGenerator();
      filterGenerator.writeCode(processingEnv.getFiler(), dataBeanModel);

      SortPropertyGenerator sortPropertyGenerator = new SortPropertyGenerator();
      sortPropertyGenerator.writeCode(processingEnv.getFiler(), dataBeanModel);

      if (dataBeanModel.getModelType().equals(DataBeanType.PLAIN)) {
        QueryInterfaceGenerator queryInterfaceGenerator = new QueryInterfaceGenerator();
        queryInterfaceGenerator.writeCode(processingEnv.getFiler(), dataBeanModel);
      }
    } catch (IOException e1) {
      logger().severe("Failrue writing code", e1);
      error("Failure writing code", typeElement);
    }
  }

  public DataBeanModel process(TypeElement typeElement) {
    processingEnv.getMessager().printMessage(Kind.WARNING,
        "Proccesing " + typeElement.getSimpleName(), typeElement);
    DataBeanPrism displayBeanPrisim = DataBeanPrism.getInstanceOn(typeElement);
    DataBeanModel dataBeanModel = new DataBeanModel(typeElement);
    dataBeanModel.setModelType(DataBeanType.valueOf(displayBeanPrisim.type()));
    dataBeanModel.getFilterProperties()
        .addAll(displayBeanPrisim.customFilters().stream()
            .map(cfPrism -> new PropertyModel(cfPrism.name(), cfPrism.type()))
            .collect(Collectors.toSet()));
    dataBeanModel.getFilterProperties()
        .addAll(exctractPropertyModel(typeElement, FilterProperty.class));
    dataBeanModel.getSortProperties()
        .addAll(exctractPropertyModel(typeElement, SortProperty.class));
    dataBeanModel.setPkg(getPackageName(typeElement));
    dataBeanModel.setIdProperty(getIdProperty(typeElement));
    if (typeElement.getKind() == ElementKind.ENUM) {
      // beanDescription.setEnumeration(true);
      // EnumScanner enumScanner = new EnumScanner();
      // enumScanner.scan(displayBeanElement, beanDescription);
      // beanDescription.setDisplayed(false);
    } else {
      BeanScanner importScanner = new BeanScanner();
      importScanner.setProcessingEnvironment(processingEnv);
      importScanner.scan(typeElement, dataBeanModel);
      dataBeanModel.setImports(new ArrayList<>(importScanner.getImportedTypes()));
    }
    return dataBeanModel;
  }

  private Optional<PropertyModel> getIdProperty(TypeElement typeElement) {
    List<PropertyModel> potentialIdProperties = extractPropertyModel(typeElement,
        element -> element.getSimpleName().toString().equals("id"));
    if (potentialIdProperties.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(potentialIdProperties.get(0));
  }

  public List<PropertyModel> extractPropertyModel(TypeElement typeElement,
      Predicate<Element> filter) {
    List<? extends Element> enclosedElements = typeElement.getEnclosedElements();

    List<PropertyModel> set = enclosedElements.stream()
        .filter(enclosedElement -> enclosedElement.getKind().equals(ElementKind.FIELD))
        .filter(filter).map(enclosedElement -> new PropertyModel((VariableElement) enclosedElement))
        .collect(Collectors.toList());

    TypeElement superClassTypeElement = getSuperClassTypeElement(typeElement);

    boolean processSuperClass = superClassTypeElement != null
        && !superClassTypeElement.getQualifiedName().toString().equals(Object.class.getName());

    if (processSuperClass) {
      set.addAll(extractPropertyModel(superClassTypeElement, filter));
    }
    return set;

  }

  public Set<PropertyModel> exctractPropertyModel(TypeElement typeElement,
      Class<? extends Annotation> annotationType) {
    List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
    Set<PropertyModel> set = enclosedElements.stream()
        .filter(enclosedElement -> enclosedElement.getKind().equals(ElementKind.FIELD))
        .filter(enclosedElement -> enclosedElement.getAnnotation(annotationType) != null)
        .map(enclosedElement -> new PropertyModel((VariableElement) enclosedElement))
        .collect(Collectors.toSet());

    TypeElement superClassTypeElement = getSuperClassTypeElement(typeElement);

    boolean processSuperClass = superClassTypeElement != null
        && !superClassTypeElement.getQualifiedName().toString().equals(Object.class.getName());

    if (processSuperClass) {
      set.addAll(exctractPropertyModel(superClassTypeElement, annotationType));
    }
    return set;
  }

  public String getPackageName(Element element) {
    return processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
  }

  private TypeElement getSuperClassTypeElement(TypeElement typeElement) {
    TypeMirror supperClassMirror = typeElement.getSuperclass();
    if (supperClassMirror.getKind() == TypeKind.NONE) {
      return null;
    }
    return (TypeElement) processingEnv.getTypeUtils().asElement(supperClassMirror);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportedAnnotations = new HashSet<>(Arrays.asList(DataBean.class.getName()
    // , SortProperty.class.getName(), FilterProperty.class.getName()
    ));
    return supportedAnnotations;
  }

  private void error(String msg, Element e) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
  }
}
