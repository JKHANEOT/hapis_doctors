package com.hapis.customer.ui.view;

import com.hapis.customer.ui.custom.dialogplus.OnClickListener;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;

import java.util.List;

public interface UpComingSchedulesFragmentView extends BaseView {

    void showError(String errorMsg, OnClickListener onClickListener, String positiveLbl, String negativeLbl, String status);

    void failedToProcess(String errorMsg);

    void showUpComingAppointments(List<AppointmentRequest> appointmentRequests);
}
