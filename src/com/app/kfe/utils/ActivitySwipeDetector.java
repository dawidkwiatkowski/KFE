package com.app.kfe.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import app.model.SwipeInterface;

public class ActivitySwipeDetector implements View.OnTouchListener {

    static final String logTag = "ActivitySwipeDetector";
    private SwipeInterface activity;
    private float downX, downY;
    private long timeDown;
    private final float MIN_DISTANCE;
    private final int VELOCITY;
    private final float MAX_OFF_PATH;

    public ActivitySwipeDetector(Context context, SwipeInterface activity){
        this.activity = activity;
        final ViewConfiguration vc = ViewConfiguration.get(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        MIN_DISTANCE = vc.getScaledPagingTouchSlop() * dm.density;
        VELOCITY = vc.getScaledMinimumFlingVelocity();
        MAX_OFF_PATH = MIN_DISTANCE * 2;            
    }

    public void onRightToLeftSwipe(View v){
        activity.onRightToLeft(v);
    }

    public void onLeftToRightSwipe(View v){
        activity.onLeftToRight(v);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN: {
            timeDown = System.currentTimeMillis();
            downX = event.getX();
            downY = event.getY();
            return true;
        }
        case MotionEvent.ACTION_UP: {
            long timeUp = System.currentTimeMillis();
            float upX = event.getX();
            float upY = event.getY();

            float deltaX = downX - upX;
            float absDeltaX = Math.abs(deltaX); 
            float deltaY = downY - upY;
            float absDeltaY = Math.abs(deltaY);

            long time = timeUp - timeDown;

            if (absDeltaY > MAX_OFF_PATH) {
                Log.i(logTag, String.format("absDeltaY=%.2f, MAX_OFF_PATH=%.2f", absDeltaY, MAX_OFF_PATH));
                return v.performClick();
            }

            final long M_SEC = 1000;
            if (absDeltaX > MIN_DISTANCE && absDeltaX > time * VELOCITY / M_SEC) {
                if(deltaX < 0) { this.onLeftToRightSwipe(v); return true; }
                if(deltaX > 0) { this.onRightToLeftSwipe(v); return true; }
            }

        }
        }
        return false;
    }

}
