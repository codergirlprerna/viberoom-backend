package com.viberoom.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_SERVICE_ACCOUNT_JSON:}")
    private String firebaseJson;

    @Value("${firebase.service-account:classpath:firebase-service-account.json}")
    private String serviceAccountPath;

    @PostConstruct
    public void initFirebase() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount;

            if (firebaseJson != null && !firebaseJson.isEmpty()) {
                // Production: use env var
                serviceAccount = new ByteArrayInputStream(
                        firebaseJson.getBytes(StandardCharsets.UTF_8)
                );
            } else {
                // Local dev: use file
                try {
                    org.springframework.core.io.Resource resource =
                            new org.springframework.core.io.ClassPathResource("firebase-service-account.json");
                    serviceAccount = resource.getInputStream();
                } catch (FileNotFoundException e) {
                    System.out.println("WARNING: No Firebase config found. Auth will not work.");
                    return;
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }
}
