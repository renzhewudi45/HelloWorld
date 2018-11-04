package cn.njcit.showimage;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
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
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private static final int CAMERA_WITH_DATA = 3023;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		getPreferencesDataFromSETTING_INFOS();
		initAlbumsList();
		initTitle();

		getListView().setOnItemClickListener(this);

		// 如果自动更新，每月1号、15号为更新日期
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
	 * 异步搜索相册
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
		}

	}

	private void isExit() {
		new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.logo)
				.setTitle("提示")
				.setMessage("未发现图像文件，是否退出程序？")
				.setPositiveButton("退出", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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

		menu.add(Menu.NONE, Menu.FIRST, 0, "扫描SD卡").setIcon(R.drawable.scan);
		menu.add(Menu.NONE, Menu.FIRST + 2, 0, "更新").setIcon(R.drawable.update);
		menu.add(Menu.NONE, Menu.FIRST + 3, 0, "设置")
				.setIcon(R.drawable.setting);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case Menu.FIRST:
			// onCreateMediaScanner();
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
			aboutDialog.setIcon(R.drawable.logo).setTitle("关于").setMessage(str);
			aboutDialog.setPositiveButton("确定", null);
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
	 * 启动相机功能
	 */
	private void openCamera() {
		// 检查相机硬件设备
		if (!Util.checkCameraHardware(this)) {
			Toast.makeText(MainActivity.this, "未发现相机设备！", Toast.LENGTH_LONG)
					.show();
			return;
		}

		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "未发现照片！", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 用当前时间给取得的图片命名
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
		case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			openCameraImage(mCurrentPhotoFile);
			break;
		}
		}

	}

	private void openCameraImage(final File f) {

		new AlertDialog.Builder(MainActivity.this)
				.setTitle("提示")
				.setIcon(R.drawable.logo)
				.setMessage("是否打开相机照片？")
				.setPositiveButton("打开", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this,
								ZoomImageActivity.class);
						final String imagePath = f.getAbsolutePath();// MetaData.cameraImagePath;
						intent.putExtra("imagePath", imagePath);
						startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();

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

}
