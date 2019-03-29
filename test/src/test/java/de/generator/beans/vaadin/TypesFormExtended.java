package de.generator.beans.vaadin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;

public class TypesFormExtended extends TypesForm {

  public Button getSubmitButton() {
    return submitButton;
  }

  public Button getCancelButton() {
    return cancelButton;
  }

  public Button getResetButton() {
    return resetButton;
  }

  public Component getLayout() {
    return layout;
  }

  public TextField getIdField() {
    return idField;
  }

  public TextField getIntTypeField() {
    return intTypeField;
  }

  public TextField getIntegerTypeField() {
    return integerTypeField;
  }

  public TextField getDoubleTypeField() {
    return doubleTypeField;
  }

  public TextField getDoubleClassTypeField() {
    return doubleClassTypeField;
  }

  public TextField getLongTypeField() {
    return longTypeField;
  }

  public TextField getLongClassTypeField() {
    return longClassTypeField;
  }

  public TextField getFloatTypeField() {
    return floatTypeField;
  }

  public TextField getFloatClassTypeField() {
    return floatClassTypeField;
  }

  public TextField getStringTypeField() {
    return stringTypeField;
  }

}
