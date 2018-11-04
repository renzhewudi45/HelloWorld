/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.njcit.showimage.appwidget;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import cn.njcit.showimage.R;
import cn.njcit.showimage.meta.MetaData;
import cn.njcit.showimage.util.BitmapUtil;

public class StackWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	private int mCount;
	private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
	private Context mContext;
	@SuppressWarnings("unused")
	private int mAppWidgetId;

	public StackRemoteViewsFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		mCount = MetaData.appWidgetPictures.size();
	}

	public void onCreate() {
		// In onCreate() you setup any connections / cursors to your data
		// source. Heavy lifting,
		// for example downloading or creating content etc, should be deferred
		// to onDataSetChanged()
		// or getViewAt(). Taking more than 20 seconds in this call will result
		// in an ANR.
		for (int i = 0; i < mCount; i++) {
			String path = MetaData.appWidgetPictures.get(i);
			WidgetItem widgetItem = new WidgetItem(path);
			mWidgetItems.add(widgetItem);
		}

		// We sleep for 3 seconds here to show how the empty view appears in the
		// interim.
		// The empty view is set in the StackWidgetProvider and should be a
		// sibling of the
		// collection view.
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		// In onDestroy() you should tear down anything that was setup for your
		// data source,
		// eg. cursors, connections, etc.
		mWidgetItems.clear();
	}

	public int getCount() {
		return mCount;
	}

	public RemoteViews getViewAt(int position) {
		// position will always range from 0 to getCount() - 1.

		// We construct a remote views item based on our widget item xml file,
		// and set the
		// text based on the position.
		RemoteViews rv = new RemoteViews(mContext.getPackageName(),
				R.layout.widget_item);
		Bitmap bitmap = BitmapUtil.fitSizePic(200,
				mWidgetItems.get(position).text);
		// recreate the reflection Bitmap
		Bitmap rectBitmap = BitmapUtil.getRectBitmap(bitmap);
		Bitmap reflectionBitmap = BitmapUtil.createReflectedImage(rectBitmap);
		// recreate the new Bitmap
		Bitmap resizedBitmap = BitmapUtil.getResizedBitmap(180, 220,
				reflectionBitmap);
		rv.setImageViewBitmap(R.id.widget_item, resizedBitmap);

		// Next, we set a fill-intent which will be used to fill-in the pending
		// intent template
		// which is set on the collection view in StackWidgetProvider.
		Bundle extras = new Bundle();
		extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

		// You can do heaving lifting in here, synchronously. For example, if
		// you need to
		// process an image, fetch something from the network, etc., it is ok to
		// do it here,
		// synchronously. A loading view will show up in lieu of the actual
		// contents in the
		// interim.
		try {
			System.out.println("Loading view " + position);
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Return the remote views object.
		return rv;
	}

	public RemoteViews getLoadingView() {
		// You can create a custom loading view (for instance when getViewAt()
		// is slow.) If you
		// return null here, you will get the default loading view.
		return null;
	}

	public int getViewTypeCount() {
		return 1;
	}

	public long getItemId(int position) {
		return position;
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onDataSetChanged() {
		// This is triggered when you call AppWidgetManager
		// notifyAppWidgetViewDataChanged
		// on the collection view corresponding to this factory. You can do
		// heaving lifting in
		// here, synchronously. For example, if you need to process an image,
		// fetch something
		// from the network, etc., it is ok to do it here, synchronously. The
		// widget will remain
		// in its current state while work is being done here, so you don't need
		// to worry about
		// locking up the widget.
		// »ñÈ¡Í¼Æ¬¼¯
		// getPictures(mContext, getHistoryFolderId(mContext));

	}

}