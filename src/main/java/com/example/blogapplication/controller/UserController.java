package com.example.blogapplication.controller;


import com.example.blogapplication.db1.entities.UserEntity;
import com.example.blogapplication.services.EmailService;
import com.example.blogapplication.services.JWTService;
import com.example.blogapplication.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController
{
@Autowired
    UserService userService;
@Autowired
JWTService jwtService;

@Autowired
    EmailService emailService;



    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String,Object>> forgotPassword(@RequestParam  String email){
        boolean isEmailCorrect = userService.checkEmail(email);
        Map<String,Object> map=new HashMap<>();
        if (isEmailCorrect){
            Optional<UserEntity> userByEmail = userService.findUserByEmail(email);
            if (userByEmail.isPresent()){
                String token=jwtService.generateToken( String.valueOf(userByEmail.get().getId()));
                userByEmail.get().setResetToken(token);
                userService.saveUser(userByEmail.get());
                boolean b = emailService.sendEmail(email, token);
                map.put("message","Email is sent to your email");
                return ResponseEntity.ok(map);
            }
            else {
                map.put("message","Email not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
            }

        }
        map.put("message","Please provide a valid email");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @PostMapping ("/reset-password")
    public ResponseEntity<Map<String,Object>> changePassword(@ModelAttribute UserEntity user,@RequestParam String token){
        boolean isPasswordCorrect = userService.checkPassword(user.getPassword(), user.getConfirmpassword());
        Map<String,Object> map=new HashMap<>();
        if (isPasswordCorrect){
            String id =jwtService.extractUserName(token);
            UserEntity userEntity = userService.findUserById(Integer.parseInt(id));
if (!ObjectUtils.isEmpty(userEntity)){
    userEntity.setPassword(user.getPassword());
    userEntity.setConfirmpassword(user.getConfirmpassword());
    userEntity.setResetToken(null);
    boolean b = userService.saveUser(userEntity);
    if (b){
        map.put("message","Password changed!!!");
        return ResponseEntity.ok(map);
    }
    map.put("message","Something went wrong!!!");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);

}
            map.put("message","Please provide the valid token");
            return ResponseEntity.ok(map);
        }
        else{
            map.put("message","Please provide the password with proper format");
            return ResponseEntity.ok(map);
        }
    }


}
