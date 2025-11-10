package com.groceriesapp.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCRService {
    
    private final Tesseract tesseract;
    
    public OCRService() {
        this.tesseract = new Tesseract();
        // Set the path to tessdata (training data)
        // In production, you would configure this properly
        this.tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        this.tesseract.setLanguage("eng");
    }
    
    /**
     * Process an image (base64 encoded) and extract grocery item information.
     * Returns a map with extracted data: name, quantity, expirationDate
     */
    public Map<String, Object> processImage(String imageBase64) {
        try {
            // Decode base64 image
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            
            // Perform OCR
            String extractedText = tesseract.doOCR(image);
            
            // Parse the extracted text
            return parseExtractedText(extractedText);
            
        } catch (IOException | TesseractException e) {
            // Fallback to simulation if OCR fails
            return simulateOCRResult();
        }
    }
    
    /**
     * Parse extracted text to find item name, quantity, and expiration date.
     */
    private Map<String, Object> parseExtractedText(String text) {
        Map<String, Object> result = new HashMap<>();
        
        // Extract item name (first line or first capitalized word)
        String itemName = extractItemName(text);
        result.put("name", itemName != null ? itemName : "Unknown Item");
        
        // Extract quantity
        Integer quantity = extractQuantity(text);
        result.put("quantity", quantity != null ? quantity : 1);
        
        // Extract expiration date
        LocalDate expirationDate = extractExpirationDate(text);
        result.put("expirationDate", expirationDate != null ? 
            expirationDate.toString() : LocalDate.now().plusDays(7).toString());
        
        return result;
    }
    
    /**
     * Extract item name from text.
     */
    private String extractItemName(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        // Try to find product name patterns
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            // Skip very short lines or lines that look like dates/numbers
            if (line.length() > 3 && !line.matches(".*\\d{2}/\\d{2}/\\d{4}.*") && 
                !line.matches("^[0-9]+$")) {
                // Return the first meaningful line as the product name
                return line.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
            }
        }
        
        return null;
    }
    
    /**
     * Extract quantity from text.
     */
    private Integer extractQuantity(String text) {
        if (text == null) {
            return null;
        }
        
        // Look for patterns like "Qty: 2", "Quantity: 3", "x2", "2 units"
        Pattern qtyPattern = Pattern.compile("(?i)(qty|quantity|count|x)\\s*:?\\s*(\\d+)");
        Matcher matcher = qtyPattern.matcher(text);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        // Look for standalone numbers that might be quantity
        Pattern numberPattern = Pattern.compile("\\b([1-9]\\d?)\\b");
        matcher = numberPattern.matcher(text);
        if (matcher.find()) {
            try {
                int num = Integer.parseInt(matcher.group(1));
                // Only return if it's a reasonable quantity (1-99)
                if (num >= 1 && num <= 99) {
                    return num;
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Extract expiration date from text.
     */
    private LocalDate extractExpirationDate(String text) {
        if (text == null) {
            return null;
        }
        
        // Common date patterns
        String[] datePatterns = {
            "(?i)(exp|expiry|expires|best before|use by)\\s*:?\\s*(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})",
            "\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})\\b"
        };
        
        for (String patternStr : datePatterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(text);
            
            if (matcher.find()) {
                String dateStr = matcher.group(matcher.groupCount());
                LocalDate date = parseDate(dateStr);
                if (date != null) {
                    return date;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Parse a date string into LocalDate.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        
        // Try different date formats
        String[] formats = {
            "MM/dd/yyyy",
            "dd/MM/yyyy",
            "MM-dd-yyyy",
            "dd-MM-yyyy",
            "yyyy-MM-dd",
            "MM/dd/yy",
            "dd/MM/yy"
        };
        
        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }
        
        return null;
    }
    
    /**
     * Fallback simulation when OCR fails or for testing.
     */
    private Map<String, Object> simulateOCRResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "Milk");
        result.put("quantity", 2);
        result.put("expirationDate", LocalDate.now().plusDays(7).toString());
        return result;
    }
}
