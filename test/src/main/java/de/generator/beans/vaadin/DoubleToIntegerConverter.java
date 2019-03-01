package de.generator.beans.vaadin;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class DoubleToIntegerConverter implements Converter<Double, Integer> {

  @Override
  public Result<Integer> convertToModel(Double value, ValueContext context) {
    if (value == null) {
      return Result.ok(null);
    }
    return Result.ok(Integer.valueOf((int) Math.round(value)));
  }

  @Override
  public Double convertToPresentation(Integer value, ValueContext context) {
    if (value == null) {
      return null;
    }
    return Double.valueOf(value);
  }

}
