package com.forex.forexpredictor;

import com.forex.forexpredictor.domain.CurrencyPairList;
import com.forex.forexpredictor.domain.Role;
import com.forex.forexpredictor.domain.User;
import com.forex.forexpredictor.repository.RoleRepository;
import com.forex.forexpredictor.repository.UserRepository;
import com.forex.forexpredictor.service.CustomUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ForexpredictorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForexpredictorApplication.class, args);
    }

    @Bean
    CommandLineRunner init(RoleRepository roleRepository, CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        return args -> {
            initRoles(roleRepository);
            initAdmin(userRepository, customUserDetailsService, roleRepository);
            initCurrencyPairs();
        };
    }

    private void initRoles(RoleRepository roleRepository) {
        if(roleRepository.findByRole("ADMIN") == null) {
            Role adminRole = new Role();
            adminRole.setRole("ADMIN");
            roleRepository.save(adminRole);
        }

        if(roleRepository.findByRole("PREMIUM") == null) {
            Role premiumRole = new Role();
            premiumRole.setRole("PREMIUM");
            roleRepository.save(premiumRole);
        }

        if(roleRepository.findByRole("USER") == null) {
            Role userRole = new Role();
            userRole.setRole("USER");
            roleRepository.save(userRole);
        }

    }

    private void initAdmin(UserRepository userRepository, CustomUserDetailsService customUserDetailsService, RoleRepository roleRepository) {
        if(userRepository.findByEmail("admin@forexpredictor.com") == null) {
            User admin = new User();
            admin.setEmail("admin@forexpredictor.com");
            admin.setPassword("administrator");
            admin.setFullname("Admin");

            customUserDetailsService.saveAsAdminUser(admin);
        }
    }

    //TODO add more currency pairs
    private void initCurrencyPairs() {
        CurrencyPairList.addCurrencyPairToDefaultCurrencyPairList("EURGBP");
        CurrencyPairList.addCurrencyPairToDefaultCurrencyPairList("CADJPY");

        CurrencyPairList.setAllCurrencyPairList(CurrencyPairList.getDefaultCurrencyPairList());
    }
}
