package com.pratikmane.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.pratikmane.utils.CloudinaryService;

/**
 * Test configuration for mocking external dependencies
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public CloudinaryService mockCloudinaryService() {
        CloudinaryService mock = Mockito.mock(CloudinaryService.class);
        
        // Default behavior for uploadImage
        Mockito.when(mock.uploadImage(Mockito.any()))
                .thenReturn("https://test-cloudinary.com/test-image.jpg");
        
        // Default behavior for deleteImage
        Mockito.when(mock.deleteImage(Mockito.anyString()))
                .thenReturn(true);
        
        return mock;
    }

    @Bean
    @Primary
    public SimpMessagingTemplate mockSimpMessagingTemplate() {
        return Mockito.mock(SimpMessagingTemplate.class);
    }
}
