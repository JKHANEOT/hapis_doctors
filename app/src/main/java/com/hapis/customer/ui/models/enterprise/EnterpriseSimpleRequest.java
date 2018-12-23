/**
 * 
 */
package com.hapis.customer.ui.models.enterprise;

import com.hapis.customer.ui.models.RequestMessageModel;

/**
 * @author Hidayathulla.Khan
 *
 */
public class EnterpriseSimpleRequest extends RequestMessageModel {

	private static final long serialVersionUID = 1219417005503164698L;

	private String mobileNo;

	private String enterpriseName;

	private String userCode;

	private String enterpriseCode;

	private Integer state;

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
}
