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
	private File mCurrentPhotoFile;// ��������յõ���ͼƬ
	private static final int CAMERA_WITH_DATA = 3023;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		getPreferencesDataFromSETTING_INFOS();
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
		}

	}

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
