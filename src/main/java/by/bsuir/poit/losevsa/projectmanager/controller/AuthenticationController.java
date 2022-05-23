package by.bsuir.poit.losevsa.projectmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    private static final String LOGIN_PAGE_PATH = "auth/login";

    @GetMapping("/login")
    public String showLoginPage() {
        return LOGIN_PAGE_PATH;
    }
}
