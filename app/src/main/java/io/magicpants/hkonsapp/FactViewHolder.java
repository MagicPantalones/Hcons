package io.magicpants.hkonsapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Erik on 18.12.2017.
 */

public class FactViewHolder extends RecyclerView.ViewHolder {
    TextView mFactsView;
    TextView mCreatorView;
    TextView mTimestampView;


    public FactViewHolder(View itemView) {
        super(itemView);
        mFactsView = itemView.findViewById(R.id.fact_text);
        mCreatorView = itemView.findViewById(R.id.creator_text);
        mTimestampView = itemView.findViewById(R.id.timestamp_text);

    }


}
