package com.tyron.xml.completion.repository.api;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tyron.builder.compiler.manifest.resources.ResourceType;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an Android style resource with a name and a list of children {@link ResourceValue}.
 */
public class StyleResourceValueImpl extends ResourceValueImpl implements StyleResourceValue {
  /**
   * Contents of the {@code parent} XML attribute. May be empty or null.
   *
   * @see #StyleResourceValueImpl(ResourceReference, String, String)
   */
  @Nullable private String parentStyle;

  /**
   * Items defined in this style, indexed by the namespace and name of the attribute they define.
   */
  @NotNull
  private final Table<ResourceNamespace, String, StyleItemResourceValue> styleItems =
      HashBasedTable.create();

  /**
   * Creates a new {@link StyleResourceValueImpl}.
   *
   * <p>Note that names of styles have more meaning than other resources: if the parent attribute is
   * not set, aapt looks for a dot in the style name and treats the string up to the last dot as the
   * name of a parent style. So {@code <style name="Foo.Bar.Baz">} has an implicit parent called
   * {@code Foo.Bar}. Setting the {@code parent} XML attribute disables this feature, even if it's
   * set to an empty string. See {@code ResourceParser::ParseStyle} in aapt for details.
   */
  public StyleResourceValueImpl(
      @NotNull ResourceNamespace namespace,
      @NotNull String name,
      @Nullable String parentStyle,
      @Nullable String libraryName) {
    super(namespace, ResourceType.STYLE, name, null, libraryName);
    this.parentStyle = parentStyle;
  }

  /**
   * Creates a new {@link StyleResourceValueImpl}.
   *
   * @see #StyleResourceValueImpl(ResourceNamespace, String, String, String)
   */
  public StyleResourceValueImpl(
      @NotNull ResourceReference reference,
      @Nullable String parentStyle,
      @Nullable String libraryName) {
    super(reference, null, libraryName);
    assert reference.getResourceType() == ResourceType.STYLE;
    this.parentStyle = parentStyle;
  }

  /** Creates a copy of the given style. */
  @NotNull
  public static StyleResourceValueImpl copyOf(@NotNull StyleResourceValue style) {
    StyleResourceValueImpl copy =
        new StyleResourceValueImpl(
            style.getNamespace(),
            style.getName(),
            style.getParentStyleName(),
            style.getLibraryName());
    for (StyleItemResourceValue item : style.getDefinedItems()) {
      copy.addItem(item);
    }
    return copy;
  }

  @Override
  @Nullable
  public String getParentStyleName() {
    return parentStyle;
  }

  @Override
  @Nullable
  public StyleItemResourceValue getItem(
      @NotNull ResourceNamespace namespace, @NotNull String name) {
    return styleItems.get(namespace, name);
  }

  @Override
  @Nullable
  public StyleItemResourceValue getItem(@NotNull ResourceReference attr) {
    assert attr.getResourceType() == ResourceType.ATTR;
    return styleItems.get(attr.getNamespace(), attr.getName());
  }

  @Override
  @NotNull
  public Collection<StyleItemResourceValue> getDefinedItems() {
    return styleItems.values();
  }

  /**
   * Adds a style item to this style.
   *
   * @param item the style item to add
   */
  public void addItem(@NotNull StyleItemResourceValue item) {
    ResourceReference attr = item.getAttr();
    if (attr != null) {
      styleItems.put(attr.getNamespace(), attr.getName(), item);
    }
  }

  @Override
  public void replaceWith(@NotNull ResourceValue style) {
    assert style instanceof StyleResourceValueImpl
        : style.getClass() + " is not StyleResourceValue";
    super.replaceWith(style);

    //noinspection ConstantConditions
    if (style instanceof StyleResourceValueImpl) {
      styleItems.clear();
      styleItems.putAll(((StyleResourceValueImpl) style).styleItems);
    }
  }
}
