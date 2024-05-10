package com.example.ninjagame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class LeaderboardAdapter extends BaseAdapter {
    private final ArrayList<Map.Entry<String, String>> mData;

    static class ViewHolder {
        TextView tvPlayer;
        TextView tvScore;
    }

    public LeaderboardAdapter(Map<String, String> map) {
        mData = new ArrayList<>();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        Map.Entry<String, String> item = mData.get(position);

        return item.getKey().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        ViewHolder viewHolder;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_adapter, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvPlayer = result.findViewById(android.R.id.text1);
            viewHolder.tvScore = result.findViewById(android.R.id.text2);

            result.setTag(viewHolder);

        } else {
            result = convertView;
            viewHolder = (ViewHolder) result.getTag();
        }

        Map.Entry<String, String> item = getItem(position);
        viewHolder.tvPlayer.setText(item.getKey());
        viewHolder.tvScore.setText(item.getValue());

        return result;
    }
}
