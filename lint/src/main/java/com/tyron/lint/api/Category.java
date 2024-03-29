package com.tyron.lint.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** A category is a container for related issues. */
public final class Category implements Comparable<Category> {
  private final String mName;
  private final int mPriority;
  private final Category mParent;

  /**
   * Creates a new {@link Category}.
   *
   * @param parent the name of a parent category, or null
   * @param name the name of the category
   * @param priority a sorting priority, with higher being more important
   */
  private Category(@Nullable Category parent, @NonNull String name, int priority) {
    mParent = parent;
    mName = name;
    mPriority = priority;
  }

  /**
   * Creates a new top level {@link Category} with the given sorting priority.
   *
   * @param name the name of the category
   * @param priority a sorting priority, with higher being more important
   * @return a new category
   */
  @NonNull
  public static Category create(@NonNull String name, int priority) {
    return new Category(null, name, priority);
  }

  /**
   * Creates a new top level {@link Category} with the given sorting priority.
   *
   * @param parent the name of a parent category, or null
   * @param name the name of the category
   * @param priority a sorting priority, with higher being more important
   * @return a new category
   */
  @NonNull
  public static Category create(@Nullable Category parent, @NonNull String name, int priority) {
    return new Category(parent, name, priority);
  }

  /**
   * Returns the parent category, or null if this is a top level category
   *
   * @return the parent category, or null if this is a top level category
   */
  public Category getParent() {
    return mParent;
  }

  /**
   * Returns the name of this category
   *
   * @return the name of this category
   */
  public String getName() {
    return mName;
  }

  /**
   * Returns a full name for this category. For a top level category, this is just the {@link
   * #getName()} value, but for nested categories it will include the parent names as well.
   *
   * @return a full name for this category
   */
  public String getFullName() {
    if (mParent != null) {
      return mParent.getFullName() + ':' + mName;
    } else {
      return mName;
    }
  }

  @Override
  public int compareTo(Category other) {
    if (other.mPriority == mPriority) {
      if (mParent == other) {
        return 1;
      } else if (other.mParent == this) {
        return -1;
      }
    }
    return other.mPriority - mPriority;
  }

  /** Issues related to running lint itself */
  public static final Category LINT = create("Lint", 110);

  /** Issues related to correctness */
  public static final Category CORRECTNESS = create("Correctness", 100);

  /** Issues related to security */
  public static final Category SECURITY = create("Security", 90);

  /** Issues related to performance */
  public static final Category PERFORMANCE = create("Performance", 80);

  /** Issues related to usability */
  public static final Category USABILITY = create("Usability", 70);

  /** Issues related to accessibility */
  public static final Category A11Y = create("Accessibility", 60);

  /** Issues related to internationalization */
  public static final Category I18N = create("Internationalization", 50);

  /** Issues related to right to left and bi-directional text support */
  public static final Category RTL = create("Bi-directional Text", 40);

  // Sub categories

  /** Issues related to icons */
  public static final Category ICONS = create(USABILITY, "Icons", 73);

  /** Issues related to typography */
  public static final Category TYPOGRAPHY = create(USABILITY, "Typography", 76);

  /** Issues related to messages/strings */
  public static final Category MESSAGES = create(CORRECTNESS, "Messages", 95);
}
