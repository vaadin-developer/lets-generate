package de.generator.beans.vaadin;

import org.rapidpm.vgu.vaadin.FilterGrid.FilterBuilder;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
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
        Address address =
            ((HasValue<ValueChangeEvent<Address>, Address>) contractGrid.getAddressFilterField())
                .getValue();
        filter.setAddress(address);
        filter.setName(
            ((HasValue<ValueChangeEvent<String>, String>) contractGrid.getNameFilterField())
                .getValue());
        return filter;
      }
    });
    getContent().add(contractGrid);
  }
}
