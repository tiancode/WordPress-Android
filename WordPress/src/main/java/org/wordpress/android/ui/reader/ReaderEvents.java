package org.wordpress.android.ui.reader;

import org.wordpress.android.models.ReaderTag;
import org.wordpress.android.ui.reader.actions.ReaderActions;
import org.wordpress.android.util.DateTimeUtils;

import java.util.Date;

/**
 * Reader-related EventBus event classes
 */
public class ReaderEvents {
    public static class FollowedTagsChanged {}
    public static class RecommendedTagsChanged{}

    public static class FollowedBlogsChanged {}
    public static class RecommendedBlogsChanged {}

    public static class HasPurgedDatabase {}
    public static class HasPerformedInitialUpdate {}

    public static class UpdatedFollowedTagsAndBlogs {
        private final Date mUpdateDate;
        public UpdatedFollowedTagsAndBlogs() {
            mUpdateDate = new Date();
        }
        public int minutesSinceLastUpdate() {
            return DateTimeUtils.minutesBetween(mUpdateDate, new Date());
        }
    }

    public static class UpdatePostsStarted {
        private final ReaderActions.RequestDataAction mAction;
        public UpdatePostsStarted(ReaderActions.RequestDataAction action) {
            mAction = action;
        }
        public ReaderActions.RequestDataAction getAction() {
            return mAction;
        }
    }

    public static class UpdatePostsEnded {
        private final ReaderTag mReaderTag;
        private final ReaderActions.UpdateResult mResult;
        private final ReaderActions.RequestDataAction mAction;
        public UpdatePostsEnded(ReaderActions.UpdateResult result,
                                ReaderActions.RequestDataAction action) {
            mResult = result;
            mAction = action;
            mReaderTag = null;
        }
        public UpdatePostsEnded(ReaderTag readerTag,
                                ReaderActions.UpdateResult result,
                                ReaderActions.RequestDataAction action) {
            mReaderTag = readerTag;
            mResult = result;
            mAction = action;
        }
        public ReaderTag getReaderTag() {
            return mReaderTag;
        }
        public ReaderActions.UpdateResult getResult() {
            return mResult;
        }
        public ReaderActions.RequestDataAction getAction() {
            return mAction;
        }
    }
}
