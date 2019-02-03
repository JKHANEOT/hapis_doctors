package com.hapis.customer.ui.fragments.consultation;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hapis.customer.R;
import com.hapis.customer.database.repository.UserProfileRepository;
import com.hapis.customer.database.tables.UserProfileTable;
import com.hapis.customer.ui.BaseFragmentActivity;
import com.hapis.customer.ui.callback.CommonSearchCallBack;
import com.hapis.customer.ui.custom.MaterialSpinner;
import com.hapis.customer.ui.custom.materialedittext.MaterialEditText;
import com.hapis.customer.ui.fragments.CommonSearchDialogFragment;
import com.hapis.customer.ui.fragments.SelectPreferredLocationDialogFragment;
import com.hapis.customer.ui.models.consultation.Drug;
import com.hapis.customer.ui.models.consultation.Ointment;
import com.hapis.customer.ui.models.consultation.Soap;
import com.hapis.customer.ui.models.consultation.Syrup;
import com.hapis.customer.ui.models.consultation.Tablet;
import com.hapis.customer.ui.models.enums.MasterDataUtils;
import com.hapis.customer.ui.utils.EditTextUtils;

import java.util.List;

public class AddEditPrescriptionDialogFragment extends DialogFragment {

    public static final String TAG = AddEditPrescriptionDialogFragment.class.getName();

    public AddEditPrescriptionDialogFragment() {
        // Required empty public constructor
    }

    private AddEditPrescriptionDialogFragmentCallBack mAddEditPrescriptionDialogFragmentCallBack;

    private BaseFragmentActivity baseFragmentActivity;

    public void setScreenTitle(String screenTitle) {
        mScreenTitle = screenTitle;
    }

    private String mScreenTitle;

    public static AddEditPrescriptionDialogFragment newInstance(BaseFragmentActivity baseFragmentActivity,
                                                                AddEditPrescriptionDialogFragmentCallBack addEditPrescriptionDialogFragmentCallBack,
                                                                Drug drug, String screenTitle) {
        AddEditPrescriptionDialogFragment f = new AddEditPrescriptionDialogFragment();
        f.setDrug(drug);
        f.setOnCallBackListener(baseFragmentActivity, addEditPrescriptionDialogFragmentCallBack);
        f.setScreenTitle(screenTitle);
        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        return f;
    }

    public void setDrug(Drug drug){
        selectedDrug = drug;
    }

    public void setOnCallBackListener(BaseFragmentActivity mBaseFragmentActivity, AddEditPrescriptionDialogFragmentCallBack addEditPrescriptionDialogFragmentCallBack) {
        mAddEditPrescriptionDialogFragmentCallBack = addEditPrescriptionDialogFragmentCallBack;
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
        return inflater.inflate(R.layout.add_edit_prescription_dialog_fragment, container);
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

    private String selectedDrugType;

    AdapterView.OnItemSelectedListener drugSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedDrugType = parent.getItemAtPosition(position).toString();
            prepareOtherAdapters();
            if(isLoadWithData && selectedDrug != null){
                if(selectedDrug.getMorningDose() != null){
                    select_morning_dose_spinner.setSelection(getIndex(getDrugDoseBasedOnDrugType(selectedDrugType), selectedDrug.getMorningDose()));
                    morning_dose_toggleButton.setChecked(true);
                }

                if(selectedDrug.getNoonDose() != null){
                    select_afternoon_dose_spinner.setSelection(getIndex(getDrugDoseBasedOnDrugType(selectedDrugType), selectedDrug.getNoonDose()));
                    afternoon_dose_toggleButton.setChecked(true);
                }

                if(selectedDrug.getNightDose() != null){
                    select_night_dose_spinner.setSelection(getIndex(getDrugDoseBasedOnDrugType(selectedDrugType), selectedDrug.getNightDose()));
                    night_dose_toggleButton.setChecked(true);
                }

                if(selectedDrug.getBeforeMorningDose() != null){
                    select_morning_dose_spinner.setSelection(getIndex(getDrugDoseBasedOnDrugType(selectedDrugType), selectedDrug.getBeforeMorningDose()));
                    morning_dose_toggleButton.setChecked(false);
                }

                if(selectedDrug.getBeforeNoonDose() != null){
                    select_afternoon_dose_spinner.setSelection(getIndex(getDrugDoseBasedOnDrugType(selectedDrugType), selectedDrug.getBeforeNoonDose()));
                    afternoon_dose_toggleButton.setChecked(false);
                }

                if(selectedDrug.getBeforeNightDose() != null){
                    select_night_dose_spinner.setSelection(getIndex(getDrugDoseBasedOnDrugType(selectedDrugType), selectedDrug.getBeforeNightDose()));
                    night_dose_toggleButton.setChecked(false);
                }

                isLoadWithData = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void prepareOtherAdapters(){
        if(selectedDrugType.equalsIgnoreCase("Tablet")){

            String[] drugs = getDrugDoseBasedOnDrugType(selectedDrugType);

            ArrayAdapter<String> morningDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            morningDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_morning_dose_spinner.setAdapter(morningDoseAdapter);

            ArrayAdapter<String> afternoonDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            afternoonDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_afternoon_dose_spinner.setAdapter(afternoonDoseAdapter);

            ArrayAdapter<String> nightDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            nightDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_night_dose_spinner.setAdapter(nightDoseAdapter);
        }else if(selectedDrugType.equalsIgnoreCase("Syrup")){

            String[] drugs = getDrugDoseBasedOnDrugType(selectedDrugType);

            ArrayAdapter<String> morningDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            morningDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_morning_dose_spinner.setAdapter(morningDoseAdapter);

            ArrayAdapter<String> afternoonDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            afternoonDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_afternoon_dose_spinner.setAdapter(afternoonDoseAdapter);

            ArrayAdapter<String> nightDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            nightDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_night_dose_spinner.setAdapter(nightDoseAdapter);
        }else if(selectedDrugType.equalsIgnoreCase("Soap")){

            String[] drugs = getDrugDoseBasedOnDrugType(selectedDrugType);

            ArrayAdapter<String> morningDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            morningDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_morning_dose_spinner.setAdapter(morningDoseAdapter);

            ArrayAdapter<String> afternoonDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            afternoonDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_afternoon_dose_spinner.setAdapter(afternoonDoseAdapter);

            ArrayAdapter<String> nightDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            nightDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_night_dose_spinner.setAdapter(nightDoseAdapter);
        }else if(selectedDrugType.equalsIgnoreCase("Ointment")){

            String[] drugs = getDrugDoseBasedOnDrugType(selectedDrugType);

            ArrayAdapter<String> morningDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            morningDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_morning_dose_spinner.setAdapter(morningDoseAdapter);

            ArrayAdapter<String> afternoonDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            afternoonDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_afternoon_dose_spinner.setAdapter(afternoonDoseAdapter);

            ArrayAdapter<String> nightDoseAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, drugs);
            nightDoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_night_dose_spinner.setAdapter(nightDoseAdapter);
        }
    }

    AdapterView.OnItemSelectedListener morningDoseSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            morningDoseSelected = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener afternoonDoseSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            afternoonDoseSelected = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener nightDoseSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            nightDoseSelected = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private String morningDoseSelected, afternoonDoseSelected, nightDoseSelected;

    private MaterialEditText select_drug;
    private MaterialSpinner select_drug_type_spinner, select_morning_dose_spinner, select_afternoon_dose_spinner, select_night_dose_spinner;
    private MaterialEditText input_period;
    private AppCompatImageView action_button_iv, action_button_clear_iv;
    private LinearLayout close_action_button_iv;
    private AppCompatTextView screen_title_tv;
    private ToggleButton morning_dose_toggleButton, afternoon_dose_toggleButton, night_dose_toggleButton;

    private String[] drugType;

    private Drug selectedDrug;
    private int selectedDrugIndex;

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        action_button_clear_iv = v.findViewById(R.id.action_button_clear_iv);
        action_button_clear_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshMedicineLayout();
            }
        });

        action_button_iv = v.findViewById(R.id.action_button_iv);
        action_button_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = isValid();
                if(msg == null){

                    if(selectedDrug == null){
                        selectedDrug = getDrugType();
                    }

                    selectedDrug.setNoOfDays(Integer.parseInt(EditTextUtils.getText(input_period)));
                    selectedDrug.setName(EditTextUtils.getText(select_drug));

                    selectedDrug.setMorningDose(null);
                    selectedDrug.setNoonDose(null);
                    selectedDrug.setNightDose(null);
                    selectedDrug.setBeforeMorningDose(null);
                    selectedDrug.setBeforeNoonDose(null);
                    selectedDrug.setBeforeNightDose(null);

                    if(select_morning_dose_spinner.getSelectedItemPosition() > 0) {
                        if (morning_dose_toggleButton.isChecked()) {
                            selectedDrug.setMorningDose(morningDoseSelected);
                            selectedDrug.setBeforeMorningDose(null);
                        } else {
                            selectedDrug.setBeforeMorningDose(morningDoseSelected);
                            selectedDrug.setMorningDose(null);
                        }
                    }

                    if(select_afternoon_dose_spinner.getSelectedItemPosition() > 0) {
                        if (afternoon_dose_toggleButton.isChecked()) {
                            selectedDrug.setNoonDose(afternoonDoseSelected);
                            selectedDrug.setBeforeNoonDose(null);
                        } else {
                            selectedDrug.setBeforeNoonDose(afternoonDoseSelected);
                            selectedDrug.setNoonDose(null);
                        }
                    }

                    if(select_night_dose_spinner.getSelectedItemPosition() > 0) {
                        if (night_dose_toggleButton.isChecked()) {
                            selectedDrug.setNightDose(nightDoseSelected);
                            selectedDrug.setBeforeNightDose(null);
                        } else {
                            selectedDrug.setBeforeNightDose(nightDoseSelected);
                            selectedDrug.setNightDose(null);
                        }
                    }

                    mAddEditPrescriptionDialogFragmentCallBack.updateSelectedValue(selectedDrug);
                }else
                    Toast.makeText(baseFragmentActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });

        close_action_button_iv = v.findViewById(R.id.close_action_button_iv);
        close_action_button_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        morning_dose_toggleButton = v.findViewById(R.id.morning_dose_toggleButton);
        morning_dose_toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        select_morning_dose_spinner = v.findViewById(R.id.select_morning_dose_spinner);
        select_morning_dose_spinner.setOnItemSelectedListener(morningDoseSpinnerSelectedListener);

        afternoon_dose_toggleButton = v.findViewById(R.id.afternoon_dose_toggleButton);
        afternoon_dose_toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        select_afternoon_dose_spinner = v.findViewById(R.id.select_afternoon_dose_spinner);
        select_afternoon_dose_spinner.setOnItemSelectedListener(afternoonDoseSpinnerSelectedListener);

        night_dose_toggleButton = v.findViewById(R.id.night_dose_toggleButton);
        night_dose_toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        select_night_dose_spinner = v.findViewById(R.id.select_night_dose_spinner);
        select_night_dose_spinner.setOnItemSelectedListener(nightDoseSpinnerSelectedListener);

        drugType = getResources().getStringArray(R.array.drug_type_array);

        select_drug_type_spinner = v.findViewById(R.id.select_drug_type_spinner);
        ArrayAdapter<String> prefixAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, drugType);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_drug_type_spinner.setAdapter(prefixAdapter);
        select_drug_type_spinner.setOnItemSelectedListener(drugSelectedListener);

        input_period = v.findViewById(R.id.input_period);

        select_drug = v.findViewById(R.id.select_drug);
        select_drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedDrugType != null){
                    new GetPrescription().execute();
                }else
                    Toast.makeText(baseFragmentActivity, baseFragmentActivity.getResources().getString(R.string.please_select_medicine_type), Toast.LENGTH_SHORT).show();
            }
        });

        screen_title_tv = v.findViewById(R.id.screen_title_tv);
        screen_title_tv.setText(mScreenTitle);

        if(selectedDrug != null)
            loadWithDrug(selectedDrug);
    }

    private void refreshMedicineLayout() {
        selectedDrug = null;
        morningDoseSelected = null;
        afternoonDoseSelected = null;
        nightDoseSelected = null;
        selectedDrugIndex = -1;
        selectedDrugType = null;

        select_drug_type_spinner.setSelection(0);
        input_period.setText("");
        select_drug.setText("");
        morning_dose_toggleButton.setChecked(true);
        select_morning_dose_spinner.setSelection(0);
        afternoon_dose_toggleButton.setChecked(true);
        select_afternoon_dose_spinner.setSelection(0);
        night_dose_toggleButton.setChecked(true);
        select_night_dose_spinner.setSelection(0);
    }

    private Drug getDrugType() {

        Drug drug = null;

        if(selectedDrugType.equalsIgnoreCase("Tablet")){
            drug = new Tablet();
        }else if(selectedDrugType.equalsIgnoreCase("Syrup")){
            drug = new Syrup();
        }else if(selectedDrugType.equalsIgnoreCase("Soap")){
            drug = new Soap();
        }else if(selectedDrugType.equalsIgnoreCase("Ointment")){
            drug = new Ointment();
        }

        return drug;
    }

    private String isValid() {
        String msg = null;

        if(selectedDrugType == null || (selectedDrugType != null && selectedDrugType.equalsIgnoreCase(baseFragmentActivity.getResources().getString(R.string.select_drug_type))))
            msg = baseFragmentActivity.getResources().getString(R.string.please_select_medicine_type);
        else if(EditTextUtils.isEmpty(input_period))
            msg = baseFragmentActivity.getResources().getString(R.string.please_enter_number_of_days);
        else if(EditTextUtils.isEmpty(select_drug))
            msg = baseFragmentActivity.getResources().getString(R.string.please_select_medicine);
        else if(!isDoseSelected())
            msg = baseFragmentActivity.getResources().getString(R.string.please_select_dose);

        return msg;
    }

    private boolean isDoseSelected(){
        boolean status = false;

        if(select_morning_dose_spinner.getSelectedItemPosition() > 0)
            status = true;

        if(!status && select_afternoon_dose_spinner.getSelectedItemPosition() > 0)
            status = true;

        if(!status && select_night_dose_spinner.getSelectedItemPosition() > 0)
            status = true;

        return status;
    }

    class GetPrescription extends AsyncTask<Void, Void, List<String>> {


        @Override
        protected List<String> doInBackground(Void... voids) {

            List<String> strings = null;

            UserProfileTable userProfileTable = new UserProfileRepository().getUserProfile();
            if(userProfileTable != null) {
                strings = MasterDataUtils.getPrescription(userProfileTable.getRoles(), selectedDrugType);
            }

            return strings;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if(strings != null && strings.size() > 0){

                CommonSearchDialogFragment dialog =
                        CommonSearchDialogFragment.newInstance((BaseFragmentActivity)getActivity(), new CommonSearchCallBack() {
                            @Override
                            public void updateSelectedValue(String selectedValue) {
                                select_drug.setText(selectedValue);
                            }
                        }, strings, "Select Prescription");
                dialog.setCancelable(false);
                dialog.show(getActivity().getSupportFragmentManager(), SelectPreferredLocationDialogFragment.TAG);
            }else{
                Toast.makeText(baseFragmentActivity, "No medicine not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLoadWithData;

    private void loadWithDrug(final Drug drug){
        if(drug != null){

            isLoadWithData = true;

            int selectedIndex = 0;

            if(drug instanceof Tablet){
                selectedIndex = 1;
            }else if(drug instanceof Syrup){
                selectedIndex = 2;
            }else if(drug instanceof Soap){
                selectedIndex = 3;
            }else if(drug instanceof Ointment){
                selectedIndex = 4;
            }
            selectedDrugType = drugType[selectedIndex];

            select_drug_type_spinner.setSelection(selectedIndex);

            if(drug.getNoOfDays() != null){
                input_period.setText(String.valueOf(drug.getNoOfDays()));
            }

            if(drug.getName() != null){
                select_drug.setText(drug.getName());
            }
        }
    }

    private String[] getDrugDoseBasedOnDrugType(String drugType){
        String[] dose = new String[]{};

        if(drugType.equalsIgnoreCase("Tablet")){
            dose = new String[]{"1/2", "3/4", "1", "2"};
        }else if(drugType.equalsIgnoreCase("Syrup")){
            dose = new String[]{"5 ml", "10 ml", "15 ml", "20 ml", "25 ml", "30 ml"};
        }else if(drugType.equalsIgnoreCase("Soap")){
            dose = new String[]{"Once a day", "Twice a day", "Thrice a day"};
        }else if(drugType.equalsIgnoreCase("Ointment")){
            dose = new String[]{"Once a day", "Twice a day", "Thrice a day"};
        }

        return dose;
    }

    private int getIndex(String[] dose, String selectedDose){
        int index = 0;

        for(int i = 0; i < dose.length; i++){
            if(dose[i].equalsIgnoreCase(selectedDose)){
                index = i;
                break;
            }
        }

        return index+1;
    }
}
