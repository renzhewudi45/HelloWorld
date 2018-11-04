package cn.njcit.showimage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.njcit.showimage.R;

public class ListDialogAdapter extends BaseAdapter{
	
	private int[] imgIds;
	private String[] labels;
	private Context context;

	public ListDialogAdapter(Context c, int[] imgIds, String[] labels) {
		context = c;
		this.imgIds = imgIds;
		this.labels = labels;
	}
	
	@Override  
    public int getCount() {  
        return imgIds.length;  
    }  

    @Override  
    public Object getItem(int position) {  
        return null;  
    }  

    @Override  
    public long getItemId(int position) {  
        return 0;  
    }  

    @Override  
    public View getView(int position,   
            View convertView, ViewGroup parent) {
    	ViewHolder holder;
    	
    	if (convertView == null) {
    		LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.dialog_item_layout, null);
			holder = new ViewHolder();

			holder.icon = (ImageView) convertView.findViewById(R.id.dialog_icon);
			holder.label = (TextView) convertView.findViewById(R.id.dialog_title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
    	
    	holder.icon.setImageResource(imgIds[position]);
    	holder.label.setText(labels[position]);
    	
        return convertView;  
    }  
      
      
    /* class ViewHolder */
	private class ViewHolder {
		TextView label;
		ImageView icon;
	}
}
