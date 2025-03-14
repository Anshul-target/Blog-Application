package com.example.blogapplication.db1.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private  Integer id;
    @NotBlank(message = "Name can not be empty")
    private String name;
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8,message = "Password must be 8 characters long")
//    @Pattern(
//            regexp ="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//            message ="Password must contain at least one uppercase,one lowercase,one digit and one special charaters"
//    )
    private String password;
    @NotBlank(message = "Please enter the password")
    @Transient
    private String confirmpassword;

    private  String resetToken;
}

