package com.groceriesapp.controller

import com.groceriesapp.service.OCRResult
import com.groceriesapp.service.OCRService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class OCRResolver(
    private val ocrService: OCRService
) {

    @MutationMapping
    fun processImageForOCR(@Argument imageBase64: String): OCRResult {
        return ocrService.processImageForItemDetails(imageBase64)
    }
}
