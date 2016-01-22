package com.anibij.demoapp.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anibij.demoapp.R;

/**
 * Created by bsoren on 02-Nov-15.
 */
public class StatusViewHolder extends RecyclerView.ViewHolder{

    protected ImageView profileImage, mediaImage, likeButtonView;
    protected TextView user, message, createdAt, retweetBy, retweetCount, favCount, screenNameView;
    protected RelativeLayout mRelativeLayout,mRelativeLayout2;
    protected LinearLayout mLinearLayout;
    protected LinearLayout retweetLinearLayout;

    public StatusViewHolder(View itemView) {
        super(itemView);

        //mLinearLayout = (LinearLayout)itemView.findViewById(R.id.list_layout);
        mRelativeLayout2 = (RelativeLayout)itemView.findViewById(R.id.list_layout2);
        profileImage = (ImageView) itemView.findViewById(R.id.list_item_profile_image);
        mediaImage = (ImageView) itemView.findViewById(R.id.list_item_media_image);

        user =  (TextView) itemView.findViewById(R.id.list_item_text_user);
        message = (TextView) itemView.findViewById(R.id.list_item_text_message);
        createdAt = (TextView) itemView.findViewById(R.id.list_item_text_created_at);
        retweetBy = (TextView) itemView.findViewById(R.id.retweetBy);
        retweetCount = (TextView) itemView.findViewById(R.id.reTweetCount);
        favCount = (TextView) itemView.findViewById(R.id.favCount);
        screenNameView = (TextView) itemView.findViewById(R.id.list_item_text_screen_name);
        retweetLinearLayout = (LinearLayout) itemView.findViewById(R.id.retweetLinearLayout);
        likeButtonView = (ImageView) itemView.findViewById(R.id.likeButton);

    }

}
