package de.generator.beans.vaadin;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("addressGrid")
public class AddressGridView extends Composite<VerticalLayout> {
  public AddressGridView() {
    AddressGrid contractGrid = new AddressGrid();
    contractGrid.setBaseQueries(new AddressQueries());
    contractGrid.setSizeFull();
    getContent().add(new VerticalLayout(contractGrid));
  }
}
