package com.hapis.customer.ui.view;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.hapis.customer.HapisApplication;
import com.hapis.customer.R;
import com.hapis.customer.database.repository.AppointmentRepository;
import com.hapis.customer.ui.callback.GetAppointmentDoctorDetailsCallBack;
import com.hapis.customer.ui.callback.GetAppointmentEnterpriseDetailsCallBack;
import com.hapis.customer.ui.models.HapisModel;
import com.hapis.customer.ui.models.ResponseStatus;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;
import com.hapis.customer.ui.models.appointments.AppointmentResponseList;
import com.hapis.customer.ui.models.enterprise.EnterpriseRequest;
import com.hapis.customer.ui.models.enums.AppointmentStatusEnum;
import com.hapis.customer.ui.models.users.UserRequest;

import java.util.List;

public class ClosedAppointmentSchedulesFragmentViewModal extends BaseViewModal<ClosedAppointmentSchedulesFragmentView> {

    private String TAG = ClosedAppointmentSchedulesFragmentViewModal.class.getName();

    private AppointmentRepository appointmentRepository;

    public ClosedAppointmentSchedulesFragmentViewModal(LifecycleOwner owner) {
        super(owner);

        appointmentRepository = new AppointmentRepository();
    }

    @Override
    protected void initObservableData() {

    }

    @Override
    protected void handleChangedDataModal(HapisModel data) {

    }

    public void loadPatientHistory() {
        MutableLiveData<AppointmentResponseList> appointmentResponseListMutableLiveData = new MutableLiveData<>();

        appointmentRepository.new GetUserDetails(appointmentResponseListMutableLiveData).execute();
        appointmentResponseListMutableLiveData.observe(mOwner, new Observer<AppointmentResponseList>() {
            @Override
            public void onChanged(@Nullable AppointmentResponseList appointmentResponseList) {
                mView.loadPatientHistory(appointmentResponseList.getResults());
            }
        });
    }
}
