package it.sinergis.element;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {
	private String httpCode;
	private String errorMessage;
	private Object responseValue;
	
	public Response() {
		
	}
	
	public Response(String httpCode, String errorMessage, Object responseValue) {
		super();
		this.httpCode = httpCode;
		this.errorMessage = errorMessage;
		this.setResponseValue(responseValue);
	}
	
	public String getHttpCode() {
		return httpCode;
	}
	
	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void setResponseValue(Object responseValue) {
		this.responseValue = responseValue;
	}
	
	public Object getResponseValue() {
		return responseValue;
	}
	
}
