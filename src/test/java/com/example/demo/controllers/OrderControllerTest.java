package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void shouldSuccessfullySubmitOrder() {
        List<Item> itemsOnCart = TestUtils.getItems();
        User user = TestUtils.getUser();
        Cart userCart = user.getCart();
        for(Item item: itemsOnCart){
            userCart.addItem( item );
            userCart.getTotal().add( item.getPrice() );
        }
        user.setCart(userCart);
        userCart.setUser(user);
        when( userRepository.findByUsername(  user.getUsername()  )).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserOrder userOrder = response.getBody();
        assertArrayEquals( userCart.getItems().toArray() ,  userOrder.getItems().toArray() );
        assertEquals( userCart.getTotal(), userOrder.getTotal() );
    }

    @Test
    public void shouldFailToCreateOrderDueToUnexistingUser() {
        List<Item> itemsOnCart = TestUtils.getItems();
        User user = TestUtils.getUser();
        when( userRepository.findByUsername(  user.getUsername()  )).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldSuccessfullyRetrieveUserOrdersHistory() {
        List<Item> items = TestUtils.getItems();
        User user = TestUtils.getUser();
        UserOrder orderToRetrieve = new UserOrder();
        orderToRetrieve.setItems(items);
        orderToRetrieve.setTotal( items.stream().map(Item::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add) );
        orderToRetrieve.setUser(user);
        orderToRetrieve.setId(0l);


        when( userRepository.findByUsername(  user.getUsername()  )).thenReturn(user);
        when( orderRepository.findByUser(  user )).thenReturn( Arrays.asList( orderToRetrieve) );

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserOrder> userOrders = response.getBody();
        assertArrayEquals( orderToRetrieve.getItems().toArray(), userOrders.get(0).getItems().toArray()  );
        assertEquals(  orderToRetrieve.getTotal(), userOrders.get(0).getTotal()  );
        assertEquals(  orderToRetrieve.getUser().getUsername(), userOrders.get(0).getUser().getUsername()  );
    }
}
