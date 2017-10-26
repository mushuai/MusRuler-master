package com.mus.ruler.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.OverScroller;

/**
 * Created by 穆帅 on 2017/10/25.
 */

public class MusRulerView extends View {

    Context context;

    //起始刻度
    private int mStartScale = 0;
    //当前刻度值
    private float mNowScale = 0;
    //最大刻度
    private int mEndScale = 100;
    //小刻度长度
    private int mSmallScaleHeight = 40;
    //大刻度长度
    private int mBigScaleHeight = 80;
    //小刻度粗细
    private int mSmallScaleWidth = 2;
    //大刻度粗细
    private int mBigScaleWidth = 4;
    //刻度的宽度
    private int mScaleWidth = 20;
    //数字文字大小
    private int mScaleTextSize = 36;
    // 数字距离大刻度距离
    private int mTextTopMargin = 80;
    //卷尺背景颜色
    private static final int BACKGROUP_COLOR = Color.rgb(244, 248, 243);
    private static final int POINT_COLOR = Color.rgb(210, 215, 209);
    private static final int TEXT_COLOR = Color.BLACK;

    // 文字画笔
    private Paint mTextPaint;
    // 小刻度画笔
    private Paint mSmallPaint;
    // 大刻度画笔
    private Paint mBigPaint;
    // 数字Rect
    private Rect mTextRect = new Rect();
    //起始位置
    private int mStartX;
    // 上次X
    private float mLastX;
    // 滑动控制器
    private OverScroller mOverScroller;
    //惯性最大最小速度
    private int mMaxVelocity, mMinVelocity;
    //速度获取
    private VelocityTracker mVelocityTracker;
    //尺子的最大长度
    private int mMaxScrollX;
    //最小滑动X
    private int mMinScrollX;
    // 平均刻度
    private int mScale;
    // 是否回滚
    private boolean isScrollBack = true;

    //刻度回调
    private ScaleCallBack mScaleCallBack;

    public MusRulerView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MusRulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MusRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(TEXT_COLOR);
        mTextPaint.setTextSize(mScaleTextSize);

        mSmallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallPaint.setColor(POINT_COLOR);
        mSmallPaint.setStrokeWidth(mSmallScaleWidth);

        mBigPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigPaint.setColor(POINT_COLOR);
        mBigPaint.setStrokeWidth(mBigScaleWidth);

        mStartX = getWindowWidth(context) / 2;
        mScale = mScaleWidth + mSmallScaleWidth;

        mOverScroller = new OverScroller(context);

        mVelocityTracker = VelocityTracker.obtain();
        mMaxVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        mMinScrollX = 0;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(BACKGROUP_COLOR);
        drawScales(canvas);
    }

    /**
     * 绘制大小刻度、数字
     */
    private void drawScales(Canvas canvas) {

        for (int i = 0; i <= (mEndScale - mStartScale); i++) {
            int x = mStartX - mBigScaleWidth + (mScaleWidth * 10 * i) + (mSmallScaleWidth * 10 * i);
            canvas.drawLine(x, 0, x, mBigScaleHeight, mBigPaint);
            String text = String.valueOf((float) ((mStartScale + i)));
            mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
            canvas.drawText(text, x - (mTextRect.width() / 2f), mBigScaleHeight + mTextTopMargin + mScaleTextSize, mTextPaint);

            if (i != (mEndScale - mStartScale)) {
                for (int j = 0; j < 10; j++) {
                    int mSmallX = x + (mScaleWidth * j) + (mSmallScaleWidth * j);
                    canvas.drawLine(mSmallX, 0, mSmallX, mSmallScaleHeight, mSmallPaint);
                }
            } else {
                mMaxScrollX = x - mStartX + 3;
            }
        }

    }

    /**
     * 获取屏幕宽
     */
    public static int getWindowWidth(Context ctx) {
        Display screenSize = ((WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = screenSize.getWidth();
        return width;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mLastX = currentX;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = mLastX - currentX;
                mLastX = currentX;
                scrollBy((int) (moveX), 0);
                break;
            case MotionEvent.ACTION_UP:
                isScrollBack = true;
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                if (Math.abs(velocityX) > mMinVelocity) {
                    fling(-velocityX);
                } else {
                    scrollBack();
                }
                mVelocityTracker.clear();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                break;
        }
        return true;
    }

    @Override
    public void scrollTo(int x, int y) {

        if (x != getScrollX()) {
            super.scrollTo(x, y);
        }

        mNowScale = (float) (x - mMinScrollX) / mScale;
        mNowScale = Math.round(mNowScale) * 0.1f;

        // 刻度回调
        if (mScaleCallBack != null) {
            mScaleCallBack.scaleResult(mNowScale + "");
        }
    }

    private void fling(int vX) {
        mOverScroller.fling(getScrollX(), 0, vX, 0, mMinScrollX, mMaxScrollX, 0, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
            //滑动结束后，回滚到最近位置
            if (!mOverScroller.computeScrollOffset()) {
                if (isScrollBack) {
                    scrollBack();
                }
            }
            invalidate();
        }
    }

    // 回滚
    private void scrollBack() {
        mOverScroller.startScroll(getScrollX(), 0, (int) ((mNowScale * 10 * mScale) - getScrollX()), 0, 1000);
        invalidate();
        isScrollBack = false;
    }

    public void setmScaleCallBack(ScaleCallBack mScaleCallBack) {
        this.mScaleCallBack = mScaleCallBack;
    }

    public interface ScaleCallBack {
        void scaleResult(String string);
    }

}
