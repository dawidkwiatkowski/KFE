package com.app.kfe;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
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


public class Tablica extends Activity implements OnSeekBarChangeListener, OnClickListener {
	
	private PaintView paintView;
	private Button yellowButton;
	private Button greenButton;
	private Button blueButton;
	private Button redButton;
	private Button whiteButton;
	private Button blackButton;
	private Paint drawPaint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tablica);
		
		SlidingDrawer toolsPanel = (SlidingDrawer) findViewById(R.id.toolsPanel);
		final ImageButton handle = (ImageButton) findViewById(R.id.handle);				
		
		paintView = (PaintView) findViewById(R.id.drawing);
		drawPaint = paintView.getDrawPaint();
		redButton = (Button) findViewById(R.id.redButton);
		yellowButton = (Button) findViewById(R.id.yellowButton);
		greenButton = (Button) findViewById(R.id.greenButton);
		blueButton = (Button) findViewById(R.id.blueButton);
		whiteButton = (Button) findViewById(R.id.whiteButton);
		blackButton = (Button) findViewById(R.id.blackButton);
		
		redButton.setOnClickListener(this);
		yellowButton.setOnClickListener(this);
		blueButton.setOnClickListener(this);
		whiteButton.setOnClickListener(this);
		blackButton.setOnClickListener(this);
		greenButton.setOnClickListener(this);
		
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
		

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
		drawPaint.setStrokeWidth((float) progress);
		
		paintView.setDrawPaint(drawPaint);
		
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
				break;
			case R.id.yellowButton:
				drawPaint.setColor(Color.YELLOW);
				break;
			case R.id.greenButton:
				drawPaint.setColor(Color.GREEN);
				break;
			case R.id.blueButton:
				drawPaint.setColor(Color.BLUE);
				break;
			case R.id.whiteButton:
				drawPaint.setColor(Color.WHITE);
				break;
			case R.id.blackButton:
				drawPaint.setColor(Color.BLACK);
				break;				
		}		
		paintView.setDrawPaint(drawPaint);
		
	}	

}
