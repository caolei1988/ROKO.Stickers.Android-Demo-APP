package com.rokolabs.app.rokostickers.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rokolabs.app.common.image.ImageFetcher;
import com.rokolabs.app.rokostickers.R;
import com.rokolabs.app.rokostickers.data.IconFile;
import com.rokolabs.app.rokostickers.data.Sticker;

import java.util.List;

public class PortalStickerAdapter extends BaseAdapter
{

    private LayoutInflater inflater;

    private int            size = 0;

    private List<Sticker> mItems;
    Context mContext;
    ImageFetcher mFetcher;

    public PortalStickerAdapter(Context context, List<Sticker> list, ImageFetcher fetcher)
    {

        this.mItems = list;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        mFetcher = fetcher;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Sticker item = mItems.get(position);
        if (convertView == null)
        {
            StickerViewHolder iconHolder = new StickerViewHolder();
            convertView = inflater.inflate(R.layout.item_face, null);
            iconHolder.stickerView = (ImageView) convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(iconHolder);
        }

        final StickerViewHolder viewHolder = (StickerViewHolder) convertView.getTag();

        IconFile iconFile = item.imageFileGroup.files.get(0).file;
        if (iconFile != null)
        {
            viewHolder.stickerView.setTag(item);
            mFetcher.loadImage(iconFile.url, viewHolder.stickerView);
        }
        viewHolder.data = item;
        return convertView;
    }

}