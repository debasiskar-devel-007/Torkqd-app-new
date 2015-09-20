package com.torkqd;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by Papai on 9/19/2015.
 */
public class GifView extends WebView {

    /**
     * @param context
     * @param attrs
     */
    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(false);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setLongClickable(false);

    }

    // actions

}
