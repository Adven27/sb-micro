package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import com.brownfield.pss.checkin.CheckInApp;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CheckInApp.class)
@WebAppConfiguration
public class CheckInAppTests {

	@Test
	public void contextLoads() {
	}

}
