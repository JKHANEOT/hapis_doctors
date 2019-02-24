package com.hapis.customer.ui.view;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.hapis.customer.HapisApplication;
import com.hapis.customer.R;
import com.hapis.customer.database.repository.AppointmentRepository;
import com.hapis.customer.ui.models.HapisModel;
import com.hapis.customer.ui.models.ResponseStatus;
import com.hapis.customer.ui.models.appointments.AppointmentBaseResponse;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;
import com.hapis.customer.ui.models.appointments.AppointmentResponseList;

public class ConsultationFragmentViewModal extends BaseViewModal<ConsultationFragmentView> {

    private String TAG = ConsultationFragmentViewModal.class.getName();

    private AppointmentRepository appointmentRepository;

    public ConsultationFragmentViewModal(LifecycleOwner owner) {
        super(owner);

        appointmentRepository = new AppointmentRepository();
    }

    @Override
    protected void initObservableData() {

    }

    @Override
    protected void handleChangedDataModal(HapisModel data) {

    }

    public void completeConsultation(AppointmentRequest appointmentRequest) {

        MutableLiveData<AppointmentBaseResponse> mutableLiveData = new MutableLiveData<>();

        appointmentRepository.updateAppointment(mutableLiveData, appointmentRequest);
        mutableLiveData.observe(mOwner, new Observer<AppointmentBaseResponse>() {
            @Override
            public void onChanged(@Nullable AppointmentBaseResponse userModelResponse) {
                if(userModelResponse != null){
                    if(userModelResponse.getStatus() != null && userModelResponse.getStatus().getStatusCode() != null && userModelResponse.getStatus().getStatusCode().intValue() == ResponseStatus.SUCCESS){
                        mView.completeConsultation(HapisApplication.getApplication().getResources().getString(R.string.consultation_completed));
                    }else{
                        mView.failedToProcess(((userModelResponse.getStatus().getErrorMessages() != null && userModelResponse.getStatus().getErrorMessages().size() > 0) ? userModelResponse.getStatus().getErrorMessages().get(0).getMessageDescription() : HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request)));
                    }
                }else{
                    mView.failedToProcess(HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request));
                }
            }
        });
    }

    public void loadPatientHistory(String doctorCode, String hospitalCode, String customerCode) {
        MutableLiveData<AppointmentResponseList> appointmentResponseListMutableLiveData = new MutableLiveData<>();

        appointmentRepository.getAppointmentsHistoryByCustomerAndDoctor(appointmentResponseListMutableLiveData, doctorCode, hospitalCode, customerCode);
        appointmentResponseListMutableLiveData.observe(mOwner, new Observer<AppointmentResponseList>() {
            @Override
            public void onChanged(@Nullable AppointmentResponseList appointmentResponseList) {
                mView.loadPatientHistory(appointmentResponseList.getResults());
            }
        });
    }
}
