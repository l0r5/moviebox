package com.example.android.moviebox.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.moviebox.R;
import com.example.android.moviebox.models.Trailer;


public class TrailerListAdapter extends RecyclerView.Adapter <TrailerListAdapter.TrailerListAdapterViewHolder>{


    private Trailer[] mTrailerData;
    final private TrailerListAdapterOnClickHandler mClickHandler;

    public interface TrailerListAdapterOnClickHandler {
        void onTrailerClick(Trailer trailerDetails);
    }

    public TrailerListAdapter(TrailerListAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class TrailerListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTrailerDataTextView;

        public TrailerListAdapterViewHolder(View view) {
            super(view);
            mTrailerDataTextView = (TextView) view.findViewById(R.id.text_view_trailer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition= getAdapterPosition();
            Trailer trailerDetails = mTrailerData[adapterPosition];
            mClickHandler.onTrailerClick(trailerDetails);
        }
    }

    @Override
    public TrailerListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerListAdapterViewHolder holder, int position) {
        String trailerLink = mTrailerData[position].getName();
        holder.mTrailerDataTextView.setText(trailerLink);
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerData) return 0;
        return mTrailerData.length;
    }

    public void setTrailerData(Trailer[] trailerData) {
        mTrailerData = trailerData;
    }



}
