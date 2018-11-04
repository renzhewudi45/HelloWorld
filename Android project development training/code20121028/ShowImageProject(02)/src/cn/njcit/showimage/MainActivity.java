package cn.njcit.showimage;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import cn.njcit.showimage.adapter.ListViewAdapter;
import cn.njcit.showimage.bean.Albums;
import cn.njcit.showimage.meta.MetaData;
import cn.njcit.showimage.util.BitmapUtil;

public class MainActivity extends ListActivity {
	
	private ListViewAdapter listViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		initData();
		listViewAdapter = new ListViewAdapter(getApplicationContext(),
				MetaData.albums);
		listViewAdapter.notifyDataSetChanged();
		setListAdapter(listViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_layout, menu);
        return true;
    }
    
    private void initData(){
    	MetaData.albums.clear();
    	
    	Albums info = new Albums();
    	info.displayName = "pic";
		info.picturecount = "5";
		info.path = "/mnt/sdcard/pic";
		info.icon = BitmapUtil.getOptionBitmap("/mnt/sdcard/pic/1.jpg");
		
		MetaData.albums.add(info);	
    	
    }

    
}
