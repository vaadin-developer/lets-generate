package de.generator.beans;

import org.rapidpm.vgu.generator.annotation.CustomFilter;
import org.rapidpm.vgu.generator.annotation.DataBean;
import org.rapidpm.vgu.generator.annotation.FilterProperty;
import org.rapidpm.vgu.generator.annotation.SortProperty;
import net.vergien.beanautoutils.annotation.Bean;

@DataBean(customFilters = {@CustomFilter(type = String.class, name = "name"),
    @CustomFilter(name = "withDefault")})
@Bean
public class Address extends BaseEntity {

  @SortProperty
  @FilterProperty
  private String fristName;
  @FilterProperty
  private String lastName;
  @SortProperty
  @FilterProperty
  private int age;
  private String phone;

  public String getFristName() {
    return fristName;
  }

  public void setFristName(String fristName) {
    this.fristName = fristName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  @Override
  public boolean equals(Object obj) {
    return AddressBeanUtil.doEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return AddressBeanUtil.doToHashCode(this);
  }

  @Override
  public String toString() {
    return AddressBeanUtil.doToString(this);
  }
}
