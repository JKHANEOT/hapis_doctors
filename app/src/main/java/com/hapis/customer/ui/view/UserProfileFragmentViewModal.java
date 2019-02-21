package com.hapis.customer.ui.view;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.hapis.customer.HapisApplication;
import com.hapis.customer.R;
import com.hapis.customer.database.repository.UserProfileRepository;
import com.hapis.customer.database.tables.UserProfileTable;
import com.hapis.customer.ui.models.HapisModel;
import com.hapis.customer.ui.models.ResponseStatus;
import com.hapis.customer.ui.models.enterprise.EnterpriseRequest;
import com.hapis.customer.ui.models.users.UserRequest;
import com.hapis.customer.ui.models.users.UserResponse;
import com.hapis.customer.ui.utils.EditTextUtils;

import java.util.Date;
import java.util.List;

public class UserProfileFragmentViewModal extends BaseViewModal<UserProfileFragmentView> {

    private String TAG = UserProfileFragmentViewModal.class.getName();

    private UserProfileRepository userProfileRepository;

    public UserProfileFragmentViewModal(LifecycleOwner owner) {
        super(owner);

        userProfileRepository = new UserProfileRepository();
    }

    @Override
    protected void initObservableData() {
    }

    @Override
    protected void handleChangedDataModal(HapisModel data) {

    }

    public void validateRegistrationDetails(String first_name, String last_name, String mobile_number, String emailId) {
        String msg = null;

        if(EditTextUtils.isEmpty(first_name)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_first_name);
        }else if(EditTextUtils.isEmpty(last_name)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_last_name);
        }else if(EditTextUtils.isEmpty(mobile_number)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_mobile_number);
        }/*else if(EditTextUtils.isEmpty(emailId)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_email_id);
        }*/else if(!EditTextUtils.isEmpty(emailId) && !EditTextUtils.isValidEmail(emailId)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_valid_email_id);
        }

        mView.validateScreenFields(msg);
    }

    public void loadUserProfileDetails() {
        MutableLiveData<UserProfileTable> userProfileTableMutableLiveData = new MutableLiveData<>();

        userProfileRepository.loadUserProfileDetails(userProfileTableMutableLiveData);
        userProfileTableMutableLiveData.observe(mOwner, new Observer<UserProfileTable>() {
            @Override
            public void onChanged(@Nullable UserProfileTable userProfileTable) {
                if(userProfileTable != null){
                    mView.loadUserProfileDetails(userProfileTable);
                }
            }
        });
    }

    public void doUserProfileUpdate(String firstName, String middleName, String lastName,
                         String mobileNumber, String emailAddress, Integer gender, Date dateOfBirth) {

        MutableLiveData<UserResponse> userResponseMutableLiveData = new MutableLiveData<>();

        userProfileRepository.updateUserProfile(userResponseMutableLiveData, firstName, middleName, lastName, mobileNumber, emailAddress, gender, dateOfBirth);
        userResponseMutableLiveData.observe(mOwner, new Observer<UserResponse>() {
            @Override
            public void onChanged(@Nullable UserResponse userModelResponse) {
                if(userModelResponse != null){
                    if(userModelResponse.getStatus() != null && userModelResponse.getStatus().getStatusCode() != null && userModelResponse.getStatus().getStatusCode().intValue() == ResponseStatus.SUCCESS){
                        mView.updateUserProfile(HapisApplication.getApplication().getResources().getString(R.string.update_profile_successful));
                    }else{
                        mView.failedToProcess(((userModelResponse.getStatus().getErrorMessages() != null && userModelResponse.getStatus().getErrorMessages().size() > 0) ? userModelResponse.getStatus().getErrorMessages().get(0).getMessageDescription() : HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request)));
                    }
                }else{
                    mView.failedToProcess(HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request));
                }
            }
        });
    }
}
