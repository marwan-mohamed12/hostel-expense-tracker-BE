package com.hostel.tracker.auth;

import com.hostel.tracker.common.AppProperties;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties properties;

    public DataSeeder(AppUserRepository users, PasswordEncoder passwordEncoder, AppProperties properties) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Seed seed = properties.getSeed();
        ensureUser(seed.getAdminUsername(), seed.getAdminPassword(), seed.getAdminDisplayName(), Role.ADMIN);
        ensureUser(seed.getViewerUsername(), seed.getViewerPassword(), seed.getViewerDisplayName(), Role.USER);
    }

    private void ensureUser(String username, String password, String displayName, Role role) {
        if (users.existsByUsernameIgnoreCase(username)) {
            return;
        }
        AppUser user = new AppUser();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setRole(role);
        user.setCreatedAt(Instant.now());
        users.save(user);
        log.info("Seeded {} user '{}'", role, username);
    }
}
