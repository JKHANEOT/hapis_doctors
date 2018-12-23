package com.hapis.customer.ui.view;

import com.hapis.customer.ui.models.enterprise.EnterpriseRequest;

import java.util.List;

public interface LoginFragmentView extends BaseView {

    void validateScreenFields(String errorMsg);

    void forgotPasswordClicked();

    void signUpClicked();

    void SigninRequestSuccess(String msg);

    void SigninRequestFailed(String errorMsg);

    void updateEnterpriseByTypeAndCity(List<EnterpriseRequest> enterpriseRequestList);
}
