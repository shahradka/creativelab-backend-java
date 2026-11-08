package top.creativelab.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import top.creativelab.user.config.NativeRuntimeHints;

@SpringBootApplication
@EnableJpaAuditing
@ImportRuntimeHints(NativeRuntimeHints.class)
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}
