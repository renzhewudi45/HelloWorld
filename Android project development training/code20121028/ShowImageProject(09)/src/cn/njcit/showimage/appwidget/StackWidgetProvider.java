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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import cn.njcit.showimage.MainActivity;
import cn.njcit.showimage.R;

public class StackWidgetProvider extends AppWidgetProvider {
	public static final String TOAST_ACTION = "cn.njcit.showimage.android.widget.TOAST_ACTION";
	public static final String EXTRA_ITEM = "cn.njcit.showimage.android.widget.EXTRA_ITEM";

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		if (intent.getAction().equals(TOAST_ACTION)) {
			@SuppressWarnings("unused")
			int appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

			this.onUpdate(context, mgr, mgr.getAppWidgetIds(new ComponentName(
					context, StackWidgetProvider.class)));
		}
		super.onReceive(context, intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// update each of the widgets with the remote adapter
		for (int i = 0; i < appWidgetIds.length; ++i) {

			// Here we setup the intent which points to the StackViewService
			// which will
			// provide the views for this collection.
			Intent intent = new Intent(context, StackWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			// When intents are compared, the extras are ignored, so we need to
			// embed the extras
			// into the data so that the extras will not be ignored.
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

			// The empty view is displayed when the collection has no items. It
			// should be a sibling
			// of the collection view.
			rv.setEmptyView(R.id.stack_view, R.id.empty_view);

			Intent toastIntent = new Intent(context, MainActivity.class);
			toastIntent.setAction(StackWidgetProvider.TOAST_ACTION);
			toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent toastPendingIntent = PendingIntent.getActivity(
					context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}