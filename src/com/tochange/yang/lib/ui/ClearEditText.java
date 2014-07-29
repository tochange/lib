package com.tochange.yang.lib.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.devspark.appmsg.R;

public class ClearEditText extends EditText implements OnFocusChangeListener,
        TextWatcher
{
    private Drawable mClearDrawable;

    private boolean hasFoucs;

    public ClearEditText(Context context)
    {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null)
            mClearDrawable = getResources().getDrawable(
                    R.drawable.delete_selector);
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (getCompoundDrawables()[2] != null)
            {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable)
                    this.setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        setVisible(hasFocus);
    }

    public void setOnFocusChangeListenerCallBack(View v, boolean hasFocus)
    {
        setVisible(hasFocus);
    }

    private void setVisible(boolean hasFocus)
    {
        this.hasFoucs = hasFocus;
        if (hasFocus)
            setClearIconVisible(getText().length() > 0);
        else
            setClearIconVisible(false);
    }

    public void setClearIconVisible(boolean visible)
    {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    public void setHasFoucs(boolean hasFoucs)
    {
        this.hasFoucs = hasFoucs;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after)
    {
        if (hasFoucs)
            setClearIconVisible(s.length() > 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after)
    {
    }

    @Override
    public void afterTextChanged(Editable s)
    {
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation()
    {
        this.setAnimation(getShakeAnimation(5));
    }

    /**
     * 晃动动画
     * 
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation getShakeAnimation(int counts)
    {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}
