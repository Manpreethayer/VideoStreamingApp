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
import com.example.hp.teacher.Camera.model.FilterModel;
import com.example.hp.teacher.Camera.model.ItemModel;
import com.example.hp.teacher.R;

import java.util.ArrayList;
import java.util.List;


public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

	private static final String TAG = FilterListAdapter.class.getSimpleName();

	private List<FilterModel> mFilters = new ArrayList<>();

	public interface Listener{
		void onFilterSelected(int position, ItemModel item);
	}

	private Listener mListener;

	private Context mContext;

	public FilterListAdapter(Context context, Listener listener) {
		mContext = context;
		mListener = listener;
	}

	public void setData(List<FilterModel> filters){
		mFilters.clear();
		if (filters != null ) {
			mFilters.addAll(filters);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		if(mFilters.size()>0)
			return mFilters.get(0).items.size();
		else
			return 0;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.bind(position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter, parent, false);
		return new ItemViewHolder(v);
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

	public class ItemViewHolder extends ViewHolder implements View.OnClickListener {
		public ImageView mImageViewItemThumbnail = null;
		public TextView mTextViewTitle = null;
		public TextView mTextViewItemStatus = null;

		public ItemModel mItem;

		public int position;

		public ItemViewHolder(View v) {
			super(v);
			mImageViewItemThumbnail = (ImageView) v.findViewById(R.id.item_thumbnail_imageview);
			mTextViewTitle = (TextView) v.findViewById(R.id.title_textview);
			mTextViewItemStatus = (TextView) v.findViewById(R.id.image_status_textview);
		}

		@Override
		void bind(int position) {
			mItem = mFilters.get(0).items.get(position);
			this.position = position;

			Log.d(TAG, "filter " + position + " " + mItem.thumbnail + " " + mItem);
			mImageViewItemThumbnail.setOnClickListener(this);

			//필터의 섬네일과 이름을 표시합니다 .

            RequestOptions options = new RequestOptions();
            options.fitCenter();
			Glide.with(mContext)
					.load(mItem.thumbnail)
					.apply(options)
					.into(mImageViewItemThumbnail);

			if (TextUtils.equals(mItem.downloadStatus, ItemModel.STATUS_OK)) {
				mTextViewItemStatus.setText("Apply");
				mTextViewItemStatus.setTextColor(Color.CYAN);
			} else {
				mTextViewItemStatus.setText("Get");
				mTextViewItemStatus.setTextColor(Color.WHITE);
			}

			mTextViewTitle.setText(mItem.title);
			mTextViewTitle.setTextColor(Color.GRAY);
		}

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onFilterSelected(position, mItem);
			}
		}
	}
}