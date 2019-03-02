package com.hapis.customer.ui.view;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.hapis.customer.HapisApplication;
import com.hapis.customer.R;
import com.hapis.customer.database.repository.AppointmentRepository;
import com.hapis.customer.database.repository.UserProfileRepository;
import com.hapis.customer.ui.models.HapisModel;
import com.hapis.customer.ui.models.ResponseStatus;
import com.hapis.customer.ui.models.enterprise.EnterpriseRequest;
import com.hapis.customer.ui.models.enterprise.EnterpriseResponseList;
import com.hapis.customer.ui.models.enums.UserTypeEnum;
import com.hapis.customer.ui.models.users.UserRequest;
import com.hapis.customer.ui.models.users.UserResponse;
import com.hapis.customer.ui.utils.EditTextUtils;

import java.util.List;

public class SignUpFragmentViewModal extends BaseViewModal<SignUpFragmentView> {

    private String TAG = SignUpFragmentViewModal.class.getName();

    private UserProfileRepository userProfileRepository;
    private AppointmentRepository appointmentRepository;

    public SignUpFragmentViewModal(LifecycleOwner owner) {
        super(owner);

        userProfileRepository = new UserProfileRepository();
        appointmentRepository = new AppointmentRepository();
    }

    @Override
    protected void initObservableData() {

    }

    @Override
    protected void handleChangedDataModal(HapisModel data) {

    }

    public void validateRegistrationDetails(List<String> mSelectedLocation, EnterpriseRequest selectedEnterpriseRequest, String specialization, String first_name, String last_name, String mobile_number, String emailId,
                                            String password) {
        String msg = null;

        if(!(mSelectedLocation != null && mSelectedLocation.size() > 0 && mSelectedLocation.size() == 3)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_select_preferred_location);
        }else if(selectedEnterpriseRequest == null){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_select_enterprise);
        }else if(EditTextUtils.isEmpty(specialization)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_select_specialization);
        }else if(EditTextUtils.isEmpty(first_name)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_first_name);
        }else if(EditTextUtils.isEmpty(last_name)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_last_name);
        }else if(EditTextUtils.isEmpty(mobile_number)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_mobile_number);
        }/*else if(EditTextUtils.isEmpty(emailId)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_email_id);
        }*/else if(!EditTextUtils.isEmpty(emailId) && !EditTextUtils.isValidEmail(emailId)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_valid_email_id);
        }else if(EditTextUtils.isEmpty(password)){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_password);
        }

        mView.validateScreenFields(msg);
    }

    MutableLiveData<UserResponse> mutableLiveData = new MutableLiveData<UserResponse>();

    public void doSignup(String enterpriseCode, String specialization, String selectedPrefix, String firstName, String middleName, String lastName,
                         String mobileNumber, String emailAddress, String password) {

        UserRequest userRequest = new UserRequest();

        userRequest.setEnterpriseCode(enterpriseCode);
        userRequest.setRoles(specialization);

        userRequest.setAgentCode("NotDefined");
        userRequest.setNamePrefix(selectedPrefix);
        userRequest.setFirstName(firstName);
        userRequest.setMiddleName(middleName);
        userRequest.setLastName(lastName);
        userRequest.setMobileNo(mobileNumber);
        userRequest.setEmailAddress(emailAddress);
        userRequest.setPassword(password);

        userRequest.setUserType(UserTypeEnum.DOCTOR.code());

        userProfileRepository.doSignup(mutableLiveData, userRequest);
        mutableLiveData.observe(mOwner, new Observer<UserResponse>() {
            @Override
            public void onChanged(@Nullable UserResponse userModelResponse) {
                if(userModelResponse != null){
                    if(userModelResponse.getStatus() != null && userModelResponse.getStatus().getStatusCode() != null && userModelResponse.getStatus().getStatusCode().intValue() == ResponseStatus.SUCCESS){
                        mView.SignupRequestSuccess(HapisApplication.getApplication().getResources().getString(R.string.signup_successful));
                    }else{
                        mView.SignupRequestFailed(((userModelResponse.getStatus().getErrorMessages() != null && userModelResponse.getStatus().getErrorMessages().size() > 0) ? userModelResponse.getStatus().getErrorMessages().get(0).getMessageDescription() : HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request)));
                    }
                }else{
                    mView.SignupRequestFailed(HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request));
                }
            }
        });
    }

    public void getEnterprisesBy(int enterpriseType, String City) {

        MutableLiveData<EnterpriseResponseList> enterpriseResponseListMutableLiveData = new MutableLiveData<>();

        appointmentRepository.getEnterprisesByEnterpriseTypeAndCity(enterpriseResponseListMutableLiveData, enterpriseType, City);
        enterpriseResponseListMutableLiveData.observe(mOwner, new Observer<EnterpriseResponseList>() {
            @Override
            public void onChanged(@Nullable EnterpriseResponseList enterpriseResponseList) {
                if(enterpriseResponseList != null){
                    if(enterpriseResponseList.getResults() != null && enterpriseResponseList.getResults().size() > 0){
                        mView.updateEnterpriseByTypeAndCity(enterpriseResponseList.getResults());
                    }else{
                        enterpriseResponseListMutableLiveData.removeObserver(new Observer<EnterpriseResponseList>() {
                            @Override
                            public void onChanged(@Nullable EnterpriseResponseList enterpriseResponseList) {

                            }
                        });
                        mView.SignupRequestFailed(((enterpriseResponseList.getStatus().getErrorMessages() != null && enterpriseResponseList.getStatus().getErrorMessages().size() > 0) ? enterpriseResponseList.getStatus().getErrorMessages().get(0).getMessageDescription() : HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request)));
                    }
                }else{
                    enterpriseResponseListMutableLiveData.removeObserver(new Observer<EnterpriseResponseList>() {
                        @Override
                        public void onChanged(@Nullable EnterpriseResponseList enterpriseResponseList) {

                        }
                    });
                    mView.SignupRequestFailed(HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request));
                }
            }
        });
    }
}
