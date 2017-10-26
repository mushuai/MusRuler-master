package com.mus.ruler.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by 穆帅 on 2017/10/26.
 */

public class CustomView extends FrameLayout implements MusRulerView.ScaleCallBack{

    Context context;

    // 中间指针画笔
    private Paint mCenterPaint;
    //中间指标粗细
    private int mCenterCurrentWidth = 6;
    //中间质变长度
    private int mCenterHeight = 100;
    //起始位置
    private int mStartX;

    MusRulerView musRulerView;
    private MusRulerView.ScaleCallBack mScaleCallBack;

    public CustomView(Context context) {
        super(context);
        initView(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private static final int INDICATOR_COLOR = Color.rgb(77, 166, 104);

    private void initView(Context context) {
        this.context = context;

        musRulerView = new MusRulerView(context);
        musRulerView.setmScaleCallBack(this);
        addView(musRulerView);

        mStartX = MusRulerView.getWindowWidth(context) / 2;

        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setColor(INDICATOR_COLOR);
        mCenterPaint.setStrokeWidth(mCenterCurrentWidth);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawLine(mStartX - mCenterCurrentWidth / 2, 0, mStartX - mCenterCurrentWidth / 2, mCenterHeight, mCenterPaint);
    }


    @Override
    public void scaleResult(String string) {
        mScaleCallBack.scaleResult(string);
    }

    public void setmScaleCallBack(MusRulerView.ScaleCallBack mScaleCallBack) {
        this.mScaleCallBack = mScaleCallBack;
    }

}
