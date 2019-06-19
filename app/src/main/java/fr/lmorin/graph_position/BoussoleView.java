package fr.lmorin.graph_position;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class BoussoleView extends View {

    private Paint paint;
    private float scale;
    private int cBackground;
    private String horizontalAxisLegend;
    private String verticalAxisLegend;
    private Paint pBold,  pArrow, pVector;
    private int width, height;
    private float    g1 ,  g2 ;

    public BoussoleView(Context context){
        super(context);
        init(context,null);
    }

    public BoussoleView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs){



        TypedArray a = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.BoussoleView,
            0, 0);
        try {
            cBackground =  a.getColor(R.styleable.BoussoleView_backgroundColor ,
                        getResources().getColor(R.color.base02));
            if(a.hasValue(R.styleable.BoussoleView_horizontalAxisLegend)) {
                horizontalAxisLegend = a.getString(R.styleable.BoussoleView_horizontalAxisLegend);
            }
            else horizontalAxisLegend="g_h";
            if(a.hasValue(R.styleable.BoussoleView_verticalAxisLegend))
                verticalAxisLegend = a.getString(R.styleable.BoussoleView_verticalAxisLegend);
            else verticalAxisLegend = "g_v";

        }finally {
            a.recycle();
        }


        paint = new Paint();
        paint.setColor(cBackground);
        paint.setDither(true);
        paint.setAntiAlias(true);
        scale = getResources().getDisplayMetrics().density;

        pBold= new Paint();
        pBold.setStrokeWidth(scale*6);
        pBold.setStyle(Paint.Style.STROKE);
        pBold.setColor(getResources().getColor(R.color.base1));

        pArrow = new Paint();
        pArrow.setStrokeWidth(scale*2);
        pArrow.setColor(getResources().getColor(R.color.yellow));
        pArrow.setTextSize(14*scale);

        pVector = new Paint();
        pVector.setStrokeWidth(scale*2.1f);
        pVector.setColor(getResources().getColor(R.color.red));

        g1=0.f;
        g2=0.f;

    }

    public void setBackgroundColor(int c){
                cBackground =  c;
                invalidate();
                requestLayout();
    }

    protected void drawArrow(float x1, float y1, float x2, float y2,
                             String s, Canvas canvas,Paint p){
        canvas.drawLine(x1, y1, x2, y2, p);

        final float th = (float) (30.f*Math.PI/180.f);
        final float l = 12.f;
        final float d_short = (float) (l*scale*sin(th));
        final float d_long = (float) (l*scale*cos(th));
        float dx = x1-x2;
        float dy = y1-y2;
        double scale = 1.f/sqrt(dx*dx+dy*dy);
        dx = (float) (dx*scale);
        dy = (float) (dy*scale);

       canvas.drawLine(x2, y2,
                x2+dx*d_long+dy*d_short,
                y2+dy*d_long-dx*d_short, p);
        canvas.drawLine(x2, y2,
                x2+dx*d_long-dy*d_short,
                y2+dy*d_long+dx*d_short, p);

        canvas.drawText(s, x2+2.f*(dx*d_long+dy*d_short), y2+2.f*(dy*d_long-dx*d_short), p );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = width;
        int y = height;



        int radius = (int) ((min(x,y)-scale*4)/2);
        canvas.drawPaint(paint);
        canvas.drawColor(paint.getColor());



        canvas.drawCircle(x/2, y/2, radius, pBold);


        drawArrow(x/2,y/2,x/2+radius, y/2,
                horizontalAxisLegend, canvas, pArrow);
        drawArrow(x/2,y/2,x/2, y/2-radius,
                verticalAxisLegend, canvas, pArrow);


        drawArrow(x/2, y/2, x/2+g1*radius/9.81f, y/2-g2*radius/9.81f,
                "g", canvas, pVector);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
		width = w;
		height = h;
		invalidate();
        requestLayout();

    }

    public void setGrav(float g_h, float g_v){
        g1 = g_h;
        g2 = g_v;
        invalidate();

    }

}
