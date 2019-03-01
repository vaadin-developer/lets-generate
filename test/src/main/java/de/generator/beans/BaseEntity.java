package de.generator.beans;

import org.rapidpm.vgu.generator.annotation.DisplayReadOnly;
import org.rapidpm.vgu.generator.annotation.SortProperty;

public abstract class BaseEntity {
  @SortProperty
  @DisplayReadOnly
  private int id;

  public BaseEntity(int id) {
    super();
    this.id = id;
  }

  public BaseEntity() {
    super();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
