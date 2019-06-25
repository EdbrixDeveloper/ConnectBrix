package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.activities.MeetingDetailsActivity;
import com.edbrix.connectbrix.commons.AlertDialogManager;
import com.edbrix.connectbrix.commons.DialogManager;
import com.edbrix.connectbrix.commons.ToastMessage;
import com.edbrix.connectbrix.data.MeetingDetailsData;
import com.edbrix.connectbrix.data.ParticipantList;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantsListAdapter extends BaseAdapter {

    private static final String TAG = ParticipantsListAdapter.class.getName();
    private Activity participantListActivity;
    ArrayList<ParticipantList> participantList;
    private static LayoutInflater inflater = null;
    private AlertDialogManager alertDialogManager;
    private String meetingDbId = "", IsHost = "", UserType = "";
    SessionManager sessionManager;
    ToastMessage toastMessage;
    DialogManager dialogManager;

    private OnButtonActionListener onButtonActionListener;

    public interface OnButtonActionListener {
        public void onButtonClicked(String ParticipantName, String RecordId, int position);
    }

    public ParticipantsListAdapter(Activity participantListActivity, ArrayList<ParticipantList> participantList, String UserType, String meetingDbId, String IsHost, OnButtonActionListener onButtonActionListener) {
        this.participantListActivity = participantListActivity;
        this.participantList = participantList;
        this.UserType = UserType;
        this.meetingDbId = meetingDbId;
        this.IsHost = IsHost;
        inflater = (LayoutInflater) participantListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        alertDialogManager = new AlertDialogManager(participantListActivity);
        sessionManager = new SessionManager(participantListActivity);
        toastMessage = new ToastMessage(participantListActivity);
        dialogManager = new DialogManager(participantListActivity);
        this.onButtonActionListener = onButtonActionListener;
    }

    @Override
    public int getCount() {
        return participantList.size();
    }

    @Override
    public Object getItem(int position) {
        return participantList.get(position);
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
            view = inflater.inflate(R.layout.activity_participant_list_item, null);
            holder = new ViewHolder();
            holder.partcipantImage = (CircleImageView) view.findViewById(R.id.imgParticipant);
            holder.participantName = (TextView) view.findViewById(R.id.txtParticipantName);
            holder.organizationName = (TextView) view.findViewById(R.id.txtorganizationName);
            holder.txtIsAvaliable = (TextView) view.findViewById(R.id.txtIsAvaliable);
            holder.remove = (ImageView) view.findViewById(R.id.imgRemove);
            holder.status = (ImageView) view.findViewById(R.id.imgStatus);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //ParticipantList participantList=this.participantList.get(position).

        if ((UserType.equals("T") || UserType.equals("A")) && IsHost.equals("1")) {
            holder.remove.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.VISIBLE);
            holder.txtIsAvaliable.setVisibility(View.VISIBLE);
        }

        //holder.participantName.setText(participantList.get(position).toString());
        //holder.organizationName.setText("The World Talent Organization");

        holder.participantName.setText(participantList.get(position).getName());

        if (participantList.get(position).getName().isEmpty()) {
            holder.participantName.setText(participantList.get(position).getEmail());
        }

        holder.organizationName.setText(participantList.get(position).getOrgName());
        if (participantList.get(position).getStatus().equals("0")) {
            holder.txtIsAvaliable.setText("Waiting for accept your invitation");
            holder.txtIsAvaliable.setTextColor(Color.parseColor("#bdbdbd"));
            holder.status.setImageResource(R.drawable.waiting);
        } else if (participantList.get(position).getStatus().equals("1")) {
            holder.txtIsAvaliable.setText("Accepted");
            holder.txtIsAvaliable.setTextColor(Color.parseColor("#47a54b"));
            holder.status.setImageResource(R.drawable.tick_mark);
        } else if (participantList.get(position).getStatus().equals("2")) {
            holder.txtIsAvaliable.setText("Rejected");
            holder.txtIsAvaliable.setTextColor(Color.parseColor("#d1395c"));
            holder.status.setImageResource(R.drawable.rejected_status);
        }
        if (participantList.get(position).getImageUrl() != null && !participantList.get(position).getImageUrl().isEmpty()) {
            Glide.with(participantListActivity).load(participantList.get(position).getImageUrl())
                    //.apply(RequestOptions.bitmapTransform(new FitCenter()))
                    .into(holder.partcipantImage);
        } else {
            holder.partcipantImage.setImageResource(R.drawable.usersp);
        }

        //holder.txtIsAvaliable.setText(participantList.get(position).getOrgName());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ParticipantName = participantList.get(position).getName().isEmpty() ? participantList.get(position).getEmail() : participantList.get(position).getName();
                onButtonActionListener.onButtonClicked(ParticipantName, participantList.get(position).getRecordId(), position);

                /*alertDialogManager.Dialog("Conformation", "Do you want to remove "+participantList.get(position).getName()+"?", "ok", "cancel", new AlertDialogManager.onTwoButtonClickListner() {
                    @Override
                    public void onPositiveClick() {
                        removeParticipant(participantList.get(position).getRecordId(),position);
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                }).show();*/
            }
        });


        return view;
    }

    static class ViewHolder {
        CircleImageView partcipantImage;
        TextView participantName;
        TextView organizationName;
        TextView txtIsAvaliable;
        ImageView remove;
        ImageView status;
    }

    private void removeParticipant(String RecordId, int position) {
        try {
            dialogManager.showBusyProgress();
            JSONObject jo = new JSONObject();

            jo.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jo.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());
            jo.put("MeetingId", meetingDbId);
            jo.put("RecordId", RecordId);

            Log.i(MeetingDetailsActivity.class.getName(), Constants.deleteMeetingParticipant + "\n\n" + jo.toString());

            GsonRequest<MeetingDetailsData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.deleteMeetingParticipant, jo.toString(), MeetingDetailsData.class,
                    new Response.Listener<MeetingDetailsData>() {
                        @Override
                        public void onResponse(@NonNull MeetingDetailsData response) {
                            if (response.getError() != null) {
                                toastMessage.showToast(response.getError().getErrorMessage());
                            } else {
                                dialogManager.hideBusyProgress();
                                if (response.getSuccess() == 1) {
                                    toastMessage.showToast("Removed " + participantList.get(position).getName());
                                    participantList.remove(position);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialogManager.hideBusyProgress();

                }
            });
            getAssignAvailabilityLearnersListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getAssignAvailabilityLearnersListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "deleteMeetingParticipant");

        } catch (JSONException e) {
            dialogManager.hideBusyProgress();
            toastMessage.showToast("Something went wrong. Please try again later.");
        }

    }
}
