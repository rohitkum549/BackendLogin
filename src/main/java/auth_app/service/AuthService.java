//package auth_app.service;
//
//import auth_app.model.User;
//import auth_app.repository.UserRepository;
//
//import java.util.ArrayList;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import auth_app.util.JwtUtil;
//
//@Service
//public class AuthService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    // Register a new user
//    public User registerUser(User user) {
//        // Check if the user already exists
//        // checkIfUserExists(user.getEmail());
//        Optional<User> alreadyExitsUser = userRepository.findByEmail(user.getEmail());
//        System.out.println("alreadyExitsUser:>> " + alreadyExitsUser);
//        if(alreadyExitsUser.isPresent()){
//            throw new UnsupportedOperationException("User already exists"+user.getEmail()+"\n try with another email");
//        }
//
//        // Hash the password before saving
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
//
//    // Login a user (for now, just return a placeholder token)
//    public String loginUser(String email, String password) {
//        String validate = validateUser(email, password);
//        Optional<User> user = userRepository.findByEmail(email);
//        if (validate.equals("Valid")) {
//            String Token = generateToken(user);
//            return Token;
//        }
//
//        return validate;
//    }
//
//    private String validateUser(String email, String password) {
//        Optional<User> user = userRepository.findByEmail(email);
//        if (!user.isPresent()) {
//            return "No Found Email!";
//        }
//        System.out.println("User>>>>" + user);
//        String pass = userRepository.findByEmail(email).get().getPassword();
//        System.out.println("pASS>>>>" + pass);
//        if (passwordEncoder.matches(password, pass)) {
//            return "Valid";
//        } else {
//            return "Invalid";
//        }
//        // throw new UnsupportedOperationException("Unimplemented method
//        // 'validateUser'");
//    }
//
//    private String generateToken(Optional<User> user) {
//        if (user.isPresent()) {
//            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                    user.get().getEmail(), user.get().getPassword(), new ArrayList<>());
//            System.out.println("userDetails>>>>>>>>>>>>>>" + userDetails);
//            return jwtUtil.generateToken(userDetails);
//        } else {
//            throw new RuntimeException("User not found");
//        }
//    }
//}


package auth_app.service;

import auth_app.exception.UserRegistrationException;
import auth_app.model.User;
import auth_app.repository.UserRepository;
import auth_app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User registerUser(User user) {
        // Input validation
        if (user == null) {
            throw new UserRegistrationException("User data cannot be null", "ERR_400_001");
        }

        // Check if user exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new UserRegistrationException(
                    "User already exists with email: " + user.getEmail(),
                    "ERR_409_001"
            );
        }

        try {
            // Hash password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserRegistrationException(
                    "Failed to register user: " + e.getMessage(),
                    "ERR_500_001"
            );
        }
    }

    public String loginUser(String email, String password) {
        // Input validation
        if (email == null || email.trim().isEmpty()) {
            throw new UserRegistrationException("Email is required", "ERR_400_002");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new UserRegistrationException("Password is required", "ERR_400_003");
        }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserRegistrationException(
                    "No user found with email: " + email,
                    "ERR_404_001"
            );
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new UserRegistrationException(
                    "Invalid credentials",
                    "ERR_401_001"
            );
        }

        try {
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(),
                    user.get().getPassword(),
                    user.get().getAuthorities()
            );
            return jwtUtil.generateToken(userDetails);
        } catch (Exception e) {
            throw new UserRegistrationException(
                    "Failed to generate token: " + e.getMessage(),
                    "ERR_500_002"
            );
        }
    }
}