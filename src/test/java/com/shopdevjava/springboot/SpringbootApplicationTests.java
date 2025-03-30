package com.shopdevjava.springboot;

import com.shopdevjava.springboot.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class SpringbootApplicationTests {

	@Test
	void contextLoads() {
	}

}
