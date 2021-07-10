package com.victoria.bleled.util.view.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CircleTimerView extends View {
    private static final String TAG = "CircleTimerView";

    // Status
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_RADIAN = "status_radian";

    // Default dimension in dp/pt
    private static final float DEFAULT_CIRCLE_BUTTON_RADIUS = 15;

    // Default color
    private static final int DEFAULT_CIRCLE_BUTTON_COLOR = 0xffd10c24;

    // Paint
    private Paint mCircleButtonPaint;

    // Dimension
    private float mCircleButtonRadius;

    // Color
    private int mCircleButtonColor;
    // Parameters
    private float mCx;
    private float mCy;
    private float mRadius;
    private float mCurrentRadian;
    private float mPreRadian;
    private boolean mInCircleButton;
    private int mCurrentTime; // seconds
    private boolean mIsTriangleThumb = false;

    // Runt
    private CircleTimerListener mCircleTimerListener;

    public CircleTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public CircleTimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTimerView(Context context) {
        this(context, null);
    }

    private void initialize() {
        Log.d(TAG, "initialize");
        mCircleButtonRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_BUTTON_RADIUS, getContext()
                .getResources().getDisplayMetrics());

        // Set default color or read xml attributes
        mCircleButtonColor = DEFAULT_CIRCLE_BUTTON_COLOR;

        // Init all paints
        mCircleButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // CircleButtonPaint
        mCircleButtonPaint.setColor(mCircleButtonColor);
        mCircleButtonPaint.setAntiAlias(true);
        mCircleButtonPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Circle button
        canvas.save();
        canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);

        if (mIsTriangleThumb) {
            Paint paint = new Paint();
            int x = (int) (mCx - mCircleButtonRadius / 2), y = (int) (getMeasuredHeight() / 2 - mRadius);
            int width = (int) mCircleButtonRadius, height = (int) mCircleButtonRadius;
            boolean inverted = false;
            Point p1 = new Point(x, y);
            int pointX = x + width / 2;
            int pointY = inverted ? y + height : y - height;

            Point p2 = new Point(pointX, pointY);
            Point p3 = new Point(x + width, y);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
            path.lineTo(p3.x, p3.y);
            path.close();
            paint.setColor(DEFAULT_CIRCLE_BUTTON_COLOR);
            canvas.drawPath(path, paint);
        } else {
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius, mCircleButtonRadius, mCircleButtonPaint);
        }

        canvas.restore();
        super.onDraw(canvas);
    }

    private float getFontHeight(Paint paint) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // If the point in the circle button
                if (mInCircleButton(event.getX(), event.getY()) && isEnabled()) {
                    mInCircleButton = true;
                    mPreRadian = getRadian(event.getX(), event.getY());
                    Log.d(TAG, "In circle button");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInCircleButton && isEnabled()) {
                    float temp = getRadian(event.getX(), event.getY());
                    if (mPreRadian > Math.toRadians(270) && temp < Math.toRadians(90)) {
                        mPreRadian -= 2 * Math.PI;
                    } else if (mPreRadian < Math.toRadians(90) && temp > Math.toRadians(270)) {
                        mPreRadian = (float) (temp + (temp - 2 * Math.PI) - mPreRadian);
                    }
                    mCurrentRadian += (temp - mPreRadian);
                    mPreRadian = temp;
//                    if (mCurrentRadian > 2 * Math.PI) {
//                        mCurrentRadian = (float) (2 * Math.PI);
//                    } else if (mCurrentRadian < 0) {
//                        mCurrentRadian = 0;
//                    }
                    if (mCircleTimerListener != null)
                        mCircleTimerListener.onTimerValueChanged(getCurrentTime());
                    mCurrentTime = (int) (60 / (2 * Math.PI) * mCurrentRadian * 60);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mInCircleButton && isEnabled()) {
                    mInCircleButton = false;
                    if (mCircleTimerListener != null)
                        mCircleTimerListener.onTimerValueChanged(getCurrentTime());
                }
                break;
        }
        return true;
    }

    // Whether the down event inside circle button
    private boolean mInCircleButton(float x, float y) {
        float r = mRadius;
        float x2 = (float) (mCx + r * Math.sin(mCurrentRadian));
        float y2 = (float) (mCy - r * Math.cos(mCurrentRadian));
        if (Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)) < mCircleButtonRadius) {
            return true;
        }
        return false;
    }

    // Use tri to cal radian
    private float getRadian(float x, float y) {
        float alpha = (float) Math.atan((x - mCx) / (mCy - y));
        // Quadrant
        if (x > mCx && y > mCy) {
            // 2
            alpha += Math.PI;
        } else if (x < mCx && y > mCy) {
            // 3
            alpha += Math.PI;
        } else if (x < mCx && y < mCy) {
            // 4
            alpha = (float) (2 * Math.PI + alpha);
        }
        return alpha;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Ensure width = height
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.mCx = width / 2;
        this.mCy = height / 2;

        // Radius
        if (0 >= mCircleButtonRadius) {
            this.mRadius = width / 2;
            Log.d(TAG, "No exceed");
        } else {
            this.mRadius = width / 2 - mCircleButtonRadius;
            Log.d(TAG, "Exceed");
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "onSaveInstanceState");
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_RADIAN, mCurrentRadian);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestoreInstanceState");
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            mCurrentRadian = bundle.getFloat(STATUS_RADIAN);
            mCurrentTime = (int) (60 / (2 * Math.PI) * mCurrentRadian * 60);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * set current time in seconds
     *
     * @param time
     */
    public void setCurrentTime(int time) {
        //if (time >= 0 && time <= 3600) {
        mCurrentTime = time;
        this.mCurrentRadian = (float) (time / 60.0f * 2 * Math.PI / 60);
        if (mCircleTimerListener != null) {
            mCircleTimerListener.onTimerValueChanged(time);
        }
        invalidate();
        //}
    }

    /**
     * set timer listener
     *
     * @param mCircleTimerListener
     */
    public void setCircleTimerListener(CircleTimerListener mCircleTimerListener) {
        this.mCircleTimerListener = mCircleTimerListener;
    }

    /**
     * get current time in seconds
     *
     * @return
     */
    public int getCurrentTime() {
        return mCurrentTime;
    }

    public void setTriThumb(boolean triThumb) {
        this.mIsTriangleThumb = triThumb;
    }


    public interface CircleTimerListener {
        /**
         * launch timer set value changed event
         *
         * @param time
         */
        void onTimerValueChanged(int time);
    }

}
