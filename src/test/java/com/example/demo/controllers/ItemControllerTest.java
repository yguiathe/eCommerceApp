package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void shouldSuccessfullyRetrieveItemByName() {
        Item item = TestUtils.getItem();
        List<Item> itemsFound = Arrays.asList(item);
        when( itemRepository.findByName(item.getName()) ).thenReturn(itemsFound);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> itemsWithName = response.getBody();
        assertArrayEquals(  itemsFound.toArray(), itemsWithName.toArray()  );
    }

    @Test
    public void shouldSuccessfullyRetrieveItemById() {
        Item item = TestUtils.getItem();
        Optional<Item> optionalItem = Optional.of(item);
        when( itemRepository.findById(item.getId()) ).thenReturn(optionalItem);
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Item itemsWithId = response.getBody();
        assertEquals(  item.getName() , itemsWithId.getName()  );
        assertEquals(  item.getDescription() , itemsWithId.getDescription()  );
        assertEquals(  item.getPrice() , itemsWithId.getPrice()  );
    }

    @Test
    public void shouldSuccessfullyRetrieveAllItems() {
        List<Item> items = TestUtils.getItems();
        when( itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> itemsOnDb = response.getBody();
        assertArrayEquals(  items.toArray(), itemsOnDb.toArray()  );
    }

}
