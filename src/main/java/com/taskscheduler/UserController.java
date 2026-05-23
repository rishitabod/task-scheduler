package com.taskscheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // REGISTER
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByUsername(username)) {
            response.put("success", false);
            response.put("message", "Username already taken!");
            return response;
        }
        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email already registered!");
            return response;
        }

        User user = new User(username, email, password);
        userRepository.save(user);
        response.put("success", true);
        response.put("message", "Registration successful!");
        return response;
    }

    // LOGIN
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body,
                                      HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");

        Map<String, Object> response = new HashMap<>();
        var userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found!");
            return response;
        }

        User user = userOpt.get();
        if (user.getPassword().equals(password)) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            response.put("success", true);
            response.put("message", "Login successful!");
            response.put("username", user.getUsername());
        } else {
            response.put("success", false);
            response.put("message", "Wrong password!");
        }
        return response;
    }

    // LOGOUT
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out!");
        return response;
    }

    // CHECK SESSION
    @GetMapping("/session")
    public Map<String, Object> checkSession(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        Map<String, Object> response = new HashMap<>();
        if (userId != null) {
            response.put("loggedIn", true);
            response.put("username", username);
            response.put("userId", userId);
        } else {
            response.put("loggedIn", false);
        }
        return response;
    }
}