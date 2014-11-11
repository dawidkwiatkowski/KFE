package com.app.kfe.rysowanie;

import java.util.UUID;

import com.app.kfe.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;


public class Tablica extends Activity implements OnSeekBarChangeListener, OnClickListener {
	
	private PaintView paintView;
	private Button yellowButton;
	private Button greenButton;
	private Button blueButton;
	private Button redButton;
	private Button whiteButton;
	private Button blackButton;
	private Paint drawPaint;
	private Paint canvasPaint;
	private ImageButton saveButton;
	private ImageButton brushTool;
	private ImageButton lineTool;
	private ImageButton rectangleTool;
	private ImageButton squareTool;
	private ImageButton circleTool;
	private ImageButton triangleTool;
	private ImageButton eraserTool;
	private ImageButton newImageTool;
	private AlertDialog.Builder saveDialog;
	private AlertDialog.Builder newImageDialog;
	private int brushColor;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tablica);
		
		SlidingDrawer toolsPanel = (SlidingDrawer) findViewById(R.id.toolsPanel);
		final ImageButton handle = (ImageButton) findViewById(R.id.handle);				
		
		paintView = (PaintView) findViewById(R.id.drawing);
		drawPaint = paintView.getDrawPaint();
		canvasPaint = paintView.getCanvasPaint();
		redButton = (Button) findViewById(R.id.redButton);
		yellowButton = (Button) findViewById(R.id.yellowButton);
		greenButton = (Button) findViewById(R.id.greenButton);
		blueButton = (Button) findViewById(R.id.blueButton);
		whiteButton = (Button) findViewById(R.id.whiteButton);
		blackButton = (Button) findViewById(R.id.blackButton);
		saveButton = (ImageButton) findViewById(R.id.saveButton);
		brushTool = (ImageButton) findViewById(R.id.brushTool);
		brushColor = drawPaint.getColor();
		lineTool = (ImageButton) findViewById(R.id.lineTool);
		rectangleTool = (ImageButton) findViewById(R.id.rectangleTool);
		squareTool = (ImageButton) findViewById(R.id.squareTool);
		circleTool = (ImageButton) findViewById(R.id.circleTool);
		triangleTool = (ImageButton) findViewById(R.id.triangleTool);
		eraserTool = (ImageButton) findViewById(R.id.eraserTool);
		newImageTool = (ImageButton) findViewById(R.id.newImageTool);
		
		redButton.setOnClickListener(this);
		yellowButton.setOnClickListener(this);
		blueButton.setOnClickListener(this);
		whiteButton.setOnClickListener(this);
		blackButton.setOnClickListener(this);
		greenButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		brushTool.setOnClickListener(this);
		lineTool.setOnClickListener(this);
		rectangleTool.setOnClickListener(this);
		squareTool.setOnClickListener(this);
		circleTool.setOnClickListener(this);
		triangleTool.setOnClickListener(this);
		eraserTool.setOnClickListener(this);
		newImageTool.setOnClickListener(this);
		
		SeekBar brashSize = (SeekBar) findViewById(R.id.brushSize);
		brashSize.setOnSeekBarChangeListener(this);
		
		toolsPanel.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
            	handle.setBackgroundResource(R.drawable.right);
            	paintView.setIsEnabled(false);
            }
        });
 
		toolsPanel.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
            	handle.setBackgroundResource(R.drawable.left);
            	paintView.setIsEnabled(true);
            }
        });
		
		saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle("Zapis obraznka");
		saveDialog.setMessage("Czy zapisaæ obrazek do galerii?");
		saveDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				saveImage();
				dialog.cancel();
			}
		});
		saveDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		
		newImageDialog = new AlertDialog.Builder(this);
		newImageDialog.setTitle("Czyszczenie tablicy");
		newImageDialog.setMessage("Czy czy wyczyœciæ tablicê?");
		newImageDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				newImage();
				dialog.cancel();
			}
		});
		newImageDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
		drawPaint.setStrokeWidth((float) progress);
		canvasPaint.setStrokeWidth((float) progress);
		
		paintView.setDrawPaint(drawPaint);
		paintView.setCanvasPaint(canvasPaint);
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			case R.id.redButton:
				drawPaint.setColor(Color.RED);
				canvasPaint.setColor(Color.RED);
				brushColor = drawPaint.getColor();
				break;
			case R.id.yellowButton:
				drawPaint.setColor(Color.YELLOW);
				canvasPaint.setColor(Color.YELLOW);
				brushColor = drawPaint.getColor();
				break;
			case R.id.greenButton:
				drawPaint.setColor(Color.GREEN);
				canvasPaint.setColor(Color.GREEN);
				brushColor = drawPaint.getColor();
				break;
			case R.id.blueButton:
				drawPaint.setColor(Color.BLUE);
				canvasPaint.setColor(Color.BLUE);
				brushColor = drawPaint.getColor();
				break;
			case R.id.whiteButton:
				drawPaint.setColor(Color.WHITE);
				canvasPaint.setColor(Color.WHITE);
				brushColor = drawPaint.getColor();
				break;
			case R.id.blackButton:
				drawPaint.setColor(Color.BLACK);
				canvasPaint.setColor(Color.BLACK);
				brushColor = drawPaint.getColor();
				break;			
			case R.id.saveButton:				
				saveDialog.show();
				break;
			case R.id.brushTool:
				setBrushTool();
				break;
			case R.id.lineTool:
				setLineTool();
				break;
			case R.id.rectangleTool:
				setRectangleTool();
				break;
			case R.id.squareTool:
				setSquareTool();
				break;
			case R.id.circleTool:
				setCircleTool();
				break;
			case R.id.triangleTool:
				setTriangleTool();
				break;
			case R.id.eraserTool:
				setEraserTool();
				break;
			case R.id.newImageTool:				
				newImageDialog.show();
				break;
		}		
		paintView.setDrawPaint(drawPaint);
		
	}
	
	public void saveImage(){
		paintView.setDrawingCacheEnabled(true);
		
		String imgSaved = MediaStore.Images.Media.insertImage(
				getContentResolver(), paintView.getDrawingCache(),
				UUID.randomUUID().toString()+".png", "drawing");
		
		if(imgSaved != null){
			Toast saveToast = Toast.makeText(getApplicationContext(), "Zapisano do galerii", Toast.LENGTH_SHORT);
			saveToast.show();
		}
		else{
		Toast unsavedToast = Toast.makeText(getApplicationContext(), "Wyst¹pi³ problem podczas zapisu", Toast.LENGTH_SHORT);
		unsavedToast.show();
		}
		
		paintView.destroyDrawingCache();
	}
	
	public void setColor(){
		drawPaint.setColor(brushColor);
		canvasPaint.setColor(brushColor);
	}
	
	public void setBrushTool(){
		setColor();
		paintView.setMCurrentShape(paintView.SMOOTHLINE);
	}
	
	public void setLineTool(){
		setColor();
		paintView.setMCurrentShape(paintView.LINE);
	}
	
	public void setRectangleTool(){
		setColor();
		paintView.setMCurrentShape(paintView.RECTANGLE);
	}
	
	public void setCircleTool(){
		setColor();
		paintView.setMCurrentShape(paintView.CIRCLE);
	}
	
	public void setSquareTool(){
		setColor();
		paintView.setMCurrentShape(paintView.SQUARE);
	}
	
	public void setTriangleTool(){
		setColor();
		paintView.setMCurrentShape(paintView.TRIANGLE);
		paintView.resetTriangle();
	}
	
	public void setEraserTool(){
		drawPaint.setColor(Color.WHITE);	
		canvasPaint.setColor(Color.WHITE);
		paintView.setMCurrentShape(paintView.SMOOTHLINE);
	}
	
	public void newImage(){
		paintView.newImage();
	}

}
