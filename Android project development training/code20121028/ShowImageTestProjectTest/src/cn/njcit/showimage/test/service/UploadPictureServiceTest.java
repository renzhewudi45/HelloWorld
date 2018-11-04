package cn.njcit.showimage.test.service;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import cn.njcit.showimage.service.UploadPictureService;

public class UploadPictureServiceTest extends
		ServiceTestCase<UploadPictureService> {

	public UploadPictureServiceTest() {
		super(UploadPictureService.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@SmallTest
	public void testPreconditions() {
	}

	/**
	 * Test basic startup/shutdown of Service
	 */
	@SmallTest
	public void testStartable() {
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), UploadPictureService.class);
		startService(startIntent);
	}

	/**
	 * Test binding to service
	 */
	@MediumTest
	public void testBindable() {
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), UploadPictureService.class);
		IBinder service = bindService(startIntent);
	}

}
