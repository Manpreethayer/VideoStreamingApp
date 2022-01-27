package com.example.hp.teacher.Camera.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemModel extends RealmObject {

	public static final String STATUS_OK = "ok";
	public static final String STATUS_NEED_DOWNLOAD = "need_download";

	@PrimaryKey
	public String uuid;

	public String title;

	public String description;

	public String thumbnail;

	public String zip_file;

	public int  num_stickers;

	public int num_effects;

	public int num_bgms;

	public int num_filters;

	public int num_masks;

	public boolean has_trigger;

	public String status;

	public long updated_at;

	public String downloadStatus;

	@Override
	public String toString() {
		return "[" +
				"uuid:" + uuid + ", " +
				"title:" + title + ", " +
				"description:" + description + ", " +
				"thumbnail:" + thumbnail + ", " +
				"zip_file:" + zip_file + ", " +
				"num_stickers:" + num_stickers + ", " +
                "num_effects:" + num_effects + ", " +
                "num_bgms:" + num_bgms + ", " +
                "num_filters:" + num_filters + ", " +
                "num_masks:" + num_masks + ", " +
                "has_trigger:" + has_trigger + ", " +
                "status:" + status + ", " +
                "updated_at:" + updated_at + ", " +
				"downloadStatus:" + downloadStatus +
				"]";
	}

	public ItemModel() {

	}
}
