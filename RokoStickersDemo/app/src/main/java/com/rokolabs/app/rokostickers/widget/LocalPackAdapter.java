package com.rokolabs.app.rokostickers.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.rokolabs.app.rokostickers.R;
import com.rokolabs.app.rokostickers.data.local.StickerPackInfo;

import java.util.ArrayList;
import java.util.List;

public class LocalPackAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private int                  mSelectChild;

    public List<StickerPackInfo> mItems;

    public LocalPackAdapter(Context context)
    {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = new ArrayList<StickerPackInfo>();
    }

    public void setSelectChild(int selectChild)
    {
        this.mSelectChild = selectChild;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.visual_entry, null);

            StickerViewHolder holder = new StickerViewHolder();
            holder.stickerView = (ImageView) convertView.findViewById(R.id.visual_image);
            convertView.setTag(holder);
        }
        final StickerViewHolder holder = (StickerViewHolder) convertView.getTag();

        Integer visual;
        if (position == mSelectChild)
        {
            visual = mItems.get(position).iconSelected;
        } else
        {
            visual = mItems.get(position).iconDefault;
        }
        if (visual != null)
        {
            holder.stickerView.setTag(visual);
            holder.stickerView.setImageResource(visual);
        }
        return convertView;
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

}
