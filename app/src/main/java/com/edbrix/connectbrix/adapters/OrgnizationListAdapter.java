package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.activities.OrgnizationListActivity;
import com.edbrix.connectbrix.data.UserOrganizationListData;

import java.util.ArrayList;

public class OrgnizationListAdapter extends BaseAdapter {

    private static final String TAG = OrgnizationListAdapter.class.getName();
    private Activity orgnizationListActivity;
    ArrayList<UserOrganizationListData> userOrganizationListData;
    private static LayoutInflater inflater = null;

    public OrgnizationListAdapter(OrgnizationListActivity orgnizationListActivity, ArrayList<UserOrganizationListData> userOrganizationListData){
        this.orgnizationListActivity = orgnizationListActivity;
        this.userOrganizationListData = userOrganizationListData;
        inflater = (LayoutInflater) orgnizationListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userOrganizationListData.size();
    }

    @Override
    public Object getItem(int position) {
        return userOrganizationListData.get(position);
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
            view = inflater.inflate(R.layout.orgnization_list_item, null);
            holder = new ViewHolder();

            holder.orgnizationImage = (ImageView)view.findViewById(R.id.imgOrgnization);
            holder.orgnizationName = (TextView)view.findViewById(R.id.txtOrgnizationName);
            holder.arrow = (ImageView)view.findViewById(R.id.arrow);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

       // Glide.with(orgnizationListActivity).load(userOrganizationListData.get(position).getSchoolLogoUrl()).into( holder.orgnizationImage);
        Glide.with(orgnizationListActivity).load(userOrganizationListData.get(position).getSchoolLogoUrl())
                //.apply(RequestOptions.bitmapTransform(new FitCenter()))
                .into(holder.orgnizationImage);

        /* holder.orgnizationImage.setImageURI(Uri.parse(userOrganizationListData.get(position).getSchoolLogoUrl()));*/
        holder.orgnizationName.setText(userOrganizationListData.get(position).getOrganizationName());

        return view;
    }

    static class ViewHolder {
        ImageView orgnizationImage;
        TextView orgnizationName;
        ImageView arrow;

    }
}
