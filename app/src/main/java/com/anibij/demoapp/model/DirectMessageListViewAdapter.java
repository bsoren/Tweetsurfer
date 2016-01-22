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
public class DirectMessageListViewAdapter extends BaseAdapter {

    // Declare variables
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<DirectMessage> mMessageList = null;

    public DirectMessageListViewAdapter(List<DirectMessage> messageList, Context context) {
        this.mMessageList = messageList;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            convertView = mLayoutInflater.inflate(R.layout.list_row_messages, null);
            holder = new ViewHolder();
            holder.createAtView = (TextView) convertView.findViewById(R.id.list_item_text_created_at);
            holder.recipientImageView = (ImageView) convertView.findViewById(R.id.list_item_profile_image);
            holder.recipientNameView = (TextView) convertView.findViewById(R.id.list_item_text_user);
            holder.recipientScreenNameView = (TextView) convertView.findViewById(R.id.list_item_text_screen_name);
            holder.textMessageView = (TextView) convertView.findViewById(R.id.list_item_text_message);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DirectMessage dme = mMessageList.get(position);

        String dateString = DateUtils.getRelativeTimeSpanString(dme.getCreatedAt()).toString();
        holder.createAtView.setText(dateString);
        holder.recipientNameView.setText(dme.getReceipientName());
        holder.recipientScreenNameView.setText(dme.getReceipientScreenName());
        holder.textMessageView.setText(dme.getTextMessage());
        Picasso.with(mContext).load(dme.getRecipientImageUrl())
                .error(R.drawable.no_image)
                .placeholder(R.drawable.twitter_placeholder_image).into(holder.recipientImageView);


        return convertView;

    }

    public class ViewHolder {

        TextView createAtView, recipientNameView, recipientScreenNameView, textMessageView;
        ImageView recipientImageView;
    }
}
