package de.generator.beans.vaadin;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder;
import de.generator.beans.Address;

public class SampleForm extends AbstractCompositeField<Component, SampleForm, Address> {
  private Binder<Address> binder = new Binder<>();

  public SampleForm() {
    super(null);
  }

  @Override
  protected void setPresentationValue(Address newPresentationValue) {
    // bla
  }
}
