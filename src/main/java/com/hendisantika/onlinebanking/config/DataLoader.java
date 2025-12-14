package com.hendisantika.onlinebanking.config;

import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.RoleDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.security.Role;
import com.hendisantika.onlinebanking.security.UserRole;
import com.hendisantika.onlinebanking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UserService userService;
    private final UserDao userDao;
    private final RoleDao roleDao;

    public DataLoader(UserService userService, UserDao userDao, RoleDao roleDao) {
        this.userService = userService;
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    public void run(String... args) {
        ensureRolesExist();
        createDefaultUserIfNotExists();
    }

    private void ensureRolesExist() {
        // Check if roles exist, create if not
        long roleCount = roleDao.count();
        log.info("Found {} roles in database", roleCount);

        if (roleCount == 0) {
            log.info("Creating default roles");

            Role userRole = new Role();
            userRole.setRoleId(0);
            userRole.setName("ROLE_USER");
            roleDao.save(userRole);

            Role adminRole = new Role();
            adminRole.setRoleId(1);
            adminRole.setName("ROLE_ADMIN");
            roleDao.save(adminRole);

            log.info("Default roles created");
        }
    }

    private void createDefaultUserIfNotExists() {
        String defaultUsername = "yu71";
        String defaultPassword = "53cret";
        String defaultEmail = "yu71@onlinebanking.com";

        if (userDao.findByUsername(defaultUsername) == null) {
            // Find the ROLE_USER
            Optional<Role> roleOptional = roleDao.findById(0);
            if (roleOptional.isEmpty()) {
                log.warn("ROLE_USER not found, cannot create default user");
                return;
            }

            log.info("Creating default user: {}", defaultUsername);

            User user = new User();
            user.setUsername(defaultUsername);
            user.setPassword(defaultPassword);
            user.setEmail(defaultEmail);
            user.setFirstName("Default");
            user.setLastName("User");
            user.setPhone("555-0000");
            user.setEnabled(true);

            Set<UserRole> userRoles = new HashSet<>();
            userRoles.add(new UserRole(user, roleOptional.get()));

            userService.createUser(user, userRoles);
            log.info("Default user '{}' created successfully", defaultUsername);
        } else {
            log.info("Default user '{}' already exists", defaultUsername);
        }
    }
}
