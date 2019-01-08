package com.uroad.dubai.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

import static android.text.InputType.TYPE_CLASS_NUMBER;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class VerifyCodeEditText  extends AppCompatEditText {

    private static final int DEFAULT_CODE_NUMBER = 6;
    private static final int DEFAULT_CODE_COLOR = Color.parseColor("#000000");
    private static final int DEFAULT_RECT_COLOR_SELECTED = Color.parseColor("#66AFFF");
    private static final int DEFAULT_RECT_COLOR_UNSELECTED = Color.parseColor("#606060");
    private static final float DEFAULT_LETTER_SPACE = 2f;
    private static final int DEFAULT_TEXT_SIZE = 60;
    private static final int DEFAULT_RECT_WIDTH = 5;
    private static final float DEFAULT_RECT_SIZE = 1.7f;
    private static final int DEFAULT_RECT_CORNER = 5;
    private static final boolean PASSWORD_MODE = false;
    private static final boolean DIDITAL_MODE = true;

    private int codeNumber = DEFAULT_CODE_NUMBER;
    private int codeColor = DEFAULT_CODE_COLOR;
    private int textSize = DEFAULT_TEXT_SIZE;
    private int selectedColor = DEFAULT_RECT_COLOR_SELECTED;
    private int unselectedColor = DEFAULT_RECT_COLOR_UNSELECTED;
    private float letterSpace = DEFAULT_LETTER_SPACE;
    private int rectStrokeWidth = DEFAULT_RECT_WIDTH;
    private int rectSize = (int) (DEFAULT_RECT_SIZE * textSize);
    private int rectCorner = DEFAULT_RECT_CORNER;
    private boolean isPassword = PASSWORD_MODE;
    private boolean isDigital = DIDITAL_MODE;
    private int width = 0;

    private String codeString = "";
    private float textTotalWidth;
    private int halfRect;

    private Paint codePaint;
    private Paint rectPaint;
    private InputFilter[] inputFilters;

    public VerifyCodeEditText(Context context) {
        super(context);
        init();
    }

    public VerifyCodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerifyCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        codeString = getText().toString().trim();
    }


    @SuppressLint("NewApi")
    private void init () {
        codePaint = new Paint();
        codePaint.setStyle(Paint.Style.FILL);
        codePaint.setTextSize(textSize);
        codePaint.setColor(codeColor);

        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(rectStrokeWidth);

        InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(codeNumber);
        inputFilters = new InputFilter[1];
        inputFilters[0] = lengthFilter;
        setFilters(inputFilters);
        if (isDigital) {
            setInputType(TYPE_CLASS_NUMBER);
        }

        setCursorDrawableColor(this);

        setSingleLine();

        setLetterSpacing(letterSpace);

        halfRect = rectSize / 2;
        textTotalWidth = (letterSpace + 1) * textSize;

        setTextSize(15);

        setBackground(null);
    }

    public void setCodeNumber(int codeNumber) {
        if (codeNumber < 1 || codeNumber > 8) {
            return;
        }
        this.codeNumber = codeNumber;
        InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(codeNumber);
        inputFilters = new InputFilter[1];
        inputFilters[0] = lengthFilter;
        setFilters(inputFilters);
    }

    @Override
    @SuppressWarnings("all")
    public boolean onTouchEvent(MotionEvent event) {
        return !(event.getX() < 0.3 * textTotalWidth) && super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measureWidth(widthMeasureSpec);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = (int) codePaint.measureText(codeString) + getPaddingLeft()
                    + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDraw(Canvas canvas) {
        float realTextWidth = codePaint.measureText("0");
        float realTextHeight = codePaint.measureText("0");

        float x = textTotalWidth - realTextWidth - 32;

        int lineMargin = 0;

        for (int i = 0; i < codeNumber; i++) {
            int index = getSelectionStart() - 1;
            if (i == index) {
                rectPaint.setColor(selectedColor);
            } else {
                rectPaint.setColor(unselectedColor);
            }

            if (lineMargin == 0){
                lineMargin = (int) (width/codeNumber - i * x + 2.3f * realTextWidth - halfRect-i * x + 2.3f * realTextWidth + halfRect)/2;
            }

            //圆角矩形
            //canvas.drawRoundRect(i * x + 2.3f * realTextWidth - halfRect , (getBottom() - getTop()) / 2 - halfRect, i * x + 2.3f * realTextWidth + halfRect, (getBottom() - getTop()) / 2 + halfRect, rectCorner, rectCorner, rectPaint);
            //canvas.drawRoundRect(i * x + 2.3f * realTextWidth - halfRect+lineMargin , (getBottom() - getTop()) / 2 - halfRect, i * x + 2.3f * realTextWidth + halfRect+lineMargin, (getBottom() - getTop()) / 2 + halfRect, rectCorner, rectCorner, rectPaint);
            //下划线
            canvas.drawLine(i * x + 2.3f * realTextWidth - halfRect+lineMargin,(getBottom() - getTop()) / 2 + halfRect,i * x + 2.3f * realTextWidth + halfRect + +lineMargin,(getBottom() - getTop()) / 2 + halfRect,rectPaint);
        }

        String cur;
        int k;
        for (int i = 0; i < codeNumber; i++) {
            if (i == codeString.length()) {
                return;
            }
            if (isPassword) {
                cur = "*";
                k = -5;
            } else {
                cur = String.valueOf(codeString.charAt(i));
                k = -10;
            }
            canvas.drawText(cur, i * x + textSize+lineMargin, (getBottom() - getTop()) / 2 + realTextHeight + k, codePaint);
        }
    }

    public String getVerifyCodeText(){
       if (codeString.length() > 6)
            return codeString.substring(0,6);
        return codeString;
    }

    private void setCursorDrawableColor(EditText editText) {
        try {
            Field field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(editText);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setSize(0, 0);
            Drawable[] drawables = {drawable, drawable};
            field = findEditorCursorDrawable(editor.getClass());
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private Field findEditorCursorDrawable(Class editor) throws Exception {
        if (editor == null) {
            return null;
        }
        if (TextUtils.equals("android.widget.Editor", editor.getCanonicalName())) {
            return editor.getDeclaredField("mCursorDrawable");
        }
        return findEditorCursorDrawable(editor.getSuperclass());
    }
}
