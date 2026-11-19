package com.vendora.helpdesk;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VendoraHelpdeskServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(VendoraHelpdeskServiceApplication.class, new String[]{})).thenReturn(null);
            VendoraHelpdeskServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(VendoraHelpdeskServiceApplication.class, new String[]{}));
        }
    }
}
