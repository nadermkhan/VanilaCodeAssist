/*
 * Copyright 2019 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.value;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.LruCache;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.flipkart.android.proteus.ProteusConstants;
import com.flipkart.android.proteus.ProteusContext;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AttributeResource
 *
 * @author adityasharat
 */
public class AttributeResource extends Value {

  public static final AttributeResource NULL = new AttributeResource(-1);

  private static final String ATTR_START_LITERAL = "?";
  private static final String ATTR_LITERAL = "attr/";
  private static final Pattern sAttributePattern =
      Pattern.compile("(\\?)(\\S*)(:?)(attr/?)(\\S*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Map<String, Class> sHashMap = new HashMap<>();

  public final int attributeId;
  private final String value;

  public AttributeResource(String value) {
    attributeId = -1;
    this.value = value;
  }

  private AttributeResource(final int attributeId) {
    this.attributeId = attributeId;
    value = String.valueOf(attributeId);
  }

  private AttributeResource(String value, Context context)
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    this.value = value;
    String attributeName;
    String packageName = null;
    Matcher matcher = sAttributePattern.matcher(value);

    if (matcher.matches()) {
      attributeName = matcher.group(5);
      packageName = matcher.group(2);
    } else {
      attributeName = value.substring(1);
    }

    Class clazz;
    if (null != packageName && !packageName.isEmpty()) {
      packageName = packageName.substring(0, packageName.length() - 1);
    } else {
      packageName = context.getPackageName();
    }

    String className = packageName + ".R$attr";
    clazz = sHashMap.get(className);
    if (null == clazz) {
      clazz = Class.forName(className);
      sHashMap.put(className, clazz);
    }
    Field field = clazz.getField(attributeName);
    attributeId = field.getInt(null);
  }

  public String getValue() {
    return value;
  }

  public String getName() {
    Matcher matcher = sAttributePattern.matcher(value);
    if (matcher.matches()) {
      String name = matcher.group(5);
      if (value.startsWith("?android:attr")) {
        return "android:" + name;
      }
      return name;
    }
    return value;
  }

  public static boolean isAttributeResource(String value) {
    return value.startsWith(ATTR_START_LITERAL) && value.contains(ATTR_LITERAL);
  }

  public static AttributeResource valueOf(String value) {
    return new AttributeResource(value);
  }

  @Nullable
  public static AttributeResource valueOf(String value, Context context) {
    AttributeResource attribute = AttributeCache.cache.get(value);
    if (null == attribute) {
      try {
        attribute = new AttributeResource(value, context);
      } catch (Exception e) {
        if (ProteusConstants.isLoggingEnabled()) {
          e.printStackTrace();
        }
        attribute = NULL;
      }
      AttributeCache.cache.put(value, attribute);
    }
    return NULL == attribute ? null : attribute;
  }

  @Nullable
  public static AttributeResource valueOf(int value) {
    return new AttributeResource(value);
  }

  public TypedArray apply(@NonNull Context context) {
    return context.obtainStyledAttributes(new int[] {attributeId});
  }

  public Value resolve(View parent, View view, ProteusContext context) {
    return context.obtainStyledAttribute(parent, view, getName());
  }

  @Override
  public Value copy() {
    return this;
  }

  public String toString() {
    return value;
  }

  private static class AttributeCache {
    static final LruCache<String, AttributeResource> cache = new LruCache<>(16);
  }
}
