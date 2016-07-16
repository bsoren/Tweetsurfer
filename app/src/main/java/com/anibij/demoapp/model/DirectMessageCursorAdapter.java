package com.anibij.demoapp.model;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anibij.demoapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by bsoren on 15-Jul-16.
 */
public class DirectMessageCursorAdapter extends CursorAdapter {

    private static final String TAG = DirectMessageCursorAdapter.class.getSimpleName();

    public DirectMessageCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = mLayoutInflater.inflate(R.layout.list_row_messages, parent, false);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.createAtView = (TextView) convertView.findViewById(R.id.created_at);
        viewHolder.senderImageView = (ImageView) convertView.findViewById(R.id.user_profile_image);
        viewHolder.senderNameView = (TextView) convertView.findViewById(R.id.userName);
        viewHolder.senderScreenNameView = (TextView) convertView.findViewById(R.id.user_screen_name);
        viewHolder.textMessageView = (TextView) convertView.findViewById(R.id.userLatestStatus);
        convertView.setTag(viewHolder);

        return convertView;
    }


    public class ViewHolder {

        TextView createAtView, senderNameView, senderScreenNameView, textMessageView;
        ImageView senderImageView;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) convertView.getTag();
        Log.d(TAG, "cursor count " + cursor.getCount());

        String dateString = DateUtils.getRelativeTimeSpanString
                (cursor.getLong(cursor.getColumnIndex(StatusContract.DirectMessage.Column.CREATED_AT))).toString();
        holder.createAtView.setText(dateString);
        holder.senderNameView.setText(cursor.getString(cursor.getColumnIndex(StatusContract.DirectMessage.Column.SENDER_NAME)));
        holder.senderScreenNameView.setText(cursor.getString(cursor.getColumnIndex(StatusContract.DirectMessage.Column.SENDER_SCREEN_NAME)));
        holder.textMessageView.setText(cursor.getString(cursor.getColumnIndex(StatusContract.DirectMessage.Column.TEXT_MESSAGE)));
        Picasso.with(mContext).load(cursor.getString(cursor.getColumnIndex(StatusContract.DirectMessage.Column.SENDER_IMAGE_URL)))
                .error(R.drawable.no_image)
                .placeholder(R.drawable.twitter_placeholder_image).into(holder.senderImageView);

    }
}
