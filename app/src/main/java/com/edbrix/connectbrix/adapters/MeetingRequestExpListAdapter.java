package com.edbrix.connectbrix.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.data.UserMeeting;
import com.edbrix.connectbrix.data.UserMeetingsDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MeetingRequestExpListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    // child data in format of header title, child title

    //private MeetingListData meetingListData;
    private ArrayList<UserMeetingsDate> userMeetingsDateListAdapter;

    /*private ArrayList<UserMeetingsDate> originalList;*/

    private OnButtonClickActionListener onButtonClickActionListener;

    public interface OnButtonClickActionListener {
        public void onChildItemClicked(UserMeeting usermeeting, int position, String status);
    }

    public MeetingRequestExpListAdapter(Context _context, ArrayList<UserMeetingsDate> userMeetingsDateListtemp, OnButtonClickActionListener onButtonClickActionListener) {
        this._context = _context;
        this.userMeetingsDateListAdapter = userMeetingsDateListtemp;
        //this.originalList = userMeetingsDateListtemp;
        this.onButtonClickActionListener = onButtonClickActionListener;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        //return meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosititon);
        return userMeetingsDateListAdapter.get(groupPosition).getUserMeetings().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //return Long.parseLong(meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId());
        return Long.parseLong(userMeetingsDateListAdapter.get(groupPosition).getUserMeetings().get(childPosition).getId());
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final UserMeeting userMeeting = (UserMeeting) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_meeting_request_list, null);
        }
        LinearLayout ll_mainMeetingList = (LinearLayout) convertView.findViewById(R.id.ll_mainMeetingList);
        //LinearLayout myMeetingList = (LinearLayout) convertView.findViewById(R.id.myMeetingList);
        TextView textViewMeetingDay = (TextView) convertView.findViewById(R.id.textViewMeetingDay);
        TextView textViewMeetingMonth = (TextView) convertView.findViewById(R.id.textViewMeetingMonth);
        TextView textViewMeetingName = (TextView) convertView.findViewById(R.id.textViewMeetingName);
        TextView textViewAgenda = (TextView) convertView.findViewById(R.id.textViewAgenda);
        TextView textViewMeetingTime = (TextView) convertView.findViewById(R.id.textViewMeetingTime);

        //TextView textViewPartycipentCount = (TextView) convertView.findViewById(R.id.textViewPartycipentCount);

        //Button btn_accept = (Button) convertView.findViewById(R.id.btn_accept);
        //Button btn_reject = (Button) convertView.findViewById(R.id.btn_reject);

        RelativeLayout btn_accept = (RelativeLayout) convertView.findViewById(R.id.btn_accept);
        RelativeLayout btn_reject = (RelativeLayout) convertView.findViewById(R.id.btn_reject);

        ImageView meetingRequest_imageView_accept = (ImageView) convertView.findViewById(R.id.meetingRequest_imageView_accept);
        TextView meetingRequest_textView_accept = (TextView) convertView.findViewById(R.id.meetingRequest_textView_accept);

        ImageView meetingRequest_imageView_reject = (ImageView) convertView.findViewById(R.id.meetingRequest_imageView_reject);
        TextView meetingRequest_textView_reject = (TextView) convertView.findViewById(R.id.meetingRequest_textView_reject);


        String day = "", monthString = "";
        try {
            SimpleDateFormat dateFormatprev = new SimpleDateFormat("dd/MMM/yyyy");
            Date d = dateFormatprev.parse(userMeeting.getMeetingDate().toString());
            day = (String) DateFormat.format("dd", d); // 20
            monthString = (String) DateFormat.format("MMM", d); // Jun
        } catch (Exception ex) {

        }
        notifyDataSetChanged();

        textViewMeetingDay.setText(day);
        textViewMeetingMonth.setText(monthString);
        textViewMeetingName.setText(userMeeting.getTitle());
        textViewAgenda.setText(userMeeting.getAgenda());

        String[] meetingTime = userMeeting.getMeetingDate().split(" ");

        textViewMeetingTime.setText(meetingTime[1] + " " + meetingTime[2]);

        /*if (userMeeting.getIsAvailable().equals("2")) {
            btn_reject.setText("Rejected");
            btn_reject.setEnabled(false);
        } else {
            btn_reject.setText("Reject");
            btn_reject.setEnabled(true);
        }

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickActionListener.onChildItemClicked(userMeeting, childPosition, "1");
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickActionListener.onChildItemClicked(userMeeting, childPosition, "2");
            }
        });*/

        if (userMeeting.getIsAvailable().equals("2")) {
            meetingRequest_textView_reject.setText("Rejected");
            meetingRequest_textView_reject.setTextColor(Color.parseColor("#b6b6b6"));//#b6b6b6
            meetingRequest_imageView_reject.setImageResource(R.drawable.rejected_status_in_fade_effect);
            //meetingRequest_imageView_reject.setBackgroundResource(R.drawable.rejected_status_in_fade_effect);
            btn_reject.setEnabled(false);
        } else {
            //meetingRequest_imageView_reject.setImageResource(R.drawable.rejected_status);
            meetingRequest_textView_reject.setText("Reject");
            //meetingRequest_textView_reject.setTextColor(Color.parseColor("#b6b6b6"));
            btn_reject.setEnabled(true);
        }

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickActionListener.onChildItemClicked(userMeeting, childPosition, "1");
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickActionListener.onChildItemClicked(userMeeting, childPosition, "2");
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return userMeetingsDateListAdapter.get(groupPosition).getUserMeetings().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return userMeetingsDateListAdapter.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return userMeetingsDateListAdapter.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        UserMeetingsDate userMeetingsDate = (UserMeetingsDate) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_meeting_request_list_seprator, null);
            convertView.setClickable(false);
        }
        TextView textView_Date = (TextView) convertView.findViewById(R.id.textView_Date);
        TextView textView_Count = (TextView) convertView.findViewById(R.id.textView_Count);

        String[] meetingDate = userMeetingsDate.getDate().split(" ");

        textView_Date.setText(meetingDate[0]);
        textView_Date.setTypeface(null, Typeface.BOLD);

        textView_Count.setTypeface(null, Typeface.BOLD);
        textView_Count.setText("" + userMeetingsDate.getMeetingCount());//Total meetings:
        notifyDataSetChanged();

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    /*public void filterData(String query) {

        query = query.toLowerCase();
        Log.v("SchoolExpListAdapter", String.valueOf(userMeetingsDateListAdapter.size()));
        userMeetingsDateListAdapter = new ArrayList<UserMeetingsDate>();

        if (query.isEmpty()) {
            userMeetingsDateListAdapter.addAll(originalList);
        } else {

            //UserMeeting userMeeting
            for (UserMeetingsDate userMeetingsDate : originalList) {
                ArrayList<UserMeeting> countryList = userMeetingsDate.getUserMeetings();
                ArrayList<UserMeeting> newList = new ArrayList<UserMeeting>();

                for (UserMeeting country : countryList) {
                    if (country.getTitle().toLowerCase().contains(query) || country.getHostName().toLowerCase().contains(query)) {//
                        newList.add(country);
                    }
                }
                if (newList.size() > 0) {
                    UserMeetingsDate nContinent = new UserMeetingsDate(userMeetingsDate.getDate(), userMeetingsDate.getMeetingCount(), newList);
                    userMeetingsDateListAdapter.add(nContinent);
                }
            }
        }

        Log.v("SchoolExpListAdapter", String.valueOf(userMeetingsDateListAdapter.size()));
        notifyDataSetChanged();

    }*/

}
