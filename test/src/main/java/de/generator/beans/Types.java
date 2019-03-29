package de.generator.beans;

import javax.validation.constraints.NotNull;
import org.rapidpm.vgu.generator.annotation.DataBean;
import org.rapidpm.vgu.generator.annotation.FilterProperty;
import org.rapidpm.vgu.generator.annotation.SortProperty;
import net.vergien.beanautoutils.annotation.Bean;

@Bean
@DataBean
public class Types extends BaseEntity {
  @SortProperty(defaultSort = true)
  private int intType;
  private Integer integerType;
  private double doubleType;
  private Double doubleClassType;
  private long longType;
  private Long longClassType;
  private float floatType;
  private Float floatClassType;
  @FilterProperty(defaultFilter = true)
  private String stringType;
  @NotNull
  private String requieredString;

  public int getIntType() {
    return intType;
  }

  public void setIntType(int intType) {
    this.intType = intType;
  }

  public Integer getIntegerType() {
    return integerType;
  }

  public void setIntegerType(Integer integerType) {
    this.integerType = integerType;
  }

  public double getDoubleType() {
    return doubleType;
  }

  public void setDoubleType(double doubleType) {
    this.doubleType = doubleType;
  }

  public Double getDoubleClassType() {
    return doubleClassType;
  }

  public void setDoubleClassType(Double doubleClassType) {
    this.doubleClassType = doubleClassType;
  }

  public long getLongType() {
    return longType;
  }

  public void setLongType(long longType) {
    this.longType = longType;
  }

  public Long getLongClassType() {
    return longClassType;
  }

  public void setLongClassType(Long longClassType) {
    this.longClassType = longClassType;
  }

  public float getFloatType() {
    return floatType;
  }

  public void setFloatType(float floatType) {
    this.floatType = floatType;
  }

  public Float getFloatClassType() {
    return floatClassType;
  }

  public void setFloatClassType(Float floatClassType) {
    this.floatClassType = floatClassType;
  }

  public String getStringType() {
    return stringType;
  }

  public void setStringType(String stringType) {
    this.stringType = stringType;
  }

  public String getRequieredString() {
    return requieredString;
  }

  public void setRequieredString(String requieredString) {
    this.requieredString = requieredString;
  }

  @Override
  public String toString() {
    return TypesBeanUtil.doToString(this);
  }

  @Override
  public int hashCode() {
    return TypesBeanUtil.doToHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return TypesBeanUtil.doEquals(this, obj);
  }
}
