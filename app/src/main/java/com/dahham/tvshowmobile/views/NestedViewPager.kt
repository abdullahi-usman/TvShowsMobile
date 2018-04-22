package com.dahham.tvshowmobile.views

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * Created by dahham on 4/21/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class NestedViewPager : ViewPager {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var widthSize = View.MeasureSpec.getSize(widthSpec)
        var heightSize = View.MeasureSpec.getSize(heightSpec)

        if (heightSize < minimumHeight || heightSize < suggestedMinimumHeight){
            heightSize = Math.max(minimumHeight, suggestedMinimumHeight)
        }

        if (widthSize < minimumWidth || heightSize < suggestedMinimumWidth){
            widthSize = Math.max(minimumWidth, suggestedMinimumWidth)
        }


        super.onMeasure(View.MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST))
    }
}