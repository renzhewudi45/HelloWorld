package cn.njcit.showimage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.njcit.showimage.util.BitmapUtil;
import cn.njcit.showimage.util.Util;

public class EffectsImageActivity extends Activity implements OnClickListener {

	private ImageView imageView;
	private Bitmap originalBitmap, dstBmp;
	private String imagePath;
	private Button backButton;
	private Button saveButton;
	private int effectType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.effectpicture_layout);

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

		backButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);

	}

	/**
	 * ³õÊ¼»¯Í¼Æ¬¿Ø¼þ
	 */
	private void initImageView() {
		imageView = (ImageView) findViewById(R.id.imageView);
		Intent intent = this.getIntent();
		imagePath = intent.getExtras().getString("imagePath");
		originalBitmap = BitmapUtil.getResizeBitmap(imagePath);
		imageView.setImageBitmap(originalBitmap);
		effectType = intent.getExtras().getInt("type");
		doEffect(effectType);

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow();
			}

		});
	}

	public void showPopupWindow() {
		Context mContext = EffectsImageActivity.this;
		LayoutInflater mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View effect_popwindow = mLayoutInflater.inflate(
				R.layout.effect_popwindow, null);
		final PopupWindow mPopupWindow = new PopupWindow(effect_popwindow,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.showAtLocation(findViewById(R.id.linearLayout_XUANZHUAN),
				Gravity.CENTER, 0, 0);

		RadioGroup radioGroup = (RadioGroup) effect_popwindow
				.findViewById(R.id.radioGroup);
		final RadioButton radioButton1 = (RadioButton) effect_popwindow
				.findViewById(R.id.effect_1);
		final RadioButton radioButton2 = (RadioButton) effect_popwindow
				.findViewById(R.id.effect_2);
		final RadioButton radioButton3 = (RadioButton) effect_popwindow
				.findViewById(R.id.effect_3);
		final RadioButton radioButton4 = (RadioButton) effect_popwindow
				.findViewById(R.id.effect_4);
		final RadioButton radioButton5 = (RadioButton) effect_popwindow
				.findViewById(R.id.effect_5);
		final RadioButton radioButton6 = (RadioButton) effect_popwindow
				.findViewById(R.id.effect_6);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int radioButtonIndex = 0;

				if (radioButton1.isChecked()) {
					radioButtonIndex = 0;
				}

				if (radioButton2.isChecked()) {
					radioButtonIndex = 1;
				}

				if (radioButton3.isChecked()) {
					radioButtonIndex = 2;
				}

				if (radioButton4.isChecked()) {
					radioButtonIndex = 3;
				}

				if (radioButton5.isChecked()) {
					radioButtonIndex = 4;
				}

				if (radioButton6.isChecked()) {
					radioButtonIndex = 5;
				}

				doEffect(radioButtonIndex);
				mPopupWindow.dismiss();
			}

		});
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.back) {
			finish();
			return;
		}

		if (v.getId() == R.id.ok) {
			Util.showSaveDialog(EffectsImageActivity.this, dstBmp, imagePath);
			return;
		}

	}

	private void doEffect(int type) {
		switch (type) {
		case 0:
			dstBmp = BitmapUtil.createNegativeImage(originalBitmap);
			break;
		case 1:
			dstBmp = BitmapUtil.createNostalgiaImage(originalBitmap);
			break;
		case 2:
			dstBmp = BitmapUtil.createReliefImage(originalBitmap);
			break;
		case 3:
			dstBmp = BitmapUtil.createSharpenImage(originalBitmap);
			break;
		case 4:
			dstBmp = BitmapUtil.createIlluminationImage(originalBitmap);
			break;
		case 5:
			dstBmp = BitmapUtil.createBlurImage(originalBitmap);
			break;
		default:
			break;
		}

		imageView.setImageBitmap(dstBmp);
	}

}