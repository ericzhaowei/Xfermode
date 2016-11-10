package com.ider.ttt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ider-eric on 2016/11/9.
 */

public class XferView extends View {

    int radius = 70;
    PorterDuff.Mode mode;
    Paint mPaint;
    Paint mSidePaint;
    Paint textPaint;

    public XferView(Context context) {
        this(context, null);
    }

    public XferView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mode = PorterDuff.Mode.SRC_OVER;
        initPaint();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XferView);
            int xfermode = typedArray.getInteger(R.styleable.XferView_xfermode, 3);
            mode = int2Mode(xfermode);
        }

    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(20);
        mPaint.setColor(Color.BLUE);

        mSidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSidePaint.setStrokeWidth(10);
        mSidePaint.setColor(Color.BLACK);
        mSidePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStrokeWidth(10);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(40);
    }

    private int dp2px(int dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = 0;
        int height = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            // match_parent或者是确定的数值
            width = measureWidth;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // wrap_content内容的宽度,不能超出父控件指定的最大尺寸，即measureWidth;
            width = 3 * dp2px(radius) + getPaddingLeft() + getPaddingRight();
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            // 此值使用比较少，多用于父控件是AdapterView的情况
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else if(heightMode == MeasureSpec.AT_MOST) {
            height = 3 * dp2px(radius) + getPaddingTop() + getPaddingBottom();
        }


        setMeasuredDimension(width, height);

    }



    @Override
    protected void onDraw(Canvas canvas) {

        // 画圆
        int cx = getWidth() / 3;
        int cy = getHeight() / 3;
        int radius = getWidth() / 3 - 20;
        mPaint.setColor(getResources().getColor(R.color.colorBlue));
        Bitmap dstBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas dstCanvas = new Canvas(dstBitmap);
        dstCanvas.drawCircle(cx, cy, radius, mPaint);
        canvas.drawBitmap(dstBitmap, 0, 0, mPaint);


        // 画矩形
        mPaint.setColor(getResources().getColor(R.color.colorYellow));
        // 创建一个空白的bitmap
        Bitmap srcBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        // 将bimtap传入一个canvas中，可以理解为将bitmap附着在画布上
        Canvas srcCanvas = new Canvas(srcBitmap);
        // 在上面生成的画布上面绘制一个矩形，实际上是绘制在bitmap上面
        srcCanvas.drawRect(getWidth()/3, getHeight()/3, getWidth()-20, getHeight()-20, mPaint);

        // 将上面的bitmap绘制出来并使用SRC_IN重叠模式
        mPaint.setXfermode(new PorterDuffXfermode(mode));
        canvas.drawBitmap(srcBitmap, 0, 0, mPaint);

        // 还原Xfermode
        mPaint.setXfermode(null);


        Path side = new Path();
        side.addRect(0, 0, getWidth(), getHeight(), Path.Direction.CW);
        canvas.drawPath(side, mSidePaint);

        String text = mode.toString();
        int textWidth = (int) textPaint.measureText(text);
        int textHeight = (int) (textPaint.descent() - textPaint.ascent());
        canvas.drawText(text, (getWidth()-textWidth)/2, getHeight()-20, textPaint);
    }

    public PorterDuff.Mode int2Mode(int value) {

        try {
            Method intToMode = PorterDuff.class.getDeclaredMethod("intToMode", Integer.TYPE);
            PorterDuff.Mode mode = (PorterDuff.Mode) intToMode.invoke(null, value);
            return mode;

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }



}
