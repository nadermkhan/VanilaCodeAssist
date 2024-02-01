package com.tyron.xml.completion.repository.api;

import com.google.common.base.Splitter;
import com.tyron.builder.compiler.manifest.resources.ResourceType;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Formats of styleable attribute value. */
public enum AttributeFormat {
  BOOLEAN("boolean", EnumSet.of(ResourceType.BOOL)),
  COLOR("color", EnumSet.of(ResourceType.COLOR, ResourceType.DRAWABLE, ResourceType.MIPMAP)),
  DIMENSION("dimension", EnumSet.of(ResourceType.DIMEN)),
  ENUM("enum", Collections.emptySet()),
  FLAGS("flags", Collections.emptySet()),
  FLOAT("float", EnumSet.of(ResourceType.INTEGER)),
  FRACTION("fraction", EnumSet.of(ResourceType.FRACTION)),
  INTEGER("integer", EnumSet.of(ResourceType.INTEGER)),
  REFERENCE("reference", ResourceType.REFERENCEABLE_TYPES),
  STRING("string", EnumSet.of(ResourceType.STRING));

  private static final Splitter PIPE_SPLITTER = Splitter.on('|').trimResults();

  private final String name;

  private final Set<ResourceType> matchingTypes;

  AttributeFormat(@NotNull String name, @NotNull Set<ResourceType> matchingTypes) {
    this.name = name;
    this.matchingTypes = matchingTypes;
  }

  AttributeFormat(@NotNull String name, @NotNull EnumSet<ResourceType> matchingTypes) {
    this(name, Collections.unmodifiableSet(matchingTypes));
  }

  /** Returns the name used for the format in XML. */
  @NotNull
  public String getName() {
    return name;
  }

  /** Returns the set of matching resource types. */
  @NotNull
  public Set<ResourceType> getMatchingTypes() {
    return matchingTypes;
  }

  /**
   * Returns the format given its XML name.
   *
   * @param name the name used for the format in XML
   * @return the format, or null if the given name doesn't match any formats
   */
  @Nullable
  public static AttributeFormat fromXmlName(@NotNull String name) {
    switch (name) {
      case "boolean":
        return BOOLEAN;
      case "color":
        return COLOR;
      case "dimension":
        return DIMENSION;
      case "enum":
        return ENUM;
      case "flags":
        return FLAGS;
      case "float":
        return FLOAT;
      case "fraction":
        return FRACTION;
      case "integer":
        return INTEGER;
      case "reference":
        return REFERENCE;
      case "string":
        return STRING;
    }
    return null;
  }

  /** Parses a pipe-separated format string to a set of formats. */
  @NotNull
  public static Set<AttributeFormat> parse(@NotNull String formatString) {
    Set<AttributeFormat> result = EnumSet.noneOf(AttributeFormat.class);
    for (String formatName : PIPE_SPLITTER.split(formatString)) {
      AttributeFormat format = fromXmlName(formatName);
      if (format != null) {
        result.add(format);
      }
    }
    return result;
  }
}
