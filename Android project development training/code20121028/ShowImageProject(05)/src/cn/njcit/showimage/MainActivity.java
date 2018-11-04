package cn.njcit.showimage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import cn.njcit.showimage.adapter.ListViewAdapter;
import cn.njcit.showimage.meta.MetaData;
import cn.njcit.showimage.util.Util;

public class MainActivity extends ListActivity implements OnItemClickListener, OnClickListener {

	private ImageButton infoButton;
	private ImageButton cameraButton;
	private static final String SETTING_INFOS = "cn.njcit.showimage_preferences";
	private ListViewAdapter listViewAdapter;
	private ScanAlbumsTask scanAlbumsTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		getPreferencesDataFromSETTING_INFOS();
		initAlbumsList();
		initTitle();
		
		getListView().setOnItemClickListener(this);
	}
	
	private void initAlbumsList() {
		
		if (scanAlbumsTask != null
				&& scanAlbumsTask.getStatus() == ScanAlbumsTask.Status.RUNNING) {
			scanAlbumsTask.cancel(true);
		}
		scanAlbumsTask = new ScanAlbumsTask();
		scanAlbumsTask.execute(Environment	
				.getExternalStorageDirectory().getPath());
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
			// updateApk();
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
			// openCamera();
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
			aboutDialog.setIcon(R.drawable.logo).setTitle("关于")
					.setMessage(str);
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


	private void settings() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingActivity.class);
		String imagePath = MetaData.cameraImagePath;
		intent.putExtra("imagePath", imagePath);
		startActivity(intent);
	}

	

}
