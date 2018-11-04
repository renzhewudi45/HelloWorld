package cn.njcit.showimage.adapter;

import java.io.File;
import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.njcit.showimage.R;
import cn.njcit.showimage.bean.Albums;
import cn.njcit.showimage.util.BitmapUtil;

public class ListViewAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private LinkedList<Albums> albums;

	public ListViewAdapter(Context context, LinkedList<Albums> imageInfos) {
		mInflater = LayoutInflater.from(context);
		albums = imageInfos;
	}

	public int getCount() {
		return albums.size();
	}

	public Object getItem(int position) {
		return albums.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.path = (TextView) convertView.findViewById(R.id.path);
			holder.picturecount = (TextView) convertView
					.findViewById(R.id.picturecount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (albums == null) {
			Log.i("ListViewAdapter", "imageInfo is null!");
			return convertView;
		}
		if (albums.get(position) == null) {
			Log.i("ListViewAdapter", "imageInfo.get(position) is null!");
			return convertView;
		}
		File f = new File(albums.get(position).path);
		String fName = f.getName();
		holder.icon.setImageBitmap(BitmapUtil.getRectBitmap(BitmapUtil.getResizedBitmap(100,100,albums.get(position).icon)));

		holder.name.setText(fName);
		holder.path.setText(albums.get(position).path);
		holder.picturecount.setText(albums.get(position).picturecount
				+ " ’≈’’∆¨");
		
		return convertView;
	}

	/* class ViewHolder */
	private class ViewHolder {
		TextView name;
		TextView path;
		TextView picturecount;
		ImageView icon;
	}
}
