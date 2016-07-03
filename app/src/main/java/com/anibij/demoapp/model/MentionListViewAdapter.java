package com.anibij.demoapp.model;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anibij.demoapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bsoren on 17-Jan-16.
 */
public class MentionListViewAdapter extends BaseAdapter {

    // Declare variables
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<Status> mStatusList = null;


    public MentionListViewAdapter(Context context, List<Status> statusList) {
        this.mContext = context;
        this.mStatusList = statusList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mStatusList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStatusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_row_mentions, null);
            // locate views in list_row
            holder.userNameView = (TextView) convertView.findViewById(R.id.userName);
            holder.userScreenNameView = (TextView) convertView.findViewById(R.id.user_screen_name);
            holder.createdAtView = (TextView) convertView.findViewById(R.id.user_add_image);
            holder.messageView = (TextView) convertView.findViewById(R.id.userLatestStatus);
            // holder.retweetCountView = (TextView) convertView.findViewById(R.id.reTweetCount);
            //holder.favCountView = (TextView) convertView.findViewById(R.id.favCount);
            holder.userImageView = (ImageView) convertView.findViewById(R.id.user_profile_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set the result into text view
        Status status = mStatusList.get(position);
        holder.userNameView.setText(status.getUser());
        holder.userScreenNameView.setText(status.getScreenName());
        String dateString = DateUtils.getRelativeTimeSpanString(status.getCreatedAt()).toString();
        holder.createdAtView.setText(dateString);

        holder.messageView.setText(status.getMessage());
        //holder.retweetCountView.setText(String.valueOf(status.getRetweetCount()));
        // holder.favCountView.setText(String.valueOf(status.getFavCount()));

        Picasso.with(mContext).load(status.getProfileImageUrl())
                .error(R.drawable.no_image)
                .placeholder(R.drawable.twitter_placeholder_image).into(holder.userImageView);


        return convertView;
    }


    public class ViewHolder {
        TextView userNameView, userScreenNameView, createdAtView, messageView, retweetCountView, favCountView;
        ImageView userImageView;
    }
}
