package org.rapidpm.vgu.generator.codegenerator.vaadin;

import java.util.Optional;
import javax.tools.Diagnostic.Kind;
import org.rapidpm.vgu.generator.codegenerator.AbstractCodeGenerator;
import org.rapidpm.vgu.generator.model.PropertyModel;
import com.squareup.javapoet.TypeName;

public abstract class AbstractVaadinCodeGenerator extends AbstractCodeGenerator {
  private FieldCreatorFactory fieldCreatorFacktory = new FieldCreatorFactory();

  protected FieldCreator getFieldCreator(PropertyModel propertyModel) {
    Optional<FieldCreator> fieldCreator =
        fieldCreatorFacktory.getFieldCreator(TypeName.get(propertyModel.getType()), FieldType.FORM);
    FieldCreator creator;
    if (!fieldCreator.isPresent()) {
      if (propertyModel.isDataBean()) {
        creator = new DataBeanFieldCreator(propertyModel);
      } else {
        String msg = "No FieldCreator found for type: " + TypeName.get(propertyModel.getType());
        processingEnvironment.getMessager().printMessage(Kind.WARNING, msg,
            propertyModel.getVariableElement().orElse(null));
        creator = new TextFieldCreator();
      }
    } else {
      creator = fieldCreator.get();
    }
    return creator;
  }
}
