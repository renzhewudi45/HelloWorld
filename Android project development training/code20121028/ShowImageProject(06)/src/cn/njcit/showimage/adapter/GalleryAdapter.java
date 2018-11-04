package cn.njcit.showimage.adapter;

import java.lang.ref.SoftReference;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.njcit.showimage.R;
import cn.njcit.showimage.meta.MetaData;
import cn.njcit.showimage.util.BitmapUtil;
import cn.njcit.showimage.view.GalleryFlow;

public class GalleryAdapter extends BaseAdapter {

	private int mGalleryItemBackground;
	private Context mContext;
	private List<String> list;// ͼ��·���б�
	private Bitmap originalImage;// �����˳ߴ��ԭʼͼ��

	public GalleryAdapter(Context c, List<String> li) {
		mContext = c;
		list = li;
		TypedArray typedArray = mContext
				.obtainStyledAttributes(R.styleable.Gallery);
		mGalleryItemBackground = typedArray.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0);
		typedArray.recycle();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position).toString();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = null;

		if (convertView != null) {
			// �����˻��յ�view ֻ��Ҫֱ�������������޸ľͺ���
			imageView = (ImageView) convertView;
			return imageView;
		} else {
			// û�й����õ�view ��һ��������½�view
			imageView = new ImageView(mContext);
		}

		// ����������Ѵ��ھʹӻ�����ȡ������
		if (MetaData.imageViewDataCache.containsKey(position)) {
			SoftReference<ImageView> softReference = MetaData.imageViewDataCache
					.get(position);
			if (softReference != null && softReference.get() != null) {
				return softReference.get();
			}
		}

		originalImage = BitmapUtil.fitSizePic(320, list.get(position));
		Bitmap bitmapWithReflection = BitmapUtil
				.createReflectedImage(originalImage);
		BitmapDrawable bd = new BitmapDrawable(bitmapWithReflection);
		bd.setAntiAlias(true);
		imageView.setImageDrawable(bd);
		imageView.setLayoutParams(new GalleryFlow.LayoutParams(280, 420));
		// imageView.setBackgroundResource(mGalleryItemBackground);

		// ��ӵ�����
		MetaData.imageViewDataCache.put(position, new SoftReference<ImageView>(
				imageView));
		MetaData.bitmapDataCache.put(position, originalImage);

		return imageView;
	}

}
