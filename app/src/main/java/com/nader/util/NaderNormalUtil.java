package com.nader.util;

import android.content.Context;
import android.widget.Toast;

import io.github.rosemoe.sora.text.Content;

public class NaderNormalUtil {
    Context context;

    public NaderNormalUtil(Context c){
        this.context =c;
    }
    public void showToast(String s){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
