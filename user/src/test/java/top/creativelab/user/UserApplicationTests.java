package top.creativelab.user;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@Disabled
class UserApplicationTests {

	@Test
	void contextLoads() {
	}

}
