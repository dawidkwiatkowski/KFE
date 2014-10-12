package com.app.kfe;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PaintView extends SurfaceView implements SurfaceHolder.Callback{
	
	private ArrayList<RectF> punkty;
    private Paint paint;

	public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
 
    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        punkty = new ArrayList<RectF>();
        paint = new Paint();
    }
    
    public PaintView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		RectF oval = new RectF(event.getX()-5, event.getY()-5, event.getX() + 5, event.getY() + 5);
		punkty.add(oval);
	    invalidate();
        return true;
    }
 
    @Override
	protected void onDraw(Canvas canvas) {
    	paint.setColor(Color.WHITE);
    	 
        for (RectF punkt : punkty) {
            canvas.drawOval(punkt, paint);
        }
    }

}
