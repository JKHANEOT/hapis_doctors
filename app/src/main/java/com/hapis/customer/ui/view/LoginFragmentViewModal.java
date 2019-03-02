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
import com.hapis.customer.ui.models.enterprise.EnterpriseSimpleRequest;
import com.hapis.customer.ui.models.enterprise.EnterpriseSimpleResponse;
import com.hapis.customer.ui.models.users.LoginResponse;
import com.hapis.customer.ui.models.users.UserRequest;
import com.hapis.customer.ui.models.users.UserResponse;
import com.hapis.customer.ui.utils.EditTextUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginFragmentViewModal extends BaseViewModal<LoginFragmentView> {

    private String TAG = LoginFragmentViewModal.class.getName();

    private UserProfileRepository userProfileRepository;
    private AppointmentRepository appointmentRepository;

    public LoginFragmentViewModal(LifecycleOwner owner) {
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

    public void validateLoginDetails(String inputEmailIdOrMobileNumber, EnterpriseRequest selectedEnterpriseRequest, String password) {
        String msg = null;

        if (inputEmailIdOrMobileNumber.length() == 0) {
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_mobile_number);
        } else if (inputEmailIdOrMobileNumber.length() > 0 && EditTextUtils.isNumeric(inputEmailIdOrMobileNumber) &&
                !(inputEmailIdOrMobileNumber.contains("@") || inputEmailIdOrMobileNumber.contains(".")) &&
                inputEmailIdOrMobileNumber.length() > HapisApplication.getApplication().getResources().getInteger(R.integer.MOBILE_NO_LENGTH)) {
            msg = HapisApplication.getApplication().getResources().getString(R.string.mobile_number_should_not_more_than_10);
        } else if (inputEmailIdOrMobileNumber.length() > 0 && EditTextUtils.isNumeric(inputEmailIdOrMobileNumber) &&
                !(inputEmailIdOrMobileNumber.contains("@") || inputEmailIdOrMobileNumber.contains("."))
                && inputEmailIdOrMobileNumber.startsWith("0")) {
            msg = HapisApplication.getApplication().getResources().getString(R.string.invalid_mobile_number_starts_with_zero);
        }else if(selectedEnterpriseRequest == null){
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_select_enterprise);
        } else if (password.length() == 0) {
            msg = HapisApplication.getApplication().getResources().getString(R.string.please_enter_password);
        } else if (password.length() < 4) {
            msg = HapisApplication.getApplication().getResources().getString(R.string.password_should_be_of_length);
        }

        mView.validateScreenFields(msg);
    }

    MutableLiveData<UserResponse> mutableLiveData = new MutableLiveData<UserResponse>();

    public void doLogin(String userName, String enterpriseCode, String password) {

        userProfileRepository.doLogin(mutableLiveData, userName, enterpriseCode, password);
        mutableLiveData.observe(mOwner, new Observer<UserResponse>() {
            @Override
            public void onChanged(@Nullable UserResponse userModelResponse) {
                if(userModelResponse != null){
                    if(userModelResponse.getStatus() != null && userModelResponse.getStatus().getStatusCode() != null && userModelResponse.getStatus().getStatusCode().intValue() == ResponseStatus.SUCCESS){
                        mView.SigninRequestSuccess(HapisApplication.getApplication().getResources().getString(R.string.sign_in_successful));
                    }else{
                        mView.SigninRequestFailed(((userModelResponse.getStatus().getErrorMessages() != null && userModelResponse.getStatus().getErrorMessages().size() > 0) ? userModelResponse.getStatus().getErrorMessages().get(0).getMessageDescription() : HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request)));
                    }
                }else{
                    mView.SigninRequestFailed(HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request));
                }
            }
        });
    }

    public void getEnterprisesBy(String mobileNumber) {

        MutableLiveData<EnterpriseSimpleResponse> enterpriseResponseListMutableLiveData = new MutableLiveData<>();

        appointmentRepository.getEnterprisesByMobileNumber(enterpriseResponseListMutableLiveData, mobileNumber);
        enterpriseResponseListMutableLiveData.observe(mOwner, new Observer<EnterpriseSimpleResponse>() {
            @Override
            public void onChanged(@Nullable EnterpriseSimpleResponse enterpriseResponseList) {
                if(enterpriseResponseList != null){
                    if(enterpriseResponseList.getEnterpriseSimpleRequests() != null && enterpriseResponseList.getEnterpriseSimpleRequests().size() > 0){
                        List<EnterpriseRequest> enterpriseRequests = new ArrayList<>();
                        for(EnterpriseSimpleRequest enterpriseSimpleRequest : enterpriseResponseList.getEnterpriseSimpleRequests()){
                            EnterpriseRequest enterpriseRequest = new EnterpriseRequest();

                            enterpriseRequest.setEnterpriseCode(enterpriseSimpleRequest.getEnterpriseCode());
                            enterpriseRequest.setEnterpriseName(enterpriseSimpleRequest.getEnterpriseName());
                            enterpriseRequest.setUserId(enterpriseSimpleRequest.getUserCode());

                            enterpriseRequests.add(enterpriseRequest);
                        }
                        mView.updateEnterpriseByTypeAndCity(enterpriseRequests);
                    }else{
                        enterpriseResponseListMutableLiveData.removeObserver(new Observer<EnterpriseSimpleResponse>() {
                            @Override
                            public void onChanged(@Nullable EnterpriseSimpleResponse enterpriseResponseList) {

                            }
                        });
                        mView.SigninRequestFailed(((enterpriseResponseList.getStatus().getErrorMessages() != null && enterpriseResponseList.getStatus().getErrorMessages().size() > 0) ? enterpriseResponseList.getStatus().getErrorMessages().get(0).getMessageDescription() : HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request)));
                    }
                }else{
                    enterpriseResponseListMutableLiveData.removeObserver(new Observer<EnterpriseSimpleResponse>() {
                        @Override
                        public void onChanged(@Nullable EnterpriseSimpleResponse enterpriseResponseList) {

                        }
                    });
                    mView.SigninRequestFailed(HapisApplication.getApplication().getResources().getString(R.string.unable_to_process_request));
                }
            }
        });
    }
}
