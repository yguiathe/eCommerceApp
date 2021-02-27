package com.example.demo;

import com.example.demo.controllers.CartControllerTest;
import com.example.demo.controllers.ItemControllerTest;
import com.example.demo.controllers.OrderControllerTest;
import com.example.demo.controllers.UserControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		UserControllerTest.class,
		ItemControllerTest.class,
		CartControllerTest.class,
		OrderControllerTest.class,
		UserEndpointsIntegrationTests.class
})
public class SareetaApplicationTests {

}
