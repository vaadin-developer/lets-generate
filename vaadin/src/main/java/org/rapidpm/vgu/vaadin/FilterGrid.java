package org.rapidpm.vgu.vaadin;

import java.util.Objects;
import org.rapidpm.dependencies.core.logger.HasLogger;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.InputNotifier;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.ValueProvider;

public class FilterGrid<T, F> extends Grid<T> implements HasLogger {
  @FunctionalInterface
  public static interface FilterBuilder<F> {
    F buildFilter();
  }

  private F filter;
  private SerializableConsumer<F> filterConsumer;
  private HeaderRow filterRow;
  private FilterBuilder<F> filterBuilder;

  public FilterGrid() {
    super();
  }

  public FilterGrid(Class<T> beanType) {
    super(beanType);
  }


  public <V extends Comparable<? super V>> Column<T> addColumn(ValueProvider<T, V> valueProvider,
      Component filterComponent, String... sortingProperties) {
    Column<T> column = super.addColumn(valueProvider, sortingProperties);
    addFilter(column, filterComponent);
    return column;
  }

  public <V extends Object> void addFilter(Column<?> column, Component component) {
    if (filterRow == null) {
      filterRow = appendHeaderRow();
    }
    filterRow.getCell(column).setComponent(component);
    if (component instanceof HasSize) {
      ((HasSize) component).setWidth("100%");
    }
    if (component instanceof HasValueChangeMode) {
      ((HasValueChangeMode) component).setValueChangeMode(ValueChangeMode.EAGER);
    }
    if (component instanceof InputNotifier) {
      ((InputNotifier) component).addInputListener(e -> updateFilter());
    } else if (component instanceof HasValue) {
      ((HasValue) component).addValueChangeListener(e -> updateFilter());
    }
  }

  public FilterGrid<T, F> setFilter(F filter) {
    this.filter = filter;
    updateFilter(filter);
    return this;
  }

  public void updateFilter() {
    updateFilter(filterBuilder.buildFilter());
  }

  private void updateFilter(F filter) {
    if (filterConsumer != null) {
      filterConsumer.accept(filter);
    }
  }

  public F getFilter() {
    return filter;
  }

  public void setFilterableDataProvider(DataProvider<T, F> dataProvider) {

    // Copied from grid, just save the filterConsumer

    Objects.requireNonNull(dataProvider, "data provider cannot be null");
    deselectAll();

    filterConsumer = getDataCommunicator().setDataProvider(dataProvider, filter);
    filterConsumer.accept(filter);
    /*
     * The visibility of the selectAll checkbox depends on whether the DataProvider is inMemory or
     * not. When changing the DataProvider, its visibility needs to be revalidated.
     */
    if (getSelectionModel() instanceof GridMultiSelectionModel) {
      GridMultiSelectionModel<T> model = (GridMultiSelectionModel<T>) getSelectionModel();
      model.setSelectAllCheckboxVisibility(model.getSelectAllCheckboxVisibility());
    }
  }

  public void setFilterBuilder(FilterBuilder<F> filterBuilder) {
    this.filterBuilder = filterBuilder;
  }

  public void addFilter(String string, Component component) {
    addFilter(getColumnByKey(string), component);
  }
}
