package com.bluebelt.fulfillment.config;

import com.bluebelt.fulfillment.model.role.Role;
import com.bluebelt.fulfillment.model.role.RoleName;
import com.bluebelt.fulfillment.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
public class InitDB {

    @Autowired
    private RoleRepository roleDAO;

    /**
     * CodeFirst. khởi chạy thêm vào DB
     * @return CommandLineRunner
     */
    @Bean
    CommandLineRunner iniCommandLineRunner() { // Code first
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                Role adminRole = new Role(RoleName.ROLE_ADMIN);
                Role moderatorRole = new Role(RoleName.ROLE_MODERATOR);
                Role userRole = new Role(RoleName.ROLE_USER);

                log.info("Insert data: " + roleDAO.save(adminRole));
                log.info("Insert data: " + roleDAO.save(moderatorRole));
                log.info("Insert data: " + roleDAO.save(userRole));

            }
        };
    }

}
