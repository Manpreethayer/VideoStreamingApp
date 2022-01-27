package com.example.hp.teacher.Camera.model;

import io.realm.RealmList;

public class CategoryRespModel {

    public String api_key;

    public String name;

    public String description;

    public String status;

    public long last_updated_at;

    public RealmList<CategoryModel> categories;

    public RealmList<FilterModel> filters;

    @Override
    public String toString() {
        return "[" +
                "api_key:" + api_key + ", " +
                "name:" + name + ", " +
                "description:" + description + ", " +
                "status:" + status + ", " +
                "last_updated_at:" + last_updated_at + ", " +
                "categories:" + (categories != null ? categories.size() : -1) + ", " +
                "filters:" + (filters != null ? filters.size() : -1) +
                "]";
    }
}
