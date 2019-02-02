/**
 * 
 */
package com.hapis.customer.ui.models.consultation;

import com.hapis.customer.ui.models.MessageModel;

/**
 * @author Hidayathulla.Khan
 *
 */
public abstract class Drug extends MessageModel {

	private static final long serialVersionUID = -6711520379106534312L;

	private String name;

	private String morningDose;
	
	private String noonDose;
	
	private String nightDose;
	
	private String beforeMorningDose;
	
	private String beforeNoonDose;
	
	private String beforeNightDose;

	public String getMorningDose() {
		return morningDose;
	}

	public void setMorningDose(String morningDose) {
		this.morningDose = morningDose;
	}

	public String getNoonDose() {
		return noonDose;
	}

	public void setNoonDose(String noonDose) {
		this.noonDose = noonDose;
	}

	public String getNightDose() {
		return nightDose;
	}

	public void setNightDose(String nightDose) {
		this.nightDose = nightDose;
	}

	public String getBeforeMorningDose() {
		return beforeMorningDose;
	}

	public void setBeforeMorningDose(String beforeMorningDose) {
		this.beforeMorningDose = beforeMorningDose;
	}

	public String getBeforeNoonDose() {
		return beforeNoonDose;
	}

	public void setBeforeNoonDose(String beforeNoonDose) {
		this.beforeNoonDose = beforeNoonDose;
	}

	public String getBeforeNightDose() {
		return beforeNightDose;
	}

	public void setBeforeNightDose(String beforeNightDose) {
		this.beforeNightDose = beforeNightDose;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Drug [name=" + name + ", morningDose=" + morningDose + ", noonDose=" + noonDose + ", nightDose="
				+ nightDose + ", beforeMorningDose=" + beforeMorningDose + ", beforeNoonDose=" + beforeNoonDose
				+ ", beforeNightDose=" + beforeNightDose + "]";
	}


}
