package com.example.rainy.gestureunlock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.rainy.gestureunlock.ViewUitl;

/**
 * 手势解锁view
 * Created by Rainy on 2016/12/24.
 */

public class UnLockView extends View {

    private static final String TAG = "UnLockView";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static int SMALL_RADIUS = 60;
    private static int BIGGER_RADIUS = 120;
    private static final String GRAYCOLOR = "#ffd5dbe8";
    private static final String BLUECOLOR = "#ff508cee";
    private static final float LINE_WIDTH = 10.0f;
    private static final float LINE_NORMAL = 1.0f;
    private int width;
    private int height;
    public Circle[] circles = new Circle[9];
    private Paint mGrayPaint;
    private Path mPath = new Path();

    public UnLockView(Context context) {
        super(context);
        initResources();
    }
    public UnLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResources();
    }
    public UnLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResources();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i=0;i<circles.length;i++){
            Circle circle = circles[i];
            if(circle.isClicked){
                mGrayPaint.setStyle(Paint.Style.STROKE);
                mGrayPaint.setColor(Color.parseColor(BLUECOLOR));
                mGrayPaint.setStrokeWidth(LINE_NORMAL);
                //画大圆的轮廓
                canvas.drawCircle(circle.x,circle.y,circle.outterRadius,mGrayPaint);
            }else{
                mGrayPaint.setColor(Color.parseColor(GRAYCOLOR));
            }
            //画小圆
            mGrayPaint.setStyle(Paint.Style.FILL);
            mGrayPaint.setStrokeWidth(LINE_NORMAL);
            canvas.drawCircle(circle.x,circle.y,circle.innderRadius,mGrayPaint);

            //画连线
            mGrayPaint.setStyle(Paint.Style.STROKE);
            mGrayPaint.setStrokeWidth(LINE_WIDTH);
            mGrayPaint.setColor(Color.parseColor(BLUECOLOR));
            canvas.drawPath(mPath,mGrayPaint);
        }
        canvas.save();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = ViewUitl.getSize(widthMeasureSpec,WIDTH);
        height = ViewUitl.getSize(widthMeasureSpec,HEIGHT);
        init();
        setMeasuredDimension(width,height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch(action){

            case MotionEvent.ACTION_DOWN:{
                Log.d(TAG,"ACTION_DOWN");
                Log.d(TAG+"onTouchEvent","getX:"+event.getX()+",getRawX="
                        +event.getRawX()+",getY="+event.getY()
                        +",getRawY="+event.getRawY());
                return true;
            }
            case MotionEvent.ACTION_MOVE:{
                Log.d(TAG,"ACTION_MOVE");
                float x = event.getX();
                float y = event.getY();
                int index = getClickedIndex(x,y);
                if(index >= 0 && index < circles.length){
                    circles[index].isClicked = true;
                    if(mPath.isEmpty()){
                        mPath.moveTo(circles[index].x,circles[index].y);
                    }else{
                        mPath.lineTo(circles[index].x,circles[index].y);
                    }
                }else{
                    mPath.lineTo(x,y);
                }
                invalidate();
                Log.d(TAG+"index","index:"+index);
            }break;
            case MotionEvent.ACTION_UP:{
                Log.d(TAG,"ACTION_UP");
                mPath.reset();
            }break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG,"dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }

    public void init(){

        int contentWidth = width - 2*BIGGER_RADIUS;
        int contentHeight = height - 2*BIGGER_RADIUS;
        for(int i = 0;i<circles.length;i++){

            circles[i] = new Circle();
          /*  if(0 == i%3){
                circles[i].x = BIGGER_RADIUS;
            }else{
                circles[i].x = (i%3)*contentWidth/2;
            }
            if(0 == i/3) {
                circles[i].y = BIGGER_RADIUS;
            }else{
                circles[i].y = (i/3)*contentHeight/2;
            }*/
            circles[i].x = BIGGER_RADIUS + (i%3)*contentWidth/2;
            circles[i].y = BIGGER_RADIUS + (i/3)*contentHeight/2;
            circles[i].isClicked = false;
            circles[i].innderRadius = SMALL_RADIUS;
            circles[i].outterRadius = BIGGER_RADIUS;
        }
    }
    public void initResources(){

        mGrayPaint = new Paint();
        mGrayPaint.setAntiAlias(true);
        mGrayPaint.setColor(Color.parseColor(GRAYCOLOR));
    }

    public int getClickedIndex(float x,float y){

        for(int i=0;i<circles.length;i++){
            Circle cirlce = circles[i];
            if( x >= cirlce.x - cirlce.outterRadius
                    && x <= cirlce.x + cirlce.outterRadius
                    && y<= cirlce.y + cirlce.outterRadius
                    && y >= cirlce.y - cirlce.outterRadius){
                return i;
            }
        }
        return -1;
    }

    static class Circle{

        private int x;
        private int y;
        private int innderRadius;
        private int outterRadius;
        private boolean isClicked;
    }
}
