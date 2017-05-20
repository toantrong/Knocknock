package com.nguyentrongtoan.knockapp.data;

import java.util.List;
import java.util.Map;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public interface BaseRepository<T> {
    interface LoadDataCompleted<T> {
        void onComplete(T item);
    }

    interface LoadTotalCompleted<T> {
        void onComplete(List<T> items);
    }


    void forceLoad(LoadDataCompleted<T> callback);
    void forceLoadTotal(LoadTotalCompleted<T> callback);
    void forceLoadAItem(String UID, LoadDataCompleted<T> mCallback);
    void unsubscribe();
}
