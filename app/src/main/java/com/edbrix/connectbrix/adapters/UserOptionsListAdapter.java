package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.activities.UserProfileActivity;

import java.util.List;

public class UserOptionsListAdapter extends BaseAdapter {

    Activity userProfileActivity;
    List<String> userOptions;
    List<Integer> userOptionsImages;
    private static LayoutInflater inflater = null;

    public UserOptionsListAdapter(UserProfileActivity userProfileActivity, List<String> userOptions, List<Integer> userOptionsImages) {
        this.userProfileActivity = userProfileActivity;
        this.userOptions = userOptions;
        this.userOptionsImages = userOptionsImages;
        inflater = (LayoutInflater) userProfileActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return userOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.user_profile_list_item, null);
            holder = new ViewHolder();

            holder.userOption = (TextView) view.findViewById(R.id.txtUserOpntionName);
            holder.optionImage = (ImageView)view.findViewById(R.id.imgUserOption);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.userOption.setText(userOptions.get(position).toString());
        holder.optionImage.setImageResource(userOptionsImages.get(position).intValue());

        return view;
    }

    static class ViewHolder {
        TextView userOption;
        ImageView optionImage;
    }
}
