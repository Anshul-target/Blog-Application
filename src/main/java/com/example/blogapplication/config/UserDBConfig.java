package com.example.blogapplication.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "userEntityManagerFactory",
        transactionManagerRef = "userTransactionManager",
        basePackages = {
                "com.example.blogapplication.db1.repositories" // ✅ Fixed package format
        }
)
public class UserDBConfig {


//    What Does @ConfigurationProperties(prefix = "spring.user.datasource") Do?
//            ✔️ Binds external configuration properties (from application.properties or application.yml) to this method.
//✔️ The prefix spring.user.datasource tells Spring Boot to look for database properties that start with this prefix.
//
//🔹 Example application.properties or application.yml:
//
//    properties
//            Copy
//    Edit
//    spring.user.datasource.url=jdbc:mysql://localhost:3306/userdb
//    spring.user.datasource.username=root
//    spring.user.datasource.password=yourpassword
//    spring.user.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
//    spring.user.datasource.hikari.maximum-pool-size=10
//            🔹 What Happens? ✅ Spring Boot automatically reads these properties.
//            ✅ It injects these values into DataSourceBuilder.create().build().
//            ✅ A DataSource object is created with this database configuration.
//            ✅ This DataSource is used for all database operations related to the User Database.
//

    @Primary
    @Bean(name = "userDataSource")  // ✅ Renamed from "dataSource" to avoid conflicts
    @ConfigurationProperties(prefix = "spring.user.datasource")

    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "userEntityManagerFactory")  // ✅ Renamed to match entityManagerFactoryRef
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(
            @Qualifier("userDataSource") DataSource dataSource) {  // ✅ Updated reference to new name

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.blogapplication.db1.entities"); // ✅ Fixed package format
        em.setPersistenceUnitName("User");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();

        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");


        em.setJpaPropertyMap(properties);

        return em;
    }

//    What is @Qualifier and How Does It Work?
//            🔹 In Spring Boot, if you have multiple beans of the same type, Spring doesn’t know which one to use.
//            🔹 @Qualifier helps Spring identify the correct bean by name.
//
//            🔹 Example Without @Qualifier (Causes Ambiguity)
//
//    java
//            Copy
//    Edit
//    @Bean
//    public DataSource userDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    public DataSource blogDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
//        // ERROR: Spring doesn't know which DataSource to use (UserDB or BlogDB)
//    }
//🔹 Solution Using @Qualifier
//
//    java
//            Copy
//    Edit
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            @Qualifier("userDataSource") DataSource dataSource) {
//        // Now Spring knows to use the "userDataSource" for this entity manager.
//    }
//✔️ @Qualifier("userDataSource") ensures that the correct DataSource is injected.
//✔️ Prevents conflicts in multi-database configurations.
//




    @Primary
    @Bean(name = "userTransactionManager")  // ✅ Renamed to match transactionManagerRef
    public PlatformTransactionManager userTransactionManager(
            @Qualifier("userEntityManagerFactory") EntityManagerFactory userEntityManagerFactory) { // ✅ Updated reference
        return new JpaTransactionManager(userEntityManagerFactory);
    }
}














//Yes! Let me explain what each part of your **UserDBConfig** is doing and how it works in your **Spring Boot multi-database JPA setup**.
//
//        ---
//
//        ## **🔹 What Does This Configuration Do?**
//        This configuration **sets up a separate database connection** for handling **User-related data** in your application.
//
//        🔹 It **defines a separate data source**, an **EntityManagerFactory**, and a **TransactionManager** for the **User Database (DB1)**.
//        🔹 Since this is the **primary** database, we mark it with `@Primary` to tell Spring that it should be used **by default** unless specified otherwise.
//
//        ---
//
//        ## **1️⃣ `@Configuration` & `@EnableTransactionManagement`**
//        ```java
//@Configuration
//@EnableTransactionManagement
//```
//        ✔️ `@Configuration` → Marks this class as a Spring **configuration** class (meaning it provides beans).
//        ✔️ `@EnableTransactionManagement` → Enables **Spring’s transaction management**, allowing you to use `@Transactional` for managing database transactions.
//
//        ---
//
//        ## **2️⃣ `@EnableJpaRepositories` – Configuring JPA Repositories**
//        ```java
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "userEntityManagerFactory",
//        transactionManagerRef = "userTransactionManager",
//        basePackages = {
//                "com.example.blogapplication.db1.repositories"
//        }
//)
//```
//        This **tells Spring Boot** where to find **the JPA repositories** for the User database and which **EntityManagerFactory & TransactionManager** to use.
//
//        ✔️ **`entityManagerFactoryRef = "userEntityManagerFactory"`**
//        → Specifies that this repository should use the **userEntityManagerFactory** (which we define later).
//
//        ✔️ **`transactionManagerRef = "userTransactionManager"`**
//        → Specifies that transactions in this repository should be managed by **userTransactionManager**.
//
//        ✔️ **`basePackages = "com.example.blogapplication.db1.repositories"`**
//        → Specifies where **Spring Data JPA** should scan for **repository interfaces** related to the **User database**.
//
//        ---
//
//        ## **3️⃣ Defining the Data Source (Database Connection)**
//        ```java
//@Primary
//@Bean(name = "userDataSource")
//@ConfigurationProperties(prefix = "spring.user.datasource")
//public DataSource userDataSource() {
//        return DataSourceBuilder.create().build();
//        }
//        ```
//        This **configures the User database connection** using properties from `application.properties` or `application.yml`.
//
//        ✔️ **`@Primary`**
//        → Marks this data source as the **default one**, so Spring uses it **unless another one is explicitly requested**.
//
//        ✔️ **`@Bean(name = "userDataSource")`**
//        → Registers this **bean** in Spring's application context under the name **`userDataSource`**.
//
//        ✔️ **`@ConfigurationProperties(prefix = "spring.user.datasource")`**
//        → Loads **database connection properties** from `application.properties` or `application.yml`, such as:
//        ```properties
//        spring.user.datasource.url=jdbc:mysql://localhost:3306/userdb
//        spring.user.datasource.username=root
//        spring.user.datasource.password=yourpassword
//        spring.user.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
//        ```
//        ---
//
//        ## **4️⃣ Defining the EntityManagerFactory (JPA Entity Manager)**
//        ```java
//@Primary
//@Bean(name = "userEntityManagerFactory")
//public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(
//@Qualifier("userDataSource") DataSource dataSource) {
//
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.example.blogapplication.db1.entities");
//        em.setPersistenceUnitName("User");
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//
//        HashMap<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.hbm2ddl.auto", "update");
//
//        em.setJpaPropertyMap(properties);
//
//        return em;
//        }
//        ```
//        This **creates an `EntityManagerFactory`** for interacting with **User Database Entities**.
//
//        ✔️ **`@Primary`** → Marks this `EntityManagerFactory` as the default one.
//
//        ✔️ **`@Bean(name = "userEntityManagerFactory")`**
//        → Creates a `LocalContainerEntityManagerFactoryBean` named **userEntityManagerFactory**.
//
//        ✔️ **`@Qualifier("userDataSource") DataSource dataSource`**
//        → Tells Spring to use the `userDataSource` bean (the User Database).
//
//        ✔️ **`em.setPackagesToScan("com.example.blogapplication.db1.entities")`**
//        → Tells Hibernate to look for **JPA entity classes** in this package.
//
//        ✔️ **`em.setPersistenceUnitName("User")`**
//        → This name is **used internally by JPA** to differentiate between multiple databases.
//
//        ✔️ **`em.setJpaVendorAdapter(new HibernateJpaVendorAdapter())`**
//        → Configures **Hibernate** as the JPA provider.
//
//        ✔️ **Sets Hibernate Properties:**
//        ```java
//        properties.put("hibernate.hbm2ddl.auto", "update");
//        ```
//        → This ensures that the **database schema updates automatically** when the app starts.
//        *(Other options: `create`, `validate`, `none`)*
//
//        ---
//
//        ## **5️⃣ Defining the Transaction Manager**
//        ```java
//@Primary
//@Bean(name = "userTransactionManager")
//public PlatformTransactionManager userTransactionManager(
//@Qualifier("userEntityManagerFactory") EntityManagerFactory userEntityManagerFactory) {
//        return new JpaTransactionManager(userEntityManagerFactory);
//        }
//        ```
//        ✔️ **This ensures that all User Database operations are managed inside transactions.**
//        ✔️ **`@Qualifier("userEntityManagerFactory")`**
//        → Ensures that this transaction manager uses the **correct database connection**.
//        ✔️ **`new JpaTransactionManager(userEntityManagerFactory)`**
//        → Uses **Spring's Transaction Manager** to **manage transactions for the User database**.
//
//        ---
//
//        ## **🚀 How This Works Together**
//        1️⃣ **Spring Boot reads `application.properties` and sets up the User Database (`userDataSource`).**
//        2️⃣ **Creates an `EntityManagerFactory` for User DB using Hibernate.**
//        3️⃣ **Scans for JPA entities in `com.example.blogapplication.db1.entities`.**
//        4️⃣ **Scans for repositories in `com.example.blogapplication.db1.repositories`.**
//        5️⃣ **Sets up a Transaction Manager (`userTransactionManager`) to handle database transactions.**
//
//        ---
//
//        ## **🔹 How Does This Work with Multiple Databases?**
//        🔹 You now have **two separate database configurations**:
//        ✔️ **`UserDBConfig`** → Handles the **User Database** (`DB1`).
//        ✔️ **`BlogDBConfig`** → Handles the **Blog Database** (`DB2`).
//
//        Both databases **have their own `DataSource`, `EntityManagerFactory`, and `TransactionManager`**, ensuring that data **doesn’t mix between databases**.
//
//        ---
//
//        ## **🎯 Final Summary**
//        | Component                     | Purpose |
//        |--------------------------------|---------|
//        | `@EnableJpaRepositories`       | Scans for User DB repositories |
//        | `userDataSource`               | Configures the User Database connection |
//        | `userEntityManagerFactory`     | Manages JPA entities for the User DB |
//        | `userTransactionManager`       | Handles transactions for User DB |
//
//        ---
//
//        ## **🚀 Conclusion**
//        ✅ This configuration ensures that **Spring Boot can connect to and manage the User Database independently**.
//        ✅ It allows you to **use multiple databases in the same application** without conflicts.
//        ✅ Your **repositories, entities, and transactions** will work correctly with the User Database (`DB1`).
//
//        With these **User & Blog DB Configurations**, your **multi-database setup is complete!** 🎉