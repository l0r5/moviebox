package com.example.android.moviebox.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.moviebox.R;
import com.example.android.moviebox.models.Review;


public class ReviewListAdapter extends RecyclerView.Adapter <ReviewListAdapter.ReviewListAdapterViewHolder>{


    private Review[] mReviewData;


    public class ReviewListAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView mReviewDataTextView;

        public ReviewListAdapterViewHolder(View view) {
            super(view);
            mReviewDataTextView = (TextView) view.findViewById(R.id.text_view_review);
        }

    }

    @Override
    public ReviewListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewListAdapterViewHolder holder, int position) {
        String reviewLink = mReviewData[position].getAuthor();
        holder.mReviewDataTextView.setText(reviewLink);
    }

    @Override
    public int getItemCount() {
        if (null == mReviewData) return 0;
        return mReviewData.length;
    }

    public void setReviewData(Review[] reviewData) {
        mReviewData = reviewData;
    }


}
