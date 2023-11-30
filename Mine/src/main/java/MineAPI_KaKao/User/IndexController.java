package MineAPI_KaKao.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    @GetMapping("/")
    public String main(){
        return "index";
    }

    @GetMapping("/signup")
    public String showSignUpPage() {
        return "signup"; // templates/signup.html을 반환
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // templates/login.html을 반환
    }


    @GetMapping("/img")
    public String img(){
        return  "img";
    }

    @GetMapping("/main")
    public String logined(){
        return  "main";
    }






}
