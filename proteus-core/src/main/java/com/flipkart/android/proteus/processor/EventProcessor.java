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

package com.flipkart.android.proteus.processor;

import android.view.View;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.value.AttributeResource;
import com.flipkart.android.proteus.value.Resource;
import com.flipkart.android.proteus.value.Style;
import com.flipkart.android.proteus.value.Value;

/**
 * EventProcessor
 *
 * <p>
 *
 * <p>Use this Attribute Processor to get event callbacks on the {@link
 * com.flipkart.android.proteus.ProteusLayoutInflater.Callback#onEvent(String, Value, ProteusView)}
 *
 * @author adityasharat
 */
public abstract class EventProcessor<T extends View> extends AttributeProcessor<T> {

  @Override
  public void handleValue(View parent, T view, Value value) {
    setOnEventListener(view, value);
  }

  @Override
  public void handleResource(View parent, T view, Resource resource) {
    setOnEventListener(view, resource);
  }

  @Override
  public void handleAttributeResource(View parent, T view, AttributeResource attribute) {
    setOnEventListener(view, attribute);
  }

  @Override
  public void handleStyle(View parent, T view, Style style) {
    setOnEventListener(view, style);
  }

  public abstract void setOnEventListener(T view, Value value);

  /**
   * @param event
   * @param value
   * @param view
   */
  public void trigger(String event, Value value, ProteusView view) {
    ProteusLayoutInflater.Callback callback = view.getViewManager().getContext().getCallback();
    if (null != callback) {
      callback.onEvent(event, value, view);
    }
  }
}
