package com.example.hp.teacher.Camera.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hp.teacher.Camera.model.ItemModel;
import com.example.hp.teacher.R;

import java.util.ArrayList;
import java.util.List;


public class StickerListAdapter extends RecyclerView.Adapter<StickerListAdapter.ViewHolder> {

	private static final String TAG = StickerListAdapter.class.getSimpleName();

	private List<ItemModel> mItems = new ArrayList<>();

	public interface Listener {
		void onStickerSelected(int position, ItemModel item);
	}

	private Listener mListener;

	private Context mContext;

	public StickerListAdapter(Context context, Listener listener) {
		mContext = context;
		mListener = listener;
	}

	public void setData(List<ItemModel> items) {
		mItems.clear();
		if(items != null) {
			mItems.addAll(items);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.bind(position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker, parent, false);
		return new StickerViewHolder(v);
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	public abstract class ViewHolder extends RecyclerView.ViewHolder {
		abstract void bind(int position);

		public ViewHolder(View v) {
			super(v);
		}
	}

	public class StickerViewHolder extends ViewHolder implements View.OnClickListener {
		public ImageView mImageViewItemThumbnail = null;
		public TextView mTextViewItemStatus = null;

		public ItemModel mItem;
		public int position;

		public StickerViewHolder(View v) {
			super(v);
			mImageViewItemThumbnail = (ImageView) v.findViewById(R.id.item_thumbnail_imageview);
			mTextViewItemStatus = (TextView) v.findViewById(R.id.image_status_textview);
		}

		@Override
		void bind(int position) {
			mItem = mItems.get(position);
			this.position = position;

			Log.d(TAG, "sticker " + position + " " + mItem.thumbnail + " " + mItem);
			mImageViewItemThumbnail.setOnClickListener(this);

			//스티커의 섬네일을 보여줍니다
			RequestOptions options=new RequestOptions();
			options.fitCenter();
			Glide.with(mContext)
					.load(mItem.thumbnail)
					.apply(options)
					.into(mImageViewItemThumbnail);

			//아이템의 상태를 표시합니다. (Download 완료 / 필요 상태)
			if(TextUtils.equals(mItem.downloadStatus, ItemModel.STATUS_OK)){
				mTextViewItemStatus.setText("Apply");
				mTextViewItemStatus.setTextColor(Color.CYAN);
			}else{
				mTextViewItemStatus.setText("Get");
				mTextViewItemStatus.setTextColor(Color.WHITE);
			}
		}

		@Override
		public void onClick(View v) {
			if(mListener != null){
				mListener.onStickerSelected(position, mItem);
			}
		}
	}
}