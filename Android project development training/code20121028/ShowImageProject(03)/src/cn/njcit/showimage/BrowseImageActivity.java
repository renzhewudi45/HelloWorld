package cn.njcit.showimage;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.njcit.showimage.adapter.GalleryAdapter;
import cn.njcit.showimage.adapter.GridViewAdapter;
import cn.njcit.showimage.adapter.ListDialogAdapter;
import cn.njcit.showimage.bean.ActionItem;
import cn.njcit.showimage.meta.MetaData;
import cn.njcit.showimage.util.BitmapUtil;
import cn.njcit.showimage.util.Util;
import cn.njcit.showimage.view.GalleryFlow;
import cn.njcit.showimage.view.QuickActionBar;

@SuppressLint("HandlerLeak")
public class BrowseImageActivity extends Activity implements OnClickListener,
		OnGestureListener {

	private ViewFlipper viewFlipper;
	private GridView gridView;
	private GalleryFlow gallery;
	private GestureDetector detector;
	private String[] testData = { "/mnt/sdcard/pic/1.jpg",
			"/mnt/sdcard/pic/2.jpg", "/mnt/sdcard/pic/3.jpg",
			"/mnt/sdcard/pic/4.jpg", "/mnt/sdcard/pic/5.jpg", };
	private int currentPosition = 0;
	private int showMode = 0;

	private String[] actionbar_array = { "��ʾ��ʽ", "ͼƬ����", "ͼƬ��Ч", "ͼƬ����", "ͼƬ����" };
	private String[] show_array = { "�������", "Gallery���" };
	private String[] operate_array = { "����ͼƬ", "����ͼƬ", "��תͼƬ", "ͼƬ��ɫ", "ɾ��ͼƬ" };
	private String[] effect_array = { "��ƬЧ��", "����Ч��", "����Ч��", "��Ч��", "����Ч��",
			"ģ��Ч��" };
	// private String[] info_array = { "��ƬЧ��"};
	private String[] share_array = { "��Ϊǽֽ", "���ŷ���", "�ϴ���������" };

	private int[] show_array_imgIds = { R.drawable.grid64, R.drawable.flow64 };
	private int[] operate_array_imgIds = { R.drawable.zoom, R.drawable.crop,
			R.drawable.rotate, R.drawable.paint, R.drawable.delete };
	private int[] effect_array_imgIds = { R.drawable.number1,
			R.drawable.number2, R.drawable.number3, R.drawable.number4,
			R.drawable.number5, R.drawable.number6 };
	private int[] share_array_imgIds = { R.drawable.wallpaper, R.drawable.mms,
			R.drawable.renren };

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

		initViewFlipper();

	}

	private void initViewFlipper() {

		showMode = 0;

		/*
		 * if (MetaData.pictures.size() == 0) { finish(); return; }
		 */

		// ��ֹMediaStore.Images.Media.EXTERNAL_CONTENT_URI�����쳣
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
		// �ļ���ֻ��һ��ͼƬ
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

	private void showActionBar(View view) {
		if (view instanceof ViewFlipper) {
			ActionItem showMode = new ActionItem(getResources().getDrawable(
					R.drawable.gallery), actionbar_array[0], this);
			ActionItem operate = new ActionItem(getResources().getDrawable(
					R.drawable.operate), actionbar_array[1], this);
			ActionItem effects = new ActionItem(getResources().getDrawable(
					R.drawable.effects), actionbar_array[2], this);
			ActionItem infoPic = new ActionItem(getResources().getDrawable(
					R.drawable.info), actionbar_array[3], this);
			ActionItem setAs = new ActionItem(getResources().getDrawable(
					R.drawable.share), actionbar_array[4], this);

			QuickActionBar qaBar = new QuickActionBar(view);
			qaBar.setEnableActionsLayoutAnim(true);

			qaBar.addActionItem(showMode);
			qaBar.addActionItem(operate);
			qaBar.addActionItem(effects);
			qaBar.addActionItem(infoPic);
			qaBar.addActionItem(setAs);

			qaBar.show();
		} else if (view instanceof LinearLayout) {
			LinearLayout actionsLayout = (LinearLayout) view;
			QuickActionBar bar = (QuickActionBar) actionsLayout.getTag();
			bar.dismissQuickActionBar();

			TextView txtView = (TextView) actionsLayout
					.findViewById(R.id.qa_actionItem_name);
			String actionName = txtView.getText().toString();

			// ��ʾͼƬ��Ϣ
			if (actionName.equals(actionbar_array[3])) {
				showImageDetail();
				return;
			}

			for (int id = 0; id < actionbar_array.length; id++) {

				if (actionName.equals(actionbar_array[id])) {
					showDialog(id);
					break;
				}
			}

		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Dialog dialog = null;
		BaseAdapter adapter = null;
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.logo);
		builder.setTitle(actionbar_array[id]);

		switch (id) {
		case 0:
			adapter = new ListDialogAdapter(BrowseImageActivity.this,
					show_array_imgIds, show_array);
			break;
		case 1:
			adapter = new ListDialogAdapter(BrowseImageActivity.this,
					operate_array_imgIds, operate_array);
			break;
		case 2:
			adapter = new ListDialogAdapter(BrowseImageActivity.this,
					effect_array_imgIds, effect_array);
			break;
		case 4:
			adapter = new ListDialogAdapter(BrowseImageActivity.this,
					share_array_imgIds, share_array);
			break;
		default:
			break;
		}

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				actionItemOperate(id, which);
			}
		};

		builder.setAdapter(adapter, listener);
		dialog = builder.create();

		return dialog;
	}

	private void actionItemOperate(int id, int position) {
		switch (id) {
		case 0:
			showMode(position);
			break;
		case 1:
			// imageOperate(position);
			break;
		case 2:
			// imageEffect(position);
			break;
		case 4:
			// imageShare(position);
			break;
		default:
			break;
		}
	}

	private void showMode(int position) {

		// �������
		if (position == 0) {
			initGridView();
			return;
		}

		// Gallery���
		if (position == 1) {
			initGallery();
			return;
		}
	}

	private void showImageDetail() {
		// String imagePath = MetaData.pictures.get(currentPosition);
		// ��������
		String imagePath = testData[0];

		final File file = new File(imagePath);
		String[] items = { "ͼƬ���ƣ�" + file.getName(),
				"ͼƬ·����" + file.getParentFile().getAbsolutePath(),
				"ͼƬ��С��" + Util.getFileSize(Util.getFileSize(file)),
				"�������ڣ�" + Util.getFileDateTime(file) };

		AlertDialog dialog = new AlertDialog.Builder(BrowseImageActivity.this)
				.setIcon(R.drawable.logo).setTitle("ͼƬ��Ϣ")
				.setItems(items, null).create();
		dialog.show();
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
		showActionBar(viewFlipper);
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

	@Override
	public void onClick(View v) {
		showActionBar(v);

	}

}
