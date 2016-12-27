package com.rokolabs.app.rokostickers.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rokolabs.app.common.image.ImageFetcher;
import com.rokolabs.app.common.image.StateListImageFetchTask;

import com.rokolabs.app.rokostickers.R;
import com.rokolabs.app.rokostickers.data.StickersList;

import java.util.ArrayList;
import java.util.List;

public class PortalPackAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private int               mSelectChild;
    public List<StickersList> mItems;
    private ImageFetcher mFetcher;

    public PortalPackAdapter(Context context, ImageFetcher fetcher)
    {
        mItems = new ArrayList<StickersList>();
        mInflater = LayoutInflater.from(context);
        mFetcher = fetcher;
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

            StickerViewHolder visualsViewHolder = new StickerViewHolder();
            visualsViewHolder.stickerView = (ImageView) convertView.findViewById(R.id.visual_image);
            convertView.setTag(visualsViewHolder);
        }

        final StickerViewHolder holder = (StickerViewHolder) convertView.getTag();
        StateListImageFetchTask task = mFetcher.getTask();
        //IconFile iconFile = null;
        task.addStateUrl(new int[]{android.R.attr.state_selected}, mItems.get(position).packIconFileGroup.getFirstUrl());
        task.addStateUrl(new int[]{}, mItems.get(position).unselectedPackIconFileGroup.getFirstUrl());
//        if (position == mSelectChild)
//        {
//            iconFile = mItems.get(position).packIconFileGroup.files.get(0).file;
//        } else
//        {
//            iconFile = mItems.get(position).unselectedPackIconFileGroup.files.get(0).file;
//        }
        holder.stickerView.setImageDrawable(null);
        mFetcher.loadImage(task, holder.stickerView);
        holder.stickerView.setSelected(position == mSelectChild);
//        if (iconFile != null)
//        {
//            holder.stickerView.setTag(iconFile);
//            mFetcher.loadImage(iconFile.url, holder.stickerView);
//        }
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
