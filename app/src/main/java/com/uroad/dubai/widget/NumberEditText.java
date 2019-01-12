package com.uroad.dubai.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uroad.dubai.R;

/**
 *
 * 方格数字输入控件
 *
 * @create by thinkin
 * e7yoo.com
 * @email thinkin.liu@gmail.com
 */
public class NumberEditText extends LinearLayout {

    /** 输入框 */
    private TextView[] inputTv = new TextView[6];
    /** 用于接受键盘输入内容 */
    private EditText invisibleEt;
    /** 输入结束监听 */
    private OnInputFinishListener onInputFinishListener;

    public NumberEditText(Context context) {
        super(context);
        createView(context);
    }

    public NumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    public NumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        createView(context);
    }

    private void createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.e7yoo_number_edittext, null);
        inputTv[0] = (TextView) view.findViewById(R.id.e7yoo_number_edittext_tv0);
        inputTv[1] = (TextView) view.findViewById(R.id.e7yoo_number_edittext_tv1);
        inputTv[2] = (TextView) view.findViewById(R.id.e7yoo_number_edittext_tv2);
        inputTv[3] = (TextView) view.findViewById(R.id.e7yoo_number_edittext_tv3);
        inputTv[4] = (TextView) view.findViewById(R.id.e7yoo_number_edittext_tv4);
        inputTv[5] = (TextView) view.findViewById(R.id.e7yoo_number_edittext_tv5);
        invisibleEt = (EditText) view.findViewById(R.id.e7yoo_number_edittext_et);
        invisibleEt.addTextChangedListener(new InvisibleEtTextWatcher());
        addView(view);
    }

    public void setOnInputFinish(OnInputFinishListener listener) {
        this.onInputFinishListener = listener;
    }

    public void clearText() {
        int tvLength = inputTv.length;
        for (int i = 0; i < tvLength; i++) {
            inputTv[i].setText("");
        }
        invisibleEt.setText("");
    }

    /**
     * 处理从edittext到textview
     */
    private class InvisibleEtTextWatcher implements  TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString().trim();
            char[] chars = text.toCharArray();
            int length = chars.length;
            int tvLength = inputTv.length;
            for(int i = 0; i < tvLength; i++) {
                if(i < length) {
                    inputTv[i].setText("" + chars[i]);
                } else {
                    inputTv[i].setText("");
                }
            }
            if(length == tvLength && onInputFinishListener != null) {
                onInputFinishListener.onInputFinish(text);
            }
        }
    }

    public interface OnInputFinishListener {
        void onInputFinish(String text);
    }

}
