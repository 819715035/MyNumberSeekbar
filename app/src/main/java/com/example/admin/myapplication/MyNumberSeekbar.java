package com.example.admin.myapplication;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author lanjian
 * @email 819715035@qq.com
 * creat at $date$
 * description
 */
public class MyNumberSeekbar extends View {

    private Paint mPaint;
    private Paint mTextPaint;
    private int width;
    private int height;
    private Path mPath;
    private int progress = 0;
    private int maxProgress = 100;
    private int startX;
    private int offestX;//偏移量
    private RectF roundRect;
    private Rect textRect;
    private onSeekbarProgressChangeListener listener;
    public MyNumberSeekbar(Context context) {
        this(context,null);
    }

    public MyNumberSeekbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyNumberSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(2);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setTextSize(20);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mPath = new Path();
        roundRect = new RectF();
        textRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        startX = (int) (progress*1.0/maxProgress*width);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0,height);
        if (startX<=roundRect.width()-20 || startX<=0){
            offestX = startX-25;
        }else if (startX+roundRect.width()-20>=width){
            offestX = (int) (-width+startX-25+roundRect.width());
        }else{
            offestX = 0;
        }
        //绘制下三角标记
        mPath.reset();
        mPath.moveTo(startX-10,-40);
        mPath.lineTo(startX,-30);
        mPath.lineTo(startX+10,-40);
        mPath.close();
        canvas.drawPath(mPath,mPaint);
        mTextPaint.getTextBounds(progress+"%",0,(progress+"%").length()-1, textRect);
        float textHeight = textRect.height();
        //绘制圆角矩形标记
        roundRect.set(startX-25-offestX,-textHeight-60,startX+25-offestX,-40);
        canvas.drawRoundRect(roundRect,10,10,mPaint);
        //绘制文字
        canvas.drawText(progress+"%",roundRect.centerX(),roundRect.centerY()+textHeight/2,mTextPaint);
        //画进度条
        canvas.drawRoundRect(0,-20,width,-10,15,15,mPaint);
        //画圆点
        canvas.drawCircle(startX,-15,10,mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startX = (int) event.getX();
        //放置圆点移出到进度条外面
        if (startX>=width){
            startX =width;
        }else if (startX<=0){
            startX = 0;
        }
        progress = startX*100/width;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (listener!=null){
                    listener.onStartTouch(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (listener!=null){
                    listener.onProgressChanged(this,progress);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (listener!=null){
                    listener.onStopTouch(this);
                }
                break;
        }
        invalidate();
        return true;
    }
    //设置当前进度
    public void setProgress(int progress) {
        this.progress = progress;
        if (progress>=maxProgress){
            this.progress = maxProgress;
        }
        invalidate();
    }

    //设置最大进度
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }
    public void setListener(onSeekbarProgressChangeListener listener) {
        this.listener = listener;
    }

    interface onSeekbarProgressChangeListener{
        //进度变化
        void onProgressChanged(MyNumberSeekbar seekbar,int progress);
        //开始触摸
        void onStartTouch(MyNumberSeekbar seekbar);
        //停止触摸
        void onStopTouch(MyNumberSeekbar seekbar);
    }
}
