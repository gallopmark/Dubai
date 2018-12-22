package com.uroad.glidev4.listener;

import android.graphics.drawable.Drawable;

/**
 * 设置此皆苦是为了业务需要，一般不需要关心网络请求回来的drawable，但是业务需要切换的地方的话，需要拿到网络请求回来的drawable
 */

public interface IGetDrawableListener {
    void onDrawable(Drawable drawable);
}
