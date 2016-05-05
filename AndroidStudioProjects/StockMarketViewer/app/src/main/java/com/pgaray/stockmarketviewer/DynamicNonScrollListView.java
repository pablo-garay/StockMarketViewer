package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

/**
 * Created by Pablo on 5/2/2016.
 */
public class DynamicNonScrollListView extends DynamicListView {

    public DynamicNonScrollListView(Context context) {
        super(context);
    }
    public DynamicNonScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DynamicNonScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
