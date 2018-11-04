package cn.njcit.showimage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

@SuppressLint("HandlerLeak")
public class SplashScreenActivity extends Activity {

	private static final int STOPSPLASH = 1;
	private static final int SPLASHTIME = 3000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);

		// ∫· ˙∆¡«–ªª∑¿÷π÷ÿ∆Ù
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		

		Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}

	private Handler splashHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STOPSPLASH:
				startActivity(new Intent(getApplicationContext(),
						MainActivity.class));
				finish();
				break;
			}
			super.handleMessage(msg);
		}
	};

}