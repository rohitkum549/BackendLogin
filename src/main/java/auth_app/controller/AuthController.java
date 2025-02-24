//package auth_app.controller;
//
//import auth_app.model.User;
//import auth_app.service.AuthService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private AuthService authService;
//
//    // User Registration
//    @PostMapping("/register")
//    public ResponseEntity<User> registerUser(@RequestBody User user) {
//        User registeredUser = authService.registerUser(user);
//        return ResponseEntity.ok(registeredUser);
//    }
//
//    // User Login
//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestBody User user) {
//        String token = authService.loginUser(user.getEmail(), user.getPassword());
//        System.out.println(">>>>>>>>>>>>>>"+token);
//        return ResponseEntity.ok(token);
//    }
//}

package auth_app.controller;

import auth_app.dto.ErrorResponse;
import auth_app.exception.UserRegistrationException;
import auth_app.model.User;
import auth_app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        user.setEmail(user.getEmail().toLowerCase());
        System.out.println("UserEmail in Cont>>>>>"+user.getEmail());
        User registeredUser = authService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        System.out.println("before>>>>>"+user.getEmail());
        user.setEmail(user.getEmail().toLowerCase());
        System.out.println("UserEmail login Cont>>>>>"+user.getEmail());
        String token = authService.loginUser(user.getEmail(), user.getPassword());
        return ResponseEntity.ok(token);
    }

    // Exception Handlers
    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleUserRegistrationException(UserRegistrationException ex) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(error, determineHttpStatus(ex.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse("ERR_400_004", message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("ERR_500_003", "An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus determineHttpStatus(String errorCode) {
        if (errorCode.startsWith("ERR_400")) return HttpStatus.BAD_REQUEST;
        if (errorCode.startsWith("ERR_401")) return HttpStatus.UNAUTHORIZED;
        if (errorCode.startsWith("ERR_404")) return HttpStatus.NOT_FOUND;
        if (errorCode.startsWith("ERR_409")) return HttpStatus.CONFLICT;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}