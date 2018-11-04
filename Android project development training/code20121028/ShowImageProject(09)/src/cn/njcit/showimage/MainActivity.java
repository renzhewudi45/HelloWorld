package cn.njcit.showimage;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.njcit.showimage.adapter.ListViewAdapter;
import cn.njcit.showimage.meta.MetaData;
import cn.njcit.showimage.util.AppUpdate;
import cn.njcit.showimage.util.Util;

public class MainActivity extends ListActivity implements OnItemClickListener,
		OnClickListener {

	private ImageButton infoButton;
	private ImageButton cameraButton;
	private static final String SETTING_INFOS = "cn.njcit.showimage_preferences";
	private ListViewAdapter listViewAdapter;
	private ScanAlbumsTask scanAlbumsTask;
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	private File mCurrentPhotoFile;// ��������յõ���ͼƬ
	private static final int CAMERA_WITH_DATA = 3023;
	private static final String SHORTCUT_INFOS = "shortCut_infos";
	private static boolean isAddShortCut = false;
	private static final String ACTION_INSTALL = "com.android.launcher.action.INSTALL_SHORTCUT";
	private ProgressDialog progressDialog;
	private static final int DIALOG_SCANNING_MEDIA = 1;
	private static final int STARTED = 1000;
	private static final int FINISHED = 2000;
	private ScanImagesReceiver scanImagesReceiver = new ScanImagesReceiver();
	private SDCardBroadCastReceiver sdcardListenerReceiver = new SDCardBroadCastReceiver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		getPreferencesDataFromSETTING_INFOS();
		getPreferencesDataFromSHORTCUT_INFOS();
		initAlbumsList();
		initTitle();

		getListView().setOnItemClickListener(this);

		// ����Զ����£�ÿ��1�š�15��Ϊ��������
		if (MetaData.isAutoUpdate && AppUpdate.isUpdateDay()) {
			AppUpdate.checkForUpdate(MainActivity.this, CONNECTIVITY_SERVICE);
		}
	}

	private void initAlbumsList() {

		if (scanAlbumsTask != null
				&& scanAlbumsTask.getStatus() == ScanAlbumsTask.Status.RUNNING) {
			scanAlbumsTask.cancel(true);
		}
		scanAlbumsTask = new ScanAlbumsTask();
		scanAlbumsTask.execute(Environment.getExternalStorageDirectory()
				.getPath());
	}

	/**
	 * �첽�������
	 */
	class ScanAlbumsTask extends AsyncTask<String, Integer, Integer> {

		public ScanAlbumsTask() {
			MetaData.albums.clear();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			if (params == null || params[0] == null || "".equals(params[0])) {
				return 0;
			}

			Util.getThumbnailsPhotosInfo(MainActivity.this, params[0]);

			return MetaData.albums.size();
		}

		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			if (MetaData.albums.size() == 0) {
				isExit();
			}

			listViewAdapter = new ListViewAdapter(getApplicationContext(),
					MetaData.albums);
			listViewAdapter.notifyDataSetChanged();
			setListAdapter(listViewAdapter);

			// ��ʼ��appwidgetͼƬ��Ϣ
			initWidgetImagesPath();
		}

	}

	private void initWidgetImagesPath() {
		MetaData.appWidgetPictures.clear();
		new Timer().schedule(new ImageSearchTimerTask(), 1);
	}

	class ImageSearchTimerTask extends TimerTask {

		@Override
		public void run() {
			Util.getWidgetImagesList(MetaData.appWidgetPath);
		}

	};

	private void isExit() {
		new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.logo)
				.setTitle("��ʾ")
				.setMessage("δ����ͼ���ļ����Ƿ��˳�����")
				.setPositiveButton("�˳�", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), BrowseImageActivity.class);
		intent.putExtra("id", position);
		this.startActivity(intent);
	}

	private void getPreferencesDataFromSETTING_INFOS() {
		final SharedPreferences settingData = getSharedPreferences(
				SETTING_INFOS, 0);
		MetaData.isCleanHistory = settingData.getBoolean("cleanHistory", false);
		MetaData.isAutoUpdate = settingData.getBoolean("autoUpdate", false);
		MetaData.appWidgetPath = settingData.getString("listPreference",
				Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, Menu.FIRST, 0, "ɨ��SD��").setIcon(R.drawable.scan);
		menu.add(Menu.NONE, Menu.FIRST + 2, 0, "����").setIcon(R.drawable.update);
		menu.add(Menu.NONE, Menu.FIRST + 3, 0, "����")
				.setIcon(R.drawable.setting);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case Menu.FIRST:
			onCreateMediaScanner();
			break;
		case Menu.FIRST + 2:
			updateApk();
			break;
		case Menu.FIRST + 3:
			settings();
			break;
		default:
			break;
		}

		return true;
	}

	private void initTitle() {
		infoButton = (ImageButton) findViewById(R.id.title_logo);
		cameraButton = (ImageButton) findViewById(R.id.title_camera);
		infoButton.setOnClickListener(this);
		cameraButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_logo:
			showAbout();
			break;
		case R.id.title_camera:
			openCamera();
			break;
		default:
			break;
		}

	}

	private void showAbout() {
		AlertDialog.Builder aboutDialog = new AlertDialog.Builder(
				MainActivity.this);
		InputStream ips = MainActivity.this.getResources().openRawResource(
				R.raw.readme);
		DataInputStream dis = new DataInputStream(ips);
		try {
			byte[] bytes = new byte[dis.available()];
			String str = "";
			while (ips.read(bytes) != -1)
				str = str + new String(bytes, "GBK");
			aboutDialog.setIcon(R.drawable.logo).setTitle("����").setMessage(str);
			aboutDialog.setPositiveButton("ȷ��", null);
			aboutDialog.create().show();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
				ips.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * �����������
	 */
	private void openCamera() {
		// ������Ӳ���豸
		if (!Util.checkCameraHardware(this)) {
			Toast.makeText(MainActivity.this, "δ��������豸��", Toast.LENGTH_LONG)
					.show();
			return;
		}

		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// �����յ���Ƭ�ļ�����
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "δ������Ƭ��", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * �õ�ǰʱ���ȡ�õ�ͼƬ����
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case CAMERA_WITH_DATA: {// ��������򷵻ص�,�ٴε���ͼƬ��������ȥ�޼�ͼƬ
			openCameraImage(mCurrentPhotoFile);
			break;
		}
		}

	}

	private void openCameraImage(final File f) {

		new AlertDialog.Builder(MainActivity.this)
				.setTitle("��ʾ")
				.setIcon(R.drawable.logo)
				.setMessage("�Ƿ�������Ƭ��")
				.setPositiveButton("��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this,
								ZoomImageActivity.class);
						final String imagePath = f.getAbsolutePath();// MetaData.cameraImagePath;
						intent.putExtra("imagePath", imagePath);
						startActivity(intent);
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();

	}

	private void onCreateMediaScanner() {
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addDataScheme("file");
		registerReceiver(scanImagesReceiver, intentFilter);
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	private class ScanImagesReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
				scanHandler.sendEmptyMessage(STARTED);
			}
			if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
				scanHandler.sendEmptyMessage(FINISHED);
			}
		}
	}

	private Handler scanHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case STARTED:
				showDialog(DIALOG_SCANNING_MEDIA);
				break;
			case FINISHED:
				listViewAdapter.notifyDataSetChanged();
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				unregisterReceiver(scanImagesReceiver);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(final int id) {
		if (id == DIALOG_SCANNING_MEDIA) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false); // ��ֹ�ͻ��������ء���
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("����ɨ��SD�������Ժ�...");
		}

		return progressDialog;
	}

	private void updateApk() {
		AppUpdate.checkForUpdate(MainActivity.this, CONNECTIVITY_SERVICE);
	}

	private void settings() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingActivity.class);
		String imagePath = MetaData.cameraImagePath;
		intent.putExtra("imagePath", imagePath);
		startActivity(intent);
	}

	private void getPreferencesDataFromSHORTCUT_INFOS() {
		final SharedPreferences shortCutData = getSharedPreferences(
				SHORTCUT_INFOS, 0);
		isAddShortCut = shortCutData.getBoolean("isAdd", true);

		if (isAddShortCut) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("��ʾ")
					.setIcon(R.drawable.logo)
					.setMessage("�Ƿ���������ӿ�ݷ�ʽ��")
					.setPositiveButton("���",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// ������������
									shortCutData.edit()
											.putBoolean("isAdd", false)
											.commit();
									addShortcutToDesktop(MainActivity.this);
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}

	}

	/* �������ͼ�� */
	private void addShortcutToDesktop(Context context) {
		Intent shortcut = new Intent(ACTION_INSTALL);

		String pakageName = context.getPackageName();
		String className = "SplashScreenActivity";
		String appClass = pakageName + "." + className;
		Resources res = context.getResources();
		String label = "ͼ��";
		Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.logo);
		Drawable drawable = new BitmapDrawable(bmp);
		BitmapDrawable iconBitmapDrawabel = (BitmapDrawable) drawable;

		// ��ȡӦ�û�����Ϣ
		PackageManager packageManager = context.getPackageManager();

		try {
			@SuppressWarnings("unused")
			ApplicationInfo appInfo = packageManager.getApplicationInfo(
					pakageName, PackageManager.GET_META_DATA
							| PackageManager.GET_ACTIVITIES);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// ��������
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				iconBitmapDrawabel.getBitmap());

		// �Ƿ������ظ�����
		shortcut.putExtra("duplicate", false);

		// ������������
		ComponentName comp = new ComponentName(pakageName, appClass);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				Intent.ACTION_MAIN).setComponent(comp));

		context.sendBroadcast(shortcut);
		Toast.makeText(context, "��ݷ�ʽ����ӣ�", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sdcardListenerReceiver != null) {
			unregisterReceiver(sdcardListenerReceiver);
			sdcardListenerReceiver = null;
		}
	}

	@Override
	protected void onResume() {

		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addDataScheme("file");

		registerReceiver(sdcardListenerReceiver, intentFilter);		

		super.onResume();
	}
	
	public class SDCardBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)
					|| Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)
					|| Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
				initAlbumsList();

			} else if (Intent.ACTION_MEDIA_REMOVED.equals(action)
					|| Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
					|| Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
				Toast.makeText(MainActivity.this, "δ����SD����", Toast.LENGTH_LONG)
						.show();

			}

		}

	}

}
