package com.anibij.demoapp.search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anibij.demoapp.R;
import com.anibij.demoapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bsoren on 02-Jul-16.
 *
 */

public class SearchUserAdapter extends ArrayAdapter<User>{

    private static final String TAG = SearchUserAdapter.class.getSimpleName();
    private Context mContext;
    private List<User> mUsers;

    public SearchUserAdapter(Context context, List<User> userItems) {
        super(context, R.layout.search_user_list_layout, userItems);
        mContext = context;
        Log.d(TAG,"created SearchUserAdapter");
    }

    public static class SearchViewHolder{
        ImageView userProfileImage;
        TextView userName;
        TextView userScreenName;
        TextView userLastestStatus;
    }

    public void setData(List<User> data) {

        if (data != null && data.size() != 0) {
            addAll(data);
            notifyDataSetChanged();
        }

    }

    /*
    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public int getCount() {
        return (mUsers==null?0:mUsers.size());
    }
   */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User searchItem =  getItem(position);
        SearchViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new SearchViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_user_list_layout, parent, false);

            viewHolder.userProfileImage = (ImageView) convertView.findViewById(R.id.user_profile_image);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.userScreenName = (TextView) convertView.findViewById(R.id.user_screen_name);
            viewHolder.userLastestStatus = (TextView) convertView.findViewById(R.id.userLatestStatus);

            convertView.setTag(viewHolder);


        }else{
            viewHolder = (SearchViewHolder) convertView.getTag();
        }

        // populate the data in the view holder
        viewHolder.userName.setText(searchItem.getName());
        viewHolder.userScreenName.setText(searchItem.getScreenName());
        viewHolder.userLastestStatus.setText(searchItem.getLatestStatus());
        String image = searchItem.getProfileImageUrl();

        Picasso.with(mContext)
                .load(image)
                .into(viewHolder.userProfileImage);

        return convertView;
    }
}
