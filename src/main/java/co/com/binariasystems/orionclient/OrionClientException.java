package co.com.binariasystems.orionclient;

import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.orion.model.enumerated.SecurityExceptionType;

public class OrionClientException extends FMWUncheckedException {
	public SecurityExceptionType exceptionType;
	
	public OrionClientException(SecurityExceptionType exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	public OrionClientException(SecurityExceptionType exceptionType, String message) {
		super(message);
		this.exceptionType = exceptionType;
	}
	
	public OrionClientException(SecurityExceptionType exceptionType, Throwable cause) {
		super(cause.getMessage(), cause);
		this.exceptionType = exceptionType;
	}

	public OrionClientException() {
		super();
	}

	public OrionClientException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public OrionClientException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public OrionClientException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public OrionClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the exceptionType
	 */
	public SecurityExceptionType getExceptionType() {
		return exceptionType;
	}

	/**
	 * @param exceptionType the exceptionType to set
	 */
	public void setExceptionType(SecurityExceptionType exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	
}
