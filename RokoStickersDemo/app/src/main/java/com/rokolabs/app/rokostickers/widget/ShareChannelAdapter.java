package com.rokolabs.app.rokostickers.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rokolabs.app.common.image.ImageFetcher;
import com.rokolabs.app.rokostickers.R;
import com.rokolabs.app.rokostickers.data.Channel;

import java.util.ArrayList;
import java.util.List;

public class ShareChannelAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public List<Channel> mItems;
	private ImageFetcher mFetcher;

	public ShareChannelAdapter(Context context, ImageFetcher fetcher) {
		mItems = new ArrayList<Channel>();
		mInflater = LayoutInflater.from(context);
		mFetcher = fetcher;
	}

	public void setItems(List<Channel> mItems) {
		this.mItems.clear();
		this.mItems.addAll(mItems);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.channel_item, parent,false);

			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.share_item_id);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Channel channel = (Channel) getItem(position);
		viewHolder.data = channel;
		if (channel.enabled) {
			mFetcher.loadImage(channel.imageFileGroup.files.get(0).file.url,
					viewHolder.imageView);
			convertView.setVisibility(View.VISIBLE);
		} else {
			convertView.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		public ImageView imageView;
		
		public Channel data;
	}

}
