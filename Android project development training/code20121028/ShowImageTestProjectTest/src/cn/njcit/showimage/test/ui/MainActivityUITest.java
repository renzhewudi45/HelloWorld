package cn.njcit.showimage.test.ui;


import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ListAdapter;
import android.widget.ListView;
import cn.njcit.showimage.MainActivity;

public class MainActivityUITest extends ActivityUnitTestCase<MainActivity> {

	private Intent mStartActivityIntent;
	private ListView ablumsListView;
	private MainActivity activity;

	public MainActivityUITest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mStartActivityIntent = new Intent(Intent.ACTION_MAIN);
	}

	/*
	 * 测试目的：Activity、ablumsListView是否能成功加载。
	 */
	@MediumTest
	public void testPreconditions() {
		activity = startActivity(mStartActivityIntent, null, null);
		ablumsListView = (ListView) activity.findViewById(android.R.id.list);
		assertNotNull(getActivity());
		assertNotNull(ablumsListView);
	}

	/*
	 * 测试目的：ablumsListView适配器是否适配成功，是否能够响应点击事件。
	 */
	@MediumTest
	public void ablumsListViewTest() {
		assertTrue(ablumsListView.getOnItemSelectedListener() != null);
		ListAdapter adapter = ablumsListView.getAdapter();
		assertEquals(adapter.getCount(), 6);
	}

	/*
	 * 测试目的：MainActivity生命周期
	 */
	@MediumTest
	public void testLifeCycleActivity() {
		MainActivity activity = startActivity(mStartActivityIntent, null, null);
		getInstrumentation().callActivityOnStart(activity);
		getInstrumentation().callActivityOnResume(activity);
		getInstrumentation().callActivityOnPause(activity);
		getInstrumentation().callActivityOnStop(activity);
	}

}
