package com.hapis.customer.ui.fragments.consultation;

import com.hapis.customer.ui.models.consultation.Drug;

import java.util.List;

public interface DoctorPrescriptionDialogFragmentCallBack {
    void updateSelectedValue(List<Drug> prescriptionList);
}
