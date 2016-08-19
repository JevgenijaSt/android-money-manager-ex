/*
 * Copyright (C) 2012-2016 The Android Money Manager Ex Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.money.manager.ex.home;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.money.manager.ex.R;
import com.money.manager.ex.core.UIHelper;
import com.money.manager.ex.sync.SyncManager;
import com.money.manager.ex.sync.SyncServiceMessage;
import com.shamanland.fonticon.FontIconDrawable;

import timber.log.Timber;

/**
 * Check for updates to the database in the cloud.
 * Ran on start of the main activity.
 */
public class CheckCloudStorageForUpdatesTask
    extends AsyncTask<Void, Integer, SyncServiceMessage> {

    public CheckCloudStorageForUpdatesTask(Context context) {
        mContext = context;
    }

    private Context mContext;
    private SyncManager mSyncManager;

    @Override
    protected SyncServiceMessage doInBackground(Void... voids) {
        SyncServiceMessage result = null;

        try {
            publishProgress(1);

            // Check if any sync action is required.
            result = getSyncManager().compareFilesForSync();

            // continues at PostExecute.
        } catch (Exception e) {
            //throw new RuntimeException("Error in check Cloud ForUpdates", e);
            Timber.e(e, "comparing files for sync");
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... params) {
        Toast.makeText(mContext, R.string.checking_remote_for_changes, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(SyncServiceMessage ret) {
        // ???
        if (ret == null) {
            Timber.w("onPostExecute ret parameter is null.");
            return;
        }

        try {
            if (ret.equals(SyncServiceMessage.STARTING_DOWNLOAD.code)) {
//            showNotificationSnackbar();
                UIHelper.showDiffNotificationDialog(getContext());
            }
        } catch (Exception ex) {
            Timber.e(ex, "showing update notification dialog");
        }

        if (ret.equals(SyncServiceMessage.STARTING_UPLOAD.code)) {
            // upload without prompting.
            getSyncManager().triggerSynchronization();
        }

    }

//    private void showNotificationSnackbar() {
//        // The context has to implement the callbacks interface!
//        final MainActivity mainActivity = (MainActivity) context;
//
//        Snackbar.with(context.getApplicationContext()) // context
//            .text(context.getString(R.string.dropbox_database_can_be_updted))
//            .actionLabel(context.getString(R.string.sync))
//            .actionColor(context.getResources().getColor(R.color.md_primary))
//            .actionListener(new ActionClickListener() {
//                @Override
//                public void onActionClicked(Snackbar snackbar) {
//                    DropboxManager dropbox = new DropboxManager(context, mDropboxHelper, mainActivity);
//                    dropbox.synchronizeDropbox();
//                }
//            })
//            .duration(5 * 1000)
//            .show(mainActivity);
//    }

    public Context getContext() {
        return mContext;
    }

    private SyncManager getSyncManager() {
        if (mSyncManager == null) {
            mSyncManager = new SyncManager(getContext());
        }
        return mSyncManager;
    }

}