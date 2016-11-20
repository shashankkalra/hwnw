package com.skapps.knowMore;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RssAdapter extends BaseAdapter {

	private final List<RssItem> items;
	private final Context context;

	public RssAdapter(Context context, List<RssItem> items) {
		this.items = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.rss_item, null);
			holder = new ViewHolder();
			holder.itemTitle = (TextView) convertView.findViewById(R.id.itemTitle);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        holder.itemTitle.setText(items.get(position).getTitle());

        if(items.get(position).isHeading()) {
            holder.itemTitle.setTextColor(Color.CYAN);
            holder.itemTitle.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.itemTitle.setTextColor(Color.BLACK);
            holder.itemTitle.setBackgroundColor(Color.WHITE);
        }

		return convertView;
	}

	static class ViewHolder {
		TextView itemTitle;
	}
}
