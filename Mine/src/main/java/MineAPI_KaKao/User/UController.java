package MineAPI_KaKao.User;

import MineAPI_KaKao.Security.Tokengiver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class UController {

    private final UServices UServices;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto.Dto dto){

    UServices.join(dto);
        String d = dto.getJwt();


    return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto.Dto dto){
        Uuser loginresult = UServices.login(dto);
        System.out.println("로그인시도: "+loginresult.getName());


        if(loginresult != null){
            System.out.println("어서오시게");
            return ResponseEntity.ok().header(Tokengiver.HEADER,loginresult.getJwt()).body("로그인 한 유저: "+loginresult.getName());
        }
        else {
            System.out.println("틀렸음");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value="/kakao/oauth")
    public String kakaoConnect() {
        return "redirect:" + UServices.kakaoConnect();
    }

    @GetMapping(value = "/kakaologin",produces ="application/json")
    public String kakaoLogin(@RequestParam("code")String code, Error error, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
        UServices.Kakaologin(code,session);
        return "main";
    }


    @GetMapping("/mem")
    public String findmem(Model model){
        List<Uuser> memlist = UServices.findall();
        model.addAttribute("userlist", memlist);
        return "mem";
    }
    @GetMapping("/mem/{id}")
    public String findbyId(@PathVariable int id, Model model){
        Uuser userDto = UServices.findByid(id);
        model.addAttribute("users",userDto);
        System.out.println(id);
        return "details";
    }

    @GetMapping("/mem/delete/{id}")
    public String deleteById(@PathVariable int id){
        UServices.deleteById(id);
        return "redirect:/mem/";
    }

    @GetMapping("/signout")
    public String logout(HttpSession session){
        UServices.unAuthorize(37);
        session.invalidate();
        return "redirect:/";
    }



}