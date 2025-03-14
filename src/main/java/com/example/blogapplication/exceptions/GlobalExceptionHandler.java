package com.example.blogapplication.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }
}

//
//Sure! Let me break down **everything** in the `GlobalExceptionHandler` class line by line. 🚀
//
//        ---
//
//        ## **📌 What is `GlobalExceptionHandler`?**
//        - This class is used to **handle validation errors** and other exceptions globally across your Spring Boot application.
//        - Instead of handling errors in each controller, you **centralize** error handling here.
//
//        ---
//
//        ## **📜 Full Code with Explanation**
//        ```java
//        import org.springframework.http.ResponseEntity;
//        import org.springframework.http.HttpStatus;
//        import org.springframework.validation.FieldError;
//        import org.springframework.web.bind.MethodArgumentNotValidException;
//        import org.springframework.web.bind.annotation.ExceptionHandler;
//        import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//        import java.util.HashMap;
//        import java.util.Map;
//        ```
//        ### **🔹 What’s Happening Here?**
//        - `import org.springframework.http.ResponseEntity;` → Used to **return HTTP responses**.
//        - `import org.springframework.http.HttpStatus;` → Used to **define HTTP status codes** like `400 BAD REQUEST`.
//        - `import org.springframework.validation.FieldError;` → Used to **extract validation errors** for specific fields.
//        - `import org.springframework.web.bind.MethodArgumentNotValidException;` → This is the **exception thrown when validation fails**.
//        - `import org.springframework.web.bind.annotation.ExceptionHandler;` → This is used to **handle exceptions** in a method.
//        - `import org.springframework.web.bind.annotation.RestControllerAdvice;` → This makes the class a **global exception handler** for all controllers.
//
//        ---
//
//        ### **📌 Step-by-Step Explanation of the Class**
//        ```java
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//```
//        - **`@RestControllerAdvice`** → This tells Spring Boot that this class will handle **exceptions globally** for all REST controllers.
//- The class `GlobalExceptionHandler` is created to manage validation-related exceptions.
//
//            ---
//
//            ### **🛑 Handling Validation Errors**
//            ```java
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//```
//        - **`@ExceptionHandler(MethodArgumentNotValidException.class)`**
//        - This means this method will handle all `MethodArgumentNotValidException` errors.
//                - This exception is thrown when `@Valid` **fails** in a controller method.
//
//        - **`public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex)`**
//        - This method **returns a `ResponseEntity` containing a `Map<String, String>`** (field name → error message).
//        - Example response:
//    ```json
//        {
//            "title": "Title cannot be empty",
//                "content": "Content must have at least 10 characters"
//        }
//    ```
//
//        ---
//
//### **🔍 Extracting Errors from the Exception**
//```java
//        Map<String, String> errors = new HashMap<>();
//```
//        - This **creates a map** to store **field names and their corresponding error messages**.
//
//        ---
//
//### **🔄 Looping Through Errors**
//```java
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            errors.put(error.getField(), error.getDefaultMessage());
//        }
//```
//        - `ex.getBindingResult().getFieldErrors()` → This **gets all validation errors** from the exception.
//                - `for (FieldError error : ...)` → This **loops through each validation error**.
//        - `error.getField()` → Extracts the **field name** that failed validation.
//                - `error.getDefaultMessage()` → Extracts the **error message** from the annotation (like `@NotBlank`).
//        - `errors.put(error.getField(), error.getDefaultMessage());` → Stores **field name → error message** in the `errors` map.
//
//                ---
//
//### **📤 Returning the Error Response**
//```java
//        return ResponseEntity.badRequest().body(errors);
//```
//        - **`ResponseEntity.badRequest()`** → Returns **HTTP 400 BAD REQUEST**.
//        - **`.body(errors)`** → Returns the `errors` map as JSON.
//
//                ---
//
//## **📌 Full Example Input & Output**
//### **✅ Correct Request**
//**Request Body (Valid JSON)**
//```json
//        {
//            "title": "My First Blog",
//                "content": "This is a great blog post.",
//                "author": "John Doe"
//        }
//```
//**Response (Success)**
//```json
//        {
//            "message": "Blog posted successfully!"
//        }
//```
//
//        ---
//
//### **❌ Invalid Request (Missing Title & Short Content)**
//**Request Body (Invalid JSON)**
//```json
//        {
//            "title": "",
//                "content": "Short",
//                "author": "John Doe"
//        }
//```
//**Response (Validation Errors)**
//```json
//        {
//            "title": "Title cannot be empty",
//                "content": "Content must have at least 10 characters"
//        }
//```
//        - `"title": "Title cannot be empty"` → Because of `@NotBlank`
//        - `"content": "Content must have at least 10 characters"` → Because of `@Size(min = 10)`
//
//        ---
//
//## **💡 Summary**
//| Feature | Explanation |
//|---------|------------|
//| **`@RestControllerAdvice`** | Global exception handler for REST controllers |
//| **`@ExceptionHandler`** | Defines a method to handle specific exceptions |
//| **`MethodArgumentNotValidException`** | Exception thrown when validation fails |
//| **`getBindingResult().getFieldErrors()`** | Extracts validation errors from exception |
//| **`ResponseEntity.badRequest()`** | Returns **400 Bad Request** with errors |
//
//        ---
//
//## **🚀 Final Thoughts**
//        - This **automatically handles all validation errors** across your application.
//                - No need to write custom error handling logic in each controller.
//        - Makes your API responses **structured and user-friendly**.
//
//        Would you like to add **custom messages** or **more validations**? Let me know! 😊🔥