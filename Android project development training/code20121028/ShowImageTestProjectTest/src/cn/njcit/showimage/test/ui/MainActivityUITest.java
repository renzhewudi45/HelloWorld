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
	 * ����Ŀ�ģ�Activity��ablumsListView�Ƿ��ܳɹ����ء�
	 */
	@MediumTest
	public void testPreconditions() {
		activity = startActivity(mStartActivityIntent, null, null);
		ablumsListView = (ListView) activity.findViewById(android.R.id.list);
		assertNotNull(getActivity());
		assertNotNull(ablumsListView);
	}

	/*
	 * ����Ŀ�ģ�ablumsListView�������Ƿ�����ɹ����Ƿ��ܹ���Ӧ����¼���
	 */
	@MediumTest
	public void ablumsListViewTest() {
		assertTrue(ablumsListView.getOnItemSelectedListener() != null);
		ListAdapter adapter = ablumsListView.getAdapter();
		assertEquals(adapter.getCount(), 6);
	}

	/*
	 * ����Ŀ�ģ�MainActivity��������
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
