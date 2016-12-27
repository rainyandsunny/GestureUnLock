package com.example.rainy.gestureunlock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.rainy.gestureunlock.ViewUitl;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

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
    private Paint mPaint;
    private float mNextX,mNextY;
    private Path mPath = new Path();
    private String mRightPsw;
    private StringBuilder mInputPsw;
    private Context mContext;



    public UnLockView(Context context) {
        super(context);
        initResources(context);
    }
    public UnLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResources(context);
    }
    public UnLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResources(context);
    }

    public String getmRightPsw() {
        return mRightPsw;
    }

    public void setmRightPsw(String mRightPsw) {
        this.mRightPsw = mRightPsw;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i=0;i<circles.length;i++){
            Circle circle = circles[i];
            if(circle.isClicked){
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.parseColor(BLUECOLOR));
                mPaint.setStrokeWidth(LINE_NORMAL);
                //画大圆的轮廓
                canvas.drawCircle(circle.x,circle.y,circle.outterRadius,mPaint);
            }else{
                mPaint.setColor(Color.parseColor(GRAYCOLOR));
            }
            //画小圆
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(LINE_NORMAL);
            canvas.drawCircle(circle.x,circle.y,circle.innderRadius,mPaint);

        }
        //画连线
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(LINE_WIDTH);
        mPaint.setColor(Color.parseColor(BLUECOLOR));
        canvas.drawPath(mPath,mPaint);
        //画连线
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
                int index = getClickedIndex(event.getX(),event.getY());
                if(index >= 0 && index <= circles.length){
                    gatherInput(index);
                    mPath.moveTo(circles[index].x,circles[index].y);
                    return true;
                }else{
                    //TODO 第一次没触到任何块则提示

                    return false;
                }

            }
            case MotionEvent.ACTION_MOVE:{
                Log.d(TAG,"ACTION_MOVE");
                float x = event.getX();
                float y = event.getY();
                int index = getClickedIndex(x,y);
                if(index >= 0 && index < circles.length){
                    circles[index].isClicked = true;
                    gatherInput(index);
                    if(getClickedIndex(mNextX,mNextY) >= 0){
                        mPath.lineTo(circles[index].x,circles[index].y);
                    }else{
                        mPath.setLastPoint(circles[index].x,circles[index].y);
                    }
                    mNextX = circles[index].x;
                    mNextY = circles[index].y;
                }else{
                    mNextX = x;
                    mNextY = y;
                    mPath.setLastPoint(mNextX,mNextY);

                }

                invalidate();
                Log.d(TAG+"index","index:"+index);
            }break;
            case MotionEvent.ACTION_UP:{
                Log.d(TAG,"ACTION_UP");
                //TODO 判断密码是否正确
                if(isInputOK()){
                    Toast.makeText(mContext, "密码正确:"+mInputPsw, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "密码错误:"+mInputPsw, Toast.LENGTH_SHORT).show();
                }
                uninit();

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
            circles[i].x = BIGGER_RADIUS + (i%3)*contentWidth/2;
            circles[i].y = BIGGER_RADIUS + (i/3)*contentHeight/2;
            circles[i].isClicked = false;
            circles[i].innderRadius = SMALL_RADIUS;
            circles[i].outterRadius = BIGGER_RADIUS;
        }
    }
    public void initResources(Context context){

        mContext = context;
        mInputPsw = new StringBuilder();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor(GRAYCOLOR));
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

    public void uninit(){

        if(null != circles){
            for(int i=0;i<circles.length;i++){
                circles[i].isClicked = false;
            }
            invalidate();
        }
        if(null != mPath){
            mPath.reset();
        }
        mInputPsw.delete(0,mInputPsw.length());

    }


    /**
     * //得到用户输入的内容
     * @param index 划过点的下标
     */
    public void gatherInput(int index){

        int content = index + 1;
        if(mInputPsw.length() == 0){
            mInputPsw.append(content);
        }else{

            char lastCharacter = mInputPsw.charAt(mInputPsw.length()-1);
            int iLastValue = Character.getNumericValue(lastCharacter);
            if(iLastValue != content){
                mInputPsw.append(content);
            }
        }

    }

    /**
     * 验证输入的密码是否正确
     * @return 正确返回true
     */
    public boolean isInputOK(){

        return mInputPsw == null || mRightPsw == null
                ? false : mRightPsw.equals(mInputPsw.toString());
    }

    static class Circle{

        private int x;
        private int y;
        private int innderRadius;
        private int outterRadius;
        private boolean isClicked;
    }
}
