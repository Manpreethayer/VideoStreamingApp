package com.example.hp.teacher.Camera.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.hp.teacher.Camera.model.CategoryModel;
import com.example.hp.teacher.R;

import java.util.ArrayList;
import java.util.List;


public class StickerCategoryListAdapter extends RecyclerView.Adapter<StickerCategoryListAdapter.ViewHolder> {

	private static final String TAG = StickerCategoryListAdapter.class.getSimpleName();

	private List<CategoryModel> mCategories = new ArrayList<>();

	public interface Listener {
		void onCategorySelected(CategoryModel category);
	}

	private Listener mListener;

	public StickerCategoryListAdapter(Listener listener) {
		mListener = listener;
	}

	public void setData(List<CategoryModel> categories){
		mCategories.clear();
		mCategories.addAll(categories);
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mCategories.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.bind(position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
		return new CategoryViewHolder(v);
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

	public class CategoryViewHolder extends ViewHolder implements View.OnClickListener {
		public Button mButtonCategory = null;
		public CategoryModel mCategory;
		public CategoryViewHolder(View v) {
			super(v);
			mButtonCategory = (Button) v.findViewById(R.id.category_button);
			mButtonCategory.setTextColor(Color.WHITE);
		}

		@Override
		void bind(int position) {
			mCategory = mCategories.get(position);

			/*
			 * Sticker Category 의 제목을 표시합니다.
			 */
			Log.d(TAG, "category " + position + " " + mCategory);
			mButtonCategory.setText(mCategory.title);
			mButtonCategory.setTextColor(Color.WHITE);
			mButtonCategory.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if(mListener != null){
				mListener.onCategorySelected(mCategory);

			}
		}
	}
}