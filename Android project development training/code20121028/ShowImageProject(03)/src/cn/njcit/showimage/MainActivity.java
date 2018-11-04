package cn.njcit.showimage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import cn.njcit.showimage.adapter.ListViewAdapter;
import cn.njcit.showimage.bean.Albums;
import cn.njcit.showimage.meta.MetaData;
import cn.njcit.showimage.util.BitmapUtil;

public class MainActivity extends ListActivity implements OnClickListener {

	private ImageButton infoButton;
	private ImageButton cameraButton;
	private ListViewAdapter listViewAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		initData();
		initTitle();
		listViewAdapter = new ListViewAdapter(getApplicationContext(),
				MetaData.albums);
		listViewAdapter.notifyDataSetChanged();
		setListAdapter(listViewAdapter);
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
			// settings();
			break;
		default:
			break;
		}

		return true;
	}

	private void initData() {
		MetaData.albums.clear();

		Albums info = new Albums();
		info.displayName = "pic";
		info.picturecount = "5";
		info.path = "/mnt/sdcard/pic";
		info.icon = BitmapUtil.getOptionBitmap("/mnt/sdcard/pic/1.jpg");

		MetaData.albums.add(info);

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



	

}
