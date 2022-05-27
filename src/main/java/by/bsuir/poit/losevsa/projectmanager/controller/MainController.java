package by.bsuir.poit.losevsa.projectmanager.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private static final String HOME_PAGE_NAME = "index";

    @GetMapping
    public String showHomePage(Authentication authentication) {
        if (authentication == null) {
            return HOME_PAGE_NAME;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "redirect:/project/admin";
            }
            if (authority.getAuthority().equals("ROLE_USER")) {
                return "redirect:/profile";
            }
        }

        return HOME_PAGE_NAME;
    }
}
