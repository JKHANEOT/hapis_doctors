package com.hapis.customer.ui.view;

import android.arch.lifecycle.LifecycleOwner;

import com.hapis.customer.database.repository.AppointmentRepository;
import com.hapis.customer.ui.models.HapisModel;

public class ConsultationSummaryFragmentViewModal extends BaseViewModal<ConsultationFragmentView> {

    private String TAG = ConsultationFragmentViewModal.class.getName();

    private AppointmentRepository appointmentRepository;

    public ConsultationSummaryFragmentViewModal(LifecycleOwner owner) {
        super(owner);

        appointmentRepository = new AppointmentRepository();
    }

    @Override
    protected void initObservableData() {

    }

    @Override
    protected void handleChangedDataModal(HapisModel data) {

    }
}
