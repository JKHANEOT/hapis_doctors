package com.hapis.customer.ui.adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hapis.customer.HapisApplication;
import com.hapis.customer.R;
import com.hapis.customer.ui.adapters.datamodels.DateItem;
import com.hapis.customer.ui.adapters.datamodels.GroupDataGeneralItem;
import com.hapis.customer.ui.adapters.datamodels.GroupDataListItem;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;
import com.hapis.customer.ui.utils.HapisSlotUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javeed on 2/27/2018.
 */

public class UpComingSchedulesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    https://krtkush.github.io/2016/07/08/android-recyclerview-grouping-data.html

    private List<GroupDataListItem> mConsolidatedList = new ArrayList<>();

    public UpComingSchedulesRecyclerViewAdapter(List<GroupDataListItem> consolidatedList, UpComingScheduleAdapterListeners upComingScheduleAdapterListeners) {
        mConsolidatedList.addAll(consolidatedList);
        mUpComingScheduleAdapterListeners = upComingScheduleAdapterListeners;
    }

    @Override
    public int getItemViewType(int position) {
        return mConsolidatedList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mConsolidatedList != null ? mConsolidatedList.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case GroupDataListItem.TYPE_GENERAL:
                View v1 = inflater.inflate(R.layout.doctor_current_day_appointment_adapter_row, parent, false);
                viewHolder = new GeneralViewHolder(v1);
                break;

            case GroupDataListItem.TYPE_DATE:
                View v2 = inflater.inflate(R.layout.grouped_data_date, parent, false);
                viewHolder = new DateViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        switch (viewHolder.getItemViewType()) {

            case GroupDataListItem.TYPE_GENERAL: {
                final GroupDataGeneralItem generalItem
                        = (GroupDataGeneralItem) mConsolidatedList.get(position);
                final GeneralViewHolder generalViewHolder
                        = (GeneralViewHolder) viewHolder;

                final AppointmentRequest appointmentRequest = (AppointmentRequest)generalItem.getHapisModel();

                if(appointmentRequest.getPatientName() != null)
                    generalViewHolder.patient_name_title_tv.setText("Name : "+appointmentRequest.getPatientName());

                if(appointmentRequest.getPatientGender() != null) {
                    generalViewHolder.patient_gender_tv.setText("Gender : " + appointmentRequest.getPatientGender());
                    if(appointmentRequest.getPatientGender().equalsIgnoreCase("Male"))
                        generalViewHolder.patient_icon.setImageDrawable(generalViewHolder.patient_icon.getResources().getDrawable(R.drawable.ic_default_avatar_male));
                    else
                        generalViewHolder.patient_icon.setImageDrawable(generalViewHolder.patient_icon.getResources().getDrawable(R.drawable.ic_default_avatar_female));
                }

                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(appointmentRequest.getAppointmentDate());
                if(appointmentRequest.getSlotBooked() != null && appointmentRequest.getSlotBooked().intValue() > 0) {
                    stringBuilder.append(" at ");
                    stringBuilder.append(HapisSlotUtils.getSlotName(appointmentRequest.getSlotBooked()));
                }

                if(appointmentRequest.getState() != null && appointmentRequest.getState().intValue() > 0) {
                    switch (appointmentRequest.getState().intValue()){
                        case 601:{
                            generalViewHolder.top_rl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mUpComingScheduleAdapterListeners.viewClicked(appointmentRequest);
                                }
                            });
                            generalViewHolder.bottom_rl.setVisibility(View.VISIBLE);
                            generalViewHolder.reschedule_consultation_tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mUpComingScheduleAdapterListeners.rescheduleAppointment(appointmentRequest, position);
                                }
                            });
                            generalViewHolder.cancel_consultation_tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mUpComingScheduleAdapterListeners.cancelAppointment(appointmentRequest, position);
                                }
                            });
                            generalViewHolder.consultation_status_indicator_iv.setImageDrawable(HapisApplication.getApplication().getResources().getDrawable(R.drawable.not_yet_consulted_indicator));
                            break;
                        }
                        case 602:{
                            generalViewHolder.bottom_rl.setVisibility(View.GONE);
                            generalViewHolder.top_rl.setOnClickListener(null);
                            generalViewHolder.reschedule_consultation_tv.setOnClickListener(null);
                            generalViewHolder.cancel_consultation_tv.setOnClickListener(null);
                            generalViewHolder.consultation_status_indicator_iv.setImageDrawable(HapisApplication.getApplication().getResources().getDrawable(R.drawable.consultation_cancelled_indicator));
                            break;
                        }
                        case 603:{
                            generalViewHolder.bottom_rl.setVisibility(View.GONE);
                            generalViewHolder.top_rl.setOnClickListener(null);
                            generalViewHolder.reschedule_consultation_tv.setOnClickListener(null);
                            generalViewHolder.cancel_consultation_tv.setOnClickListener(null);
                            generalViewHolder.consultation_status_indicator_iv.setImageDrawable(HapisApplication.getApplication().getResources().getDrawable(R.drawable.consulted_indicator));
                            break;
                        }
                    }
                }

                generalViewHolder.appointment_date_tv.setText(stringBuilder.toString());

                break;
            }

            case GroupDataListItem.TYPE_DATE: {
                final DateItem dateItem
                        = (DateItem) mConsolidatedList.get(position);
                final DateViewHolder dateViewHolder
                        = (DateViewHolder) viewHolder;
                dateViewHolder.date_tv.setText(dateItem.getDate());

                break;
            }
        }
    }

    // ViewHolder for date row item
    class DateViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView date_tv;

        public DateViewHolder(View v) {
            super(v);

            date_tv = v.findViewById(R.id.date_tv);
        }
    }

    // View holder for general row item
    class GeneralViewHolder extends RecyclerView.ViewHolder {

        private ImageView patient_icon;
        private TextView patient_name_title_tv, patient_gender_tv, appointment_date_tv;
        private AppCompatImageView consultation_status_indicator_iv;
        private RelativeLayout top_rl;
        private LinearLayout bottom_rl;
        private AppCompatTextView reschedule_consultation_tv, cancel_consultation_tv;

        public GeneralViewHolder(View v) {
            super(v);

            patient_icon = v.findViewById(R.id.patient_icon);
            patient_name_title_tv = v.findViewById(R.id.patient_name_title_tv);
            patient_gender_tv = v.findViewById(R.id.patient_gender_tv);
            appointment_date_tv = v.findViewById(R.id.appointment_date_tv);
            consultation_status_indicator_iv = v.findViewById(R.id.consultation_status_indicator_iv);

            top_rl = v.findViewById(R.id.top_rl);
            bottom_rl = v.findViewById(R.id.bottom_rl);
            reschedule_consultation_tv = v.findViewById(R.id.reschedule_consultation_tv);
            cancel_consultation_tv = v.findViewById(R.id.cancel_consultation_tv);
        }
    }

    private UpComingScheduleAdapterListeners mUpComingScheduleAdapterListeners;

    public interface UpComingScheduleAdapterListeners {
        void viewClicked(final AppointmentRequest appointmentRequest);
        void rescheduleAppointment(final AppointmentRequest appointmentRequest, int selectedIndex);
        void cancelAppointment(final AppointmentRequest appointmentRequest, int selectedIndex);
    }
}
