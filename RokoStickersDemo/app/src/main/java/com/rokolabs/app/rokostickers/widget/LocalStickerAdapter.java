package com.rokolabs.app.rokostickers.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.rokolabs.app.rokostickers.R;

import java.util.List;

public class LocalStickerAdapter extends BaseAdapter
{

    private List<Integer> items;

    private LayoutInflater inflater;

    private int            size = 0;

    public LocalStickerAdapter(Context context, List<Integer> list)
    {
        this.inflater = LayoutInflater.from(context);
        this.items = list;
        this.size = list.size();
    }

    @Override
    public int getCount()
    {
        return this.size;
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Integer stickerRes = items.get(position);
        StickerViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new StickerViewHolder();
            convertView = inflater.inflate(R.layout.item_face, null);
            viewHolder.stickerView = (ImageView) convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (StickerViewHolder) convertView.getTag();
        }
        viewHolder.stickerView.setTag(stickerRes);
        viewHolder.stickerView.setImageResource(stickerRes);

        return convertView;
    }

}