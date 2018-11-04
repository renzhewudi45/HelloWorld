package cn.njcit.showimage;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import cn.njcit.showimage.util.BitmapUtil;
import cn.njcit.showimage.util.Util;

public class ClipImageActivity extends Activity {

	private ImageView imageView;
	private Bitmap mBitmap;
	private String imagePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.croppicture_layout);

		initImageView();
		startPhotoCrop(imagePath);
	}

	private void setFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		Window myWindow = this.getWindow();
		myWindow.setFlags(flag, flag);
	}

	/**
	 * 初始化图片控件
	 */
	private void initImageView() {
		imageView = (ImageView) findViewById(R.id.imageView);
		Intent intent = this.getIntent();
		imagePath = intent.getExtras().getString("imagePath");
		mBitmap = BitmapUtil.getResizeBitmap(imagePath);
		imageView.setImageBitmap(mBitmap);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoCrop(String path) {
		File mFile = new File(path);
		Uri uri = Uri.parse("file://" + mFile.getAbsolutePath());

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 3);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 480);

		intent.putExtra("return-data", true);

		try {
			startActivityForResult(intent, 1);
			return;
		} catch (ActivityNotFoundException ex) {
			Log.d("PictureShow", "activity not found! ");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 取得裁剪后的图片
		case 1:
			if (data != null) {
				setPicToView(data);
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		final Bitmap bmp;

		if (extras != null) {
			bmp = extras.getParcelable("data");
			Util.showSaveDialog(ClipImageActivity.this, bmp, imagePath);
		}
	}

}