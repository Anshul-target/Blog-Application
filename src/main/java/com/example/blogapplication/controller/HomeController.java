package com.example.blogapplication.controller;

import com.example.blogapplication.db1.entities.UserEntity;
import com.example.blogapplication.services.JWTService;
import com.example.blogapplication.services.user.UserService;
import com.example.blogapplication.util.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class HomeController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping("/register")
    public ResponseEntity<String> save(@Valid @ModelAttribute UserEntity user){
        boolean isExist=userService.checkUserExist(user.getEmail());
        if (!isExist){

            boolean isPasswordCorrect=userService.checkPassword(user.getPassword(),user.getConfirmpassword());
            boolean isEmailCorrect=userService.checkEmail(user.getEmail());

            if(isPasswordCorrect && isEmailCorrect){

        boolean saveUser=userService.saveUser(user);
        if (saveUser)
            return  ResponseEntity.ok("User saved");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong ");

            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong credentials");
        }

        return ResponseEntity.status(HttpStatus.OK).body("User Already Exist");

    }

@PostMapping("/login")
public ResponseEntity<Map<String,Object>> logUser(@Valid @ModelAttribute User user) {
    Optional<UserEntity> userEntity = userService.findUserByEmail(user.getEmail());
    Map<String, Object> map = new HashMap<>(); // Use Object instead of ?

    if (userEntity.isPresent()) {
        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());

        Authentication authenticate = authenticationManager.authenticate(token);
if(authenticate.isAuthenticated())
         {
            map.put("message", "Login Successful");
             System.out.println(user.getEmail());
            map.put("token",jwtService.generateToken(user.getEmail()));
            map.put("body", userEntity.get());
            return ResponseEntity.ok(map);
        }
        map.put("message", "Login failed!!!");
        map.put("body", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    } else {
        map.put("message", "Email not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }
}



}
