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
import com.hapis.customer.ui.models.appointments.AppointmentResponseList;
import com.hapis.customer.utils.DateUtil;

import java.util.Date;

public class UpComingSchedulesFragmentViewModal extends BaseViewModal<UpComingSchedulesFragmentView> {

    private String TAG = UpComingSchedulesFragmentViewModal.class.getName();

    private AppointmentRepository appointmentRepository;

    public UpComingSchedulesFragmentViewModal(LifecycleOwner owner) {
        super(owner);

        appointmentRepository = new AppointmentRepository();
    }

    @Override
    protected void initObservableData() {

    }

    @Override
    protected void handleChangedDataModal(HapisModel data) {

    }

    private GetAppointmentDoctorDetailsCallBack getAppointmentDetailsCallBack;
    private GetAppointmentEnterpriseDetailsCallBack getAppointmentEnterpriseDetailsCallBack;

    public void getUpcomingAppointments(){
        MutableLiveData<AppointmentResponseList> mutableLiveData = new MutableLiveData<>();

        final String appointmentDate = DateUtil.convertDateToDateStr(new Date(), DateUtil.DATE_FORMAT_dd_MM_yyyy_SEP_HIPHEN);

        appointmentRepository.new GetAppointmentsForDoctorTask(mutableLiveData, appointmentDate).execute();
        mutableLiveData.observe(mOwner, new Observer<AppointmentResponseList>() {
            @Override
            public void onChanged(@Nullable AppointmentResponseList userModelResponse) {
                if(userModelResponse != null){
                    if(userModelResponse.getStatus() != null && userModelResponse.getStatus().getStatusCode() != null && userModelResponse.getStatus().getStatusCode().intValue() == ResponseStatus.SUCCESS && userModelResponse.getResults() != null && userModelResponse.getResults().size() > 0){
                        mView.showUpComingAppointments(userModelResponse.getResults());
                    }else{
                        mView.failedToProcess(((userModelResponse.getStatus().getErrorMessages() != null && userModelResponse.getStatus().getErrorMessages().size() > 0) ? userModelResponse.getStatus().getErrorMessages().get(0).getMessageDescription() : HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request)));
                    }
                }else{
                    mView.failedToProcess(HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request));
                }
            }
        });
    }
}
