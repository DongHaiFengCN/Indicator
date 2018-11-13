package com.example.ydd.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Indicator extends LinearLayout {


    /**
     * 默认设置三角形的底边长度是导航的1/6高
     */
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6f;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 用来构成三角形
     */
    private Path mPath;

    /**
     * 三角形的宽
     */
    private int mTriangleWidth;

    /**
     * 三角形的长
     */
    private int mTriangleHeight;


    /**
     * 初始化的时候三角形的位置
     */
    private int mInitTranslationX;

    /**
     * 移动的时候三角形的位置
     */
    private int mTranslationX;


    /**
     *
     */
    private int visibleTableCount;

    private static final int DEFAULT_COUNT = 3;

    private int tableWidth;

    public Indicator(Context context) {
        this(context, null);

    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Indicator);

        visibleTableCount = typedArray.getInt(R.styleable.Indicator_visible_table_count, DEFAULT_COUNT);


        if (visibleTableCount < 0) {

            visibleTableCount = DEFAULT_COUNT;
        }

        typedArray.recycle();


        mPaint = new Paint();

        mPaint.setAntiAlias(true);

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.parseColor("#ffffff"));

        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    /**
     * 测量控件的高度设置三角形的属性
     */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        tableWidth = w / visibleTableCount;

        mTriangleWidth = (int) ((w / visibleTableCount) * RADIO_TRIANGLE_WIDTH);

        //三角形的左起点在每个table中间位置左便宜底边长度一半的位置
        mInitTranslationX = w / visibleTableCount / 2 - mTriangleWidth / 2;

        initTriangle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int cCount = getChildCount();

        if (cCount == 0) {
            return;
        }

        View view;

        for (int i = 0; i < cCount; i++) {

            view = getChildAt(i);

            LinearLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();

            params.weight = 0;

            params.width = getScreenWidth() / visibleTableCount;

            view.setLayoutParams(params);

        }

    }

    private int getScreenWidth() {

        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();


        //mInitTranslationX 初始化的位置，mTranslationX 默认为0，在Y轴getHeight方向绘制
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());


        //平移到指定位置就可以绘制了
        canvas.drawPath(mPath, mPaint);

        canvas.restore();

        super.dispatchDraw(canvas);

    }

    /**
     * 绘制三角形的形状
     */

    private void initTriangle() {

        //先设置角度为45度
        mTriangleHeight = mTriangleWidth / 2;

        mPath = new Path();

        mPath.moveTo(0, 0);

        mPath.lineTo(mTriangleWidth, 0);

        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);

        mPath.close();
    }

    /*
     * 第几个table 然后偏移了多少
     * @param position 第几个table下
     * @param offset 偏移比例（0 - 1）
     */
    public void scrolled(int position, float offset) {

        int width = getWidth() / visibleTableCount;

        mTranslationX = (int) (tableWidth * (position + offset));

        if (position >= visibleTableCount - 2 && offset > 0 && getChildCount() > visibleTableCount) {

            if (visibleTableCount != 1) {

                int sum = position - visibleTableCount + 2;

                this.scrollTo((int) (sum * width + width * offset), 0);
            } else {

                this.scrollTo((int) ((position + offset) * width), 0);
            }


        }

        invalidate();

    }
}
