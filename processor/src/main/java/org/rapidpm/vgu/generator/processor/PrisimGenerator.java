package org.rapidpm.vgu.generator.processor;

import org.rapidpm.vgu.generator.annotation.CustomFilter;
import org.rapidpm.vgu.generator.annotation.DataBean;
import org.rapidpm.vgu.generator.annotation.FilterProperty;
import org.rapidpm.vgu.generator.annotation.SortProperty;
import org.rapidpm.vgu.generator.annotation.VaadinDataBeans;
import net.java.dev.hickory.prism.GeneratePrism;
import net.java.dev.hickory.prism.GeneratePrisms;

@GeneratePrisms({@GeneratePrism(value = DataBean.class, publicAccess = true),
    @GeneratePrism(value = FilterProperty.class, publicAccess = true),
    @GeneratePrism(value = SortProperty.class, publicAccess = true),
    @GeneratePrism(value = CustomFilter.class, publicAccess = true),
    @GeneratePrism(value = VaadinDataBeans.class, publicAccess = true)})
public class PrisimGenerator {

}
