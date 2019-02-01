package org.rapidpm.vgu.generator.codegenerator;

import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import org.infinitenature.commons.pagination.OffsetRequest;
import org.infinitenature.commons.pagination.Slice;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class QueryInterfaceGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    TypeSpec baseQueriesInterface =
        TypeSpec.interfaceBuilder(model.getName() + classSuffix()).addModifiers(Modifier.PUBLIC)
            .addMethod(findMethod(model)).addMethod(countMethod(model)).build();
    writeClass(filer, model, baseQueriesInterface);
  }

  private MethodSpec countMethod(DataBeanModel model) {
    MethodSpec count = MethodSpec.methodBuilder("count")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).returns(TypeName.LONG)
        .addParameter(ClassName.get(model.getPkg() + ".filter", model.getName() + "Filter"),
            "filter")
        .build();
    return count;
  }

  private MethodSpec findMethod(DataBeanModel model) {
    ClassName sortPropertyClass =
        ClassName.get(ClassNameUtils.getFilterPackage(model), model.getName() + "SortFields");
    TypeName slice = ParameterizedTypeName.get(ClassName.get(Slice.class),
        ClassName.get(model.getPackage(), model.getName()), sortPropertyClass);
    TypeName offsetRequest =
        ParameterizedTypeName.get(ClassName.get(OffsetRequest.class), sortPropertyClass);
    return MethodSpec.methodBuilder("find").addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(slice)
        .addParameter(
            ClassName.get(ClassNameUtils.getFilterPackage(model), model.getName() + "Filter"),
            "filter")
        .addParameter(offsetRequest, "offsetRequest").build();
  }

  @Override
  public String packageSuffix() {
    return "repo";
  }

  @Override
  public String classSuffix() {
    return "BaseQueries";
  }

}
