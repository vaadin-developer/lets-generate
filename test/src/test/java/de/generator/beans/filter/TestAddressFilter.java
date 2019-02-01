package de.generator.beans.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TestAddressFilter {

  @Test
  void test() {
    AddressFilter filter = new AddressFilter();

    filter.setMaxAge(Integer.valueOf(22));
    filter.setName("Bla");

    assertEquals(Integer.class, filter.getMaxAge().getClass());
    assertEquals(String.class, filter.getName().getClass());
  }

}
