package com.app.kfe;

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
	
	private boolean isEnabled;

	public PaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		isEnabled = true;
		setupDrawing();
	}
	
	public void setDrawPaint(Paint drawPaint){
		this.drawPaint = drawPaint;		
	}
	
	public Paint getDrawPaint(){
		return drawPaint;
	}
	
	public void setIsEnabled(boolean isEnabled){
		this.isEnabled = isEnabled;
	}
	
	public boolean getIsEnabled(){
		return isEnabled;
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
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if( isEnabled){
			float touchX = event.getX();
			float touchY = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			    drawPath.moveTo(touchX, touchY);
			    break;
			case MotionEvent.ACTION_MOVE:
			    drawPath.lineTo(touchX, touchY);
			    break;
			case MotionEvent.ACTION_UP:
			    drawCanvas.drawPath(drawPath, drawPaint);
			    drawPath.reset();
			    break;
			default:
			    return false;
			}
			invalidate();
			return true;
		}
		else
			return false;
	}

}
