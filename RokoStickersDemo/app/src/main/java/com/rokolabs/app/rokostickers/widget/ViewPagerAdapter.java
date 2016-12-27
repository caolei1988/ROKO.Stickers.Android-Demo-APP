package com.rokolabs.app.rokostickers.widget;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter
{

    public List<View> pageViews = new ArrayList<View>();

    public ViewPagerAdapter(List<View> iViews)
    {
        super();
        this.pageViews = iViews;
    }

    @Override
    public int getCount()
    {
        return pageViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1)
    {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(View arg0, int position, Object arg2)
    {
        if (position < pageViews.size())
            ((ViewPager) arg0).removeView(pageViews.get(position));
    }

    @Override
    public Object instantiateItem(View arg0, int arg1)
    {
        ((ViewPager) arg0).addView(pageViews.get(arg1));
        return pageViews.get(arg1);
    }
}