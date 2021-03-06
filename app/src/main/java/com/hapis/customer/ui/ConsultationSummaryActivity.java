package com.hapis.customer.ui;

import android.os.Bundle;

import com.hapis.customer.R;
import com.hapis.customer.networking.json.JSONAdaptor;
import com.hapis.customer.ui.custom.dialogplus.OnClickListener;
import com.hapis.customer.ui.fragments.consultation.ConsultationSummaryFragment;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;
import com.hapis.customer.ui.utils.AlertUtil;
import com.hapis.customer.ui.utils.DialogIconCodes;
import com.hapis.customer.ui.view.BaseView;
import com.hapis.customer.ui.view.ConsultationView;
import com.hapis.customer.ui.view.ConsultationViewModal;

public class ConsultationSummaryActivity extends BaseFragmentActivity<ConsultationViewModal> implements ConsultationView {

    public static final String APPOINTMENT_DETAILS_TAG = "APPOINTMENT_DETAILS";

    private AppointmentRequest appointmentRequest;

    @Override
    protected Class getViewModalClass() {
        return ConsultationViewModal.class;
    }

    @Override
    protected BaseView getViewImpl() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);

        if(getIntent() != null && getIntent().getStringExtra(APPOINTMENT_DETAILS_TAG) != null)
        {
            try {
                String appointmentJson = getIntent().getStringExtra(APPOINTMENT_DETAILS_TAG);
                if(appointmentJson != null && appointmentJson.length() > 0){
                    appointmentRequest = (AppointmentRequest)JSONAdaptor.fromJSON(getIntent().getStringExtra(APPOINTMENT_DETAILS_TAG), AppointmentRequest.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setUpNavigationDrawer(getResources().getString(R.string.consultation_summary_page), null, true, null);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ConsultationSummaryFragment(), ConsultationSummaryFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void showError(String errorMsg, OnClickListener onClickListener, String positiveLbl, String negativeLbl, String status) {
        if(onClickListener == null){
            AlertUtil.showAlert(ConsultationSummaryActivity.this, getResources().getString(R.string.consultation_summary), errorMsg, DialogIconCodes.DIALOG_FAILED.getIconCode());
        }else{
            if(positiveLbl != null && positiveLbl.length() > 0 && (negativeLbl == null || (negativeLbl != null && negativeLbl.length() == 0)))
                AlertUtil.showAlert(ConsultationSummaryActivity.this, getResources().getString(R.string.consultation_summary), errorMsg, positiveLbl, onClickListener, status);
            else if(positiveLbl != null && positiveLbl.length() > 0 && negativeLbl != null && negativeLbl.length() > 0)
                AlertUtil.showAlert(ConsultationSummaryActivity.this, getResources().getString(R.string.consultation_summary), errorMsg, positiveLbl, negativeLbl, onClickListener, status);
            else
                AlertUtil.showAlert(ConsultationSummaryActivity.this, getResources().getString(R.string.consultation_summary), errorMsg, DialogIconCodes.DIALOG_FAILED.getIconCode());
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public AppointmentRequest getAppointmentRequest() {
        return appointmentRequest;
    }
}

