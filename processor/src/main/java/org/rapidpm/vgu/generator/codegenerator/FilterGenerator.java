package org.rapidpm.vgu.generator.codegenerator;

import static org.rapidpm.vgu.generator.codegenerator.ClassNameUtils.prefixCamelCase;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import org.rapidpm.vgu.generator.annotation.DataBeanType;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import org.rapidpm.vgu.generator.model.PropertyModel;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.FieldSpec.Builder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.vergien.beanautoutils.annotation.Bean;

public class FilterGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    TypeSpec filterClass =
        TypeSpec.classBuilder(model.getName() + classSuffix()).addModifiers(Modifier.PUBLIC)
            .addFields(model.getFilterProperties().stream()
                .map(p -> generateFieldSpec(p, model.getModelType())).collect(Collectors.toSet()))
            .addMethods(model.getFilterProperties().stream().map(p -> generateGetter(p))
                .collect(Collectors.toSet()))
            .addMethods(model.getFilterProperties().stream().map(p -> generateSetter(p))
                .collect(Collectors.toSet()))
            .addMethod(generateToString(model)).addMethod(generateHashCode(model))
            .addMethod(generateEquals(model)).addAnnotation(Bean.class).build();

    writeClass(filer, model, filterClass);
  }

  @Override
  public String classSuffix() {
    return "Filter";
  }

  @Override
  public String packageSuffix() {
    return "filter";
  }

  private MethodSpec generateEquals(DataBeanModel model) {
    return MethodSpec.methodBuilder("equals").addAnnotation(Override.class)
        .returns(TypeName.BOOLEAN).addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.OBJECT, "obj")
        .addStatement("return " + model.getName() + "FilterBeanUtil.doEquals(this, obj)").build();
  }

  private MethodSpec generateToString(DataBeanModel model) {
    return MethodSpec.methodBuilder("toString").addAnnotation(Override.class).returns(String.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("return " + model.getName() + "FilterBeanUtil.doToString(this)").build();
  }

  private MethodSpec generateHashCode(DataBeanModel model) {
    return MethodSpec.methodBuilder("hashCode").addAnnotation(Override.class).returns(TypeName.INT)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("return " + model.getName() + "FilterBeanUtil.doToHashCode(this)").build();
  }

  private MethodSpec generateGetter(PropertyModel p) {
    MethodSpec methodSpec = MethodSpec.methodBuilder(prefixCamelCase("get", p.getName()))
        .addModifiers(Modifier.PUBLIC).returns(TypeName.get(p.getType()).box())
        .addStatement("return this." + p.getName()).build();
    return methodSpec;
  }

  private MethodSpec generateSetter(PropertyModel p) {
    MethodSpec methodSpec =
        MethodSpec.methodBuilder(prefixCamelCase("set", p.getName())).addModifiers(Modifier.PUBLIC)
            .returns(void.class).addParameter(TypeName.get(p.getType()).box(), p.getName())
            .addStatement("this." + p.getName() + " = " + p.getName()).build();
    return methodSpec;
  }

  private FieldSpec generateFieldSpec(PropertyModel p, DataBeanType beanType) {
    Builder builder =
        FieldSpec.builder(TypeName.get(p.getType()).box(), p.getName(), Modifier.PRIVATE);
    if (DataBeanType.JAXRS.equals(beanType)) {
      builder.addAnnotation(AnnotationSpec.builder(ClassName.get("javax.ws.rs", "QueryParam"))
          .addMember("value", "$S", p.getName()).build());
    }
    FieldSpec fieldSpec = builder.build();
    return fieldSpec;
  }
}
