package com.anibij.demoapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anibij.demoapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bsoren on 02-Jul-16.
 *
 */

public class SearchHelpAdapter extends ArrayAdapter<SearchItem>{

    private Context mContext;

    public SearchHelpAdapter(Context context, ArrayList<SearchItem> searchItems) {
        super(context, R.layout.search_help_item_layout, searchItems);
        mContext = context;
    }

    public static class SearchViewHolder{
        ImageView searchImage;
        TextView searchName;
        TextView searchText;
        TextView searchItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SearchItem searchItem =  getItem(position);
        SearchViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new SearchViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_help_item_layout, parent, false);

            viewHolder.searchImage = (ImageView) convertView.findViewById(R.id.searchImage);
            viewHolder.searchName = (TextView) convertView.findViewById(R.id.searchName);
            viewHolder.searchItem = (TextView) convertView.findViewById(R.id.searchItems);
            viewHolder.searchText = (TextView) convertView.findViewById(R.id.searchText);

            convertView.setTag(viewHolder);


        }else{
            viewHolder = (SearchViewHolder) convertView.getTag();
        }

        // populate the data in the view holder
        String searchTitle = mContext.getString(R.string.search_name, searchItem.getName());
        viewHolder.searchName.setText(searchTitle);
        viewHolder.searchItem.setText(searchItem.getSearchItems());
        viewHolder.searchText.setText(searchItem.getSearchText());
        String image = searchItem.getImageName();
        int imageInt =  new Integer(image);

        Picasso.with(mContext)
                .load(imageInt)
                .into(viewHolder.searchImage);

        return convertView;
    }

}
