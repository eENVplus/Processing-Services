package it.sinergis.element;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SingleUserElement {
	
	private String name;
	private String email;
	private String sms;
	private String phone;
	private String fax;
	private String http;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getSms() {
		return sms;
	}
	
	public void setSms(String sms) {
		this.sms = sms;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getFax() {
		return fax;
	}
	
	public void setFax(String fax) {
		this.fax = fax;
	}
	
	public String getHttp() {
		return http;
	}
	
	public void setHttp(String http) {
		this.http = http;
	}
	
}
