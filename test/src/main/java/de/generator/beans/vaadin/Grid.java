package de.generator.beans.vaadin;

import org.rapidpm.vgu.vaadin.FilterGrid.FilterBuilder;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.generator.beans.Address;
import de.generator.beans.Contract;
import de.generator.beans.filter.ContractFilter;

@Route("grid")
public class Grid extends Composite<VerticalLayout> {
  public Grid() {
    ContractGrid contractGrid = new ContractGrid() {
      @Override
      protected Column<Contract> createAddressColumn() {
        Column<Contract> addColumn = getGrid().addColumn(
            contract -> contract.getAddress() != null ? contract.getAddress().computeCaption()
                : "");
        return addColumn;
      }
    };
    contractGrid.setBaseQueries(new ContractQueries());
    contractGrid.setAddressBaseQueries(new AddressQueries());
    contractGrid.setSizeFull();
    contractGrid.getGrid().setFilterBuilder(new FilterBuilder<ContractFilter>() {
      @Override
      public ContractFilter buildFilter() {
        ContractFilter filter = new ContractFilter();
        Address address = contractGrid.getAddressFilterField().getValue();
        filter.setAddress(address);
        filter.setName(contractGrid.getNameFilterField().getValue());
        return filter;
      }
    });

    ContractForm details = new ContractForm();
    details.setAddressBaseQueries(new AddressQueries());
    contractGrid.getGrid().addSelectionListener(
        event -> details.setValue(event.getFirstSelectedItem().orElseGet(null)));
    getContent().add(new VerticalLayout(contractGrid, details));
  }
}
