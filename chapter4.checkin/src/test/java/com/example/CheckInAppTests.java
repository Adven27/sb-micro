package com.example;

import com.brownfield.pss.checkin.CheckInApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CheckInApp.class)
@WebAppConfiguration
public class CheckInAppTests {

	@Test
	public void contextLoads() {
	}

}
