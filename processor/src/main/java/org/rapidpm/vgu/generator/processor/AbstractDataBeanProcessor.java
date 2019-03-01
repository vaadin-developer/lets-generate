package org.rapidpm.vgu.generator.processor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vgu.generator.annotation.Caption;
import org.rapidpm.vgu.generator.annotation.DataBeanType;
import org.rapidpm.vgu.generator.annotation.FilterProperty;
import org.rapidpm.vgu.generator.annotation.SortProperty;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import org.rapidpm.vgu.generator.model.PropertyModel;

public abstract class AbstractDataBeanProcessor extends AbstractProcessor implements HasLogger {

  public DataBeanModel process(TypeElement typeElement) {
    processingEnv.getMessager().printMessage(Kind.WARNING,
        "Proccesing " + typeElement.getSimpleName(), typeElement);
    DataBeanPrism displayBeanPrisim = DataBeanPrism.getInstanceOn(typeElement);
    DataBeanModel dataBeanModel = new DataBeanModel(typeElement);
    dataBeanModel.setModelType(DataBeanType.valueOf(displayBeanPrisim.type()));
    dataBeanModel.getProperties().addAll(extractPropertyModel(typeElement, e -> true));

    dataBeanModel.setCaptionMethod(getCaptionMethod(typeElement));
    List<PropertyModel> defaultFilterCandidates = new ArrayList<>();
    dataBeanModel.getFilterProperties()
        .addAll(displayBeanPrisim.customFilters().stream().map(cfPrism -> {
          PropertyModel propertyModel = new PropertyModel(cfPrism.name(), cfPrism.type());
          if (Boolean.TRUE.equals(cfPrism.defaultFilter())) {
            defaultFilterCandidates.add(propertyModel);
          }
          return propertyModel;
        }).collect(Collectors.toSet()));
    Set<PropertyModel> filterProperties = exctractPropertyModel(typeElement, FilterProperty.class);
    for (PropertyModel propertyModel : filterProperties) {
      FilterPropertyPrism fpp =
          FilterPropertyPrism.getInstanceOn(propertyModel.getVariableElement().get());
      if (Boolean.TRUE.equals(fpp.defaultFilter())) {
        defaultFilterCandidates.add(propertyModel);
      }
    }
    dataBeanModel.getFilterProperties().addAll(filterProperties);
    switch (defaultFilterCandidates.size()) {
      case 0:
        dataBeanModel.setDefaultFilterProperty(Optional.empty());
        break;
      case 1:
        dataBeanModel.setDefaultFilterProperty(Optional.ofNullable(defaultFilterCandidates.get(0)));
        break;
      default:
        error("more than one default sort candidate found", typeElement);
        break;
    }

    dataBeanModel.getSortProperties()
        .addAll(exctractPropertyModel(typeElement, SortProperty.class));
    dataBeanModel.setPkg(getPackageName(typeElement));
    dataBeanModel.setIdProperty(getIdProperty(typeElement));

    setDefaultSort(typeElement, dataBeanModel);


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

  private Optional<ExecutableElement> getCaptionMethod(TypeElement typeElement) {
    List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
    Optional<ExecutableElement> captionMethod = enclosedElements.stream()
        .filter(enclosedElement -> enclosedElement.getKind().equals(ElementKind.METHOD))
        .filter(enclosedElement -> enclosedElement.getAnnotation(Caption.class) != null)
        .map(enclosedElement -> (ExecutableElement) enclosedElement).findFirst();

    if (captionMethod.isPresent()) {
      return captionMethod;
    }
    TypeElement superClassTypeElement = getSuperClassTypeElement(typeElement);

    boolean processSuperClass = superClassTypeElement != null
        && !superClassTypeElement.getQualifiedName().toString().equals(Object.class.getName());

    if (processSuperClass) {
      return getCaptionMethod(superClassTypeElement);
    }
    return Optional.empty();
  }

  private void setDefaultSort(TypeElement typeElement, DataBeanModel dataBeanModel) {
    List<PropertyModel> defaultSortCandidates = new ArrayList<>();
    for (PropertyModel propertyModel : dataBeanModel.getSortProperties()) {
      SortPropertyPrism ssp =
          SortPropertyPrism.getInstanceOn(propertyModel.getVariableElement().get());
      if (Boolean.TRUE.equals(ssp.defaultSort())) {
        defaultSortCandidates.add(propertyModel);
      }
    }
    switch (defaultSortCandidates.size()) {
      case 0:
        dataBeanModel.setDefaultSortProperty(Optional.empty());
        break;
      case 1:
        dataBeanModel.setDefaultSortProperty(Optional.ofNullable(defaultSortCandidates.get(0)));
        break;
      default:
        error("more than one default sort candidate found", typeElement);
        break;
    }
  }

  private void setDefaultFilter(TypeElement typeElement, DataBeanModel dataBeanModel) {
    List<PropertyModel> defaultSortCandidates = new ArrayList<>();
    for (PropertyModel propertyModel : dataBeanModel.getSortProperties()) {
      SortPropertyPrism ssp =
          SortPropertyPrism.getInstanceOn(propertyModel.getVariableElement().get());
      if (Boolean.TRUE.equals(ssp.defaultSort())) {
        defaultSortCandidates.add(propertyModel);
      }
    }
    switch (defaultSortCandidates.size()) {
      case 0:
        dataBeanModel.setDefaultSortProperty(Optional.empty());
        break;
      case 1:
        dataBeanModel.setDefaultSortProperty(Optional.ofNullable(defaultSortCandidates.get(0)));
        break;
      default:
        error("more than one default sort candidate found", typeElement);
        break;
    }
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
    List<PropertyModel> properties = new ArrayList<>();

    TypeElement superClassTypeElement = getSuperClassTypeElement(typeElement);

    boolean processSuperClass = superClassTypeElement != null
        && !superClassTypeElement.getQualifiedName().toString().equals(Object.class.getName());

    if (processSuperClass) {
      properties.addAll(extractPropertyModel(superClassTypeElement, filter));
    }

    properties.addAll(enclosedElements.stream()
        .filter(enclosedElement -> enclosedElement.getKind().equals(ElementKind.FIELD))
        .filter(filter).map(enclosedElement -> new PropertyModel((VariableElement) enclosedElement))
        .collect(Collectors.toList()));

    return properties;
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

  protected void error(String msg, Element e) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
  }

  protected void warn(String msg, Element e) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, e);
  }

  public abstract void write(TypeElement typeElement, DataBeanModel dataBeanModel);
}
