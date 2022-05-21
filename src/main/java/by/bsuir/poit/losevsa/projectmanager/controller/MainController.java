package by.bsuir.poit.losevsa.projectmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private static final String HOME_PAGE_NAME = "index";

    @GetMapping
    public String showHomePage() {
        return HOME_PAGE_NAME;
    }
}
