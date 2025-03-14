package com.example.blogapplication.db1.repositories;

import com.example.blogapplication.db1.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {


    Optional<UserEntity> findByEmail(String email);
}


//# **JPA Repository in Spring Boot & How Its Implementation is Created**
//
//        ### **🔹 What is JPA Repository?**
//        - `JpaRepository` is a part of **Spring Data JPA**, which provides **built-in CRUD methods** for database operations.
//        - Instead of writing **boilerplate DAO code**, you just define an **interface** that extends `JpaRepository`, and Spring Boot **automatically generates its implementation**.
//        - It works by leveraging **Spring’s IoC (Inversion of Control) container and Proxy Pattern** to create repository implementations at runtime.
//
//        ---
//
//        ## **🔹 How Spring Boot Creates `JpaRepository` Implementation?**
//        Spring Boot automatically generates the **implementation** of a repository interface when:
//        ✅ The interface extends `JpaRepository<T, ID>` or `CrudRepository<T, ID>`
//        ✅ The **Spring Boot application is running**
//        ✅ **`spring-boot-starter-data-jpa`** dependency is present
//
//        📌 **Example: User Repository**
//        ```java
//        import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface UserRepository extends JpaRepository<User, Long> {
//}
//```
//        ✔ **This interface provides all CRUD operations for the `User` entity without writing any method manually.**
//        ✔ **Spring Boot auto-generates its implementation!**
//
//        ---
//
//        ## **🔹 Step-by-Step Process: How JPA Repository Implementation is Created?**
//        ### **1️⃣ Spring Boot Scans for Repository Interfaces**
//        - When the application starts, Spring Boot **scans the package for repository interfaces** using `@ComponentScan` and `@EnableJpaRepositories` (enabled by default).
//        - If a repository interface **extends `JpaRepository` or `CrudRepository`**, Spring Boot **registers it as a Spring Bean**.
//
//        📌 **Default Behavior** (Implicit Scanning)
//        ```java
//@SpringBootApplication  // Enables component scanning
//public class MySpringBootApp {
//    public static void main(String[] args) {
//        SpringApplication.run(MySpringBootApp.class, args);
//    }
//}
//```
//        📌 **Explicit Scanning (Optional)**
//        ```java
//@EnableJpaRepositories("com.example.repository")  // Scans for JPA Repositories
//```
//        ✔ **Spring Boot finds `UserRepository` and prepares to create its implementation.**
//
//        ---
//
//        ### **2️⃣ Spring Boot Uses `SimpleJpaRepository` as Default Implementation**
//        - `JpaRepository` is just an **interface**. Spring Boot **injects a real implementation** at runtime.
//        - The default implementation is `SimpleJpaRepository`, which provides **CRUD methods**.
//
//        📌 **How it Works Internally:**
//        ```java
//public class SimpleJpaRepository<T, ID> implements JpaRepository<T, ID> {
//    private final EntityManager entityManager;
//
//    // Constructor
//    public SimpleJpaRepository(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    // Auto-implemented methods
//    @Override
//    public Optional<T> findById(ID id) {
//        return Optional.ofNullable(entityManager.find(domainClass, id));
//    }
//
//    @Override
//    public void delete(T entity) {
//        entityManager.remove(entity);
//    }
//
//    @Override
//    public T save(T entity) {
//        if (isNew(entity)) {
//            entityManager.persist(entity);
//            return entity;
//        } else {
//            return entityManager.merge(entity);
//        }
//    }
//}
//```
//        ✔ **Spring Boot injects `EntityManager` and delegates method calls to Hibernate automatically.**
//
//        ---
//
//        ### **3️⃣ Spring Boot Creates a Proxy for the Repository**
//        - **Spring Boot uses `Spring Data JPA Proxy` mechanism** to create a **proxy object** at runtime.
//        - This proxy **implements the repository interface (`UserRepository`)** and **delegates method calls** to `SimpleJpaRepository`.
//
//        📌 **Behind the Scenes – What Happens Internally?**
//        ```java
//public interface UserRepository extends JpaRepository<User, Long> {
//}
//
//// Spring Boot generates a proxy class similar to:
//public class UserRepositoryImpl extends SimpleJpaRepository<User, Long> implements UserRepository {
//    public UserRepositoryImpl(EntityManager em) {
//        super(User.class, em);
//    }
//}
//```
//        ✔ **Now, whenever you call `userRepository.findById(1L)`, it is handled by `SimpleJpaRepository`.**
//
//        ---
//
//        ### **4️⃣ Spring Boot Injects the Repository into Other Beans**
//        - Since `UserRepository` is **now a Spring Bean**, you can **autowire it** anywhere.
//        - Spring Boot **injects the proxy object into services or controllers automatically**.
//
//        📌 **Example: Using Repository in a Service**
//        ```java
//        import org.springframework.beans.factory.annotation.Autowired;
//        import org.springframework.stereotype.Service;
//        import java.util.List;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();  // Calls SimpleJpaRepository.findAll()
//    }
//}
//```
//        ✔ **No need to create `EntityManager` or manually write SQL queries.**
//        ✔ **Spring Boot automatically handles database operations using Hibernate.**
//
//        ---
//
//        ## **🔹 Custom Query Methods in JPA Repository**
//        Spring Boot allows defining **custom finder methods** based on method names.
//
//        📌 **Example: Custom Query Methods**
//        ```java
//        import org.springframework.data.jpa.repository.JpaRepository;
//        import java.util.List;
//
//public interface UserRepository extends JpaRepository<User, Long> {
//    List<User> findByName(String name);
//    List<User> findByEmailContaining(String keyword);
//}
//```
//        ✔ **Spring Boot generates SQL queries automatically based on method names!**
//
//        📌 **Equivalent SQL Queries (Generated by Spring Boot)**
//        ```sql
//        SELECT * FROM user WHERE name = ?;
//        SELECT * FROM user WHERE email LIKE '%?%';
//        ```
//        ✔ **No need to write SQL manually!**
//
//        ---
//
//        ## **🔹 Summary: How JPA Repository is Created & Works**
//        | Step | Process |
//        |------|---------|
//        | **1️⃣ Spring Boot Scans Repositories** | Detects interfaces extending `JpaRepository`. |
//        | **2️⃣ Uses `SimpleJpaRepository` as Default** | Provides implementation of CRUD methods. |
//        | **3️⃣ Creates Proxy for Repository** | Uses `Spring Data JPA Proxy` to handle method calls. |
//        | **4️⃣ Injects into Other Beans** | Allows using `@Autowired` in services/controllers. |
//        | **5️⃣ Handles Custom Queries Automatically** | Generates SQL based on method names. |
//
//        ---
//
//        ## **🔹 Key Takeaways**
//        ✔ **`JpaRepository` removes the need for boilerplate DAO code.**
//        ✔ **Spring Boot scans repositories and generates a proxy implementation at runtime.**
//        ✔ **The default implementation (`SimpleJpaRepository`) handles CRUD operations using Hibernate.**
//        ✔ **Repositories can be injected into services using `@Autowired`.**
//        ✔ **Spring Boot automatically generates SQL queries for finder methods.**
//
//        🚀 **Spring Boot’s JPA Repository makes database operations super easy!**



//# **How Spring Boot Internally Works – Components & Workflow**
//
//        Spring Boot simplifies **Spring Framework** by handling **configuration, dependency management, and server setup**. It follows the **Inversion of Control (IoC)** principle, where Spring manages objects and dependencies instead of manually configuring them.
//
//        ---
//
//        ## **🔹 Core Components of Spring Boot**
//        Spring Boot consists of several core components that work together to build and run applications.
//
//        | Component | Description |
//        |-----------|------------|
//        | **SpringApplication** | Starts and configures the application automatically. |
//        | **Spring Boot AutoConfiguration** | Configures beans automatically based on dependencies. |
//        | **Spring Boot Starter** | Provides pre-configured dependencies for different functionalities. |
//        | **Spring Boot Embedded Server** | Runs the application without needing an external server (Tomcat, Jetty, Undertow). |
//        | **Spring Boot Actuator** | Provides monitoring and management endpoints. |
//        | **Spring Boot CLI** | Allows running Spring Boot applications via the command line. |
//        | **SpringApplicationContext** | Manages beans, dependency injection, and application configurations. |
//
//        ---
//
//        ## **🔹 Spring Boot Internal Workflow (Step-by-Step)**
//        ### **1️⃣ Application Startup (`@SpringBootApplication`)**
//        - The **main class** annotated with `@SpringBootApplication` is executed.
//        - This internally enables:
//        - `@Configuration` → Registers beans.
//        - `@EnableAutoConfiguration` → Enables automatic configuration.
//        - `@ComponentScan` → Scans for beans (`@Component`, `@Service`, `@Repository`).
//
//        **Example:**
//        ```java
//@SpringBootApplication
//public class MySpringBootApp {
//    public static void main(String[] args) {
//        SpringApplication.run(MySpringBootApp.class, args);
//    }
//}
//```
//
//        📌 **Spring Boot initializes the `SpringApplication` class, which starts the application.**
//
//        ---
//
//        ### **2️⃣ Creating `ApplicationContext`**
//        - **Spring Boot creates the `ApplicationContext` (IoC container)**.
//        - It **scans for components** (`@Component`, `@Service`, `@Repository`, `@Controller`).
//        - It **registers beans** and **injects dependencies**.
//
//        📌 `ApplicationContext` manages the **lifecycles of all beans**.
//
//        ---
//
//        ### **3️⃣ Auto-Configuration (`@EnableAutoConfiguration`)**
//        - **Spring Boot scans dependencies** (like Spring MVC, JPA, Security, etc.).
//        - It **configures required beans** automatically based on the classpath.
//
//        📌 Example: If Spring Boot detects **`spring-boot-starter-web`**, it will **auto-configure Tomcat**.
//
//        ---
//
//        ### **4️⃣ Embedded Server Initialization**
//        - If the project is a **web application**, Spring Boot:
//        - **Starts an embedded web server** (Tomcat, Jetty, or Undertow).
//        - Configures the **dispatcher servlet** for handling requests.
//
//        📌 **Spring Boot eliminates the need for external Tomcat installation!**
//
//        ---
//
//        ### **5️⃣ Bean Creation & Dependency Injection (`@Component`, `@Service`, `@Repository`)**
//        - Spring **creates and registers beans** based on component scanning.
//        - Dependencies are **automatically injected** using `@Autowired`.
//
//        📌 **Example: Service Layer**
//        ```java
//@Service
//public class MyService {
//    public String getMessage() {
//        return "Hello, Spring Boot!";
//    }
//}
//```
//
//        📌 **Example: Controller Layer**
//        ```java
//@RestController
//public class MyController {
//
//    @Autowired
//    private MyService myService;
//
//    @GetMapping("/hello")
//    public String hello() {
//        return myService.getMessage();
//    }
//}
//```
//        ✔ **Spring injects `MyService` into `MyController` automatically.**
//
//        ---
//
//        ### **6️⃣ Loading Configuration Properties (`application.properties`)**
//        - Spring Boot loads application properties (`application.properties` or `application.yml`).
//        - This configures **database connections, server ports, and other settings**.
//
//        📌 **Example:**
//        ```
//        server.port=9090
//        spring.datasource.url=jdbc:mysql://localhost:3306/mydb
//        ```
//        ✔ This **changes the server port** and **configures the database**.
//
//        ---
//
//        ### **7️⃣ Handling HTTP Requests (Dispatcher Servlet)**
//        - When a request comes in, Spring Boot:
//        - **Passes the request to `DispatcherServlet`**.
//        - `DispatcherServlet` **routes the request to the correct controller**.
//        - The controller **processes and returns the response**.
//
//        📌 **Example Request:**
//        ```
//        GET http://localhost:9090/hello
//        ```
//        ✔ **Spring Boot handles the request and returns `"Hello, Spring Boot!"`.**
//
//        ---
//
//        ### **8️⃣ Spring Boot Actuator (Optional)**
//        - Actuator provides **built-in monitoring endpoints**.
//        - Example endpoints:
//        - `/actuator/health` → Checks application health.
//        - `/actuator/info` → Provides application metadata.
//
//        📌 **To enable Actuator, add this to `application.properties`:**
//        ```
//        management.endpoints.web.exposure.include=*
//        ```
//
//        ---
//
//        ### **9️⃣ Application Ready to Serve Requests**
//        - At this point, Spring Boot:
//        - **Registered all beans**.
//        - **Injected dependencies**.
//        - **Started the embedded server**.
//        - **Loaded configurations**.
//        - **Is now ready to handle requests**.
//
//        ✔ **Spring Boot is running and serving requests automatically!** 🚀
//
//        ---
//
//        ## **🔹 Summary of Spring Boot Internal Workflow**
//        | Step | Process |
//        |------|---------|
//        | **1️⃣ Startup** | `@SpringBootApplication` triggers Spring Boot initialization. |
//        | **2️⃣ Creating `ApplicationContext`** | Spring scans and registers beans. |
//        | **3️⃣ Auto-Configuration** | Spring configures required components based on dependencies. |
//        | **4️⃣ Embedded Server** | Tomcat/Jetty starts if it's a web application. |
//        | **5️⃣ Dependency Injection** | `@Autowired` injects dependencies automatically. |
//        | **6️⃣ Loading Properties** | Reads `application.properties` for configuration. |
//        | **7️⃣ Request Handling** | `DispatcherServlet` routes requests to controllers. |
//        | **8️⃣ Actuator (Optional)** | Provides monitoring endpoints. |
//        | **9️⃣ Application Ready** | Spring Boot is now serving requests. |
//
//        ---
//
//        ## **🔹 Key Takeaways**
//        ✔ **Spring Boot simplifies application development by handling configurations automatically.**
//        ✔ **It uses `ApplicationContext` to manage beans and dependencies.**
//        ✔ **Auto-Configuration sets up required components based on dependencies.**
//        ✔ **Embedded server eliminates the need for an external Tomcat.**
//        ✔ **Spring Boot follows a clear request-handling workflow with DispatcherServlet.**
//
//        🚀 **Spring Boot makes application development fast and hassle-free!**



//# **Step 3️⃣: Auto-Configuration in Spring Boot – Explained with Example 🚀**
//
//        ### **🔹 What is Auto-Configuration in Spring Boot?**
//        - **Auto-Configuration** automatically configures Spring components **based on dependencies** present in the classpath.
//        - It eliminates the need for **manual configuration** (`@Bean`, XML setup, etc.).
//        - Enabled by **`@EnableAutoConfiguration`**, which is **included in `@SpringBootApplication`**.
//
//        ---
//
//        ## **🔹 How Auto-Configuration Works?**
//        ### **1️⃣ Spring Boot Scans Dependencies**
//        - At startup, Spring Boot **scans the classpath** to check for available dependencies.
//        - Based on the **dependencies**, Spring Boot loads the required configurations.
//
//        📌 **For example:**
//        - If `spring-boot-starter-web` is present → **Spring MVC & Embedded Tomcat are auto-configured**.
//        - If `spring-boot-starter-data-jpa` is present → **Spring JPA & Hibernate are auto-configured**.
//        - If `spring-boot-starter-security` is present → **Spring Security is auto-configured**.
//
//        ---
//
//        ### **2️⃣ Spring Boot Loads Predefined Auto-Configuration Classes**
//        - Spring Boot **loads `spring.factories`** from `spring-boot-autoconfigure.jar`.
//        - This file contains a list of **auto-configuration classes**.
//        - Spring **checks conditions** (`@ConditionalOnClass`, `@ConditionalOnProperty`) to decide whether to enable a configuration.
//
//        📌 **Example from `spring.factories`:**
//        ```
//        org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
//        org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration,\
//        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
//        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
//        ```
//
//        ✔ **If a required class is present, its corresponding auto-configuration is enabled.**
//
//        ---
//
//        ### **3️⃣ Auto-Configuration in Action (Example)**
//        Let’s say we want to create a simple **Spring Boot REST API** using **Spring MVC and JPA**.
//
//        ### **🔹 Example: REST API with Spring Boot Auto-Configuration**
//        #### **📌 Step 1: Add Dependencies**
//        Add these dependencies in `pom.xml`:
//
//        ```xml
//<dependencies>
//<!-- Spring Boot Web for REST API -->
//<dependency>
//<groupId>org.springframework.boot</groupId>
//<artifactId>spring-boot-starter-web</artifactId>
//</dependency>
//
//<!-- Spring Boot JPA for Database Access -->
//<dependency>
//<groupId>org.springframework.boot</groupId>
//<artifactId>spring-boot-starter-data-jpa</artifactId>
//</dependency>
//
//<!-- H2 Database (In-Memory) -->
//<dependency>
//<groupId>com.h2database</groupId>
//<artifactId>h2</artifactId>
//<scope>runtime</scope>
//</dependency>
//</dependencies>
//        ```
//
//        📌 **Spring Boot will now auto-configure:**
//        ✅ **Spring MVC** (because of `spring-boot-starter-web`)
//        ✅ **JPA & Hibernate** (because of `spring-boot-starter-data-jpa`)
//        ✅ **H2 Database** (because of `h2` dependency)
//
//        ---
//
//        #### **📌 Step 2: Define Entity Class**
//        ```java
//        import jakarta.persistence.Entity;
//        import jakarta.persistence.GeneratedValue;
//        import jakarta.persistence.GenerationType;
//        import jakarta.persistence.Id;
//
//@Entity
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String name;
//    private String email;
//
//    // Getters and Setters
//}
//```
//        ✔ Spring Boot **auto-configures Hibernate** to map this entity to a table.
//
//        ---
//
//        #### **📌 Step 3: Define JPA Repository**
//        ```java
//        import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface UserRepository extends JpaRepository<User, Long> {
//}
//```
//        ✔ **Spring Boot auto-configures the database connection and repository!**
//        ✔ No need for **manual `@Bean` definitions** for JPA.
//
//        ---
//
//        #### **📌 Step 4: Create REST Controller**
//        ```java
//        import org.springframework.beans.factory.annotation.Autowired;
//        import org.springframework.web.bind.annotation.*;
//
//        import java.util.List;
//
//@RestController
//@RequestMapping("/users")
//public class UserController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @PostMapping
//    public User createUser(@RequestBody User user) {
//        return userRepository.save(user);
//    }
//
//    @GetMapping
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//}
//```
//        ✔ **Spring Boot auto-configures Spring MVC** to handle REST requests.
//
//        ---
//
//        #### **📌 Step 5: Configure Database in `application.properties`**
//        ```properties
//        spring.datasource.url=jdbc:h2:mem:testdb
//        spring.datasource.driverClassName=org.h2.Driver
//        spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
//        ```
//        ✔ Spring Boot **auto-configures the H2 database**.
//
//        ---
//
//        ### **🔹 Step-by-Step Workflow of Auto-Configuration**
//        1️⃣ **Spring Boot Starts**
//        - `@SpringBootApplication` triggers `SpringApplication.run()`.
//
//        2️⃣ **Auto-Configuration Checks Dependencies**
//        - **Finds `spring-boot-starter-web` → Configures Spring MVC & Tomcat.**
//        - **Finds `spring-boot-starter-data-jpa` → Configures Hibernate & JPA.**
//        - **Finds `h2` database → Configures H2 as the default database.**
//
//        3️⃣ **Beans Are Created Automatically**
//        - **`EntityManagerFactory` (for JPA) is created.**
//        - **A `DataSource` (for H2) is set up automatically.**
//        - **Spring Boot registers `UserRepository` without needing an explicit `@Bean`.**
//
//        4️⃣ **Application is Ready**
//        - REST endpoints (`/users`) are now active.
//        - The H2 database is configured and ready to use.
//
//        ---
//
//        ## **🔹 Disabling Auto-Configuration (Manual Configuration)**
//        If you **don’t want** auto-configuration, you can **disable it** using:
//
//        ```java
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//```
//        ✔ This prevents **Spring Boot from configuring the database automatically.**
//
//        ---
//
//        ## **🔹 Key Takeaways**
//        ✔ **Auto-Configuration eliminates manual configurations** (XML, `@Bean` methods).
//        ✔ **Spring Boot scans dependencies and configures required components automatically.**
//        ✔ **Uses `@ConditionalOnClass` & `@ConditionalOnProperty` to enable/disable configurations.**
//        ✔ **Can be overridden manually with `application.properties` or `@Configuration` classes.**
//
//        🚀 **Spring Boot's Auto-Configuration makes development super fast!**
//
