package cn.njcit.showimage;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import cn.njcit.showimage.util.BitmapUtil;
import cn.njcit.showimage.util.Util;
import cn.njcit.showimage.view.ToningView;

public class ToningImageActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener {

	private ImageView imageView;
	private Bitmap mBitmap;
	private String imagePath;
	private Button backButton;
	private Button saveButton;
	private ToningView mToneLayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.toningpicture_layout);

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

		mToneLayer = new ToningView(this);
		((LinearLayout) findViewById(R.id.tone_view)).addView(mToneLayer
				.getParentView());

		ArrayList<SeekBar> seekBars = mToneLayer.getSeekBars();
		for (int i = 0, size = seekBars.size(); i < size; i++) {
			seekBars.get(i).setOnSeekBarChangeListener(this);
		}
	}

	/**
	 * ³õÊ¼»¯Í¼Æ¬¿Ø¼þ
	 */
	private void initImageView() {
		imageView = (ImageView) findViewById(R.id.imageView);
		Intent intent = this.getIntent();
		imagePath = intent.getExtras().getString("imagePath");
		mBitmap = BitmapUtil.getResizeBitmap(imagePath);
		imageView.setImageBitmap(mBitmap);
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.back){
			finish();
			return;
		}
		if(v.getId() == R.id.ok){
			Util.showSaveDialog(ToningImageActivity.this, mBitmap, imagePath);
			return;
		}		

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int flag = (Integer) seekBar.getTag();
		switch (flag) {
		case ToningView.FLAG_SATURATION:
			mToneLayer.setSaturation(progress);
			break;
		case ToningView.FLAG_LUM:
			mToneLayer.setLum(progress);
			break;
		case ToningView.FLAG_HUE:
			mToneLayer.setHue(progress);
			break;
		}

		imageView.setImageBitmap(mToneLayer.handleImage(mBitmap, flag));

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

}