package org.rapidpm.vgu.generator.codegenerator.vaadin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

class TestFieldCreatorFactory {
  private FieldCreatorFactory factoryUT = new FieldCreatorFactory();

  @Test
  @DisplayName("Default int creator")
  void test001() {
    Optional<FieldCreator> creator = factoryUT.getFieldCreator(TypeName.INT);
    assertTrue(creator.isPresent());
    assertEquals(IntegerFieldCreator.class, creator.get().getClass());
  }

  @Test
  @DisplayName("Default Integer creator")
  void test002() {
    Optional<FieldCreator> creator = factoryUT.getFieldCreator(ClassName.get(Integer.class));
    assertTrue(creator.isPresent());
    assertEquals(IntegerFieldCreator.class, creator.get().getClass());
  }
  
  @Test
  @DisplayName("Default String creator")
  void test003() {
    Optional<FieldCreator> creator = factoryUT.getFieldCreator(ClassName.get(String.class));
    assertTrue(creator.isPresent());
    assertEquals(TextFieldCreator.class, creator.get().getClass());
  }
}
