package com.hostel.tracker.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Cors cors = new Cors();
    private final Seed seed = new Seed();

    public Jwt getJwt() {
        return jwt;
    }

    public Cors getCors() {
        return cors;
    }

    public Seed getSeed() {
        return seed;
    }

    public static class Jwt {
        private String secret = "dev-only-change-me-please-use-a-long-secret-key-32b";
        private long expirationMs = 86_400_000L;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationMs() {
            return expirationMs;
        }

        public void setExpirationMs(long expirationMs) {
            this.expirationMs = expirationMs;
        }
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:4200";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public static class Seed {
        private String adminUsername = "admin";
        private String adminPassword = "admin123";
        private String adminDisplayName = "Hostel Admin";
        private String viewerUsername = "viewer";
        private String viewerPassword = "viewer123";
        private String viewerDisplayName = "Hostel Viewer";

        public String getAdminUsername() {
            return adminUsername;
        }

        public void setAdminUsername(String adminUsername) {
            this.adminUsername = adminUsername;
        }

        public String getAdminPassword() {
            return adminPassword;
        }

        public void setAdminPassword(String adminPassword) {
            this.adminPassword = adminPassword;
        }

        public String getAdminDisplayName() {
            return adminDisplayName;
        }

        public void setAdminDisplayName(String adminDisplayName) {
            this.adminDisplayName = adminDisplayName;
        }

        public String getViewerUsername() {
            return viewerUsername;
        }

        public void setViewerUsername(String viewerUsername) {
            this.viewerUsername = viewerUsername;
        }

        public String getViewerPassword() {
            return viewerPassword;
        }

        public void setViewerPassword(String viewerPassword) {
            this.viewerPassword = viewerPassword;
        }

        public String getViewerDisplayName() {
            return viewerDisplayName;
        }

        public void setViewerDisplayName(String viewerDisplayName) {
            this.viewerDisplayName = viewerDisplayName;
        }
    }
}
