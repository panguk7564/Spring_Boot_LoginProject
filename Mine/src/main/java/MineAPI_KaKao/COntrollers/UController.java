package MineAPI_KaKao.COntrollers;

import MineAPI_KaKao.Security.Tokengiver;
import MineAPI_KaKao.User.UServices;
import MineAPI_KaKao.User.UserDto;
import MineAPI_KaKao.User.Uuser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UController { // -- JSON 데이터를 반환하는 CONTROLLER

    private final MineAPI_KaKao.User.UServices UServices; // -- 서비스 호출

    @PostMapping("/signup") // -- 회원가입
    public ResponseEntity<?> signup(@RequestBody UserDto dto){ // -- 입력된 본문에 포함된 내용을 객체화

    UServices.join(dto);

    return ResponseEntity.ok(dto);
    }

    @PostMapping("/login") // 로그인
    public ResponseEntity<?> login(@RequestBody UserDto dto){
        Uuser loginresult = UServices.login(dto); //-- 사용자의 정보가 일치하는지 판단

        System.out.println("로그인시도: "+loginresult.getName());


        if(loginresult != null){
            System.out.println("어서오시게"); // -- 로그인 성공 응답
            return ResponseEntity.ok().header(Tokengiver.HEADER,loginresult.getJwt()).body("로그인 한 유저: "+loginresult.getName());
        } //-- 응답객체 (토큰, 엔티티))
        else {
            System.out.println("틀렸음"); // -- 로그인 실패
            return ResponseEntity.badRequest().build();
        }
    }
}