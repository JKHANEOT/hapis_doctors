package com.hapis.customer.ui.view;

import com.hapis.customer.ui.models.enterprise.EnterpriseRequest;

import java.util.List;

public interface SignUpFragmentView extends BaseView {

    void validateScreenFields(String errorMsg);

    void signInClicked();

    void SignupRequestFailed(String errorMsg);

    void SignupRequestSuccess(String msg);

    void updateEnterpriseByTypeAndCity(List<EnterpriseRequest> enterpriseRequestList);
}
