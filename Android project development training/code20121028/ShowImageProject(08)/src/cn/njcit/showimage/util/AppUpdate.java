package cn.njcit.showimage.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import cn.njcit.showimage.R;
import cn.njcit.showimage.meta.MetaData;

public class AppUpdate {

	/* ������� */
	private static ConnectivityManager manager;
	private static ProgressDialog pBar;
	private static int percent;
	private static long length;
	private static long count;
	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				percent = (int) (count * 100 / length);
				pBar.setMessage("���Ժ�... (" + percent + "%)");
				break;
			}

		}
	};

	/**
	 * �Ƿ�����
	 */
	public static void checkForUpdate(Context context,
			String CONNECTIVITY_SERVICE) {
		// ��������Ƿ����
		if (!Util.checkNet(context, CONNECTIVITY_SERVICE)) {
			showNetSettingDialog(context);
			return;
		}

		// ��ȡ��ǰ����汾��
		MetaData.currentVersionCode = getVerCode(context);
		MetaData.currentVersionName = getVerName(context);

		if (getServerVerCode()) {
			if (MetaData.newVerCode > MetaData.currentVersionCode) {
				// �����µİ汾
				updateApk(context);
			} else {
				// ɾ���ɵ���ʷ��װ�ļ�
				deleteOldApk();
			}
		}
	}

	/**
	 * �Ƿ���ת���������ó���
	 */
	private static void showNetSettingDialog(final Context context) {
		new AlertDialog.Builder(context)
				.setTitle("��ʾ")
				.setIcon(R.drawable.logo)
				.setMessage("��Ӧ����Ҫ����֧�֣��Ƿ��������磿")
				.setPositiveButton("����", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								"android.settings.WIRELESS_SETTINGS");
						context.startActivity(intent);
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	/**
	 * �����µİ汾
	 */
	private static void updateApk(final Context context) {
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾���ǣ�");
		sb.append(MetaData.currentVersionName);
		sb.append(" �����ǣ�");
		sb.append(MetaData.currentVersionCode);
		sb.append("\n�µİ汾���ǣ�");
		sb.append(MetaData.newVerName);
		sb.append("�����ǣ�");
		sb.append(MetaData.newVerCode);
		sb.append("\n�Ƿ�������");

		Dialog dialog = new AlertDialog.Builder(context).setTitle("������ʾ")
				.setMessage(sb.toString())
				// ��������
				.setPositiveButton("����", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						pBar = new ProgressDialog(context);
						pBar.setTitle("�����µ�apk");
						pBar.setMessage("��������Ӧ�ó������Ժ�...");
						pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						downFile(MetaData.UPDATE_APKSERVER, context);

					}

				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}

	/**
	 * ɾ���İ汾
	 */
	private static void deleteOldApk() {
		File file = new File(Environment.getExternalStorageDirectory(),
				"TuShow.apk");
		if (file.exists()) {
			// ������Ҫ��ʱ�����֮ǰ�������ļ��������˷��û��ռ�
			file.delete();
		}
	}

	/**
	 * �����µİ汾
	 */
	private static void downFile(final String url, final Context context) {
		pBar.show();

		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {

						File file = new File(
								Environment.getExternalStorageDirectory(),
								"ShowImageProject.apk");
						fileOutputStream = new FileOutputStream(file);

						byte[] buf = new byte[1024];
						int ch = -1;
						count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;

							sentMassage(0);
						}

					}

					fileOutputStream.flush();

					if (fileOutputStream != null) {
						fileOutputStream.close();
					}

					downComplet(context);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	/**
	 * �������ؽ���
	 */
	private static void sentMassage(int flag) {
		Message msg = new Message();
		msg.what = flag;
		handler.sendMessage(msg);
	}

	/**
	 * �������
	 */
	private static void downComplet(final Context context) {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				installApk(context);
			}
		});

	}

	/**
	 * ��װ�İ汾
	 */
	private static void installApk(final Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), "TuShow.apk")),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * ��ȡ����Ӧ�ó���汾��
	 * 
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
		int verCode = 1;

		try {
			verCode = context.getPackageManager().getPackageInfo(
					"cn.njcit.showimage", 0).versionCode;
		} catch (NameNotFoundException e) {
		}

		return verCode;
	}

	/**
	 * ��ȡ����Ӧ�ó���汾��
	 * 
	 * @param context
	 * @return
	 */
	public static String getVerName(Context context) {
		String verName = "1.0";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"cn.njcit.showimage", 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return verName;
	}

	/**
	 * ��ȡ������Ӧ�ó���汾��
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getServerVerCode() {
		try {
			String verjson = getContent(MetaData.UPDATE_SERVER);
			JSONArray array = new JSONArray(verjson);
			if (array.length() > 0) {
				JSONObject obj = array.getJSONObject(0);
				try {
					MetaData.newVerCode = Integer.parseInt(obj
							.getString("verCode"));
					MetaData.newVerName = obj.getString("verName");
				} catch (Exception e) {
					MetaData.newVerCode = 1;
					MetaData.newVerName = "1.0";
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * ��ȡ����˰汾json����
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private static String getContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		HttpClient client = new DefaultHttpClient(httpParams);

		// �����������Ϊwap��ʱ�򣬾���Ҫ����й��ƶ�����
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
			if (networkInfo.getExtraInfo().toLowerCase().indexOf("wap") != -1) {// wap
				HttpHost proxy = new HttpHost("10.0.0.172", 80);
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
						proxy);
			}
		}

		HttpPost hp = new HttpPost(url);
		hp.setHeader("Charset", "UTF-8");
		hp.setHeader("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse response = null;
		response = client.execute(hp);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"), 8192);

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		}

		return sb.toString();

	}

	/**
	 * ����Ƿ�Ϊ��������
	 */
	public static boolean isUpdateDay() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		if (day == 1 || day == 15) {
			return true;
		} else {
			return false;
		}
	}

}
