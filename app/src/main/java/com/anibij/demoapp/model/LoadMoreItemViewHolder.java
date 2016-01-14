package com.anibij.demoapp.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anibij.demoapp.R;

/**
 * Created by bsoren on 16-Dec-15.
 */
public class LoadMoreItemViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout mLinearLayout;
    public TextView mTextView;

    public LoadMoreItemViewHolder(View itemView) {
        super(itemView);

        mLinearLayout = (LinearLayout)itemView.findViewById(R.id.loadMoreItemsLayout);
        mTextView = (TextView) itemView.findViewById(R.id.load_more_items_text);

    }
}
