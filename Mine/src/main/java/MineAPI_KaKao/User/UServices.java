package MineAPI_KaKao.User;


import MineAPI_KaKao.Security.Tokengiver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service // --서비스
@Transactional(readOnly = true) // -- 데이터 무결성을 위해 데이터 조작 방지
@RequiredArgsConstructor
public class UServices {


    private final UReposit reposit;  //-- 리포지토리
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // -- 비밀번호 암/복호화
    private final Uuser uuser; // -- 유저 객체

    private final String restAPI = "dbbeb9fe5a383feb97c82242d7a3f157";  //-- 카카오톡 API
    private final String redirectURL = "http://localhost:8304/kakaologin"; // -- 로그인 맵핑 주소


    private final HttpClient client = HttpClientBuilder.create().build();
    private HttpPost post = null;

    @Transactional
    public ResponseEntity join(UserDto dto){ // -- 회원가입 (Transactional 활성화)
        valideuser(dto.getEmail()); //-- 이미 존재하는 회원인지 확인

        String enpass = bCryptPasswordEncoder.encode(dto.getPass()); // -- 패스워드 암호화
        dto.setPass(enpass);
        reposit.save(dto.toEntity()); //-- 객체화된 유저정보를 저장

        System.out.println("회원가입 완료");
        return ResponseEntity.ok().body(dto);
    }

    private void valideuser(String email){
        Optional<Uuser> uuser1 = reposit.findByemail(email); // -- 이메일로 회원 중복여부 판단
        if(uuser1.isPresent()){
            throw new RuntimeException("이미 있는 계정입니다.");
        }
    }

    @Transactional
    public Uuser login(UserDto dto){ // -- 로그인
        Optional<Uuser> optionalUser = reposit.findByemail(dto.getEmail()); // ---객체화된 정보가 DB의 정보와 일치하는지 파악

        if(optionalUser.isPresent()){// DB에 저장된 정보와 일치한다면
            Uuser uuser = optionalUser.get(); 
            if(bCryptPasswordEncoder.matches(dto.getPass(), uuser.getPass())){
                System.out.println("토큰 발급중");
                String token = Tokengiver.createjwt(uuser); // -- 토큰 발급
                System.out.println("토큰 발급완료: "+token);

                uuser.setJwt(token);
                reposit.save(uuser); // -- 발급받은 토큰을 DB에 저장

                return optionalUser.get();
            }
            else {
                throw new RuntimeException("패스워드 오류");
            }
        }
        else {
            System.out.println("누구세요?");
            return null;
        }
    }

    public String kakaoConnect(){ // -- 카카오 로그인 화면 URL 조합

        StringBuffer url = new StringBuffer();
        url.append("https://kauth.kakao.com/oauth/authorize?");
        url.append("client_id=").append(restAPI);
        url.append("&redirect_uri=").append(redirectURL);
        url.append("&response_type=" + "code");

        System.out.println("까까오에 연결중");

        return url.toString();
    }

    public void Kakaologin(String code, HttpSession session){ // -- 카카오 API호출 및 정보추출

        try {
            System.out.println("\n카카오 코드:" + code);

            JsonNode token = getToken(code);
            JsonNode access_token = token.get("access_token");
            JsonNode re_token = token.get("refresh_token");
            session.setAttribute("access_token",access_token.asText());

            JsonNode userinfo = getInfo(access_token);
            JsonNode properties = userinfo.path("properties");
            JsonNode kakaoinfo = userinfo.path("kakao_account");
            String enpass = bCryptPasswordEncoder.encode(userinfo.get("id").asText());

            System.out.println("-------------정보----------------");

            System.out.println("PK: " + userinfo.get("id").asText());
            System.out.println("이름 : " + properties.path("nickname").asText());
            System.out.println("프로필 : " + properties.path("profile_image").asText());
            System.out.println("이메일 : " + kakaoinfo.path("email").asText());

            if(reposit.findByemail(kakaoinfo.get("email").asText()).isPresent()){
                System.out.println("까까오로그인 완료"); // -- 이미 있는 정보라면 로그인만 실행
            }
            else {
                uuser.setEmail(kakaoinfo.path("email").asText());
                uuser.setName(properties.path("nickname").asText());
                uuser.setPass(enpass);
                uuser.setPhoto(properties.path("profile_image").asText());
                uuser.setJwt(access_token.asText());
                uuser.setRe_token(re_token.asText());
                reposit.save(uuser);
                System.out.println("까까오 회원가입 완료"); // -- 새로운 정보라면 DB에 저장
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public JsonNode getToken(String code){ // -- 카카오 토큰 발급
        final String RequestURL = "https://kauth.kakao.com/oauth/token";
        final List<NameValuePair> postParams = new ArrayList<>();

        postParams.add(new BasicNameValuePair("grant_type","authorization_code"));
        postParams.add(new BasicNameValuePair("client_id",restAPI));
        postParams.add(new BasicNameValuePair("redirect_uri",redirectURL));
        postParams.add(new BasicNameValuePair("code",code));

        try {
            post = new HttpPost(RequestURL);
            post.setEntity(new UrlEncodedFormEntity(postParams));

            final HttpResponse response = client.execute(post);

            System.out.println("RequestURL: " + RequestURL);
            System.out.println(postParams);
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println("카톡 토큰 발급중");

            return jsonResponse(response);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public JsonNode getInfo(JsonNode accessToken){ // -- 요청시 헤더에 토큰정보 첨부
        final String requestUrl = "https://kapi.kakao.com/v2/user/me";

        try{
            post = new HttpPost(requestUrl);
            post.addHeader("Authorization", "Bearer "+ accessToken);

            final HttpResponse response = client.execute(post);

            return jsonResponse(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public JsonNode jsonResponse(HttpResponse response){ // -- Http응답을 객체화
        try{
            JsonNode returnNode = null;
            ObjectMapper mapper = new ObjectMapper();
            returnNode = mapper.readTree(response.getEntity().getContent());

            return  returnNode;
        } catch (Exception e){
            e.printStackTrace();
        } return null;
    }

    public List<Uuser> findall() { //-- 유저 천체 조회
        List<Uuser> memlist = reposit.findAll();
        List<Uuser> memedto = new ArrayList<>();
        for(Uuser uuser1: memlist){
            memedto.add(UserDto.listofUser(uuser1));
        }
        return memedto;
    }

    public Uuser findByid(int id) { // -- Repository 기능 전가

        Optional<Uuser> byid = reposit.findById(id);
        if(byid.isPresent()){
            return UserDto.listofUser(byid.get());
        }
        else {return null;}
    }

    @Transactional
    public void deleteById(int id) { // -- 데이터 삭제
        reposit.deleteById(id);
        System.out.println("삭제된 회원번호: " + id);
    }

    @Transactional
    public void unAuthorize(int id) { // -- 로그아웃
        Optional<Uuser> userOptional = reposit.findById(id);
        if (userOptional.isPresent()) {
            Uuser jwtinit_user = userOptional.get();
            System.out.println("잘가시오: " + jwtinit_user.getName());
            jwtinit_user.setRe_token("");
            jwtinit_user.setJwt("");
            reposit.save(jwtinit_user);
        }
    }

}