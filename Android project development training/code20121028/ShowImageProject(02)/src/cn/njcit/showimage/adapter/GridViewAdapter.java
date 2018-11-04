package cn.njcit.showimage.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.njcit.showimage.R;
import cn.njcit.showimage.util.BitmapUtil;

public class GridViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private String[] rowid;
	private Context c;

	public GridViewAdapter(Context context, String[] rows) {
		mInflater = LayoutInflater.from(context);
		c = context;
		rowid = rows;
	}

	public int getCount() {
		return rowid.length;
	}

	public Object getItem(int position) {
		return rowid[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.grid_row, null);
			imageView = (ImageView) convertView.findViewById(R.id.imageItem);
			convertView.setTag(imageView);
		} else {
			imageView = (ImageView) convertView.getTag();
		}

		/*imageView.setImageBitmap(Thumbnails.getThumbnail(
				c.getContentResolver(), Integer.valueOf(rowid[position]),
				Thumbnails.MICRO_KIND, new BitmapFactory.Options()));*/
		// ≤‚ ‘”√
		imageView.setImageBitmap(BitmapUtil.getOptionBitmap(rowid[position]));

		return convertView;
	}
}