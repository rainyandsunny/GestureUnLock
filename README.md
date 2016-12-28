# GestureUnLock
Android手势解锁
早上看个bug，忘了吃饭，最后定位到ROM厂商，反馈了。现在戴上耳机，写个清爽点的文章。虽然很简单，刷刷存在感也好啊，毕竟好久没写文章了。
先直接上效果吧。

![京东金融手势解锁](http://og1qqf1es.bkt.clouddn.com/device-2016-12-27-180010.gif)

如何使用呢？

（1）对象的获取并设置正确的密码

	mUnLockView = (UnLockView) findViewById(R.id.unlockview);
        mUnLockView.setmRightPsw("14789");

（2）然后在当前Activity或者Fragment中实现 UnLockView.ResponseInput接口。例子如下：

 	@Override
    public void inputOK() {
		//TODO
        Toast.makeText(this, "密码正确", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void inputErr() {
		//TODO
        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
    }



自定义view(viewgroup)的步骤就是下面这个样子，很官方呢。

![图片来自网络](http://og1qqf1es.bkt.clouddn.com/%E8%87%AA%E5%AE%9A%E4%B9%89view.jpg)
我们的主要工作在onMeasure()和onDraw()中。在onMeasure()中负责测量view的大小，在onDraw()中负责view的绘制。

观察效果为9个圆，在未点击时为灰色，在点击或者划过的时候将背景变为蓝色并画连线，在该圆外围画一个蓝色线条的大圆。确实很简单，但是在实际的过程中遇到了几个问题，分享一下。

#### 1.对象的存储

	static class Circle{

        private int x;//x坐标
        private int y;//y坐标
        private int innderRadius; //小圆半径
        private int outterRadius; //大圆半径
        private boolean isClicked; //是否点击
    }

当某个圆被划过或者被点击的时候，将isClicked置为true。

#### 2.线条的绘制

我用path存储用户手势的路径，从点击屏幕到手指抬起为止。在画圆与圆之间的线条时又有所不同，涉及到一个小知识点与大家分享下。那就是Path的lineTo与setLastPoint方法的区别。先看下使用lineTo的效果。

![lineTo](http://og1qqf1es.bkt.clouddn.com/lineTo.png)

因为onTouchEvent是个回调方法，会不停被系统回调，所以如果用lineTo这个方法的话，因为坐标不停地变会画出曲线来，这个时候我们就需要用另外一个方法setLastPoint,这个会改变上一次绘制的点的位置，所以会画出一条直线来。

#### 3.如何判断输入是否正确

用一个StringBuilder对象保存用户的输入数据，当用户手指抬起来时对比输入的内容与正确的密码，并告知用户。那么如何采集用户的输入呢？我们在点击圆或者划过某个圆的时候将圆的下标采集。


#### 4.将密码的判断结果反馈给用户
在UnLockView中有个接口ResponseInput，只需要在当前的Activity或者Fragment中实现该接口即可。

	public interface ResponseInput{

        public void inputOK();
        public void inputErr();
    }

看下核心的代码吧，发现要将一个东西说明白真的挺难的，读书人的事就不要多说了，大家直接看代码吧。
	
**判断点击或者划过的是哪个圆**

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


这个里面最重要的就是事件的处理了，我们简单看看事件处理的代码吧。

	public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch(action){

            case MotionEvent.ACTION_DOWN:{
               
                int index = getClickedIndex(event.getX(),event.getY());
                if(index >= 0 && index <= circles.length){
                    
					//采集用户输入
					gatherInput(index);
                    mPath.moveTo(circles[index].x,circles[index].y);
                    return true;
                }else{
                    //TODO 第一次没触到任何块则提示

                    return false;
                }

            }
            case MotionEvent.ACTION_MOVE:{
               
                float x = event.getX();
                float y = event.getY();
                int index = getClickedIndex(x,y);
                if(index >= 0 && index < circles.length){
                    circles[index].isClicked = true;
					//采集用户输入
                    gatherInput(index);

					//这个地方是为了解决第一次点击时画点击点与点（0,0）的bug
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
                
            }break;
            case MotionEvent.ACTION_UP:{
                
                //TODO 判断密码是否正确
                if(isInputOK()){

                    object.inputOK();
                    
                }else{
                    object.inputErr();
                }
                uninit();

            }break;
        }
        return super.onTouchEvent(event);
    }

从代码可以看出来，在MotionEvent.ACTION_DOWN中，我们分别进行了处理，那是因为如果return true的话，之后的MotionEvent.ACTION_UP
与MotionEvent.ACTION_MOVE事件才会被捕获，如果返回false则不会被捕获。

还有其他一些模块简单介绍下，属于不重要的部分。

#####1. 采集用户的输入

gatherInput()

#####2. 判断输入是否正确

isInputOK()

#####3. circles初始化
init()

#####4. 画笔的初始化与设置
initResources(Context context)

#####3. circles反初始化（在MotionEvent.ACTION_UP时调用将circles的isClicked置为false，path清空，input数据清空）
uninit()。

代码在[git@github.com:rainyandsunny/GestureUnLock.git](git@github.com:rainyandsunny/GestureUnLock.git)，欢迎star与下载。

