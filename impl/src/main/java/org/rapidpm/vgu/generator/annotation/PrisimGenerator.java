package org.rapidpm.vgu.generator.annotation;

import net.java.dev.hickory.prism.GeneratePrism;
import net.java.dev.hickory.prism.GeneratePrisms;

@GeneratePrisms({
        @GeneratePrism(value = DataBean.class, publicAccess = true),
        @GeneratePrism(value = FilterProperty.class, publicAccess = true),
        @GeneratePrism(value = SortProperty.class, publicAccess = true),
        @GeneratePrism(value = CustomFilter.class, publicAccess = true) })
public class PrisimGenerator {

}
