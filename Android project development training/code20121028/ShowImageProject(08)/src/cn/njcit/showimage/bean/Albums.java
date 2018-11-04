package cn.njcit.showimage.bean;

import java.util.Comparator;
import java.util.List;

import android.graphics.Bitmap;

public class Albums implements Comparator<Object>{
	public int id;
	public Bitmap icon;
	public String displayName;  
	public String path;
	public String picturecount;
	@SuppressWarnings("rawtypes")
	public List tag;
	@Override
	public int compare(Object object1, Object object2) {
		Albums albums1 = (Albums)object1;
		Albums albums2 = (Albums)object2;
		return albums1.displayName.compareToIgnoreCase(albums2.displayName);
	}
}
