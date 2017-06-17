package com.example.android.moviebox.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moviebox.R;
import com.example.android.moviebox.models.Movie;
import com.squareup.picasso.Picasso;


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListAdapterViewHolder> {

    private Movie[] mMovieData;

    final private MovieListAdapterOnClickHandler mClickHandler;

    public interface MovieListAdapterOnClickHandler {
        void onClick(Movie movieDetails);
    }

    public MovieListAdapter(MovieListAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mMovieDataImageView;

        public MovieListAdapterViewHolder (View view) {
            super(view);
            mMovieDataImageView = (ImageView) view.findViewById(R.id.image_view_movie_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition= getAdapterPosition();
            Movie movieDetails = mMovieData[adapterPosition];
            mClickHandler.onClick(movieDetails);
        }
    }

    /** View Holder Methods */
    @Override
    public MovieListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movielist_list_item;
        LayoutInflater inflater= LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieListAdapter.MovieListAdapterViewHolder holder, int position) {
        String movieThumbnail = mMovieData[position].getThumbnailUrlStr();
        Picasso.with(holder.mMovieDataImageView.getContext()).load(movieThumbnail).into(holder.mMovieDataImageView);
    }

    /** Item Count */
    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.length;
    }

    /** Getter, Setter */
    public void setMovieData(Movie[] movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }



}
