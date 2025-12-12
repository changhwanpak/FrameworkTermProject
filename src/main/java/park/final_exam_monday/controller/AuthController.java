package park.final_exam_monday.controller;

import park.final_exam_monday.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/admin_index")
    public String adminIndex() {
        return "admin_index";
    }

}
