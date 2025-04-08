package com.example.ticketable;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TicketableApplicationTests {

	@Autowired
	private Environment env;

	@Test
	void contextLoads() {
		String datasourceUrl = env.getProperty("spring.datasource.url");
		System.out.println("Using datasource: " + datasourceUrl);		// CI에서 어떤 프로퍼티스가 로드 되는지 확인 로그
	}
	
}
