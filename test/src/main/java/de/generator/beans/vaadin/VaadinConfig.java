package de.generator.beans.vaadin;

import org.rapidpm.vgu.generator.annotation.VaadinDataBeans;
import de.generator.beans.Address;
import de.generator.beans.Contract;
import de.generator.beans.Types;

@VaadinDataBeans({Address.class, Contract.class, Types.class})
public interface VaadinConfig {

}
