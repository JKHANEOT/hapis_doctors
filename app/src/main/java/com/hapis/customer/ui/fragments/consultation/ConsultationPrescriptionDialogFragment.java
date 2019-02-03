package com.hapis.customer.ui.fragments.consultation;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.hapis.customer.R;
import com.hapis.customer.ui.BaseFragmentActivity;
import com.hapis.customer.ui.ConsultationActivity;
import com.hapis.customer.ui.adapters.VerticalSpaceItemDecoration;
import com.hapis.customer.ui.models.consultation.Drug;

import java.util.ArrayList;
import java.util.List;

public class ConsultationPrescriptionDialogFragment extends DialogFragment {

    public static final String TAG = ConsultationPrescriptionDialogFragment.class.getName();

    public ConsultationPrescriptionDialogFragment() {
        // Required empty public constructor
    }

    private DoctorPrescriptionDialogFragmentCallBack mDoctorPrescriptionDialogFragmentCallBack;

    private BaseFragmentActivity baseFragmentActivity;

    private List<Drug> mPrescriptionList = new ArrayList<>();

    public void setScreenTitle(String screenTitle) {
        mScreenTitle = screenTitle;
    }

    private String mScreenTitle;

    public static ConsultationPrescriptionDialogFragment newInstance(BaseFragmentActivity baseFragmentActivity,
                                                                     DoctorPrescriptionDialogFragmentCallBack doctorPrescriptionDialogFragmentCallBack,
                                                                     List<Drug> mPrescriptionList, String screenTitle) {
        ConsultationPrescriptionDialogFragment f = new ConsultationPrescriptionDialogFragment();
        f.setOnCallBackListener(baseFragmentActivity, doctorPrescriptionDialogFragmentCallBack);
        f.setSearchData(mPrescriptionList);
        f.setScreenTitle(screenTitle);
        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        return f;
    }

    public void setSearchData(List<Drug> prescriptionList) {
        if(prescriptionList == null)
            prescriptionList = new ArrayList<>();

        mPrescriptionList.addAll(prescriptionList);
    }

    public void setOnCallBackListener(BaseFragmentActivity mBaseFragmentActivity, DoctorPrescriptionDialogFragmentCallBack doctorPrescriptionDialogFragmentCallBack) {
        mDoctorPrescriptionDialogFragmentCallBack = doctorPrescriptionDialogFragmentCallBack;
        if (mBaseFragmentActivity != null)
            baseFragmentActivity = mBaseFragmentActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.prescription_master_dialog_fragment, container);
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    private AppCompatImageView action_button_iv, action_button_done_iv;
    private LinearLayout close_action_button_iv;
    private AppCompatTextView screen_title_tv, no_medicines_added_tv;
    private RecyclerView prescription_rv;

    private Drug selectedDrug;
    private int selectedDrugIndex;

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        action_button_done_iv = v.findViewById(R.id.action_button_done_iv);
        action_button_done_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoctorPrescriptionDialogFragmentCallBack.updateSelectedValue(mPrescriptionList);
            }
        });

        action_button_iv = v.findViewById(R.id.action_button_iv);
        action_button_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDrug = null;
                selectedDrugIndex = -1;
                addEditMedicine();
            }
        });

        close_action_button_iv = v.findViewById(R.id.close_action_button_iv);
        close_action_button_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoctorPrescriptionDialogFragmentCallBack.updateSelectedValue(mPrescriptionList);
                dismiss();
            }
        });

        screen_title_tv = v.findViewById(R.id.screen_title_tv);
        screen_title_tv.setText(mScreenTitle);

        no_medicines_added_tv = v.findViewById(R.id.no_medicines_added_tv);

        prescription_rv = v.findViewById(R.id.prescription_rv);

        prescription_rv.setLayoutManager(new LinearLayoutManager(baseFragmentActivity));
        prescription_rv.setItemAnimator(new DefaultItemAnimator());
        prescription_rv.addItemDecoration(new VerticalSpaceItemDecoration(10));

        //ADAPTER
        doctorPrescriptionRecyclerViewAdapter = new DoctorPrescriptionRecyclerViewAdapter(mPrescriptionList, new DoctorPrescriptionRecyclerViewAdapter.DoctorPrescriptionAdapterListeners() {
            @Override
            public void viewClicked(Drug drug) {

            }

            @Override
            public void deleteDrug(Drug drug, int selectedIndex) {
                mPrescriptionList.remove(selectedIndex);
                doctorPrescriptionRecyclerViewAdapter.notifyItemRemoved(selectedIndex);
                refreshUI();
            }

            @Override
            public void editDrug(Drug drug, int selectedIndex) {
                selectedDrug = drug;
                selectedDrugIndex = selectedIndex;
                addEditMedicine();
            }
        });
        prescription_rv.setAdapter(doctorPrescriptionRecyclerViewAdapter);

        refreshUI();
    }

    private void refreshUI() {
        if(mPrescriptionList == null || (mPrescriptionList != null && mPrescriptionList.size() == 0)){
            no_medicines_added_tv.setVisibility(View.VISIBLE);
            prescription_rv.setVisibility(View.GONE);
            action_button_done_iv.setVisibility(View.GONE);
        }else{
            no_medicines_added_tv.setVisibility(View.GONE);
            prescription_rv.setVisibility(View.VISIBLE);
            action_button_done_iv.setVisibility(View.VISIBLE);
        }
    }

    private DoctorPrescriptionRecyclerViewAdapter doctorPrescriptionRecyclerViewAdapter;
    private AddEditPrescriptionDialogFragment dialog;
    private void addEditMedicine(){
        dialog =
                AddEditPrescriptionDialogFragment.newInstance(((ConsultationActivity) getActivity()), new AddEditPrescriptionDialogFragmentCallBack() {
                    @Override
                    public void updateSelectedValue(Drug drug) {
                        dialog.dismiss();
                        dialog = null;
                        if(selectedDrugIndex >= 0){
                            mPrescriptionList.set(selectedDrugIndex, drug);
                            doctorPrescriptionRecyclerViewAdapter.notifyItemChanged(selectedDrugIndex, drug);
                            selectedDrug = null;
                            selectedDrugIndex = -1;
                        }else{
                            mPrescriptionList.add(drug);
                            doctorPrescriptionRecyclerViewAdapter.addPrescription(drug);
                        }
                        refreshUI();
                    }
                }, selectedDrug, selectedDrug == null ? baseFragmentActivity.getResources().getString(R.string.add_medicine) : baseFragmentActivity.getResources().getString(R.string.edit_medicine));
        dialog.setCancelable(false);
        dialog.show(getActivity().getSupportFragmentManager(), ConsultationPrescriptionDialogFragment.TAG);
    }
}
