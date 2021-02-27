package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void shouldSuccessfullyCreateUser() throws Exception {
        when(bCryptPasswordEncoder.encode("password")).thenReturn("At3145R233131413134ewfdwdwewrq!");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("jdoe");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("jdoe", user.getUsername());
        assertEquals("At3145R233131413134ewfdwdwewrq!", user.getPassword());
    }

    @Test
    public void shouldFailToCreateUserWithShortPassword() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("jdoe");
        createUserRequest.setPassword("passw");
        createUserRequest.setConfirmPassword("passw");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    public void shouldSuccessfullyRetrieveUserByUsername() {
        User userToFind = TestUtils.getUser();
        when(userRepository.findByUsername(userToFind.getUsername())).thenReturn(userToFind);
        ResponseEntity<User> responseFind = userController.findByUserName(userToFind.getUsername());
        Assert.assertNotNull(responseFind);
        Assert.assertEquals(HttpStatus.OK, responseFind.getStatusCode());
        User userCreated = responseFind.getBody();
        Assert.assertEquals( userToFind.getUsername(), userCreated.getUsername() );
    }

    @Test
    public void shouldSuccessfullyRetrieveUserById() {
        User userToFind = TestUtils.getUser();
        Optional<User> optionalUser =Optional.of(userToFind);
        when(userRepository.findById(userToFind.getId())).thenReturn(optionalUser);
        ResponseEntity<User> responseFind = userController.findById(userToFind.getId());
        Assert.assertNotNull(responseFind);
        Assert.assertEquals(HttpStatus.OK, responseFind.getStatusCode());
        User userCreated = responseFind.getBody();
        Assert.assertEquals( userToFind.getUsername(), userCreated.getUsername() );
    }
}
