package com.gp2u.lite.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.renderscript.Sampler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;


public class CCView extends View {

    private float arcAngle;
    private float scale = 1.0f;

    Path crossPath = new Path();
    Paint mPaint = new Paint();
    Paint fillPaint = new Paint();

    Canvas canvas;
    float length;

    public CCView(Context context) {
        super(context);
        arcAngle = 0;

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.argb(255, 0, 66, 128));

        fillPaint = new Paint();
        fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
    }

    public CCView(Context context, AttributeSet attrs) {
        super(context, attrs);
        arcAngle = 0;

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.argb(255, 0, 66, 128));

        fillPaint = new Paint();
        fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.canvas = canvas;
        addCircle(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Point center = new Point(width / 2 + 225 * width / 800, height / 2);
        canvas.scale(scale ,scale ,center.x ,center.y);
        canvas.drawPath(crossPath ,fillPaint);
        canvas.drawPath(crossPath ,mPaint);
    }

    public float getArcAngle() {
        return arcAngle;
    }

    public void setArcAngle(float arcAngle) {
        this.arcAngle = arcAngle;
    }

    public void addCircle(Canvas canvas)
    {
        int width = canvas.getWidth();
        //int height = canvas.getHeight();
        int circleWidth = 500 * width / 800;

        RectF rect1 = new RectF((width - circleWidth) / 2 , (width - circleWidth) / 2,(width + circleWidth) / 2  ,(width + circleWidth) / 2);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(42);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.argb(255, 85, 138, 200));
        canvas.drawArc(rect1 , 30f - arcAngle ,arcAngle ,false ,paint);
    }

    public void addCross(Canvas canvas)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Point center = new Point(width / 2 + 225 * width / 800, height / 2);
        float scale = (float) (50.0 * width / 800) * this.scale;

        crossPath = new Path();
        crossPath.moveTo(center.x + scale * -1 ,center.y + scale * -1);
        drawLine(center ,scale ,new Point(-1 ,-1) ,new Point(-1 ,-2));
        drawHalfCircle(center ,scale ,new Point(0 ,-2) , 180 ,180);
        drawLine(center ,scale ,new Point(1 ,-2) ,new Point(1 ,-1));

        drawLine(center ,scale ,new Point(1, -1) ,new Point(2, -1));
        drawHalfCircle(center ,scale ,new Point(2, 0) , -90 ,180);
        drawLine(center ,scale ,new Point(2, 1) ,new Point(1, 1));

        drawLine(center ,scale ,new Point(1, 1) ,new Point(1, 2));
        drawHalfCircle(center ,scale ,new Point(0 ,2) , 0 ,180);
        drawLine(center ,scale ,new Point(-1, 2) ,new Point(-1, 1));

        drawLine(center ,scale ,new Point(-1, 1) ,new Point(-2, 1));
        drawHalfCircle(center ,scale ,new Point(-2 ,0) , 90 ,180);
        drawLine(center ,scale ,new Point(-2, -1) ,new Point(-1, -1));
        crossPath.close();

        PathMeasure measure = new PathMeasure(crossPath, false);
        length = measure.getLength();
        //canvas.drawPath(crossPath ,mPaint);
    }

    private void drawLine(Point center , float scale , Point start , Point end)
    {
        crossPath.lineTo(center.x + scale * start.x ,center.y + scale * start.y);
        crossPath.lineTo(center.x + scale * end.x ,center.y + scale * end.y);
    }

    private void drawHalfCircle(Point center , float scale , Point offset , float start , float end)
    {
        crossPath.arcTo(new RectF(center.x + scale * offset.x - scale , center.y + scale * offset.y - scale,center.x + scale * offset.x + scale  ,center.y + scale * offset.y + scale) ,start ,end);
    }

    public void crossAnimation()
    {
        addCross(canvas);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f);
        animator.setDuration(1000);
        animator.start();

        ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(1.0f ,1.4f);
        valueAnimator1.setInterpolator(new DecelerateInterpolator());
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                scale = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator1.setDuration(400);
        valueAnimator1.setStartDelay(1000);
        valueAnimator1.start();

        ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(1.4f ,0.9f);
        valueAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                scale = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator2.setDuration(600);
        valueAnimator2.setStartDelay(1400);
        valueAnimator2.start();

        ValueAnimator valueAnimator3 = ValueAnimator.ofFloat(0.9f ,1.0f);
        valueAnimator3.setInterpolator(new AccelerateInterpolator());
        valueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                scale = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator3.setDuration(400);
        valueAnimator3.setStartDelay(2000);
        valueAnimator3.start();

        ValueAnimator valueAnimator4 = ValueAnimator.ofFloat(0f ,80.0f);
        valueAnimator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                scale = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator4.setDuration(500);
        valueAnimator4.setStartDelay(3500);
        valueAnimator4.start();
    }

    public void crossAnimation2()
    {
        addCross(canvas);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f);
        animator.setDuration(1000);
        animator.start();

        ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(1.0f ,1.05f);
        valueAnimator1.setInterpolator(new DecelerateInterpolator());
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                scale = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator1.setDuration(400);
        valueAnimator1.setStartDelay(1000);
        valueAnimator1.start();

        ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(1.05f ,0.95f);
        valueAnimator2.setInterpolator(new DecelerateInterpolator());
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                scale = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator2.setDuration(500);
        valueAnimator2.setStartDelay(1400);
        valueAnimator2.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator2.start();

    }

    public void setPhase(float phase)
    {
        Log.d("pathview","setPhase called with:" + String.valueOf(phase));
        mPaint.setPathEffect(createPathEffect(length, phase, 0.0f));
        invalidate();
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset)
    {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }

}
