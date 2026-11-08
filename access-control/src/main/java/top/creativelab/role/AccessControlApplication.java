package top.creativelab.role;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import top.creativelab.role.config.NativeRuntimeHints;

@SpringBootApplication
@EnableJpaAuditing
@ImportRuntimeHints(NativeRuntimeHints.class)
public class AccessControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessControlApplication.class, args);
    }

}
