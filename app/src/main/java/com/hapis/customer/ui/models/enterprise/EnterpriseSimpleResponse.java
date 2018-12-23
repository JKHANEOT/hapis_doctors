/**
 *
 */
package com.hapis.customer.ui.models.enterprise;

import com.hapis.customer.ui.models.ResponseModel;

import java.util.List;

/**
 * @author Hidayathulla.Khan
 *
 */
public class EnterpriseSimpleResponse extends ResponseModel {

	private static final long serialVersionUID = 1518471016985280625L;

	private List<EnterpriseSimpleRequest> enterpriseSimpleRequests;

	public List<EnterpriseSimpleRequest> getEnterpriseSimpleRequests() {
		return enterpriseSimpleRequests;
	}

	public void setEnterpriseSimpleRequests(List<EnterpriseSimpleRequest> enterpriseSimpleRequests) {
		this.enterpriseSimpleRequests = enterpriseSimpleRequests;
	}
}
