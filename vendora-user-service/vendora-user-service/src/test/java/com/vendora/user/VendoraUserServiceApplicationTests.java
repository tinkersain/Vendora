package com.vendora.user;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VendoraUserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodTest() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			mocked.when(() -> SpringApplication.run(VendoraUserServiceApplication.class, new String[]{})).thenReturn(null);
			VendoraUserServiceApplication.main(new String[]{});
			mocked.verify(() -> SpringApplication.run(VendoraUserServiceApplication.class, new String[]{}));
		}
	}
}
