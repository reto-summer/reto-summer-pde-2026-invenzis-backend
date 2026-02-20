package com.example.reto_backend_febrero2026;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class RetoBackendFebrero2026Application {

	public static void main(String[] args) {
		SpringApplication.run(RetoBackendFebrero2026Application.class, args);
	}

}
