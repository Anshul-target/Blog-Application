package com.example.blogapplication.services.user;

import com.example.blogapplication.db1.entities.UserEntity;
import com.example.blogapplication.db1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Transactional
    public boolean saveUser(UserEntity user) {

        if (Objects.nonNull(user)) {
            if (user.getResetToken()!=null){
                UserEntity save = Objects.requireNonNull(userRepository.save(user));
                return true;
            }

            String password=passwordEncoder.encode(user.getPassword());
            user.setPassword(password);
            try {

                UserEntity save = Objects.requireNonNull(userRepository.save(user));
                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
                return false;
            }
        }
        return false;

    }

    public boolean checkEmail(String email){
        return Pattern.matches(EMAIL_REGEX,email) && email.length()>1;
    }

    public boolean checkPassword(String password,String confirmPassword){
        if(password.length()<8 && !password.equals(confirmPassword) && Pattern.matches(PASSWORD_REGEX,password))
            return false;
        return true;
    }

    public Optional<UserEntity> findUserByEmail(String email){
        Optional<UserEntity> user = userRepository.findByEmail(email);
       return user;
    }

    public boolean checkUserExist(String email){
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if(user.isPresent())
            return true;
        else
            return false;
    }
    public UserEntity findUserById(Integer id){
        Optional<UserEntity> user=userRepository.findById(id);
        if(user.isPresent()){
            return user.get();
        }
        else
            return null;
    }

    public boolean changePassword(Integer id,String newPasswod,String confirmPassword){
        boolean isPasswordCorrect = checkPassword(newPasswod, confirmPassword);
        if (!isPasswordCorrect)
            return false;

        UserEntity user=findUserById(id);
        if (Objects.nonNull(user)){
            user.setPassword(newPasswod);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> userByEmail = findUserByEmail(email);
        if (userByEmail.isPresent()){
            UserEntity user=userByEmail.get();
            return new User(user.getEmail(),user.getPassword(), Collections.emptyList());
        }
        return null;
    }
}
