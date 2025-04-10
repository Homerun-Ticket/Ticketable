package com.example.ticketable.common.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamPortConfig {
	
	@Value("${import.api.key}")
	private String apiKey;
	
	@Value("${import.api.secret}")
	private String apiSecret;
	
	@Bean
	public IamportClient iamportClient() {
		return new IamportClient(apiKey, apiSecret);
	}
}
