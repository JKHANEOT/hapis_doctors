package com.hapis.customer.ui.adapters;

import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hapis.customer.R;
import com.hapis.customer.networking.json.JSONAdaptor;
import com.hapis.customer.ui.BaseFragmentActivity;
import com.hapis.customer.ui.ConsultationSummaryActivity;
import com.hapis.customer.ui.adapters.datamodels.DateItem;
import com.hapis.customer.ui.adapters.datamodels.GroupDataGeneralItem;
import com.hapis.customer.ui.adapters.datamodels.GroupDataListItem;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;
import com.hapis.customer.utils.DateUtil;
import com.hapis.customer.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Javeed on 2/27/2018.
 */

public class ClosedAppointmentSchedulesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    https://krtkush.github.io/2016/07/08/android-recyclerview-grouping-data.html

    private List<GroupDataListItem> consolidatedList = new ArrayList<>();
    private BaseFragmentActivity baseFragmentActivity;

    public ClosedAppointmentSchedulesRecyclerViewAdapter(BaseFragmentActivity baseFragmentActivity, List<GroupDataListItem> consolidatedList, ClosedAppointmentScheduleAdapterListeners closedAppointmentScheduleAdapterListeners) {
        this.consolidatedList = consolidatedList;
        mClosedAppointmentScheduleAdapterListeners = closedAppointmentScheduleAdapterListeners;
        this.baseFragmentActivity = baseFragmentActivity;
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return consolidatedList != null ? consolidatedList.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case GroupDataListItem.TYPE_GENERAL:
                View v1 = inflater.inflate(R.layout.patient_appointment_history_info_row, parent, false);
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
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {

            case GroupDataListItem.TYPE_GENERAL: {
                final GroupDataGeneralItem generalItem
                        = (GroupDataGeneralItem) consolidatedList.get(position);
                final GeneralViewHolder generalViewHolder
                        = (GeneralViewHolder) viewHolder;

                final AppointmentRequest appointmentRequest = (AppointmentRequest)generalItem.getHapisModel();

                final Date date = new Date(appointmentRequest.getCheckInTime());

                String dayAsString = DateUtil.convertDateToDateStr(date, DateUtil.DATE_FORMAT_EEE);
                String dateAsString = DateUtil.convertDateToDateStr(date, DateUtil.DATE_FORMAT_dd);
                String yearAsString = DateUtil.convertDateToDateStr(date, DateUtil.DATE_FORMAT_MMM_YYYY);
                String timeAsString = DateUtil.convertDateToDateStr(date, DateUtil.DATE_FORMAT_HH_mm_am_pm);

                if(dayAsString != null){
                    generalViewHolder.appointment_day_tv.setText(dayAsString);
                }

                if(dateAsString != null){
                    generalViewHolder.appointment_date_tv.setText(dateAsString);
                }

                if(yearAsString != null){
                    generalViewHolder.appointment_month_year_tv.setText(yearAsString);
                }

                if(timeAsString != null){
                    generalViewHolder.appointment_time_tv.setText("Appointment at "+timeAsString);
                    generalViewHolder.appointment_time_tv.setGravity(Gravity.START);
                }

                if(appointmentRequest.getNotes() != null && appointmentRequest.getNotes().length() > 0){
                    generalViewHolder.appointment_doctor_notes_tv.setText("Notes: "+appointmentRequest.getNotes());
                    generalViewHolder.appointment_doctor_notes_tv.setGravity(Gravity.START);
                }

                if(appointmentRequest.getFee() != null)
                    generalViewHolder.appointment_fees_tv.setText("Fee: "+Util.getFormattedAmount(appointmentRequest.getFee()));
                generalViewHolder.appointment_fees_tv.setGravity(Gravity.START);

                generalViewHolder.patient_appointment_history_info_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            Intent intent = new Intent(baseFragmentActivity, ConsultationSummaryActivity.class);
                            intent.putExtra(ConsultationSummaryActivity.APPOINTMENT_DETAILS_TAG, JSONAdaptor.toJSON(appointmentRequest));
                            baseFragmentActivity.startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

                break;
            }

            case GroupDataListItem.TYPE_DATE: {
                final DateItem dateItem
                        = (DateItem) consolidatedList.get(position);
                final DateViewHolder dateViewHolder
                        = (DateViewHolder) viewHolder;
                dateViewHolder.date_tv.setText(dateItem.getDate());

                break;
            }
        }
    }

    private void showPopupMenu(View view,int position,AppointmentRequest appointmentRequest) {
//        http://stackoverflow.com/questions/34641240/toolbar-inside-cardview-to-create-a-popup-menu-overflow-icon
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.history_appointments_item_overflow_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, appointmentRequest));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int mPosition;
        private AppointmentRequest mAppointmentRequest;

        public MyMenuItemClickListener(int positon, AppointmentRequest appointmentRequest) {
            mPosition = positon;
            mAppointmentRequest = appointmentRequest;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {

                case R.id.menu_followup: {
                    mClosedAppointmentScheduleAdapterListeners.followupAppointment(mAppointmentRequest, mPosition);
                    return true;
                }
                default:
            }
            return false;
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

        private AppCompatTextView patient_appointment_history_tv, view_more_patient_appointment_history_tv;
        private LinearLayout patient_appointment_history_info_rl;
        private AppCompatTextView appointment_day_tv, appointment_date_tv, appointment_month_year_tv, appointment_time_tv, appointment_doctor_notes_tv, appointment_fees_tv;
        private AppCompatImageView view_appointment_details_iv;

        public GeneralViewHolder(View v) {
            super(v);

            patient_appointment_history_tv = v.findViewById(R.id.patient_appointment_history_tv);
            view_more_patient_appointment_history_tv = v.findViewById(R.id.view_more_patient_appointment_history_tv);

            patient_appointment_history_info_rl = v.findViewById(R.id.patient_appointment_history_info_rl);

            appointment_day_tv = v.findViewById(R.id.appointment_day_tv);
            appointment_date_tv = v.findViewById(R.id.appointment_date_tv);
            appointment_month_year_tv = v.findViewById(R.id.appointment_month_year_tv);
            appointment_time_tv = v.findViewById(R.id.appointment_time_tv);
            appointment_doctor_notes_tv = v.findViewById(R.id.appointment_doctor_notes_tv);
            appointment_fees_tv = v.findViewById(R.id.appointment_fees_tv);
            view_appointment_details_iv = v.findViewById(R.id.view_appointment_details_iv);
        }
    }

    private ClosedAppointmentScheduleAdapterListeners mClosedAppointmentScheduleAdapterListeners;

    public interface ClosedAppointmentScheduleAdapterListeners {
        void followupAppointment(final AppointmentRequest appointmentRequest, int selectedIndex);
        void viewConsultationDetails(final AppointmentRequest appointmentRequest, int selectedIndex);
    }
}
