package com.vendora.eureka;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VendoraEurekaServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodTest() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			mocked.when(() -> SpringApplication.run(VendoraEurekaServerApplication.class, new String[]{})).thenReturn(null);
			VendoraEurekaServerApplication.main(new String[]{});
			mocked.verify(() -> SpringApplication.run(VendoraEurekaServerApplication.class, new String[]{}));
		}
	}
}
