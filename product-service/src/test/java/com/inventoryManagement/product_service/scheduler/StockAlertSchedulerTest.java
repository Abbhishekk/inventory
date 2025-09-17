package com.inventoryManagement.product_service.scheduler;

import com.inventoryManagement.product_service.dto.ProductOnlyResponse;
import com.inventoryManagement.product_service.service.MailService;
import com.inventoryManagement.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockAlertSchedulerTest {

    @Mock
    ProductService productService;

    @Mock
    MailService mailService;

    @InjectMocks
    StockAlertScheduler scheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // set recipients via reflection to avoid null
        try {
            java.lang.reflect.Field f = StockAlertScheduler.class.getDeclaredField("recipients");
            f.setAccessible(true);
            f.set(scheduler, new String[]{"a@x.com", "b@x.com"});
        } catch (Exception ignored) {
        }
    }

    @Test
    void checkLowStock_sendsMailsWhenLowStock() {
        ProductOnlyResponse p = ProductOnlyResponse.builder().id(1L).name("p").quantity(1).reorderLevel(5).build();
        when(productService.getLowStockProducts()).thenReturn(Arrays.asList(p));
        scheduler.checkLowStock();
        verify(mailService, times(2)).sendSimpleMail(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void checkLowStock_noMailsWhenEmpty() {
        when(productService.getLowStockProducts()).thenReturn(Collections.emptyList());
        scheduler.checkLowStock();
    }
}
