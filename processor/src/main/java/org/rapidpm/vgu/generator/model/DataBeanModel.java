package org.rapidpm.vgu.generator.model;

import static org.rapidpm.vgu.generator.codegenerator.ClassNameUtils.getPackageName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.rapidpm.vgu.generator.annotation.DataBeanType;
import net.vergien.beanautoutils.annotation.Bean;

@Bean
public class DataBeanModel {
  private Class<?> clazz;
  private DataBeanType modelType;
  private String name;
  private String pkg;
  private String fqnNAme;
  private Optional<ExecutableElement> captionMethod;
  private List<String> imports;
  private Optional<PropertyModel> idProperty = Optional.empty();
  private List<PropertyModel> properties = new ArrayList<>();
  private Set<PropertyModel> sortProperties = new HashSet<>();
  private Set<PropertyModel> filterProperties = new HashSet<>();
  private Optional<PropertyModel> defaultSortProperty = Optional.empty();
  private Optional<PropertyModel> defaultFilterProperty = Optional.empty();

  public DataBeanModel(String simpleName) {
    this.name = simpleName;
  }

  public DataBeanModel(TypeElement typeElement) {
    super();
    this.clazz = typeElement.getClass();
    this.name = typeElement.getSimpleName().toString();
    this.fqnNAme = typeElement.getQualifiedName().toString();
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public String getFqnNAme() {
    return fqnNAme;
  }

  public Optional<PropertyModel> getIdProperty() {
    return idProperty;
  }

  public void setIdProperty(Optional<PropertyModel> idProperty) {
    this.idProperty = idProperty;
  }

  public void addSortProperty(PropertyModel sortProperty) {
    this.sortProperties.add(sortProperty);
  }

  public void addFilterProperty(PropertyModel filterProperty) {
    this.filterProperties.add(filterProperty);
  }

  public Set<PropertyModel> getSortProperties() {
    return sortProperties;
  }

  public String getName() {
    return name;
  }

  public Set<PropertyModel> getFilterProperties() {
    return filterProperties;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setFqnNAme(String fqnNAme) {
    this.fqnNAme = fqnNAme;
  }

  public void setSortProperties(Set<PropertyModel> sortProperties) {
    this.sortProperties = sortProperties;
  }

  public void setFilterProperties(Set<PropertyModel> filterProperties) {
    this.filterProperties = filterProperties;
  }

  public String getPkg() {
    return pkg;
  }

  public void setPkg(String pkg) {
    this.pkg = pkg;
  }

  public List<String> getImports() {
    return imports;
  }

  public void setImports(List<String> imports) {
    this.imports = imports;
  }

  public DataBeanType getModelType() {
    return modelType;
  }

  public void setModelType(DataBeanType modelType) {
    this.modelType = modelType;
  }

  public Optional<ExecutableElement> getCaptionMethod() {
    return captionMethod;
  }

  public void setCaptionMethod(Optional<ExecutableElement> captionMethod) {
    this.captionMethod = captionMethod;
  }

  @Override
  public String toString() {
    return DataBeanModelBeanUtil.doToString(this);
  }

  @Override
  public int hashCode() {
    return DataBeanModelBeanUtil.doToHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return DataBeanModelBeanUtil.doEquals(this, obj);
  }

  public String getPackage() {
    return getPackageName(fqnNAme);
  }

  public Optional<PropertyModel> getDefaultSortProperty() {
    return defaultSortProperty;
  }

  public void setDefaultSortProperty(Optional<PropertyModel> defaultSortProperty) {
    this.defaultSortProperty = defaultSortProperty;
  }

  public Optional<PropertyModel> getDefaultFilterProperty() {
    return defaultFilterProperty;
  }

  public void setDefaultFilterProperty(Optional<PropertyModel> defaultFilterProperty) {
    this.defaultFilterProperty = defaultFilterProperty;
  }

  public List<PropertyModel> getProperties() {
    return properties;
  }

  public void setProperties(List<PropertyModel> properties) {
    this.properties = properties;
  }
}
