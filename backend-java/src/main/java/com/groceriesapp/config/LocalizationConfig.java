package com.groceriesapp.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Internationalization (i18n) Configuration
 * 
 * Supports 20+ languages for global accessibility.
 */
@Configuration
public class LocalizationConfig {
    
    /**
     * Supported languages list.
     * Add more languages as needed.
     */
    public static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
        Locale.ENGLISH,                    // en
        new Locale("es"),                  // Spanish
        Locale.FRENCH,                     // fr
        Locale.GERMAN,                     // de
        Locale.ITALIAN,                    // it
        new Locale("pt"),                  // Portuguese
        Locale.SIMPLIFIED_CHINESE,         // zh-CN
        Locale.TRADITIONAL_CHINESE,        // zh-TW
        Locale.JAPANESE,                   // ja
        Locale.KOREAN,                     // ko
        new Locale("ar"),                  // Arabic
        new Locale("hi"),                  // Hindi
        new Locale("ru"),                  // Russian
        new Locale("tr"),                  // Turkish
        new Locale("nl"),                  // Dutch
        new Locale("pl"),                  // Polish
        new Locale("sv"),                  // Swedish
        new Locale("id"),                  // Indonesian
        new Locale("th"),                  // Thai
        new Locale("vi")                   // Vietnamese
    );
    
    /**
     * Configure message source for internationalization.
     * Messages are stored in messages_xx.properties files.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(3600); // Cache for 1 hour
        return messageSource;
    }
    
    /**
     * Configure locale resolver to detect user's preferred language.
     * Uses Accept-Language header from HTTP request.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setSupportedLocales(SUPPORTED_LOCALES);
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }
}
