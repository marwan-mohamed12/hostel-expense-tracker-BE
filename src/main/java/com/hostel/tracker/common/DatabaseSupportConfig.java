package com.hostel.tracker.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DatabaseSupportConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSupportConfig.class);
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private static final String SQLITE_DIALECT = "org.hibernate.community.dialect.SQLiteDialect";

    @Bean
    static BeanFactoryPostProcessor sqliteParentDirectory(Environment environment) {
        return beanFactory -> {
            String url = environment.getProperty("spring.datasource.url", "");
            Path file = sqliteFile(url);
            if (file == null) {
                return;
            }
            Path parent = file.getParent();
            if (parent == null) {
                return;
            }
            try {
                Files.createDirectories(parent);
            } catch (IOException ex) {
                throw new IllegalStateException("Could not create SQLite directory " + parent, ex);
            }
        };
    }

    @Bean
    HibernatePropertiesCustomizer dialectFromJdbcUrl(Environment environment) {
        return hibernate -> {
            if (hibernate.containsKey("hibernate.dialect")) {
                return;
            }
            String explicit = environment.getProperty("spring.jpa.database-platform");
            if (explicit != null && !explicit.isBlank()) {
                return;
            }
            String url = environment.getProperty("spring.datasource.url", "");
            if (url.startsWith(SQLITE_PREFIX)) {
                hibernate.put("hibernate.dialect", SQLITE_DIALECT);
                log.info("Using SQLite dialect for {}", url);
            }
        };
    }

    private static Path sqliteFile(String url) {
        if (!url.startsWith(SQLITE_PREFIX)) {
            return null;
        }
        String path = url.substring(SQLITE_PREFIX.length());
        if (path.isBlank() || path.startsWith(":memory:") || path.contains("mode=memory")) {
            return null;
        }
        return Path.of(path);
    }
}
