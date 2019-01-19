package com.uroad.dubai.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.mapbox.services.android.navigation.ui.v5.NavigationView;

public class MyNavigationView extends NavigationView {
    public MyNavigationView(Context context) {
        super(context);
    }

    public MyNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
