package com.torkqd;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Papai on 9/20/2015.
 */
public class TapAwareRelativeLayout extends RelativeLayout {

    private final float MOVE_THRESHOLD_DP = 20 * getResources().getDisplayMetrics().density;

    private boolean mMoveOccured;
    private float mDownPosX;
    private float mDownPosY;

    public TapAwareRelativeLayout(Context context) {
        super(context);
    }

    public TapAwareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mMoveOccured = false;
                mDownPosX = ev.getX();
                mDownPosY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (!mMoveOccured) {
                    // TAP occured
                    performClick();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mDownPosX) > MOVE_THRESHOLD_DP || Math.abs(ev.getY() - mDownPosY) > MOVE_THRESHOLD_DP) {
                    mMoveOccured = true;
                }
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }
}