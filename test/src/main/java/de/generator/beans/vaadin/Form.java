package de.generator.beans.vaadin;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.generator.beans.Address;

@Route("form")
public class Form extends Composite<VerticalLayout> {
  public Form() {
    AddressForm addressForm = new AddressForm();
    Address address = new Address();

    address.setFristName("Daniel");
    address.setLastName("Nordhoff-Vergien");
    address.setId(123);
    address.setAge(77);
    address.setPhone("123-33-22");
    addressForm.setValue(address);
    getContent().add(addressForm);
    addressForm.addValueChangeListener(
        e -> Notification.show(e.getOldValue().toString() + "\n" + e.getValue().toString()));
  }

}
