package com.bos.payment.appName.data.model.travel.flight.flightticketcancelResponse;

import com.bos.payment.appName.data.model.travel.flight.ResponseHeader;
import com.google.gson.annotations.SerializedName;

public class TicketCancelResponse{

	@SerializedName("response_Header")
	private ResponseHeader responseHeader;

	@SerializedName("message")
	private String message;

	@SerializedName("statuss")
	private String statuss;

	@SerializedName("value")
	private String value;

	public ResponseHeader getResponseHeader(){
		return responseHeader;
	}

	public String getMessage(){
		return message;
	}

	public String getStatuss(){
		return statuss;
	}

	public String getValue(){
		return value;
	}
}