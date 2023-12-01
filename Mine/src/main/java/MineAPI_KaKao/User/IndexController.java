package MineAPI_KaKao.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController { // -- HTML 전환 위주의 Controller

    private final UServices UServices;

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

    @GetMapping("/main") // -- 로그인후 표시되는 화면(메인화면)
    public String logined(){
        return  "main";
    }

    @GetMapping(value="/kakao/oauth") //-- 카카오 로그인으로 이동
    public String kakaoConnect() {
        return "redirect:" + UServices.kakaoConnect();
    }

    @GetMapping(value = "/kakaologin",produces ="application/json")  //-- 로그인 진행
    public String kakaoLogin(@RequestParam("code")String code, Error error, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
        UServices.Kakaologin(code,session);
        return "main";
    }


    @GetMapping("/mem") //-- 유저 출력
    public String findmem(Model model){
        List<Uuser> memlist = UServices.findall();
        model.addAttribute("userlist", memlist);
        return "mem";
    }
    @GetMapping("/mem/{id}") // -- 유저 상세정보
    public String findbyId(@PathVariable int id, Model model){
        Uuser userDto = UServices.findByid(id);
        model.addAttribute("users",userDto);
        System.out.println(id);
        return "details";
    }

    @GetMapping("/mem/delete/{id}") // -- 유저 삭제
    public String deleteById(@PathVariable int id){
        UServices.deleteById(id);
        return "redirect:/mem/";
    }

    @GetMapping("/signout") // -- 로그아웃
    public String logout(HttpSession session){
        UServices.unAuthorize(37);
        session.invalidate();
        return "redirect:/";
    }
}
