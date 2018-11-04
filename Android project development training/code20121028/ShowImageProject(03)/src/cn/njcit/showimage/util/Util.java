package cn.njcit.showimage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.njcit.showimage.R;
import cn.njcit.showimage.bean.Albums;
import cn.njcit.showimage.meta.MetaData;

public class Util {
	public static String getFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static long getFileSize(File f) {

		if (f == null)
			return 0;
		long size = 0;
		File[] files = f.listFiles();
		if (files == null || files.length == 0)
			return f.length();
		for (File file : files) {
			if (file.isDirectory()) {
				size = size + getFileSize(file);
			} else {
				size = size + file.length();
			}
		}

		return size;
	}

	@SuppressWarnings("deprecation")
	public static String getFileDateTime(File file) {
		long time = file.lastModified();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal.getTime().toLocaleString();
	}

	/**
	 * 存到BitMap到SDCard
	 * 
	 * @param bmp
	 * @param strPath
	 */
	public static void saveBitmapToSDCard(Bitmap bmp, String strPath) {
		if (null != bmp && null != strPath && !strPath.equalsIgnoreCase("")) {
			try {
				File file = new File(strPath);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = bitampToByteArray(bmp);
				fos.write(buffer);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * bitamp To ByteArray
	 * 
	 * @param bmp
	 * @return
	 */
	public static byte[] bitampToByteArray(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);

		return baos.toByteArray();
	}

	/**
	 * 校验输入的文件夹、文件名称是否合法
	 * 
	 * @param newName
	 * @return
	 */
	public static boolean checkName(String newName) {
		boolean ret = false;
		if (newName.indexOf("\\") == -1) {
			ret = true;
		}

		return ret;
	}

	public static void showSaveDialog(final Context context,
			final Bitmap bitmap, final String path) {

		if (bitmap == null) {
			Toast.makeText(context, "图片没有变化！", Toast.LENGTH_LONG).show();
			return;
		}

		AlertDialog.Builder saveDialog = new AlertDialog.Builder(context);
		saveDialog.setIcon(R.drawable.ic_launcher);
		saveDialog.setTitle("注意");
		saveDialog.setMessage("保存剪裁后的结果吗？");
		saveDialog.setPositiveButton("保存",
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Util.saveBitmapToSDCard(bitmap, path);
					}

				});

		saveDialog.setNeutralButton("另存为",
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						showSaveAsDialog(context, bitmap, path);
					}

				});

		saveDialog.setNegativeButton("放弃",
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
					}

				});

		saveDialog.show();
	}

	private static void showSaveAsDialog(final Context context,
			final Bitmap bmp, final String path) {
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory
				.inflate(R.layout.saveas_dialog, null);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		final EditText picPath = (EditText) textEntryView
				.findViewById(R.id.picpath_edit);
		picPath.setText(path);
		alertDialog.setTitle("另存为");
		alertDialog.setView(textEntryView);
		alertDialog.setPositiveButton("保存",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						final String newFilePath = picPath.getText().toString();
						if (!Util.checkName(newFilePath)) {
							Toast.makeText(context, "您输入的路径有误，请重试！",
									Toast.LENGTH_LONG).show();
							return;
						}

						final File f_new = new File(newFilePath);
						if (f_new.exists()) {
							Toast.makeText(context, "该文件已存在，请重试！",
									Toast.LENGTH_LONG).show();
							return;
						}

						// save file
						saveBitmapToSDCard(bmp, newFilePath);
						// 更新ContentProvider
						notyfyMediaAdd(context, newFilePath);
					}
				});
		alertDialog.setNegativeButton("放弃",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		alertDialog.show();

	}

	private static void notyfyMediaAdd(Context context, String path) {
		File file = new File(path);
		String uriStr = file.getAbsolutePath().replaceFirst(".*/?sdcard",
				"file:///mnt/sdcard");
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse(uriStr)));
	}

	public static void notifyMediaRemove(Context context, File file) {
		String where = "";

		String path = file.getAbsolutePath().replaceFirst(".*/?sdcard",
				"/mnt/sdcard");

		where = MediaStore.Images.Media.DATA + "='" + path + "'";
		context.getContentResolver().delete(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, null);

	}

	public static void getThumbnailsPhotosInfo(Context context, String path) {

		Cursor cursor = null;

		try {
			cursor = context.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			if (cursor == null) {
				Toast.makeText(context, "未发现图像文件", Toast.LENGTH_LONG).show();
				return;
			}
		} catch (Exception err) {
			if (cursor != null)
				cursor.close();
			return;
		}

		Albums info = null;

		HashMap<String, LinkedList<String>> albums = getAlbumsInfo(cursor);
		cursor.close();

		for (Iterator<?> it = albums.entrySet().iterator(); it.hasNext();) {
			@SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) it.next();
			@SuppressWarnings("unchecked")
			LinkedList<String> album = (LinkedList<String>) e.getValue();

			if (album != null && album.size() > 0) {
				info = new Albums();
				info.displayName = (String) e.getKey();
				info.picturecount = String.valueOf(album.size());

				String id = album.get(0).split("&")[0];
				String albumpath = album.get(0).split("&")[1];
				albumpath = albumpath.substring(0, albumpath.lastIndexOf("/"));

				info.icon = Thumbnails.getThumbnail(
						context.getContentResolver(), Integer.valueOf(id),
						Thumbnails.MICRO_KIND, new BitmapFactory.Options());

				info.path = albumpath;

				List<String> list = new ArrayList<String>();
				for (String str : album) {
					list.add(str);
				}
				info.tag = list;
				MetaData.albums.add(info);
			}
		}
		Collections.sort(MetaData.albums, new Albums());
		cursor.close();
	}

	private static HashMap<String, LinkedList<String>> getAlbumsInfo(
			Cursor cursor) {
		HashMap<String, LinkedList<String>> albumsInfos = new HashMap<String, LinkedList<String>>();

		String _path = "_data";
		String _album = "bucket_display_name";

		if (cursor.moveToFirst()) {
			do {
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				String path = cursor.getString(cursor.getColumnIndex(_path));
				String album = cursor.getString(cursor.getColumnIndex(_album));

				if (albumsInfos.containsKey(album)) {
					LinkedList<String> albums = albumsInfos.remove(album);
					albums.add(_id + "&" + path);
					albumsInfos.put(album, albums);
				} else {
					LinkedList<String> albums = new LinkedList<String>();
					albums.add(_id + "&" + path);
					albumsInfos.put(album, albums);
				}
			} while (cursor.moveToNext());
		}

		return albumsInfos;
	}

	public static String getDate() {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
		Date dd = new Date();
		return ft.format(dd);
	}

	public static long getQuot(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
		try {
			Date date2 = ft.parse(time1);
			Date date1 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return quot;
	}

	public static boolean isImage(String fileName) {

		if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg")
				&& !fileName.endsWith(".png") && !fileName.endsWith(".bmp")
				&& !fileName.endsWith("gif")) {
			return false;
		}

		return true;
	}
	
	public static void getPicList(String path) {
		File file = new File(path);
		File[] files = file.listFiles();

		if (files != null) {
			for (File f : files) {
				String name = f.getName().toLowerCase();

				if (f.isDirectory()) {
					getPicList(f.getPath());
				} else if (Util.isImage(name)) {
					MetaData.pictures.add(f.getPath());
				}
			}
		}

	}
	
	
	/**
	 * 检查网络 是否正常
	 * 
	 * @return
	 */
	public static ConnectivityManager manager;

	public static boolean checkNet(Context context, String connectivity) {
		manager = (ConnectivityManager) context.getSystemService(connectivity);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			return false;
		}
		return true;
	}

	/**
	 * 检测当前URL是否可连接或是否有效, 最多连接网络 5 次, 如果 5 次都不成功说明该地址不存在或视为无效地址.
	 * 
	 * @param url
	 *            指定URL网络地址
	 * 
	 * @return String
	 */
	public static synchronized boolean isConnect(String url) {
		int counts = 0;
		int TIME_OUT = 1000 * 6;
		String GET = "GET";

		if (url == null || url.length() <= 0) {
			return false;
		}

		while (counts < 5) {
			try {
				URL urlStr = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlStr
						.openConnection();
				connection.setReadTimeout(TIME_OUT);
				connection.setRequestMethod(GET);
				connection.setUseCaches(false);

				int state = connection.getResponseCode();
				if (state == 200) {
					return true;
				}
				break;
			} catch (Exception ex) {
				counts++;
				continue;
			}
		}

		return false;
	}

	/**
	 * 下载图片
	 * 
	 * @param url
	 * @return
	 */
	public static synchronized boolean downloadImage(String url) {
		URL imageURL = null;
		int TIME_OUT = 1000 * 6;
		String GET = "GET";
		Bitmap bitmap = null;
		HttpURLConnection conn;

		try {
			imageURL = new URL(url);
		} catch (MalformedURLException e) {
			return false;
		}

		try {
			// 判断联网方式是否为wap
			boolean isProxy = false;
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
				if (networkInfo.getExtraInfo().toLowerCase().indexOf("wap") != -1) {// wap
					isProxy = true;
				}
			}

			// 如果是wap方式，要加网关
			if (isProxy) {
				InetSocketAddress address;
				address = new InetSocketAddress("10.0.0.172", 80);
				java.net.Proxy proxy = new java.net.Proxy(
						java.net.Proxy.Type.HTTP, address);
				conn = (HttpURLConnection) imageURL.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) imageURL.openConnection();
			}

			conn.setReadTimeout(TIME_OUT);
			conn.setRequestMethod(GET);
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			int length = (int) conn.getContentLength();
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}
				bitmap = BitmapFactory.decodeByteArray(imgData, 0,
						imgData.length);
				int pos = url.lastIndexOf("/");
				String name = url.substring(pos + 1, url.length());
				saveBitmapToSDCard(bitmap, "/sdcard/download/" + name);
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

}
