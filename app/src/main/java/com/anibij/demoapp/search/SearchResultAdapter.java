package com.anibij.demoapp.search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anibij.demoapp.DetailsActivity;
import com.anibij.demoapp.R;
import com.anibij.demoapp.StatusActivity;
import com.anibij.demoapp.model.LoadMoreItemViewHolder;
import com.anibij.demoapp.model.Status;
import com.anibij.demoapp.model.StatusContract;
import com.anibij.demoapp.model.StatusViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by bsoren on 02-Nov-15.
 */
public class SearchResultAdapter extends RecyclerView.Adapter implements View.OnCreateContextMenuListener{

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
    private static final String TAG = SearchResultAdapter.class.getSimpleName();
    private static final int VIEW_ITEM = 0 ;
    private static final int VIEW_LOAD = 1;

    private Context mContext;
    private List<Status> mStatuses;

    public SearchResultAdapter(Context context, List<Status> statuses) {
        this.mContext = context;
        this.mStatuses = statuses;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
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

            //((StatusViewHolder) holder).retweetButtonView.setOnCreateContextMenuListener(this);

            ((StatusViewHolder) holder).retweetButtonView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    final Dialog dialog =  new Dialog(mContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.retweet_popup_menu);
                    dialog.setCanceledOnTouchOutside(true);

                    dialog.findViewById(R.id.menuItem1).setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext,"Retweet clicked ",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    });

                    dialog.findViewById(R.id.menuItem2).setOnClickListener(new View.OnClickListener(){


                        @Override
                        public void onClick(View v) {

                            Toast.makeText(mContext,"Retweet with Comment clicked ",Toast.LENGTH_SHORT).show();

                            Status replyStatus = mStatuses.get(position);
                            Intent replyTo = new Intent(mContext,StatusActivity.class);
                            replyTo.putExtra("TWEET_ID",replyStatus.getId());
                            replyTo.putExtra("TWEET_USER",replyStatus.getScreenName());
                            replyTo.putExtra("TWEET_MESSAGE",replyStatus.getMessage());
                            mContext.startActivity(replyTo);

                            dialog.dismiss();

                        }
                    });

                    dialog.show();


                }
            });


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
                            break;

                        case R.drawable.like_pink:
                            ((ImageView) v).setImageResource(R.drawable.like_grey);
                            v.setTag(R.drawable.like_grey);
                            mStatuses.get(position).setFavourite(false);
                            mStatuses.get(position).setFavCount(favCount - 1);
                            notifyItemChanged(position);
                            break;

                        default:
                            break;

                    }
                }
            });

            ((StatusViewHolder) holder).replyButtonView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d(TAG,"Clicked Reply To");
                    Status replyStatus = mStatuses.get(position);
                    Intent replyTo = new Intent(mContext,StatusActivity.class);
                    replyTo.putExtra("TWEET_ID",replyStatus.getId());
                    replyTo.putExtra("TWEET_USER",replyStatus.getScreenName());
                    mContext.startActivity(replyTo);

                }

            });


            ((StatusViewHolder) holder).user.setText(status.getUser());
            ((StatusViewHolder)holder).message.setText(status.getMessage());

            ((StatusViewHolder) holder).message.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Getting Tweet Details");
                    Intent detailIntent = new Intent(mContext, DetailsActivity.class);
                    detailIntent.putExtra(StatusContract.Column.ID, statusId);
                    mContext.startActivity(detailIntent);
                }
            });


            String dateString = DateUtils.getRelativeTimeSpanString(status.getCreatedAt()).toString();
            ((StatusViewHolder) holder).createdAt.setText(dateString);

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

        }

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "SMS");
    }

}
