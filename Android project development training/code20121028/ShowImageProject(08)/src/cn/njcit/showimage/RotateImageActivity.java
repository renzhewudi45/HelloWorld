package cn.njcit.showimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import cn.njcit.showimage.util.BitmapUtil;
import cn.njcit.showimage.util.Util;

public class RotateImageActivity extends Activity implements OnClickListener {

	private ImageView imageView;
	private Bitmap originalBitmap, dstBmp;
	private String imagePath;
	private Button backButton;
	private Button saveButton;
	private Spinner spinner;
	private static final String[] type = { "请选择", " 左旋转", "右旋转", " 水平翻转",
			" 垂直翻转" };
	private ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.rotatepicture_layout);

		initView();
		initImageView();
	}

	private void setFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		Window myWindow = this.getWindow();
		myWindow.setFlags(flag, flag);
	}

	private void initView() {
		backButton = (Button) findViewById(R.id.back);
		saveButton = (Button) findViewById(R.id.ok);
		spinner = (Spinner) findViewById(R.id.spinner);

		backButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, type);
		// adapter.setDropDownViewResource(R.layout.myspinner_dropdown);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		/* 将ArrayAdapter 添加Spinner 对象中 */
		spinner.setAdapter(adapter);
		/* 下拉菜单弹出的内容选项被选中事件处理 */
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				doRotate(arg2);
				arg0.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		/* 下拉菜单弹出的内容选项触屏事件处理 */
		spinner.setOnTouchListener(new Spinner.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.setVisibility(View.INVISIBLE);
				return false;
			}
		});

		/* 下拉菜单弹出的内容选项焦点改变事件处理 */
		spinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			}
		});

	}

	/**
	 * 初始化图片控件
	 */
	private void initImageView() {
		imageView = (ImageView) findViewById(R.id.imageView);
		Intent intent = this.getIntent();
		imagePath = intent.getExtras().getString("imagePath");
		originalBitmap = BitmapUtil.getResizeBitmap(imagePath);
		imageView.setImageBitmap(originalBitmap);
	}

	private void doRotate(int id) {
		switch (id) {
		case 1:
			rotateLeft();
			break;
		case 2:
			rotateRight();
			break;
		case 3:
			rotateHorizontal();
			break;
		case 4:
			rotateVertical();
			break;
		default:
			break;

		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.back) {
			finish();
			return;
		}

		if (v.getId() == R.id.ok) {
			Util.showSaveDialog(RotateImageActivity.this, dstBmp, imagePath);
			return;
		}

	}

	private void rotateLeft() {
		Matrix matrix = new Matrix();
		// 缩放
		matrix.postScale(2f, 2f);
		// 旋转角度
		matrix.postRotate(-90);
		// 修改完后再生成图片
		dstBmp = Bitmap.createBitmap(originalBitmap, 0, 0,
				originalBitmap.getWidth(), originalBitmap.getHeight(), matrix,
				true);
		imageView.setImageBitmap(dstBmp);
	}

	private void rotateRight() {
		Matrix matrix = new Matrix();
		// 缩放
		matrix.postScale(2f, 2f);
		// 旋转角度
		matrix.postRotate(90);
		// 修改完后再生成图片
		dstBmp = Bitmap.createBitmap(originalBitmap, 0, 0,
				originalBitmap.getWidth(), originalBitmap.getHeight(), matrix,
				true);
		imageView.setImageBitmap(dstBmp);
	}

	private void rotateHorizontal() {
		int flag = 0;
		dstBmp = BitmapUtil.reverseBitmap(originalBitmap, flag);
		imageView.setImageBitmap(dstBmp);
	}

	private void rotateVertical() {
		int flag = 1;
		dstBmp = BitmapUtil.reverseBitmap(originalBitmap, flag);
		imageView.setImageBitmap(dstBmp);
	}

}