package com.example.ticketable.domain.point.service;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.domain.point.entity.PointPayment;
import com.siot.IamportRestClient.response.IamportResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PointPaymentService {
	
	@Value("${import.api.key}")
	private String apiKey;
	
	@Value("${import.api.secret}")
	private String apiSecret;
	
	public IamportResponse<PointPayment> iamPortPayment(Auth auth, Long imp_uid) {
		
		return null;
	}
}
