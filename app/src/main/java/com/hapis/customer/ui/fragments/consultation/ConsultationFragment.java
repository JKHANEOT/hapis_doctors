package com.hapis.customer.ui.fragments.consultation;

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

import com.hapis.customer.R;
import com.hapis.customer.ui.ConsultationActivity;
import com.hapis.customer.ui.fragments.BaseAbstractFragment;
import com.hapis.customer.ui.models.appointments.AppointmentRequest;
import com.hapis.customer.ui.models.consultation.Drug;
import com.hapis.customer.ui.models.enums.PaymentMode;
import com.hapis.customer.ui.models.enums.PaymentStatus;
import com.hapis.customer.ui.view.BaseView;
import com.hapis.customer.ui.view.ConsultationFragmentView;
import com.hapis.customer.ui.view.ConsultationFragmentViewModal;
import com.hapis.customer.utils.Util;

import java.util.ArrayList;
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

            }
        });

        attach_prescription_rl = v.findViewById(R.id.attach_prescription_rl);
        attach_prescription_rl.setVisibility(View.GONE);

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
                long[] checkInAndOut = ((ConsultationActivity)getActivity()).getCheckInAndOut();
            }
        });

        patient_appointment_history_ll = v.findViewById(R.id.patient_appointment_history_ll);
        patient_appointment_history_ll.setVisibility(View.GONE);

        patient_appointment_history_tv = v.findViewById(R.id.patient_appointment_history_tv);
        view_more_patient_appointment_history_tv = v.findViewById(R.id.view_more_patient_appointment_history_tv);

        patient_appointment_history_info_rl = v.findViewById(R.id.patient_appointment_history_info_rl);
        patient_appointment_history_info_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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

                    if(appointmentRequest.getAppointmentShortNote() != null) {
                        input_patients_notes.setText(appointmentRequest.getAppointmentShortNote());
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

    }

    @Override
    public void completeConsultation(String msg) {

    }
}
