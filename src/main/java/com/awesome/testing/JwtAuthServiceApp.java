package com.awesome.testing;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.awesome.testing.model.Role;
import com.awesome.testing.model.User;
import com.awesome.testing.service.UserService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.zalando.logbook.HeaderFilter;

import static org.zalando.logbook.HeaderFilter.none;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class JwtAuthServiceApp implements CommandLineRunner {

    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthServiceApp.class, args);
    }

    @Bean
    public HeaderFilter headerFilter() {
        return none();
    }

    @SuppressWarnings("unused")
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void run(String... params) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setEmail("admin@email.com");
        admin.setFirstName("Slawomir");
        admin.setLastName("Radzyminski");
        admin.setRoles(List.of(Role.ROLE_ADMIN, Role.ROLE_CLIENT));
        userService.signUp(admin);

        User client = new User();
        client.setUsername("client");
        client.setPassword("client");
        client.setEmail("client@email.com");
        client.setFirstName("Gosia");
        client.setLastName("Radzyminska");
        client.setRoles(List.of(Role.ROLE_CLIENT));
        userService.signUp(client);
    }

}
