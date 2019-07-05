package de.generator.beans;

import org.rapidpm.vgu.generator.annotation.Caption;
import org.rapidpm.vgu.generator.annotation.CustomFilter;
import org.rapidpm.vgu.generator.annotation.DataBean;
import org.rapidpm.vgu.generator.annotation.FilterProperty;
import org.rapidpm.vgu.generator.annotation.SortProperty;
import net.vergien.beanautoutils.annotation.Bean;

@DataBean(customFilters = {@CustomFilter(type = Integer.class, name = "maxAge", sort = false),
    @CustomFilter(name = "name", defaultFilter = true)})
@Bean
public class Address extends BaseEntity {

  @SortProperty(defaultSort = true)
  @FilterProperty
  private String fristName;
  @FilterProperty
  private String lastName;
  @SortProperty
  @FilterProperty
  private int age;
  @FilterProperty
  private boolean privateAddress;
  private String phone;

  public Address() {
    super();
  }

  public Address(int id, String fristName, String lastName, int age, String phone) {
    super(id);
    this.fristName = fristName;
    this.lastName = lastName;
    this.age = age;
    this.phone = phone;
  }

  @Caption
  public String computeCaption() {
    return getId() + " - " + fristName + " " + lastName;
  }

  public boolean isPrivateAddress() {
    return privateAddress;
  }

  public void setPrivateAddress(boolean privateAddress) {
    this.privateAddress = privateAddress;
  }

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
