package org.rapidpm.vgu.generator.processor;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementScanner8;
import org.rapidpm.vgu.generator.annotation.FilterPropertyPrism;
import org.rapidpm.vgu.generator.model.DataBeanModel;

public class BeanScanner extends ElementScanner8<Void, DataBeanModel> {
  private ProcessingEnvironment processingEnvironment;
  private Set<String> importedTypes = new HashSet<>();

  public void setProcessingEnvironment(ProcessingEnvironment processingEnvironment) {
    this.processingEnvironment = processingEnvironment;
  }

  public Set<String> getImportedTypes() {
    return importedTypes;
  }

  @Override
  public Void visitVariable(VariableElement e, DataBeanModel p) {
    if (e.asType().getKind() == TypeKind.DECLARED) {
      importedTypes.add(e.asType().toString());
    }

    if (e.getKind() == ElementKind.FIELD) {
      FilterPropertyPrism displayPropertyPrism = FilterPropertyPrism.getInstanceOn(e);
      if (displayPropertyPrism != null) {
        System.out.println(e + " annotated field");
        String propertyClassName;
        if (e.asType().getKind().isPrimitive()) {
          PrimitiveType pt =
              processingEnvironment.getTypeUtils().getPrimitiveType(e.asType().getKind());
          propertyClassName = pt.toString();
        } else {
          propertyClassName =
              processingEnvironment.getTypeUtils().asElement(e.asType()).getSimpleName().toString();
        }
        String propertyName = e.getSimpleName().toString();

      }
    }
    return super.visitVariable(e, p);
  }

  private void createPropertyDescriptions(DataBeanModel p, FilterPropertyPrism displayPropertyPrism,
      String propertyClassName, String propertyName) {
    System.out.println("bla");
  }

}
