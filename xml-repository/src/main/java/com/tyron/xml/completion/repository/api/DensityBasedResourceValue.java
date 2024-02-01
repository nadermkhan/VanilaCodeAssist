package com.tyron.xml.completion.repository.api;

import com.tyron.builder.compiler.manifest.resources.Density;
import com.tyron.builder.compiler.manifest.resources.ResourceType;
import org.jetbrains.annotations.NotNull;

/** Represents an Android resource value associated with a particular screen density. */
public interface DensityBasedResourceValue extends ResourceValue {
  /** Returns the density for which this resource is configured. */
  @NotNull
  Density getResourceDensity();

  /**
   * Checks if resources of the given resource type should be created as density based when they
   * belong to a folder with a density qualifier.
   */
  static boolean isDensityBasedResourceType(@NotNull ResourceType resourceType) {
    // It is not clear why only drawables and mipmaps are treated as density dependent.
    // This logic has been moved from ResourceMergerItem.getResourceValue.
    return resourceType == ResourceType.DRAWABLE || resourceType == ResourceType.MIPMAP;
  }
}
