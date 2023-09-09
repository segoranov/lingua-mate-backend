package mate.lingua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestLinguaMateBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(LinguaMateBackendApplication::main).with(TestLinguaMateBackendApplication.class).run(args);
	}

}
