package com.hapis.customer.ui.view;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.hapis.customer.database.repository.UserProfileRepository;
import com.hapis.customer.ui.models.HapisModel;

public class DashboardViewModal extends BaseViewModal<DashboardView> {

    private String TAG = DashboardViewModal.class.getName();

    private UserProfileRepository userProfileRepository;

    public DashboardViewModal(LifecycleOwner owner) {
        super(owner);

        userProfileRepository = new UserProfileRepository();
    }

    @Override
    protected void initObservableData() {

    }

    @Override
    protected void handleChangedDataModal(HapisModel data) {

    }

    public void proceedWithLogout(){

        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<Boolean>();

        userProfileRepository.new LogoutAsyncTask(mutableLiveData).execute();
        mutableLiveData.observe(mOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null){
                    mView.logoutStatus(aBoolean.booleanValue());
                }else{
                    mView.logoutStatus(false);
                }
            }
        });
    }
}
