package com.hapis.customer.ui.view;

import android.arch.lifecycle.LifecycleOwner;

import com.hapis.customer.database.repository.AppointmentRepository;
import com.hapis.customer.ui.models.HapisModel;

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

    public void completeConsultation() {

        /*MutableLiveData<AppointmentBaseResponse> mutableLiveData = new MutableLiveData<>();

        appointmentRepository.new CreateAppointmentTask(mutableLiveData, appointmentDate, doctorCode, hospitalCode, slotBooked).execute();
        mutableLiveData.observe(mOwner, new Observer<AppointmentBaseResponse>() {
            @Override
            public void onChanged(@Nullable AppointmentBaseResponse userModelResponse) {
                if(userModelResponse != null){
                    if(userModelResponse.getStatus() != null && userModelResponse.getStatus().getStatusCode() != null && userModelResponse.getStatus().getStatusCode().intValue() == ResponseStatus.SUCCESS){
                        mView.createAppointment(HapisApplication.getApplication().getResources().getString(R.string.appointment_successfull));
                    }else{
                        mView.failedToProcess(((userModelResponse.getStatus().getErrorMessages() != null && userModelResponse.getStatus().getErrorMessages().size() > 0) ? userModelResponse.getStatus().getErrorMessages().get(0).getMessageDescription() : HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request)));
                    }
                }else{
                    mView.failedToProcess(HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request));
                }
            }
        });*/
    }
}
