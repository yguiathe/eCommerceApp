package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void shouldSuccessfullyAddItemToCart(){
        Item itemToAdd = TestUtils.getItem();
        User user = TestUtils.getUser();

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(itemToAdd.getId());
        request.setQuantity(2);

        Optional<Item> optionalItem = Optional.of(itemToAdd);
        when( userRepository.findByUsername(  request.getUsername()  )).thenReturn(user);
        when( itemRepository.findById(  request.getItemId()  )).thenReturn(optionalItem);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals( 2 , cart.getItems().size() );
        assertEquals( itemToAdd.getName() , cart.getItems().get(0).getName() );

    }

    @Test
    public void shouldFailToRemoveUnexistingItemFromCart() {
        User user = TestUtils.getUser();
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(2l);
        request.setQuantity(1);
        when( userRepository.findByUsername(  request.getUsername()  )).thenReturn(user);
        when( itemRepository.findById(  request.getItemId()  )).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldSuccessfullyRemoveItemFromCart(){
        List<Item> itemsOnCart = TestUtils.getItems();
        User user = TestUtils.getUser();
        Cart userCart = user.getCart();
        for(Item item: itemsOnCart){
            userCart.addItem( item );
            userCart.getTotal().add( item.getPrice() );
        }

        Item itemToRemove = itemsOnCart.get(0);
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(itemToRemove.getId());
        request.setQuantity(1);

        Cart cartItemRemoved = new Cart();
        cartItemRemoved.setId(userCart.getId());
        cartItemRemoved.setTotal(userCart.getTotal());
        cartItemRemoved.setUser(user);
        cartItemRemoved.setItems(userCart.getItems());
        cartItemRemoved.setTotal(userCart.getTotal());

        cartItemRemoved.removeItem(itemToRemove);

        Optional<Item> optionalItem = Optional.of(itemToRemove);
        when( userRepository.findByUsername(  request.getUsername()  )).thenReturn(user);
        when( itemRepository.findById(  request.getItemId()  )).thenReturn(optionalItem);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals( cartItemRemoved.getItems().size(), cart.getItems().size() );
        assertArrayEquals( cartItemRemoved.getItems().toArray(), cart.getItems().toArray() );
        assertEquals( cartItemRemoved.getTotal(), cart.getTotal() );

    }
}
