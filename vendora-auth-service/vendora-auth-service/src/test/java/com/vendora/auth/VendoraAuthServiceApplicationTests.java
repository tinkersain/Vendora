package com.vendora.auth;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VendoraAuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodTest() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			mocked.when(() -> SpringApplication.run(VendoraAuthServiceApplication.class, new String[]{})).thenReturn(null);
			VendoraAuthServiceApplication.main(new String[]{});
			mocked.verify(() -> SpringApplication.run(VendoraAuthServiceApplication.class, new String[]{}));
		}
	}
}
