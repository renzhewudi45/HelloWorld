package cn.njcit.showimage;

import java.io.File;

import cn.njcit.showimage.meta.MetaData;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.setting_preference);

		initListPreference();
	}

	private void initListPreference() {
		// getPreferenceManager返回首选项管理器对象
		@SuppressWarnings("deprecation")
		PreferenceManager manager = getPreferenceManager();
		// 根据android:key中指定的名称（相当于id）来获取首选项
		ListPreference listPreference = (ListPreference) manager
				.findPreference("listPreference");

		int count = MetaData.albums.size();
		CharSequence[] entries = new CharSequence[count];
		CharSequence[] entryValues = new CharSequence[count];

		for (int i = 0; i < count; i++) {
			File f = new File(MetaData.albums.get(i).path);
			String fName = f.getName();
			entries[i] = fName;
			entryValues[i] = MetaData.albums.get(i).path;
		}
		
		listPreference.setEntries(entries);
		listPreference.setEntryValues(entryValues);
		
		
	}
	
}
