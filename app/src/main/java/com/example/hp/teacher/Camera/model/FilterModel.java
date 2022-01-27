package com.example.hp.teacher.Camera.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by daewookjung on 06/03/2018.
 */

public class FilterModel extends RealmObject {

    @PrimaryKey
    public String uuid;

    public String title;

    public boolean is_bundle;

    public long updated_at;

    public String description;

    public String status;

    public RealmList<ItemModel> items;

    @Override
    public String toString() {
        return "[" +
                "uuid:" + uuid + ", " +
                "title:" + title + ", " +
                "is_bundle:" + is_bundle + ", " +
                "updated_at: " + updated_at + ", " +
                "description: " + description + ", " +
                "status: " + status + ", " +
                "items:" + (items != null ? items.size() : -1) +
                "]";
    }
}
