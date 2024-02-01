package com.tyron.layout.appcompat.widget;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.tyron.layout.appcompat.view.ProteusAppCompatToolbar;

public class AppCompatToolbarParser<V extends View> extends ViewTypeParser<V> {
  @NonNull
  @Override
  public String getType() {
    return Toolbar.class.getName();
  }

  @Nullable
  @Override
  public String getParentType() {
    return ViewGroup.class.getName();
  }

  @NonNull
  @Override
  public ProteusView createView(
      @NonNull ProteusContext context,
      @NonNull Layout layout,
      @NonNull ObjectValue data,
      @Nullable ViewGroup parent,
      int dataIndex) {
    return new ProteusAppCompatToolbar(context);
  }

  @Override
  protected void addAttributeProcessors() {
    // TODO: add processors
  }
}
