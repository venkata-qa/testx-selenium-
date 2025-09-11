package com.testx.web.api.selenium.restassured.qe.api.httpoperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Secure HTTPS certificate handling with proper validation.
 * Replaces the previous insecure "trust all certificates" implementation.
 */
public class HttpsCertTrust {
    
    private static final Logger log = LoggerFactory.getLogger(HttpsCertTrust.class);
    
    /**
     * Creates a secure SSL context with proper certificate validation.
     * This method uses the default trust store and validates certificates properly.
     * 
     * @return SSLContext configured with default certificate validation
     * @throws RuntimeException if SSL context cannot be created
     */
    public static SSLContext createSecureSSLContext() {
        try {
            // Use only secure TLS version (1.3 preferred, 1.2 as fallback)
            SSLContext sslContext;
            try {
                // Try TLS 1.3 first (most secure)
                sslContext = SSLContext.getInstance("TLSv1.3");
                log.info("Using TLS 1.3 for maximum security");
            } catch (NoSuchAlgorithmException e) {
                // Fallback to TLS 1.2 if 1.3 not available
                sslContext = SSLContext.getInstance("TLSv1.2");
                log.info("Using TLS 1.2 (TLS 1.3 not available)");
            }
            
            // Use default trust manager factory for proper certificate validation
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null); // Use default keystore
            
            // Initialize with proper trust managers (enables certificate validation)
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            
            log.info("Secure SSL context created with proper certificate validation and secure TLS version");
            return sslContext;
            
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            log.error("Failed to create secure SSL context: {}", e.getMessage(), e);
            throw new RuntimeException("SSL context creation failed", e);
        }
    }
    
    /**
     * DEPRECATED: For development/testing only - disables certificate validation.
     * WARNING: This method should NEVER be used in production as it creates security vulnerabilities.
     * Use createSecureSSLContext() instead for production code.
     * 
     * @deprecated Use createSecureSSLContext() for secure certificate validation
     */
    @Deprecated
    public static void trustAllHttpsCertificatesForTestingOnly() {
        log.warn("WARNING: Certificate validation is being disabled! This should ONLY be used for testing!");
        log.warn("For production, use createSecureSSLContext() method instead.");
        
        // Implementation removed for security - use createSecureSSLContext() instead
        throw new UnsupportedOperationException(
            "Certificate validation bypass is disabled for security. Use createSecureSSLContext() instead.");
    }
}
