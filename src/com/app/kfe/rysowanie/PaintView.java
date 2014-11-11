package com.app.kfe.rysowanie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PaintView extends View {
	
	 public static final int LINE = 1;
	 public static final int RECTANGLE = 3;
	 public static final int SQUARE = 4;
	 public static final int CIRCLE = 5;
	 public static final int TRIANGLE = 6;
	 public static final int SMOOTHLINE = 2;
	 
	 public int mCurrentShape;
	 
	 public boolean isDrawing = false;
	
	//drawing path
	private Path drawPath;
	//drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	//initial color
	private int paintColor = Color.WHITE;
	//canvas
	private Canvas drawCanvas;
	//canvas bitmap
	private Bitmap canvasBitmap;
	
	private float touchX;
	
	private float touchY;
	
	private float mx;
	
	private float my;
	
	private boolean isEnabled;

	public PaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		isEnabled = true;
		setupDrawing();
	}
	
	public void setDrawCanvas(Canvas drawCanvas){
		this.drawCanvas = drawCanvas;
	}
	
	public Canvas getDrawCanvas(){
		return drawCanvas;
	}
	
	public void setDrawPaint(Paint drawPaint){
		this.drawPaint = drawPaint;		
	}
	
	public Paint getDrawPaint(){
		return drawPaint;
	}
	
	public void setCanvasPaint(Paint canvasPaint){
		this.canvasPaint = canvasPaint;
	}
	
	public Paint getCanvasPaint(){
		return canvasPaint;
	}
	
	public void setIsEnabled(boolean isEnabled){
		this.isEnabled = isEnabled;
	}
	
	public boolean getIsEnabled(){
		return isEnabled;
	}
	
	public void setMCurrentShape(int mCurrentShape){
		this.mCurrentShape = mCurrentShape;
	}
	
	public int getMCurrentShape(){
		return mCurrentShape;
	}
	
	private void setupDrawing(){
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		canvasPaint.setAntiAlias(true);
		canvasPaint.setStrokeWidth(20);
		canvasPaint.setStyle(Paint.Style.STROKE);
		canvasPaint.setStrokeJoin(Paint.Join.ROUND);
		canvasPaint.setStrokeCap(Paint.Cap.ROUND);
		
		mCurrentShape = SMOOTHLINE;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
		drawCanvas.drawColor(Color.WHITE);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		//canvas.drawPath(drawPath, drawPaint);
		
		if (isDrawing){
			switch (mCurrentShape) {
				case LINE:
					onDrawLine(canvas);
					break;
				case RECTANGLE:
					onDrawRectangle(canvas);
					break;
				case SQUARE:
					onDrawSquare(canvas);
					break;
				case CIRCLE:
					onDrawCircle(canvas);
					break;
				case TRIANGLE:
					onDrawTriangle(canvas);
					break;
				}
			}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		touchX = event.getX();
		touchY = event.getY();
		if( isEnabled){
			switch (mCurrentShape) {
				case LINE:
					onTouchEventLine(event);
					break;
				case SMOOTHLINE:
					onTouchEventSmoothLine(event);
					break;
				case RECTANGLE:
					onTouchEventRectangle(event);
					break;
				case SQUARE:
					onTouchEventSquare(event);
					break;
				case CIRCLE:
					onTouchEventCircle(event);
					break;
				case TRIANGLE:
					onTouchEventTriangle(event);
					break;
			}
			return true;
		}
		else
			return false;
	}
	
	private void onTouchEventSmoothLine(MotionEvent event) {
		
		mx = touchX;
		my = touchY;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isDrawing = true;
			    drawPath.moveTo(mx, my);
			    invalidate();
			    break;
			case MotionEvent.ACTION_MOVE:
			    drawPath.lineTo(mx, my);
			    drawCanvas.drawPath(drawPath, drawPaint);
			    invalidate();
			    break;
			case MotionEvent.ACTION_UP:
				isDrawing = false;
			    drawCanvas.drawPath(drawPath, drawPaint);
			    drawPath.reset();
			    invalidate();
			    break;
		}		
	}
	
	private void onDrawRectangle(Canvas canvas) {
		drawRectangle(canvas,canvasPaint);
	}
	
	private void onDrawLine(Canvas canvas) {
        canvas.drawLine(mx, my, touchX, touchY, canvasPaint);        
    }
	
	private void onDrawCircle(Canvas canvas){
        canvas.drawCircle(mx, my, calculateRadius(mx, my, touchX, touchY), canvasPaint);
    }
	
	private void onDrawSquare(Canvas canvas) {
        onDrawRectangle(canvas);
    }
	
	int countTouch = 0;
    float basexTriangle = 0;
    float baseyTriangle = 0;

    private void onDrawTriangle(Canvas canvas){

        if (countTouch<3){
            canvas.drawLine(mx,my,touchX,touchY,canvasPaint);
        }else if (countTouch==3){
            canvas.drawLine(touchX,touchY,mx,my,canvasPaint);
            canvas.drawLine(touchX,touchY,basexTriangle,baseyTriangle,canvasPaint);
        }
    }
	
	private void onTouchEventRectangle(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isDrawing = true;
				mx = touchX;
				my = touchY;
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				isDrawing = false;
				drawRectangle(drawCanvas,drawPaint);
				invalidate();
				break;
		}		
	}
	
	private void onTouchEventLine(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mx = touchX;
                my = touchY;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                drawCanvas.drawLine(mx, my, touchX, touchY, drawPaint);
                invalidate();
                break;
        }
    }
	
	private void onTouchEventCircle(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mx = touchX;
                my = touchY;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                drawCanvas.drawCircle(mx, my,
                     calculateRadius(mx,my,touchX,touchY), drawPaint);
                invalidate();
                break;
        }
    }
	
	private void onTouchEventSquare(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mx = touchX;
                my = touchY;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                adjustSquare(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                adjustSquare(touchX, touchY);
                drawRectangle(drawCanvas,drawPaint);
                invalidate();
                break;
        }
    }
	
	private void onTouchEventTriangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                countTouch++;
                if (countTouch==1){
                    isDrawing = true;
                    mx = touchX;
                    my = touchY;
                } else if (countTouch==3){
                    isDrawing = true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                countTouch++;
                isDrawing = false;
                if (countTouch<3){
                    basexTriangle=touchX;
                    baseyTriangle=touchY;
                    drawCanvas.drawLine(mx,my,touchX,touchY,drawPaint);
                } else if (countTouch>=3){
                	drawCanvas.drawLine(touchX,touchY,mx,my,drawPaint);
                	drawCanvas.drawLine(touchX,touchY,basexTriangle,baseyTriangle,drawPaint);
                    countTouch = 0;
                }
                invalidate();
                break;
        }
    }
	
	private void drawRectangle(Canvas canvas,Paint paint){
		float right = mx > touchX ? mx : touchX;
		float left = mx > touchX ? touchX : mx;
		float bottom = my > touchY ? my : touchY;
		float top = my > touchY ? touchY : my;
		canvas.drawRect(left, top , right, bottom, paint);
	}
	
	protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }
	
	protected void adjustSquare(float x, float y) {
        float deltaX = Math.abs(mx - x);
        float deltaY = Math.abs(my - y);

        float max = Math.max(deltaX, deltaY);

        touchX = mx - x < 0 ? mx + max : mx - max;
        touchY = my - y < 0 ? my + max : my - max;
    }
	
	public void newImage(){
		drawCanvas.drawColor(paintColor, android.graphics.PorterDuff.Mode.CLEAR);
		invalidate();
	}
	
	public void resetTriangle(){
		countTouch = 0;
	    basexTriangle = 0;
	    baseyTriangle = 0;
	}

}
