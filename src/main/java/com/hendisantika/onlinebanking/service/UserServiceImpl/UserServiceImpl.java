package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.RoleDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.security.UserRole;
import com.hendisantika.onlinebanking.service.AccountService;
import com.hendisantika.onlinebanking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Project : online-banking
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 10/08/18
 * Time: 06.23
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AccountService accountService;

    public void save(User user) {
        userDao.save(user);
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }


    public User createUser(User user, Set<UserRole> userRoles) {
        User localUser = userDao.findByUsername(user.getUsername());

        if (localUser != null) {
            LOG.info("User with username {} already exist. Nothing will be done. ", user.getUsername());
        } else {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            for (UserRole ur : userRoles) {
                roleDao.save(ur.getRole());
            }

            user.getUserRoles().addAll(userRoles);

            user.setPrimaryAccount(accountService.createPrimaryAccount());
            user.setSavingsAccount(accountService.createSavingsAccount());

            localUser = userDao.save(user);
        }

        return localUser;
    }

    public boolean checkUserExists(String username, String email) {
        return checkUsernameExists(username) || checkEmailExists(username);
    }

    public boolean checkUsernameExists(String username) {
        return null != findByUsername(username);

    }

    public boolean checkEmailExists(String email) {
        return null != findByEmail(email);

    }

    public User saveUser(User user) {
        return userDao.save(user);
    }

    public List<User> findUserList() {
        return userDao.findAll();
    }

    public void enableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(true);
        userDao.save(user);
    }

    public void disableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(false);
        LOG.info("user status : {}", user.isEnabled());
        userDao.save(user);
        LOG.info(username, "{}, is disabled.");
    }
}