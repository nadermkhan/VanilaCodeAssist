package com.tyron.xml.completion.repository.api;

import com.tyron.builder.compiler.manifest.resources.ResourceType;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A resource value representing an attr resource. */
public class AttrResourceValueImpl extends ResourceValueImpl implements AttrResourceValue {
  /** The keys are enum or flag names, the values are corresponding numeric values. */
  @Nullable private Map<String, Integer> valueMap;
  /** The keys are enum or flag names, the values are the value descriptions. */
  @Nullable private Map<String, String> valueDescriptionMap;

  @Nullable private String description;
  @Nullable private String groupName;
  @NotNull private Set<AttributeFormat> formats = EnumSet.noneOf(AttributeFormat.class);

  public AttrResourceValueImpl(
      @NotNull ResourceNamespace namespace, @NotNull String name, @Nullable String libraryName) {
    super(namespace, ResourceType.ATTR, name, null, libraryName);
  }

  public AttrResourceValueImpl(@NotNull ResourceReference reference, @Nullable String libraryName) {
    super(reference, null, libraryName);
  }

  @Override
  @NotNull
  public Map<String, Integer> getAttributeValues() {
    return valueMap == null ? Collections.emptyMap() : valueMap;
  }

  @Override
  @Nullable
  public String getValueDescription(@NotNull String valueName) {
    return valueDescriptionMap == null ? null : valueDescriptionMap.get(valueName);
  }

  @Override
  @Nullable
  public String getDescription() {
    return description;
  }

  @Override
  @Nullable
  public String getGroupName() {
    return groupName;
  }

  @Override
  @NotNull
  public Set<AttributeFormat> getFormats() {
    return formats;
  }

  /**
   * Adds a possible value of the flag or enum attribute.
   *
   * @param valueName the name of the value
   * @param numericValue the corresponding numeric value
   * @param valueName the description of the value
   */
  public void addValue(
      @NotNull String valueName, @Nullable Integer numericValue, @Nullable String description) {
    if (valueMap == null) {
      valueMap = new LinkedHashMap<>();
    }

    valueMap.put(valueName, numericValue);

    if (description != null) {
      if (valueDescriptionMap == null) {
        valueDescriptionMap = new HashMap<>();
      }

      valueDescriptionMap.put(valueName, description);
    }
  }

  /**
   * Sets the description of the attr resource.
   *
   * @param description the description to set
   */
  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  /**
   * Sets the name of group the attr resource belongs to.
   *
   * @param groupName the name of the group to set
   */
  public void setGroupName(@Nullable String groupName) {
    this.groupName = groupName;
  }

  /**
   * Sets the formats allowed for the attribute.
   *
   * @param formats the formats to set
   */
  public void setFormats(@NotNull Collection<AttributeFormat> formats) {
    this.formats = EnumSet.copyOf(formats);
  }
}
