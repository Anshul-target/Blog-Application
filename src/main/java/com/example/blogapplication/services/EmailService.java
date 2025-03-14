package com.example.blogapplication.services;


import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class EmailService {
    @Autowired
    public JavaMailSender mailSender;
    public boolean sendEmail(String email,String token){
        try {
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            helper.setTo(email);
            helper.setSubject("Request to change the password");

            Path path= Objects.requireNonNull(Paths.get("src/main/resources/templates/index.html"));

            byte[] bytes = Files.readAllBytes(path);
            String content=new String(bytes, StandardCharsets.UTF_8);
            String resetLink = "http://localhost:8080/user/reset-password?token=" + token;
            content = content.replace("${resetToken}", resetLink);
            //            File imageFile = new File("D:\\Spring Projects\\DemoProjects\\RegistrationForm\\src\\main\\resources\\templates\\images\\icon.svg");
//            System.out.println("File exists: " + imageFile.exists()); // Check if file exists

            helper.setText(content,true);
//        helper.addInline("icon",new File("D:\\Spring Projects\\DemoProjects\\RegistrationForm\\src\\main\\resources\\templates\\images\\icon.svg"));
//            helper.addInline("icon", imageFile);

            mailSender.send(message);
            return true;
        }
        catch (Exception exception){
            System.out.println(exception.getMessage());
            return false;
        }
    }

}
