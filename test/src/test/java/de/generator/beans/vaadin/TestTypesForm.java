package de.generator.beans.vaadin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;

class TestTypesForm {
  private TypesFormExtended formUT = new TypesFormExtended();

  @Test
  void testGetIdField() {
    assertThat(formUT.getIdField().isRequired(), is(true));
  }

  @Test
  void testGetIntTypeField() {
    assertThat(formUT.getIntTypeField().isRequired(), is(true));
  }

  @Test
  void testGetIntegerTypeField() {
    assertThat(formUT.getIntegerTypeField().isRequired(), is(false));
  }

  @Test
  void testGetDoubleTypeField() {
    assertThat(formUT.getDoubleTypeField().isRequired(), is(true));
  }

  @Test
  void testGetDoubleClassTypeField() {
    assertThat(formUT.getDoubleClassTypeField().isRequired(), is(false));
  }

  @Test
  void testGetLongTypeField() {
    assertThat(formUT.getLongTypeField().isRequired(), is(true));
  }

  @Test
  void testGetLongClassTypeField() {
    assertThat(formUT.getLongClassTypeField().isRequired(), is(false));
  }

  @Test
  void testGetFloatTypeField() {
    assertThat(formUT.getFloatTypeField().isRequired(), is(true));
  }

  @Test
  void testGetFloatClassTypeField() {
    assertThat(formUT.getFloatClassTypeField().isRequired(), is(false));
  }

  @Test
  void testGetStringTypeField() {
    assertThat(formUT.getStringTypeField().isRequired(), is(false));
  }
}
