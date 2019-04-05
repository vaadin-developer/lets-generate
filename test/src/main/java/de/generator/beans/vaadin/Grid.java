package de.generator.beans.vaadin;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("grid")
public class Grid extends Composite<VerticalLayout> {
  public Grid() {
    ContractGrid contractGrid = new ContractGrid();
    contractGrid.setBaseQueries(new ContractQueries());
    contractGrid.setSizeFull();
    getContent().add(contractGrid);
  }
}
