package com.inventoryManagement.product_service.scheduler;

import com.inventoryManagement.product_service.dto.ProductOnlyResponse;
import com.inventoryManagement.product_service.dto.ProductResponse;
import com.inventoryManagement.product_service.service.MailService;
import com.inventoryManagement.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockAlertScheduler {

    private final ProductService productService;
    private final MailService mailService;

    @Value("${inventory.alert.recipients}")
    private String[] recipients;

    @Scheduled(fixedRate = 3600000) // every hour
    public void checkLowStock() {
        List<ProductOnlyResponse> lowStock = productService.getLowStockProducts();
        if (!lowStock.isEmpty()) {
            System.out.println("⚠️ Low stock products: " + lowStock);

            StringBuilder alertMsg = new StringBuilder("Low stock alert for following products:\n");
            for (ProductOnlyResponse p : lowStock) {
                alertMsg.append("- ")
                        .append(p.getName())
                        .append(" (Qty: ")
                        .append(p.getQuantity())
                        .append(", Reorder Level: ")
                        .append(p.getReorderLevel())
                        .append(")\n");
            }

            for (String recipient : recipients) {
                mailService.sendSimpleMail(recipient, "Low Stock Alert", alertMsg.toString());
            }
        }
    }
}

