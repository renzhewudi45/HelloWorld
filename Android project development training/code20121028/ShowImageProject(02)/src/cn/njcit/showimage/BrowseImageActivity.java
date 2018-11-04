package cn.njcit.showimage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import cn.njcit.showimage.adapter.GalleryAdapter;
import cn.njcit.showimage.adapter.GridViewAdapter;
import cn.njcit.showimage.meta.MetaData;
import cn.njcit.showimage.util.BitmapUtil;
import cn.njcit.showimage.view.GalleryFlow;

@SuppressLint("HandlerLeak")
public class BrowseImageActivity extends Activity implements OnGestureListener {

	private ViewFlipper viewFlipper;
	private GridView gridView;
	private GalleryFlow gallery;
	private GestureDetector detector;
	private String[] testData = { "/mnt/sdcard/pic/1.jpg",
			"/mnt/sdcard/pic/2.jpg", "/mnt/sdcard/pic/3.jpg",
			"/mnt/sdcard/pic/4.jpg", "/mnt/sdcard/pic/5.jpg", };
	private int currentPosition = 0;
	private int showMode = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.browseimage_layout);

		Display display = getWindowManager().getDefaultDisplay();
		MetaData.screenWidth = display.getWidth();
		MetaData.screenHeight = display.getHeight();

		initView();
	}

	private void setFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		Window myWindow = this.getWindow();
		myWindow.setFlags(flag, flag);
	}

	private void initView() {

		detector = new GestureDetector(this);

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		viewFlipper.removeAllViews();

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new GridViewAdapter(getApplicationContext(),
				testData));

		gallery = (GalleryFlow) findViewById(R.id.gallery);

		initGallery();// initGridView();//initViewFlipper();

	}

	private void initViewFlipper() {

		showMode = 0;

		/*
		 * if (MetaData.pictures.size() == 0) { finish(); return; }
		 */

		// 防止MediaStore.Images.Media.EXTERNAL_CONTENT_URI数据异常
		/*
		 * if (currentPosition >= MetaData.pictures.size()) { currentPosition =
		 * MetaData.pictures.size() - 1; }
		 */

		showBitmap(currentPosition);

		viewFlipper.setVisibility(View.VISIBLE);
		gridView.setVisibility(View.GONE);
		gallery.setVisibility(View.GONE);

	}

	private void initGridView() {
		showMode = 1;
		gridView.setSelection(currentPosition);

		viewFlipper.setVisibility(View.GONE);
		gridView.setVisibility(View.VISIBLE);
		gallery.setVisibility(View.GONE);
	}

	private void initGallery() {
		for (int i = 0; i < testData.length; i++) {
			MetaData.pictures.add(testData[i]);
		}

		showMode = 2;
		gallery.setAdapter(new GalleryAdapter(getApplicationContext(),
				MetaData.pictures));
		gallery.setSelection(currentPosition);

		viewFlipper.setVisibility(View.GONE);
		gridView.setVisibility(View.GONE);
		gallery.setVisibility(View.VISIBLE);
	}

	private void showBitmap(int pos) {

		viewFlipper.removeAllViews();

		String picturePath;
		// 文件夹只有一张图片
		if (this.testData.length == 1) {
			picturePath = this.testData[0];
		} else {
			picturePath = this.testData[pos];
		}
		ImageView imageView = new ImageView(this);
		Bitmap mBitmap = BitmapUtil.getResizeBitmap(picturePath);

		imageView.setImageBitmap(mBitmap);
		viewFlipper.addView(imageView);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// showActionBar(viewFlipper);
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (e1 == null || e2 == null) {
			return false;
		}

		if (e1.getX() - e2.getX() > 120) {
			if (showMode == 0) {
				showNext();
			}

			return true;
		} else if (e1.getX() - e2.getX() < -120) {

			if (showMode == 0) {
				showPrevious();
			}

			return true;
		}

		return false;
	}

	private void showNext() {
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.in_rightleft));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.out_rightleft));

		if (++currentPosition < testData.length) {
			showBitmap(currentPosition);
		} else {
			currentPosition = 0;
			showBitmap(currentPosition);
		}

		viewFlipper.showNext();
	}

	private void showPrevious() {
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.in_leftright));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.out_leftright));

		if (--currentPosition < 0) {
			currentPosition = testData.length - 1;
			showBitmap(currentPosition);
		} else {
			showBitmap(currentPosition);
		}

		viewFlipper.showPrevious();
	}

}
