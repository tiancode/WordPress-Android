package org.wordpress.android.ui.reader.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.wordpress.android.models.ReaderTag;
import org.wordpress.android.ui.reader.ReaderEvents;
import org.wordpress.android.ui.reader.actions.ReaderActions;
import org.wordpress.android.ui.reader.actions.ReaderActions.RequestDataAction;
import org.wordpress.android.ui.reader.actions.ReaderPostActions;
import org.wordpress.android.util.AppLog;

import de.greenrobot.event.EventBus;

/***
 * service which updates posts with specific tags or in specific blogs
 */

public class ReaderPostService extends Service {

    private static final String ARG_TAG = "tag";
    private static final String ARG_ACTION = "action";
    private static final String ARG_BLOG_ID = "blog_id";
    private static final String ARG_FEED_ID = "feed_id";

    public static void startService(Context context,
                                    ReaderTag tag,
                                    RequestDataAction action) {
        Intent intent = new Intent(context, ReaderPostService.class);
        intent.putExtra(ARG_TAG, tag);
        intent.putExtra(ARG_ACTION, action);
        context.startService(intent);
    }

    public static void startService(Context context,
                                    long blogId,
                                    long feedId,
                                    RequestDataAction action) {
        Intent intent = new Intent(context, ReaderPostService.class);
        intent.putExtra(ARG_BLOG_ID, blogId);
        intent.putExtra(ARG_FEED_ID, feedId);
        intent.putExtra(ARG_ACTION, action);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.i(AppLog.T.READER, "reader post service > created");
    }

    @Override
    public void onDestroy() {
        AppLog.i(AppLog.T.READER, "reader post service > destroyed");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        RequestDataAction action;
        if (intent.hasExtra(ARG_ACTION)) {
            action = (RequestDataAction) intent.getSerializableExtra(ARG_ACTION);
        } else {
            action = RequestDataAction.LOAD_NEWER;
        }

        if (intent.hasExtra(ARG_TAG)) {
            ReaderTag tag = (ReaderTag) intent.getSerializableExtra(ARG_TAG);
            updatePostsWithTag(tag, action);
        } else if (intent.hasExtra(ARG_BLOG_ID) || intent.hasExtra(ARG_FEED_ID)) {
            long blogId = intent.getLongExtra(ARG_BLOG_ID, 0);
            long feedId = intent.getLongExtra(ARG_FEED_ID, 0);
            updatePostsInBlog(blogId, feedId, action);
        }

        return START_NOT_STICKY;
    }

    void updatePostsWithTag(final ReaderTag tag, final RequestDataAction action) {
        ReaderPostActions.updatePostsInTag(
                tag,
                action,
                new ReaderActions.UpdateResultListener() {
                    @Override
                    public void onUpdateResult(ReaderActions.UpdateResult result) {
                        EventBus.getDefault().post(new ReaderEvents.UpdatePostsEnded(tag, result, action));
                        stopSelf();
                    }
                });
        EventBus.getDefault().post(new ReaderEvents.UpdatePostsStarted(action));
    }

    void updatePostsInBlog(final long blogId, final long feedId, final RequestDataAction action) {
        ReaderActions.UpdateResultListener listener = new ReaderActions.UpdateResultListener() {
            @Override
            public void onUpdateResult(ReaderActions.UpdateResult result) {
                EventBus.getDefault().post(new ReaderEvents.UpdatePostsEnded(result, action));
                stopSelf();
            }
        };
        if (feedId != 0) {
            ReaderPostActions.requestPostsForFeed(feedId, action, listener);
        } else {
            ReaderPostActions.requestPostsForBlog(blogId, action, listener);
        }
    }
}
