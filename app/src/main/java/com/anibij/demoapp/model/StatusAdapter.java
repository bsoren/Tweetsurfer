package com.anibij.demoapp.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.anibij.demoapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bsoren on 02-Nov-15.
 */
public class StatusAdapter extends RecyclerView.Adapter {

    private static final String TAG = StatusAdapter.class.getSimpleName();
    private static final int VIEW_ITEM = 0 ;
    private static final int VIEW_LOAD = 1;

    private Context mContext;
    private List<Status> mStatuses;

    public StatusAdapter(Context context, List<Status> statuses) {
        this.mContext = context;
        this.mStatuses = statuses;
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
            Log.d(TAG,"Assigned LoadMoreItemViewHolder");
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof StatusViewHolder) {

            Log.d(TAG,"Inside StatusViewHolder");

            Status statusElem = mStatuses.get(position);
           // Log.d(TAG, String.format("Id : %s \n user : %s \n tweet : %s", statusElem.getId(),
               //     statusElem.getUser(), statusElem.getMessage()));

            // holder.mRelativeLayout2.setSelected(mStatuses.contains(position));
            Status status = mStatuses.get(position);

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

            ((StatusViewHolder)holder).user.setText(status.getUser());
            ((StatusViewHolder)holder).message.setText(status.getMessage());

            String dateString = DateUtils.getRelativeTimeSpanString(status.getCreatedAt()).toString();
            ((StatusViewHolder)holder).createdAt.setText(dateString);

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
            ((LoadMoreItemViewHolder)holder).mTextView.setText("Load More Items!!!");

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

}
