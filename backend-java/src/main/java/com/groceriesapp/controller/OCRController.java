package com.groceriesapp.controller;

import com.groceriesapp.service.OCRService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OCRController {
    
    private final OCRService ocrService;
    
    @MutationMapping
    public Map<String, Object> processImageForOCR(@Argument String imageBase64) {
        return ocrService.processImage(imageBase64);
    }
}
