package com.uroad.dubai.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class ColorView extends View {

    private Paint paint;
    private int size = 12;
    private float allWeight;
    private List<String> colorList;//颜色
    private List<Float> weightList;
    private int width;

    public ColorView(Context context) {
        this(context,null);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) paint.measureText("") + getPaddingLeft() + getPaddingRight();
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }

    private int measureHeight(int defaultHeight, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (-paint.ascent() + paint.descent()) + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
//        1.基准点是baseline
//        2.ascent：是baseline之上至字符最高处的距离
//        3.descent：是baseline之下至字符最低处的距离
//        4.leading：是上一行字符的descent到下一行的ascent之间的距离,也就是相邻行间的空白距离
//        5.top：是指的是最高字符到baseline的值,即ascent的最大值
//        6.bottom：是指最低字符到baseline的值,即descent的最大值

                break;
        }
        return defaultHeight;
    }

    float last = 0f;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        last = 0f;
        float proportion = width / allWeight;//比例
        if (colorList != null){
            for (int i = 0; i < colorList.size(); i++) {
                String color = colorList.get(i);
                Float weight = weightList.get(i);
                paint.setColor(Color.parseColor(color));
                paint.setStrokeWidth(20f);
                canvas.drawLine(proportion*last,0,
                        proportion*(weight+last),0,paint);
                canvas.save();
                paint.reset();
                canvas.restore();
                last = weight;
            }
        }
    }

    public void setColorsList(List<String> list){
        colorList = list;
        last = 0;
    }

    public void setColorWeightList(List<Float> list){
         float size = 0;
        for (Float aFloat : list) {
            size += aFloat;
        }
        allWeight = size;
        weightList = list;
    }
}
