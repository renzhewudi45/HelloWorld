package cn.njcit.showimage;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ZoomControls;
import cn.njcit.showimage.util.BitmapUtil;

public class ZoomImageActivity extends Activity implements OnTouchListener {
	private static final String TAG = "ZoomPictureActivity";

	// These matrices will be used to move and zoom image
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	float oldDist;
	private ImageView imageView;
	private ZoomControls zoomControls;

	// button zoom
	private float scaleWidth = 1;
	private float scaleHeight = 1;
	private int displayWidth;
	private int displayHeight;
	private Bitmap mBitmap, zoomedBMP;
	private static final double ZOOM_IN_SCALE = 1.25;// 放大系数
	private static final double ZOOM_OUT_SCALE = 0.8;// 缩小系数

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// private boolean isContinue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.zoompicture_layout);

		// 横竖屏切换防止重启
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		initZoomControls();
		initImageView();
		initViewSize();
		enlarge();
	}
	
	private void setFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		Window myWindow = this.getWindow();
		myWindow.setFlags(flag, flag);
	}

	/**
	 * 初始化缩放控件
	 */
	private void initZoomControls() {
		// 放大按钮
		zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		/* 放大按钮onClickListener */
		zoomControls.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enlarge();
			}
		});

		/* 缩小按钮onClickListener */
		zoomControls.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				small();
			}

		});
	}

	/**
	 * 初始化图片控件
	 */
	private void initImageView() {
		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setOnTouchListener(this);
		String imagePath;
		Intent intent = this.getIntent();
		final String action = intent.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri mUri = intent.getData();
			imagePath = mUri.getPath().toString();
		} else {
			imagePath = intent.getExtras().getString("imagePath");
		}

		mBitmap = BitmapUtil.getResizeBitmap(imagePath);
		imageView.setImageBitmap(mBitmap);
		imageView.setOnTouchListener(this);
	}

	/**
	 * 初始化屏幕尺寸
	 */
	private void initViewSize() {
		/* 取得屏幕分辨率大小 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		displayWidth = dm.widthPixels;
		displayHeight = dm.heightPixels;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Handle touch events here...
		ImageView view = (ImageView) v;

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 设置拖拉模式
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;
		// 设置多点触摸模式
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		// 若为DRAG模式，则点击移动图片
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				// 设置位移
				matrix.postTranslate(event.getX() - start.x, event.getX()
						- start.x);
			}
			// 若为ZOOM模式，则多点触摸缩放
			else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					// 设置缩放比例和图片中点位置
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		// Perform the transformation
		view.setImageMatrix(matrix);

		return true; // indicate event was handled
	}

	/*
	 * private void continueShow() { new Thread() { public void run() { try {
	 * Thread.sleep(3000); isContinue = true; mHandler.sendEmptyMessage(1); }
	 * catch (Exception e) { System.out.println(e.toString()); } } }.start(); }
	 * 
	 * private void goBack() { new Thread() { public void run() { try {
	 * Thread.sleep(3000); isContinue = false; mHandler.sendEmptyMessage(0); }
	 * catch (Exception e) { System.out.println(e.toString()); } } }.start(); }
	 * 
	 * 
	 * private Handler mHandler = new Handler() { public void
	 * handleMessage(Message msg) { if (msg.what == 1) { if (isContinue)
	 * mHandler.sendEmptyMessageDelayed(1, 100); } else if (msg.what == 0) { if
	 * (!isContinue){ mHandler.sendEmptyMessageDelayed(0, 100); finish(); }
	 * 
	 * } } };
	 */

	// 计算移动距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算中点位置
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 按钮点击缩小函数
	private void small() {

		int bmpWidth = mBitmap.getWidth();
		int bmpHeight = mBitmap.getHeight();

		scaleWidth = (float) (scaleWidth * ZOOM_OUT_SCALE);
		scaleHeight = (float) (scaleHeight * ZOOM_OUT_SCALE);

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		zoomedBMP = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		imageView.setImageBitmap(zoomedBMP);
		imageView.setOnTouchListener(this);

		/* 因为图片放到最大时放大按钮会disable，所以在缩小时把它重设为enable */
		zoomControls.setIsZoomInEnabled(true);
	}

	// 按钮点击放大函数
	private void enlarge() {

		int bmpWidth = mBitmap.getWidth();
		int bmpHeight = mBitmap.getHeight();

		scaleWidth = (float) (scaleWidth * ZOOM_IN_SCALE);
		scaleHeight = (float) (scaleHeight * ZOOM_IN_SCALE);

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		zoomedBMP = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		imageView.setImageBitmap(zoomedBMP);
		imageView.setOnTouchListener(this);

		/* 如果再放大会超过屏幕大小，就把Button disable */
		if (scaleWidth * ZOOM_IN_SCALE * bmpWidth > bmpWidth * 3
				|| scaleHeight * ZOOM_IN_SCALE * bmpHeight > bmpWidth * 3
				|| scaleWidth * ZOOM_IN_SCALE * bmpWidth > displayWidth * 5
				|| scaleHeight * ZOOM_IN_SCALE * bmpHeight > displayHeight * 5) {
			zoomControls.setIsZoomInEnabled(false);
		} else {
			zoomControls.setIsZoomInEnabled(true);
		}
	}
}