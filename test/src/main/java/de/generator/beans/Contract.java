package de.generator.beans;

import org.rapidpm.vgu.generator.annotation.DataBean;
import org.rapidpm.vgu.generator.annotation.FilterProperty;
import org.rapidpm.vgu.generator.annotation.SortProperty;
import net.vergien.beanautoutils.annotation.Bean;

@DataBean
@Bean
public class Contract extends BaseEntity {
  @SortProperty(defaultSort = true)
  @FilterProperty(defaultFilter = true)
  private String name;

  private Address address;

  public Contract() {
    super();
  }

  public Contract(int id, String name, Address address) {
    super(id);
    this.name = name;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  @Override
  public String toString() {
    return ContractBeanUtil.doToString(this);
  }

  @Override
  public int hashCode() {
    return ContractBeanUtil.doToHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return ContractBeanUtil.doEquals(this, obj);
  }
}
