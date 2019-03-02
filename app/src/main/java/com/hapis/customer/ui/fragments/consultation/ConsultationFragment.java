package com.hapis.customer.ui.fragments.consultation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hapis.customer.R;
import com.hapis.customer.networking.json.JSONAdaptor;
import com.hapis.customer.ui.ConsultationActivity;
import com.hapis.customer.ui.ConsultationSummaryActivity;
import com.hapis.customer.ui.custom.dialogplus.DialogPlus;
import com.hapis.customer.ui.custom.dialogplus.OnClickListener;
import com.hapis.customer.ui.fragments.BaseAbstractFragment;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;
import com.hapis.customer.ui.models.consultation.Drug;
import com.hapis.customer.ui.models.consultation.Ointment;
import com.hapis.customer.ui.models.consultation.Prescription;
import com.hapis.customer.ui.models.consultation.Soap;
import com.hapis.customer.ui.models.consultation.Syrup;
import com.hapis.customer.ui.models.consultation.Tablet;
import com.hapis.customer.ui.models.enums.AppointmentStatusEnum;
import com.hapis.customer.ui.models.enums.PaymentMode;
import com.hapis.customer.ui.models.enums.PaymentStatus;
import com.hapis.customer.ui.utils.DialogIconCodes;
import com.hapis.customer.ui.utils.EditTextUtils;
import com.hapis.customer.ui.view.BaseView;
import com.hapis.customer.ui.view.ConsultationFragmentView;
import com.hapis.customer.ui.view.ConsultationFragmentViewModal;
import com.hapis.customer.utils.DateUtil;
import com.hapis.customer.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsultationFragment extends BaseAbstractFragment<ConsultationFragmentViewModal> implements ConsultationFragmentView {

    public static final String TAG = ConsultationFragment.class.getName();

    public ConsultationFragment() {
        // Required empty public constructor
    }

    private AppCompatImageView patient_profile_pic;
    private AppCompatTextView patient_name_val_tv, patient_id_lbl_tv, patient_id_val_tv, patient_age_lbl_tv, patient_age_val_tv, patient_gender_lbl_tv, patient_gender_val_tv, patient_mobile_lbl_tv, patient_mobile_val_tv;
    private TextInputEditText input_patients_notes;

    private AppCompatTextView payment_mode_val_tv, fee_val_tv;
    private AppCompatButton payment_status_button;

    private RelativeLayout capture_prescription_rl, attach_prescription_rl, add_prescription_rl, added_prescription_rl;
    private AppCompatEditText input_doctor_prescription;
    private LinearLayout edit_prescription_ll, delete_prescription_ll;
    private RecyclerView prescription_documents_rv;

    private TextInputEditText doctor_appointment_notes;
    private AppCompatButton submit_button;

    private LinearLayout patient_appointment_history_ll;
    private AppCompatTextView patient_appointment_history_tv, view_more_patient_appointment_history_tv;
    private LinearLayout patient_appointment_history_info_rl;
    private AppCompatTextView appointment_day_tv, appointment_date_tv, appointment_month_year_tv, appointment_time_tv, appointment_doctor_notes_tv, appointment_fees_tv;
    private AppCompatImageView view_appointment_details_iv;

    private List<Drug> mPrescription = new ArrayList<>();

    public void setPrescription(List<Drug> prescription) {
        if(prescription != null && prescription.size() > 0){
            mPrescription = new ArrayList<>();
            mPrescription.addAll(prescription);

            input_doctor_prescription.setText("");
            input_doctor_prescription.setText(getPrescriptionSummary());
            added_prescription_rl.setVisibility(View.VISIBLE);
        }else{
            mPrescription = null;
            added_prescription_rl.setVisibility(View.GONE);
            input_doctor_prescription.setText("");
        }
    }

    private String getPrescriptionSummary(){
        StringBuilder stringBuilder = new StringBuilder();

        for(final Drug drug : mPrescription){
            final int noOfDays = drug.getNoOfDays();
            stringBuilder.append(drug.getName() +" for "+String.valueOf(noOfDays)+(noOfDays > 1 ? " days" : " days")+ " \n");
        }

        return stringBuilder.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.patient_appointment_info_history_fragment, container, false);

        patient_profile_pic = v.findViewById(R.id.patient_profile_pic);
        patient_name_val_tv = v.findViewById(R.id.patient_name_val_tv);
        patient_id_lbl_tv = v.findViewById(R.id.patient_id_lbl_tv);
        patient_id_val_tv = v.findViewById(R.id.patient_id_val_tv);
        patient_age_lbl_tv = v.findViewById(R.id.patient_age_lbl_tv);
        patient_age_val_tv = v.findViewById(R.id.patient_age_val_tv);
        patient_gender_lbl_tv = v.findViewById(R.id.patient_gender_lbl_tv);
        patient_gender_val_tv = v.findViewById(R.id.patient_gender_val_tv);
        patient_mobile_lbl_tv = v.findViewById(R.id.patient_mobile_lbl_tv);
        patient_mobile_val_tv = v.findViewById(R.id.patient_mobile_val_tv);

        payment_mode_val_tv = v.findViewById(R.id.payment_mode_val_tv);
        fee_val_tv = v.findViewById(R.id.fee_val_tv);
        payment_status_button = v.findViewById(R.id.payment_status_button);
        input_patients_notes = v.findViewById(R.id.input_patients_notes);

        capture_prescription_rl = v.findViewById(R.id.capture_prescription_rl);
        capture_prescription_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Will introduce shortly.", Toast.LENGTH_SHORT).show();
            }
        });

        attach_prescription_rl = v.findViewById(R.id.attach_prescription_rl);
        attach_prescription_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Will introduce shortly.", Toast.LENGTH_SHORT).show();
            }
        });

        add_prescription_rl = v.findViewById(R.id.add_prescription_rl);
        add_prescription_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrescription();
            }
        });

        added_prescription_rl = v.findViewById(R.id.added_prescription_rl);
        added_prescription_rl.setVisibility(View.GONE);
        added_prescription_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        input_doctor_prescription = v.findViewById(R.id.input_doctor_prescription);

        edit_prescription_ll = v.findViewById(R.id.edit_prescription_ll);
        edit_prescription_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrescription();
            }
        });

        delete_prescription_ll = v.findViewById(R.id.delete_prescription_ll);
        delete_prescription_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrescription(null);
            }
        });

        prescription_documents_rv = v.findViewById(R.id.prescription_documents_rv);

        doctor_appointment_notes = v.findViewById(R.id.doctor_appointment_notes);
        submit_button = v.findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppointmentRequest appointmentRequest = ((ConsultationActivity)getActivity()).getAppointmentRequest();
                if(appointmentRequest != null) {

                    boolean prescriptionNotFound = false;

                    if(mPrescription == null || (mPrescription != null && mPrescription.size() == 0))
                        prescriptionNotFound = true;

                    if(prescriptionNotFound && EditTextUtils.isEmpty(doctor_appointment_notes)){
                        showError(getResources().getString(R.string.please_provide_prescription_or_consultation_summary_for_reference), null, null, null, DialogIconCodes.DIALOG_FAILED.getIconCode());
                    }else{
                        ((ConsultationActivity)getActivity()).showProgressDialog(getActivity(), getResources().getString(R.string.consultation));
                        long[] checkInAndOut = ((ConsultationActivity) getActivity()).getCheckInAndOut();
                        if(checkInAndOut != null && checkInAndOut.length == 2){

                            if(!EditTextUtils.isEmpty(doctor_appointment_notes))
                                appointmentRequest.setDoctorNotes(EditTextUtils.getText(doctor_appointment_notes));

                            appointmentRequest.setCheckInTime(checkInAndOut[0]);
                            appointmentRequest.setCheckOutTime(checkInAndOut[1]);

                            Prescription prescription = new Prescription();
                            for(Drug drug : mPrescription){
                                if(drug instanceof Tablet){
                                    prescription.getTablets().add((Tablet)drug);
                                }else if(drug instanceof Syrup){
                                    prescription.getSyrups().add((Syrup)drug);
                                }else if(drug instanceof Ointment){
                                    prescription.getOintments().add((Ointment)drug);
                                }else if(drug instanceof Soap){
                                    prescription.getSoap().add((Soap)drug);
                                }
                            }

                            appointmentRequest.setPrescription(prescription);
                        }
                        ((ConsultationActivity) getActivity()).stopTimer();

                        appointmentRequest.setState(AppointmentStatusEnum.CLOSED.code());

                        mViewModal.completeConsultation(appointmentRequest);
                    }
                }else
                    showError(getResources().getString(R.string.appointment_details_not_found_to_proceed), null, null,null, DialogIconCodes.DIALOG_NOT_AVAILABLE.getIconCode());
            }
        });

        patient_appointment_history_ll = v.findViewById(R.id.patient_appointment_history_ll);
        patient_appointment_history_ll.setVisibility(View.GONE);

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

        new LoadPatientInformation().execute(((ConsultationActivity)getActivity()).getAppointmentRequest());

        return v;
    }

    private ConsultationPrescriptionDialogFragment dialog;

    private void loadPrescription() {
        dialog =
                ConsultationPrescriptionDialogFragment.newInstance(((ConsultationActivity) getActivity()), new DoctorPrescriptionDialogFragmentCallBack() {
                    @Override
                    public void updateSelectedValue(List<Drug> prescriptionList) {
                        dialog.dismiss();
                        dialog = null;
                        setPrescription(prescriptionList);
                    }
                }, mPrescription, getResources().getString(R.string.prescription));
        dialog.setCancelable(false);
        dialog.show(getActivity().getSupportFragmentManager(), ConsultationPrescriptionDialogFragment.TAG);
    }

    class LoadPatientInformation extends AsyncTask<AppointmentRequest, Void, Void>{

        @Override
        protected Void doInBackground(AppointmentRequest... appointmentRequests) {
            loadPatientInformation(appointmentRequests[0]);
            return null;
        }
    }

    private void loadPatientInformation(final AppointmentRequest appointmentRequest){
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(appointmentRequest.getCustomerCode() != null && appointmentRequest.getCustomerCode().length() > 0)
                        patient_id_val_tv.setText(appointmentRequest.getCustomerCode());

                    if(appointmentRequest.getPatientName() != null)
                        patient_name_val_tv.setText(appointmentRequest.getPatientName());

                    if(appointmentRequest.getPatientGender() != null) {
                        patient_gender_val_tv.setText(appointmentRequest.getPatientGender());
                    }

                    if(appointmentRequest.getPatientAge() != null){
                        patient_age_val_tv.setText(String.valueOf(appointmentRequest.getPatientAge()));
                    }

                    if(appointmentRequest.getMobileNumber() != null) {
                        patient_mobile_val_tv.setText(appointmentRequest.getMobileNumber());
                    }

                    if(appointmentRequest.getNotes() != null) {
                        input_patients_notes.setText(appointmentRequest.getNotes());
                    }

                    if(appointmentRequest.getPaymentMode() != null) {
                        payment_mode_val_tv.setText(PaymentMode.getValue(appointmentRequest.getPaymentMode()));
                    }

                    if(appointmentRequest.getAppointmentFee() != null) {
                        fee_val_tv.setText(Util.getFormattedAmount(appointmentRequest.getAppointmentFee()));
                    }

                    if(appointmentRequest.getPaymentStatus() != null){
                        if(appointmentRequest.getPaymentStatus().intValue() == PaymentStatus.PAID.code().intValue()){
                            payment_status_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.payment_paid_rounded_button));
                            payment_status_button.setText(getResources().getString(R.string.payment_paid));
                        }else if(appointmentRequest.getPaymentStatus().intValue() == PaymentStatus.PENDING.code().intValue()){
                            payment_status_button.setText(getResources().getString(R.string.payment_pending));
                            payment_status_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.payment_pending_rounded_button));
                        }
                    }
                }
            });
        }
    }

    @Override
    protected Class getViewModalClass() {
        return ConsultationFragmentViewModal.class;
    }

    @Override
    protected BaseView getViewImpl() {
        return this;
    }


    @Override
    public void failedToProcess(String errorMsg) {
        ((ConsultationActivity)getActivity()).dismissProgressDialog();
        ((ConsultationActivity)getActivity()).showError(errorMsg, null, null, null, DialogIconCodes.DIALOG_FAILED.getIconCode());
    }

    @Override
    public void completeConsultation(String msg) {
        ((ConsultationActivity)getActivity()).dismissProgressDialog();
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
                switch (view.getId()){
                    case R.id.positive_btn:
                    {
                        dialog.dismiss();
                        getActivity().finish();
                        break;
                    }
                }
            }
        };

        showError(getResources().getString(R.string.thank_you_doctor), onClickListener, getResources().getString(R.string.consultation_completed), null, DialogIconCodes.DIALOG_SUCCESS.getIconCode());
    }

    @Override
    public void showError(String errorMsg, OnClickListener onClickListener, String positiveLbl, String negativeLbl, String status) {
        ((ConsultationActivity)getActivity()).showError(errorMsg, onClickListener, positiveLbl, negativeLbl, status);
    }

    @Override
    public void loadPatientHistory(final List<AppointmentRequest> appointmentRequests) {
        if(appointmentRequests != null && appointmentRequests.size() > 0){

            final AppointmentRequest appointmentRequest = appointmentRequests.get(0);

            patient_appointment_history_ll.setVisibility(View.VISIBLE);
            if(appointmentRequests.size() > 1) {
                view_more_patient_appointment_history_tv.setVisibility(View.VISIBLE);
                view_more_patient_appointment_history_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO:Show all appointments.
                    }
                });
            }else{
                view_more_patient_appointment_history_tv.setVisibility(View.GONE);
                view_more_patient_appointment_history_tv.setOnClickListener(null);

                final Date date = new Date(appointmentRequest.getCheckInTime());

                String dayAsString = DateUtil.convertDateToDateStr(date, DateUtil.DATE_FORMAT_EEE);
                String dateAsString = DateUtil.convertDateToDateStr(date, DateUtil.DATE_FORMAT_dd);
                String yearAsString = DateUtil.convertDateToDateStr(date, DateUtil.DATE_FORMAT_MMM_YYYY);
                String timeAsString = DateUtil.convertDateToDateStr(date, DateUtil.DATE_FORMAT_HH_mm_am_pm);

                if(dayAsString != null){
                    appointment_day_tv.setText(dayAsString);
                }

                if(dateAsString != null){
                    appointment_date_tv.setText(dateAsString);
                }

                if(yearAsString != null){
                    appointment_month_year_tv.setText(yearAsString);
                }

                if(timeAsString != null){
                    appointment_time_tv.setText(timeAsString);
                }

                if(appointmentRequest.getNotes() != null && appointmentRequest.getNotes().length() > 0){
                    appointment_doctor_notes_tv.setText(appointmentRequest.getNotes());
                }

                if(appointmentRequest.getFee() != null)
                    appointment_fees_tv.setText(Util.getFormattedAmount(appointmentRequest.getFee()));
            }

            if(appointmentRequest != null){
                patient_appointment_history_info_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            Intent intent = new Intent(getActivity(), ConsultationSummaryActivity.class);
                            intent.putExtra(ConsultationSummaryActivity.APPOINTMENT_DETAILS_TAG, JSONAdaptor.toJSON(appointmentRequest));
                            getActivity().startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                patient_appointment_history_ll.setVisibility(View.GONE);
            }
        }else{
            patient_appointment_history_ll.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(((ConsultationActivity)getActivity()).getAppointmentRequest() != null){
            String customerCode = (((ConsultationActivity)getActivity()).getAppointmentRequest()).getCustomerCode();
            String doctorCode = (((ConsultationActivity)getActivity()).getAppointmentRequest()).getDoctorCode();
            String hospitalCode = (((ConsultationActivity)getActivity()).getAppointmentRequest()).getHospitalCode();

            if(customerCode != null && doctorCode != null && hospitalCode != null){
                mViewModal.loadPatientHistory(doctorCode, hospitalCode, customerCode);
            }
        }
    }
}
