package auth_app.service;

import auth_app.model.User;
import auth_app.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import auth_app.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new user
    public User registerUser(User user) {
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Login a user (for now, just return a placeholder token)
    public String loginUser(String email, String password) {
        String validate = validateUser(email, password);
        Optional<User> user = userRepository.findByEmail(email);
        if (validate.equals("Valid")) {
            String Token = generateToken(user);
            return Token;
        }

        return validate;
    }

    private String validateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            return "No Found Email!";
        }
        System.out.println("User>>>>" + user);
        String pass = userRepository.findByEmail(email).get().getPassword();
        System.out.println("pASS>>>>" + pass);
        if (passwordEncoder.matches(password, pass)) {
            return "Valid";
        } else {
            return "Invalid";
        }
        // throw new UnsupportedOperationException("Unimplemented method
        // 'validateUser'");
    }

    private String generateToken(Optional<User> user) {
        if (user.isPresent()) {
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(), user.get().getPassword(), new ArrayList<>());
            System.out.println("userDetails>>>>>>>>>>>>>>" + userDetails);
            return jwtUtil.generateToken(userDetails);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}