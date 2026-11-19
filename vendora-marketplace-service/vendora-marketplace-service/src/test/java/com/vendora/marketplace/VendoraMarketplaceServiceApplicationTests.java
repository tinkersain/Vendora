package com.vendora.marketplace;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VendoraMarketplaceServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(VendoraMarketplaceServiceApplication.class, new String[]{})).thenReturn(null);
            VendoraMarketplaceServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(VendoraMarketplaceServiceApplication.class, new String[]{}));
        }
    }
}
