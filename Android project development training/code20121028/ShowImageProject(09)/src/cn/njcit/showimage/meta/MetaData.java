package cn.njcit.showimage.meta;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.widget.ImageView;
import cn.njcit.showimage.bean.Albums;

public class MetaData {

	public static LinkedList<Albums> albums = new LinkedList<Albums>();
	public static ArrayList<String> pictures = new ArrayList<String>();
	public static ArrayList<String> appWidgetPictures = new ArrayList<String>();
	
	@SuppressLint("UseSparseArrays")
	public static HashMap<Integer, SoftReference<ImageView>> imageViewDataCache = new HashMap<Integer, SoftReference<ImageView>>();
	public static HashMap<Integer, Bitmap> bitmapDataCache = new HashMap<Integer, Bitmap>();

	
	public static int screenWidth;
	public static int screenHeight;
	
	public static final String UPDATE_INFOS = "update_infos";
	// 下面的地址用户可根据自己的配置修改
	public static final String UPDATE_SERVER = "http://10.0.2.2:8084/UpdateServlet";
	public static final String UPDATE_APKSERVER = "http://download.xici.net/d156578647.0/ShowImageProject.apk";
	public static int currentVersionCode = 1;
	public static String currentVersionName = "1.0";
	public static int newVerCode = 1;	
	public static String newVerName = "1.0";
	
	public static String cameraImagePath;
	
	public static boolean isCleanHistory;
	public static boolean isAutoUpdate;
	public static String appWidgetPath;
	
	
}
