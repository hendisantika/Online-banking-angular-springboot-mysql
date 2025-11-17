package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.RoleDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.hendisantika.onlinebanking.service.AccountService accountService;

    @InjectMocks
    private com.hendisantika.onlinebanking.service.UserServiceImpl.UserServiceImpl userService;

    @Test
    void createUser_whenNotExists_shouldEncodePasswordSetAccountsAndSave() {
        User input = new User();
        input.setUsername("alice");
        input.setPassword("plain");

        when(userDao.findByUsername("alice")).thenReturn(null);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        PrimaryAccount pa = new PrimaryAccount();
        pa.setAccountNumber(1001);
        SavingsAccount sa = new SavingsAccount();
        sa.setAccountNumber(2001);

        when(accountService.createPrimaryAccount()).thenReturn(pa);
        when(accountService.createSavingsAccount()).thenReturn(sa);

        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser(input, Collections.emptySet());

        assertNotNull(created);
        assertEquals("encoded", created.getPassword());
        assertNotNull(created.getPrimaryAccount());
        assertNotNull(created.getSavingsAccount());
        verify(userDao).save(any(User.class));
        verify(accountService).createPrimaryAccount();
        verify(accountService).createSavingsAccount();
    }

    @Test
    void createUser_whenAlreadyExists_shouldReturnExistingAndNotSaveAgain() {
        User existing = new User();
        existing.setUsername("bob");
        when(userDao.findByUsername("bob")).thenReturn(existing);

        User toCreate = new User();
        toCreate.setUsername("bob");

        User result = userService.createUser(toCreate, Collections.emptySet());

        assertSame(existing, result);
        verify(userDao, never()).save(toCreate);
        verify(accountService, never()).createPrimaryAccount();
    }

    @Test
    void enable_disable_user_shouldUpdateEnabledFlagAndSave() {
        User u = new User();
        u.setUsername("carol");
        u.setEnabled(false);
        when(userDao.findByUsername("carol")).thenReturn(u);

        userService.enableUser("carol");
        assertTrue(u.isEnabled());
        verify(userDao).save(u);

        // now disable
        u.setEnabled(true);
        when(userDao.findByUsername("carol")).thenReturn(u);
        userService.disableUser("carol");
        assertFalse(u.isEnabled());
        verify(userDao, times(2)).save(u);
    }

    @Test
    void checkUsernameAndEmailExistence_simpleChecks() {
        when(userDao.findByUsername("dave")).thenReturn(new User());
        when(userDao.findByEmail("dave@example.com")).thenReturn(null);

        assertTrue(userService.checkUsernameExists("dave"));
        assertFalse(userService.checkEmailExists("dave@example.com"));
    }

}
