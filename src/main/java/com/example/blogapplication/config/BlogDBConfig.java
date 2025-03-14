package com.example.blogapplication.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "blogEntityManagerFactory",
        transactionManagerRef = "blogTransactionManager",
        basePackages = {
                "com.example.blogapplication.db2.repository"  // ✅ Fixed the package format
        }
)
public class BlogDBConfig {

    @Bean(name = "blogDataSource")
    @ConfigurationProperties(prefix = "spring.blog.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "blogEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean blogEntityManagerFactory(@Qualifier("blogDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.blogapplication.db2.entities"); // ✅ Fixed the package format
        em.setPersistenceUnitName("Blog");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");


        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "blogTransactionManager")
    public PlatformTransactionManager blogTransactionManager(
            @Qualifier("blogEntityManagerFactory") EntityManagerFactory blogEntityManagerFactory) {  // ✅ Fixed variable name
        return new JpaTransactionManager(blogEntityManagerFactory);
    }
}
