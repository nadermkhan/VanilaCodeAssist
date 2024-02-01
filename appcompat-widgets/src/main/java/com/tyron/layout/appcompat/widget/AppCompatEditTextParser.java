package com.tyron.layout.appcompat.widget;

import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.tyron.layout.appcompat.view.ProteusAppCompatEditText;

public class AppCompatEditTextParser extends ViewTypeParser<AppCompatEditText> {
  @NonNull
  @Override
  public String getType() {
    return AppCompatEditText.class.getName();
  }

  @Nullable
  @Override
  public String getParentType() {
    return EditText.class.getName();
  }

  @NonNull
  @Override
  public ProteusView createView(
      @NonNull ProteusContext context,
      @NonNull Layout layout,
      @NonNull ObjectValue data,
      @Nullable ViewGroup parent,
      int dataIndex) {
    return new ProteusAppCompatEditText(context);
  }

  @Override
  protected void addAttributeProcessors() {}
}
