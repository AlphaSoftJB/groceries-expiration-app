package com.groceriesapp.service

import org.springframework.stereotype.Service
import java.time.LocalDate

data class OCRResult(
    val name: String,
    val quantity: Int,
    val expirationDate: LocalDate?
)

@Service
class OCRService {

    /**
     * Simulates an OCR process on an image of a receipt or product label.
     * In a real application, this would involve uploading the image and calling an OCR API.
     *
     * @param imageBase64 A base64 encoded string of the image (placeholder).
     * @return An OCRResult object with extracted data.
     */
    fun processImageForItemDetails(imageBase64: String): OCRResult {
        // Placeholder logic:
        // Since we can't process the image, we'll return a mock result based on a simple check.
        return when {
            imageBase64.contains("milk") -> OCRResult("Milk (OCR)", 1, LocalDate.now().plusDays(10))
            imageBase64.contains("bread") -> OCRResult("Bread (OCR)", 1, LocalDate.now().plusDays(5))
            else -> OCRResult("Unknown Item (OCR)", 1, null)
        }
    }
}
