package com.vendora.scrap;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VendoraScrapServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(VendoraScrapServiceApplication.class, new String[]{})).thenReturn(null);
            VendoraScrapServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(VendoraScrapServiceApplication.class, new String[]{}));
        }
    }
}
