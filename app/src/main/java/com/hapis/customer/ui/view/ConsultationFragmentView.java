package com.hapis.customer.ui.view;

public interface ConsultationFragmentView extends BaseView {

    void failedToProcess(String errorMsg);

    void completeConsultation(String msg);

}
