package com.groceriesapp.controller;

import com.groceriesapp.service.BarcodeService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * GraphQL controller for barcode scanning operations.
 */
@Controller
public class BarcodeController {
    
    private final BarcodeService barcodeService;
    
    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }
    
    @MutationMapping
    public Map<String, Object> scanBarcode(@Argument String barcode) {
        return barcodeService.scanBarcode(barcode);
    }
}
