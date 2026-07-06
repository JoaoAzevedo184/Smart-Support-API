package io.github.joaovictor.smartsupport;

import io.github.joaovictor.smartsupport.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfig.class)
class SmartSupportApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
