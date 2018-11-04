package cn.njcit.showimage.service;

import java.io.File;

import cn.njcit.showimage.BrowseImageActivity;
import cn.njcit.showimage.util.Util;

import com.renren.api.connect.android.exception.RenrenException;
import com.renren.api.connect.android.photos.PhotoUploadRequestParam;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class UploadPictureService extends Service {
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	// 事件处理
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			synchronized (this) {
				try {
					switch (msg.arg1) {
					case 1: // 上传图片
						publishPhotoRenRen(msg);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onCreate() {
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);
		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("service", "onStart");
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.i("service", "onBind");
		return new MyBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("service", "onUnbind");
		return super.onUnbind(intent);
	}

	public class MyBinder extends Binder {
		public UploadPictureService getService() {
			return UploadPictureService.this;
		}
	}

	// change this interface.now,this will be more reasonable.
	public void publishPhoto(long aid, String picPath) throws RenrenException,
			Throwable {
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = 1;
		Bundle request = new Bundle();
		request.putString("picPath", picPath);
		request.putLong("aid", aid);
		// request.putSerializable("param",photoRequest);
		msg.setData(request);
		mServiceHandler.sendMessage(msg);
	}

	private void publishPhotoRenRen(Message msg) throws RenrenException,
			Throwable {
		long aid = msg.getData().getLong("aid");
		String picPath = msg.getData().getString("picPath");
		PhotoUploadRequestParam param;

		// 对大图片进行预处理
		File f = new File(picPath);

		// 大于2MB要压缩
		if (f.length() > 2097152) {
			Bitmap resizeBmp = null;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 10;
			resizeBmp = BitmapFactory.decodeFile(f.getPath(), opts);
			String extension = picPath.substring(picPath.lastIndexOf(".") + 1,
					picPath.length()).toLowerCase();
			Util.saveBitmapToSDCard(resizeBmp, "/sdcard/_tmp." + extension);
			param = new PhotoUploadRequestParam(new File("/sdcard/_tmp."
					+ extension));
		} else {
			param = new PhotoUploadRequestParam(new File(picPath));
		}

		if (aid != -1) {
			param.setAid(aid);
		}
		if (BrowseImageActivity.renren.publishPhoto(param) == null) {
			Toast.makeText(getApplicationContext(), "图片上传失败",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "图片上传成功",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy() {
		Log.i("service", "onDestroy");
		super.onDestroy();
	}
}
