package com.hapis.customer.ui.fragments.consultation;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hapis.customer.R;
import com.hapis.customer.ui.models.consultation.Drug;
import com.hapis.customer.ui.models.consultation.Ointment;
import com.hapis.customer.ui.models.consultation.Soap;
import com.hapis.customer.ui.models.consultation.Syrup;
import com.hapis.customer.ui.models.consultation.Tablet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javeed on 2/27/2018.
 */

public class DoctorPrescriptionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    https://krtkush.github.io/2016/07/08/android-recyclerview-grouping-data.html

    private List<Drug> mConsolidatedList = new ArrayList<>();

    public DoctorPrescriptionRecyclerViewAdapter(List<Drug> consolidatedList, DoctorPrescriptionAdapterListeners doctorPrescriptionAdapterListeners) {
        mConsolidatedList.addAll(consolidatedList);
        mDoctorPrescriptionAdapterListeners = doctorPrescriptionAdapterListeners;
    }

    public void addPrescription(Drug drug){
        mConsolidatedList.add(drug);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mConsolidatedList != null ? mConsolidatedList.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v1 = inflater.inflate(R.layout.doctor_prescription_adapter_row, parent, false);
        viewHolder = new DoctorPrescriptionViewHolder(v1);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        final Drug drug = mConsolidatedList.get(position);
        DoctorPrescriptionViewHolder doctorPrescriptionViewHolder = (DoctorPrescriptionViewHolder)viewHolder;
        if(drug != null){
            if(drug.getName() != null){
                doctorPrescriptionViewHolder.prescription_name_tv.setText(drug.getName());
            }

            String dosage = "";

            StringBuilder stringBuilder = new StringBuilder();

            if(drug instanceof Tablet || drug instanceof Syrup){
                if(drug instanceof Tablet){
                    if(drug.getBeforeMorningDose() != null && drug.getBeforeMorningDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getBeforeMorningDose()+" tablet in the morning before food."+"\n");
                    }
                    if(drug.getBeforeNoonDose() != null && drug.getBeforeNoonDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getBeforeNoonDose()+" tablet in the afternoon before food."+"\n");
                    }
                    if(drug.getBeforeNightDose() != null && drug.getBeforeNightDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getBeforeNightDose()+" tablet in the night before food."+"\n");
                    }
                    if(drug.getMorningDose() != null && drug.getMorningDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getMorningDose()+" tablet in the morning after food."+"\n");
                    }
                    if(drug.getNoonDose() != null && drug.getNoonDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getNoonDose()+" tablet in the afternoon after food."+"\n");
                    }
                    if(drug.getNightDose() != null && drug.getNightDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getNightDose()+" tablet in the night after food."+"\n");
                    }
                    dosage = stringBuilder.toString();
                }else if(drug instanceof Syrup){
                    if(drug.getBeforeMorningDose() != null && drug.getBeforeMorningDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getBeforeMorningDose()+" syrup in the morning before food."+"\n");
                    }
                    if(drug.getBeforeNoonDose() != null && drug.getBeforeNoonDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getBeforeNoonDose()+" syrup in the afternoon before food."+"\n");
                    }
                    if(drug.getBeforeNightDose() != null && drug.getBeforeNightDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getBeforeNightDose()+" syrup in the night before food."+"\n");
                    }
                    if(drug.getMorningDose() != null && drug.getMorningDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getMorningDose()+" syrup in the morning after food."+"\n");
                    }
                    if(drug.getNoonDose() != null && drug.getNoonDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getNoonDose()+" syrup in the afternoon after food."+"\n");
                    }
                    if(drug.getNightDose() != null && drug.getNightDose().length() > 0){
                        stringBuilder.append("Consume "+drug.getNightDose()+" syrup in the night after food."+"\n");
                    }
                    dosage = stringBuilder.toString();
                }
            }else if(drug instanceof Soap || drug instanceof Ointment){
                if(drug.getMorningDose() != null && drug.getMorningDose().length() > 0){
                    stringBuilder.append("Apply "+drug.getMorningDose()+" in the morning."+"\n");
                }
                if(drug.getNoonDose() != null && drug.getNoonDose().length() > 0){
                    stringBuilder.append("Apply "+drug.getNoonDose()+" in the afternoon."+"\n");
                }
                if(drug.getNightDose() != null && drug.getNightDose().length() > 0){
                    stringBuilder.append( "Apply "+drug.getNightDose()+" in the night."+"\n");
                }
                dosage = stringBuilder.toString();
            }

            if(dosage != null && dosage.length() > 0){
                doctorPrescriptionViewHolder.prescription_description_usage_tv.setText(dosage);
            }

            if(drug.getNoOfDays() != null){
                doctorPrescriptionViewHolder.prescription_days_tv.setText("Number Of Days : "+String.valueOf(drug.getNoOfDays()));
            }

            doctorPrescriptionViewHolder.delete_prescription_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDoctorPrescriptionAdapterListeners.deleteDrug(drug, position);
                }
            });
            doctorPrescriptionViewHolder.edit_prescription_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDoctorPrescriptionAdapterListeners.editDrug(drug, position);
                }
            });
        }
    }

    // View holder for general row item
    class DoctorPrescriptionViewHolder extends RecyclerView.ViewHolder {

        private ImageView prescription_icon;
        private TextView prescription_name_tv, prescription_description_usage_tv, prescription_days_tv;
        private AppCompatTextView delete_prescription_tv, edit_prescription_tv;

        public DoctorPrescriptionViewHolder(View v) {
            super(v);

            prescription_icon = v.findViewById(R.id.prescription_icon);
            prescription_name_tv = v.findViewById(R.id.prescription_name_tv);
            prescription_description_usage_tv = v.findViewById(R.id.prescription_description_usage_tv);
            prescription_days_tv = v.findViewById(R.id.prescription_days_tv);

            delete_prescription_tv = v.findViewById(R.id.delete_prescription_tv);
            edit_prescription_tv = v.findViewById(R.id.edit_prescription_tv);
        }
    }

    private DoctorPrescriptionAdapterListeners mDoctorPrescriptionAdapterListeners;

    public interface DoctorPrescriptionAdapterListeners {
        void viewClicked(final Drug drug);
        void deleteDrug(final Drug drug, int selectedIndex);
        void editDrug(final Drug drug, int selectedIndex);
    }
}
