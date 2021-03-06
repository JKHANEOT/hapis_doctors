package com.hapis.customer.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hapis.customer.R;
import com.hapis.customer.networking.json.JSONAdaptor;
import com.hapis.customer.ui.BaseFragmentActivity;
import com.hapis.customer.ui.ConsultationActivity;
import com.hapis.customer.ui.DashboardActivity;
import com.hapis.customer.ui.adapters.UpComingSchedulesRecyclerViewAdapter;
import com.hapis.customer.ui.adapters.datamodels.DateItem;
import com.hapis.customer.ui.adapters.datamodels.GroupDataGeneralItem;
import com.hapis.customer.ui.adapters.datamodels.GroupDataListItem;
import com.hapis.customer.ui.custom.dialogplus.OnClickListener;
import com.hapis.customer.ui.custom.recyclerviewanimations.RecyclerviewClickListeners;
import com.hapis.customer.ui.custom.recyclerviewanimations.animators.SlideInUpAnimator;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;
import com.hapis.customer.ui.utils.DialogIconCodes;
import com.hapis.customer.ui.view.BaseView;
import com.hapis.customer.ui.view.UpComingSchedulesFragmentView;
import com.hapis.customer.ui.view.UpComingSchedulesFragmentViewModal;
import com.hapis.customer.utils.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UpComingSchedulesFrag extends BaseAbstractFragment<UpComingSchedulesFragmentViewModal> implements UpComingSchedulesFragmentView,
        UpComingSchedulesRecyclerViewAdapter.UpComingScheduleAdapterListeners {

    private View v;
    private static final String TAG = UpComingSchedulesFrag.class.getName();
    private AppCompatTextView list_empty_tv;
    private RecyclerView upcoming_appointments_rv;
    private UpComingSchedulesRecyclerViewAdapter mAdapter;

    private LinearLayout previous_date_ll, next_date_ll;
    private TextView date_tv;
    private AppCompatImageView previous_date_bttn, next_date_bttn;

    public UpComingSchedulesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_up_coming_schedules, container, false);

        previous_date_ll = v.findViewById(R.id.previous_date_ll);
        previous_date_ll.setOnClickListener(previous_date_OnClickListener);

        previous_date_bttn = v.findViewById(R.id.previous_date_bttn);
        next_date_bttn = v.findViewById(R.id.next_date_bttn);

        next_date_ll = v.findViewById(R.id.next_date_ll);
        next_date_ll.setOnClickListener(next_date_OnClickListener);

        date_tv = v.findViewById(R.id.date_tv);

        currentDate = DateUtil.normalizeTime(new Date());

        selectedDate = DateUtil.normalizeTime(new Date());

        date_tv.setText(DateUtil.convertDateToDateStr(selectedDate, DateUtil.DATE_FORMAT_MMM_dd_yyyy));

        upcoming_appointments_rv = v.findViewById(R.id.upcoming_appointments_rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        upcoming_appointments_rv.setLayoutManager(mLayoutManager);
        upcoming_appointments_rv.setHasFixedSize(true);

        upcoming_appointments_rv.setItemAnimator(new SlideInUpAnimator());
        upcoming_appointments_rv.addItemDecoration(new RecyclerviewClickListeners.VerticalSpaceItemDecoration(10));

        list_empty_tv = v.findViewById(R.id.list_empty_tv);

        return v;
    }

    private Date selectedDate, currentDate;

    private View.OnClickListener previous_date_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            selectedDate = DateUtil.addOrSubtractDays(selectedDate, -1);
            selectedDate = DateUtil.normalizeTime(selectedDate);

            if(currentDate.compareTo(selectedDate) == 0){
                previous_date_bttn.setImageDrawable(getResources().getDrawable(R.drawable.ic_curved_arrow_left_transparent_white));
                date_tv.setText(DateUtil.convertDateToDateStr(selectedDate, DateUtil.DATE_FORMAT_MMM_dd_yyyy));

                initAndGetUpcomingAppointments(selectedDate);
            }else if(selectedDate.compareTo(currentDate) > 0){
                previous_date_bttn.setImageDrawable(getResources().getDrawable(R.drawable.ic_curved_arrow_left_white));
                date_tv.setText(DateUtil.convertDateToDateStr(selectedDate, DateUtil.DATE_FORMAT_MMM_dd_yyyy));

                initAndGetUpcomingAppointments(selectedDate);
            }else{
                selectedDate = DateUtil.addOrSubtractDays(selectedDate, 1);
                selectedDate = DateUtil.normalizeTime(selectedDate);
            }
        }
    };

    private View.OnClickListener next_date_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectedDate = DateUtil.addOrSubtractDays(selectedDate, 1);
            selectedDate = DateUtil.normalizeTime(selectedDate);
            date_tv.setText(DateUtil.convertDateToDateStr(selectedDate, DateUtil.DATE_FORMAT_MMM_dd_yyyy));

            if(selectedDate.compareTo(currentDate) > 0)
                previous_date_bttn.setImageDrawable(getResources().getDrawable(R.drawable.ic_curved_arrow_left_white));

            initAndGetUpcomingAppointments(selectedDate);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        initAndGetUpcomingAppointments(selectedDate);
    }

    private void initAndGetUpcomingAppointments(Date appointmentsDateToFetch){
        ((BaseFragmentActivity)getActivity()).showProgressDialog(getActivity(), getResources().getString(R.string.loading_items));
        mViewModal.getUpcomingAppointments(appointmentsDateToFetch);
    }

    @Override
    protected Class getViewModalClass() {
        return UpComingSchedulesFragmentViewModal.class;
    }

    @Override
    protected BaseView getViewImpl() {
        return this;
    }

    @Override
    public void showError(String errorMsg, OnClickListener onClickListener, String positiveLbl, String negativeLbl, String status) {
        ((BaseFragmentActivity)getActivity()).dismissProgressDialog();
        ((DashboardActivity)getActivity()).showError(errorMsg, onClickListener, positiveLbl, negativeLbl, status);
    }

    @Override
    public void failedToProcess(String errorMsg) {
        ((BaseFragmentActivity)getActivity()).dismissProgressDialog();
        list_empty_tv.setVisibility(View.VISIBLE);
        upcoming_appointments_rv.setVisibility(View.GONE);
    }

    @Override
    public void showUpComingAppointments(List<AppointmentRequest> appointmentRequests) {
        new RefreshDataToGroupedHashmap().execute(appointmentRequests);
    }

    class RefreshDataToGroupedHashmap extends AsyncTask<List<AppointmentRequest>, Void, List<GroupDataListItem>> {

        @Override
        protected List<GroupDataListItem> doInBackground(List<AppointmentRequest>... lists) {

            Map<String, List<AppointmentRequest>> groupedHashmap = null;
            List<GroupDataListItem> consolidatedList = null;

            List<AppointmentRequest> appointmentRequests = lists[0];

            if (appointmentRequests != null && appointmentRequests.size() > 0) {
                groupedHashmap = new TreeMap<>(Collections.reverseOrder(new DateUtil.StringDateComparator(DateUtil.DATE_FORMAT_EEE_MMM_d_YY)));
                for (AppointmentRequest appointmentRequest : appointmentRequests) {
                    if (appointmentRequest.getAppointmentDate() != null) {

                        String convertedDate = DateUtil.convertDateToDateStr(DateUtil.convertDateStrToDate(appointmentRequest.getAppointmentDate(), DateUtil.DATE_FORMAT_dd_MM_yyyy_SEP_HIPHEN), DateUtil.DATE_FORMAT_EEE_MMM_d_YY);
                        List<AppointmentRequest> groupedList = null;

                        if (groupedHashmap.containsKey(convertedDate)) {
                            groupedList = groupedHashmap.get(convertedDate);
                            groupedList.add(appointmentRequest);
                            groupedHashmap.put(convertedDate, groupedList);
                        } else {
                            groupedList = new ArrayList<>();
                            groupedList.add(appointmentRequest);
                            groupedHashmap.put(convertedDate, groupedList);
                        }
                    }
                }

                if (groupedHashmap != null && groupedHashmap.size() > 0) {
                    consolidatedList = new ArrayList<>();

                    for (String date : groupedHashmap.keySet()) {
                        DateItem dateItem = new DateItem();
                        dateItem.setDate(date);
//                        consolidatedList.add(dateItem);

                        List<AppointmentRequest> individualPaymentHistoryList = groupedHashmap.get(date);

                        for (AppointmentRequest serviceRequestInvoiceModel : individualPaymentHistoryList) {
                            GroupDataGeneralItem generalItem = new GroupDataGeneralItem();
                            generalItem.setHapisModel(serviceRequestInvoiceModel);
                            consolidatedList.add(generalItem);
                        }
                    }
                }
            }

            return consolidatedList;
        }

        @Override
        protected void onPostExecute(List<GroupDataListItem> consolidatedList) {
            ((BaseFragmentActivity)getActivity()).dismissProgressDialog();
            if (consolidatedList != null && consolidatedList.size() > 0) {
                if (upcoming_appointments_rv != null) {
                    upcoming_appointments_rv.setVisibility(View.VISIBLE);
                    list_empty_tv.setVisibility(View.GONE);
                    mAdapter = new UpComingSchedulesRecyclerViewAdapter(consolidatedList, UpComingSchedulesFrag.this);
                    upcoming_appointments_rv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void viewClicked(AppointmentRequest appointmentRequest) {

        //---------Get Patient Details : http://115.99.196.11:9000/customers/{customer_cd} : CustomerResponse -> CustomerRequest

        String appointmentJson = null;
        try {
            appointmentJson = JSONAdaptor.toJSON(appointmentRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(appointmentJson != null && appointmentJson.length() > 0){
            Intent intent = new Intent(getActivity(), ConsultationActivity.class);
            intent.putExtra(ConsultationActivity.APPOINTMENT_DETAILS_TAG, appointmentJson);
            getActivity().startActivity(intent);
        }else{
            ((DashboardActivity)getActivity()).showError("Appointment details not found.", null, getResources().getString(R.string.ok), null, DialogIconCodes.DIALOG_NOT_AVAILABLE.getIconCode());
        }
    }

    @Override
    public void rescheduleAppointment(AppointmentRequest appointmentRequest, int selectedIndex) {
        Toast.makeText(getActivity(), "Clicked to reschedule", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelAppointment(AppointmentRequest appointmentRequest, int selectedIndex) {
        Toast.makeText(getActivity(), "Clicked to cancel", Toast.LENGTH_SHORT).show();
    }
}
