package com.victoria.bleled.util.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.victoria.bleled.R;

import java.util.ArrayList;

public class DrawFrameLayout extends FrameLayout {
    private ArrayList<Point> arrPoint1 = new ArrayList<>();
    private ArrayList<Point> arrPoint2 = new ArrayList<>();
    private Paint paint = new Paint();

    public DrawFrameLayout(Context context) {
        this(context, null);
    }

    public DrawFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        paint.setColor(getContext().getResources().getColor(R.color.black));
        paint.setStrokeWidth(10f);
    }

    public void clearLine() {
        arrPoint1.clear();
        arrPoint2.clear();
        invalidate();
    }

    public void drawLine(int x, int y, int x1, int y1) {
        Point point1 = new Point(x, y);
        Point point2 = new Point(x1, y1);
        arrPoint1.add(point1);
        arrPoint2.add(point2);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < (arrPoint1.size()); i++) {
            Point point1 = arrPoint1.get(i);
            Point point2 = arrPoint2.get(i);
            canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
        }
    }
}


