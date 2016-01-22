package com.anibij.demoapp.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anibij.demoapp.DetailsActivity;
import com.anibij.demoapp.R;
import com.anibij.demoapp.service.RefreshService;
import com.squareup.picasso.Picasso;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;



/**
 * Created by bsoren on 02-Nov-15.
 */
public class StatusAdapter extends RecyclerView.Adapter {

    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    private static SharedPreferences mSharedPreferences;
    SQLiteDatabase db;
    private static final String TAG = StatusAdapter.class.getSimpleName();
    private static final int VIEW_ITEM = 0 ;
    private static final int VIEW_LOAD = 1;

    private Context mContext;
    private List<Status> mStatuses;
    private ContentResolver mContentResolver;
    Handler uiHandler;

    public StatusAdapter(Context context, List<Status> statuses, Handler uiHandler) {
        this.mContext = context;
        this.mStatuses = statuses;
        this.mContentResolver = context.getContentResolver();
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        this.uiHandler = uiHandler;
    }

    @Override
    public int getItemViewType(int position) {
        return mStatuses.get(position).getMoreItems() == 0 ? VIEW_ITEM : VIEW_LOAD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_ITEM ) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);
            vh  = new StatusViewHolder(view);
            Log.d(TAG,"Assigned StatusViewHolder");

        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_items, parent, false);
            vh = new LoadMoreItemViewHolder(view);
            Log.d(TAG, "Assigned LoadMoreItemViewHolder");
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof StatusViewHolder) {

            Log.d(TAG,"Inside StatusViewHolder");

            Status statusElem = mStatuses.get(position);
           // Log.d(TAG, String.format("Id : %s \n user : %s \n tweet : %s", statusElem.getId(),
               //     statusElem.getUser(), statusElem.getMessage()));

            // holder.mRelativeLayout2.setSelected(mStatuses.contains(position));
            Status status = mStatuses.get(position);
            final int favCount = status.getFavCount();

            final long statusId = Long.valueOf(status.getId());

            if(status.getRetweetBy() != null) {

                ((StatusViewHolder) holder).retweetBy.setText(status.getRetweetBy()+" Retweeted");
                ((StatusViewHolder) holder).retweetLinearLayout.setVisibility(View.VISIBLE);

                RelativeLayout mRelativeLayout = ((StatusViewHolder)holder).mRelativeLayout2;
                FrameLayout.LayoutParams relativeParams = (FrameLayout.LayoutParams)mRelativeLayout.getLayoutParams();
                relativeParams.topMargin = 80;
                mRelativeLayout.setLayoutParams(relativeParams);
                mRelativeLayout.requestLayout();
            }else {

                ((StatusViewHolder) holder).retweetLinearLayout.setVisibility(View.GONE);
                RelativeLayout mRelativeLayout = ((StatusViewHolder)holder).mRelativeLayout2;
                FrameLayout.LayoutParams relativeParams = (FrameLayout.LayoutParams)mRelativeLayout.getLayoutParams();
                relativeParams.topMargin = 0 ;
                mRelativeLayout.setLayoutParams(relativeParams);
                mRelativeLayout.requestLayout();
            }


            ((StatusViewHolder) holder).retweetCount.setText(String.valueOf(status.getRetweetCount()));
            ((StatusViewHolder) holder).favCount.setText(String.valueOf(status.getFavCount()));
            ((StatusViewHolder) holder).screenNameView.setText("@" + status.getScreenName());


            boolean isFavourite = status.isFavourite();
            if (isFavourite) {
                ((StatusViewHolder) holder).likeButtonView.setImageResource(R.drawable.like_pink);
                ((StatusViewHolder) holder).likeButtonView.setTag(R.drawable.like_pink);
            } else {
                ((StatusViewHolder) holder).likeButtonView.setImageResource(R.drawable.like_grey);
                ((StatusViewHolder) holder).likeButtonView.setTag(R.drawable.like_grey);
            }

            ((StatusViewHolder) holder).likeButtonView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int resId = (Integer) v.getTag();
                    switch (resId) {
                        case R.drawable.like_grey:
                            ((ImageView) v).setImageResource(R.drawable.like_pink);
                            v.setTag(R.drawable.like_pink);
                            mStatuses.get(position).setFavourite(true);
                            mStatuses.get(position).setFavCount(favCount + 1);
                            notifyItemChanged(position);
                            makeFavourite(String.valueOf(statusId), "true", String.valueOf(position));
                            break;

                        case R.drawable.like_pink:
                            ((ImageView) v).setImageResource(R.drawable.like_grey);
                            v.setTag(R.drawable.like_grey);
                            mStatuses.get(position).setFavourite(false);
                            mStatuses.get(position).setFavCount(favCount - 1);
                            notifyItemChanged(position);
                            makeFavourite(String.valueOf(statusId), "false", String.valueOf(position));
                            break;

                        default:
                            break;

                    }
                }
            });


            ((StatusViewHolder) holder).user.setText(status.getUser());
            ((StatusViewHolder)holder).message.setText(status.getMessage());

            ((StatusViewHolder) holder).message.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Getting Tweet Details");
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    Intent detailIntent = new Intent(mContext, DetailsActivity.class);
                    detailIntent.putExtra(StatusContract.Column.ID, statusId);
                    mContext.startActivity(detailIntent);
                }
            });


            String dateString = DateUtils.getRelativeTimeSpanString(status.getCreatedAt()).toString();
            ((StatusViewHolder) holder).createdAt.setText(dateString);

//            String dateString = DateUtils.formatDateTime(mContext,status.getCreatedAt(),DateUtils.FORMAT_SHOW_TIME);
//            ((StatusViewHolder) holder).createdAt.setText(dateString);



            Picasso.with(mContext).load(status.getProfileImageUrl())
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.twitter_placeholder_image).into(((StatusViewHolder)holder).profileImage);

            String mediaImageUrl = status.getMediaImageUrl();

            if (!mediaImageUrl.equals("NO_IMAGE")) {
                ((StatusViewHolder)holder).mediaImage.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(mediaImageUrl)
                        .error(R.drawable.no_image)
                        .placeholder(R.drawable.image_loading_animation).into(((StatusViewHolder)holder).mediaImage);

            } else {
                ((StatusViewHolder)holder).mediaImage.setVisibility(View.GONE);
                ((StatusViewHolder)holder).mediaImage.setImageResource(R.drawable.no_image);
            }
        }else{

            Log.d(TAG,"Inside Load More Items");
            Status status = mStatuses.get(position);
            final String statusId = status.getId();
            final int positionFinal = position;

            ((LoadMoreItemViewHolder)holder).mTextView.setText("Load More Items!!!");
            ((LoadMoreItemViewHolder) holder).mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri deleteUri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, Long.valueOf(statusId));
                    int rowsDeleted = mContentResolver.delete(deleteUri, null, null);

                    Log.d(TAG, "Row Deleted : " + rowsDeleted);

                    Intent refreshIntent = new Intent(mContext, RefreshService.class);
                    refreshIntent.putExtra("MAX_ID", statusId);
                    mContext.startService(refreshIntent);

                    mStatuses.remove(positionFinal);
                    notifyItemRemoved(positionFinal);
                }
            });

        }

    }


    private synchronized void makeFavourite(String statusId, String toggle, String position) {
        new FavoriteToggleTask().execute(statusId, toggle, position);
    }

    @Override
    public int getItemCount() {
        return mStatuses.size();
    }

    public void loadNewData(List<Status> statuses){
        this.mStatuses =  statuses;
        notifyDataSetChanged();
    }

    public void setData(List<Status> statuses){

        if(statuses != null){
            this.mStatuses = statuses;
            //Toast.makeText(mContext, "Items Added", Toast.LENGTH_LONG).show();

        }else {
           // Toast.makeText(mContext,"List is null",Toast.LENGTH_LONG).show();
        }
        notifyDataSetChanged();
    }

    private class FavoriteToggleTask extends AsyncTask<String, Void, twitter4j.Status> {

        long statusId;
        String toggle;
        int position;

        @Override
        protected twitter4j.Status doInBackground(String... params) {


            try {

                statusId = Long.valueOf(params[0]);
                toggle = params[1];
                position = Integer.valueOf(params[2]);

                Log.d(TAG, "Updating Favourite statusId : " + statusId + " Toggle : " + toggle);

                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                twitter4j.Status status;

                if (toggle.equals("true")) {
                    status = twitter.createFavorite(statusId);
                } else {
                    status = twitter.destroyFavorite(statusId);
                }

                return status;

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(twitter4j.Status status) {

            if (status == null) {
                return;
            }
            super.onPostExecute(status);
            boolean isFavourite = status.isFavorited();

            ContentValues values = new ContentValues();
            values.put(StatusContract.Column.IS_FAVOURITE, (isFavourite) ? 1 : 0);
            values.put(StatusContract.Column.FAV_COUNT, status.getFavoriteCount());
            Uri updateUri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, Long.valueOf(statusId));
            int rowUpdated = mContentResolver.update(updateUri, values, null, null);

            //mStatuses.get(position).setFavourite(isFavourite);
            //mStatuses.get(position).setFavCount(status.getFavoriteCount());
            //notifyItemChanged(position);
            Log.d(TAG, "Row updated : " + rowUpdated);
        }
    }


}
