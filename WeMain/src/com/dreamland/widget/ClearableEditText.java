package com.dreamland.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.dreamland.R;
import com.dreamland.util.DisplayUtil;

/**
 * 带清除按钮的EditText控件
 */
public class ClearableEditText extends EditText {
    /**
     * 清除按钮
     */
    Drawable mClearBtnDrawable;

    /**
     * 清除按钮宽度
     */
    private int mWidth;

    /**
     * 清除按钮高度
     */
    private int mHeight;

    /**
     * 清除内容后的回调
     */
    OnTextClearedListener mListener;

    public ClearableEditText(Context context) {
        this(context, null, android.R.attr.editTextStyle);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 主要用来加载清除按钮的资源
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.clearableEditTextStyle);
        mClearBtnDrawable = array.getDrawable(R.styleable.clearableEditTextStyle_clearBtnDrawable);
        mWidth = array.getDimensionPixelSize(R.styleable
                .clearableEditTextStyle_clearBtnDrawableWidth, -1);
        mHeight = array.getDimensionPixelSize(R.styleable
                .clearableEditTextStyle_clearBtnDrawableHeight, -1);

        if (mClearBtnDrawable == null) {
            mClearBtnDrawable = getResources().getDrawable(R.drawable.cross);
        }

        if (mClearBtnDrawable != null) {
            // 宽或高未设置，则使用默认的，19px
            if (mWidth == -1 || mHeight == -1) {
                mWidth = (int) (DisplayUtil.dp2px(19, getResources()));
                mHeight = (int) (DisplayUtil.dp2px(19, getResources()));
            }

            mClearBtnDrawable.setBounds(0, 0, mWidth, mHeight);

            setClearBtnVisible(false);
        }

        /*
        处理X点击事件
         */
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ClearableEditText editText = ClearableEditText.this;
                if (editText.getCompoundDrawables()[2] == null) return false;

                if (motionEvent.getAction() != MotionEvent.ACTION_UP) return false;

                boolean isXTapped = motionEvent.getX() > (getWidth()) - getPaddingRight() -
                        mClearBtnDrawable.getIntrinsicWidth();

                if (isXTapped) {
                    setText("");
                    setClearBtnVisible(false);

                    if (mListener != null) {
                        mListener.afterTextCleared();
                    }
                }
                return false;
            }
        });

        /*
        监听内容变化
         */
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ClearableEditText.this.getText().toString();
                if (TextUtils.isEmpty(text))
                    setClearBtnVisible(false);
                else
                    setClearBtnVisible(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        array.recycle();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (!focused) {
            setCursorVisible(false);
            setClearBtnVisible(false);
        } else {
            setCursorVisible(true);
            String text = getText().toString();
            if (!TextUtils.isEmpty(text) && text.length() > 0) {
                setClearBtnVisible(true);
            }
        }
    }

    public void setTextClearedListener(OnTextClearedListener listener) {
        this.mListener = listener;
    }

    public void setClearBtnVisible(boolean isVisible) {
        Drawable drawable = isVisible ? mClearBtnDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], drawable,
                getCompoundDrawables()[3]);
    }

    public interface OnTextClearedListener {
        /**
         * 内容清空后的回调
         */
        public void afterTextCleared();
    }
}
