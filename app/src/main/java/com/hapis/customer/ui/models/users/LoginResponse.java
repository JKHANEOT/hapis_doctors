/**
 * 
 */
package com.hapis.customer.ui.models.users;

import com.hapis.customer.ui.models.ResponseModel;

/**
 * @author Hidayathulla.Khan
 *
 */
public class LoginResponse  extends ResponseModel {

	private static final long serialVersionUID = 8911702648351315575L;

	private LoginRequest message;

	public LoginRequest getMessage() {
		return message;
	}

	public void setMessage(LoginRequest arg0) {
		message = arg0;
	}

	private String userCode;

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
}
