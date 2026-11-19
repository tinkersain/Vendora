package com.vendora.payment;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VendoraPaymentServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(VendoraPaymentServiceApplication.class, new String[]{})).thenReturn(null);
            VendoraPaymentServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(VendoraPaymentServiceApplication.class, new String[]{}));
        }
    }
}
