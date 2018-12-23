/**
 * 
 */
package com.hapis.customer.ui.models.users;

import com.hapis.customer.ui.models.RequestMessageModel;

/**
 * @author Hidayathulla.Khan
 *
 */
public class LoginRequest extends RequestMessageModel {

	private static final long serialVersionUID = 6977595734770260818L;
	
	private String mobileNo;
	
	private String password;

	private String enterpriseCode;

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}
}
