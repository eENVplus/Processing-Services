package it.sinergis.element;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DoNotificationElement {

	// general
	private String userId;
	private long maxTTLOfMessage;
	private String shortMessage;

	// serviceDescription
	private String serviceType;
	private String serviceTypeVersion;
	private String serviceUrl;
	
	//payload
	private String status;
	private String SPSCorrID;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceTypeVersion() {
		return serviceTypeVersion;
	}

	public void setServiceTypeVersion(String serviceTypeVersion) {
		this.serviceTypeVersion = serviceTypeVersion;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	public long getMaxTTLOfMessage() {
		return maxTTLOfMessage;
	}

	public void setMaxTTLOfMessage(long maxTTLOfMessage) {
		this.maxTTLOfMessage = maxTTLOfMessage;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSPSCorrID() {
		return SPSCorrID;
	}

	public void setSPSCorrID(String sPSCorrID) {
		SPSCorrID = sPSCorrID;
	}
}
