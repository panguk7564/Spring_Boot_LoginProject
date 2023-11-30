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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UServices {


    private final AuthenticationManager authenticationManager;
    private final UReposit reposit;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Uuser uuser;

    private final String restAPI = "dbbeb9fe5a383feb97c82242d7a3f157";
    private final String redirectURL = "http://localhost:8304/kakaologin";


    private final HttpClient client = HttpClientBuilder.create().build();
    private HttpPost post = null;

    @Transactional
    public ResponseEntity join(UserDto.Dto dto){
        valideuser(dto.getEmail());

        String enpass = bCryptPasswordEncoder.encode(dto.getPass());
        dto.setPass(enpass);
        reposit.save(dto.toEntity());

        System.out.println("됬다");
        return ResponseEntity.ok().body(dto);
    }

    private void valideuser(String email){
        Optional<Uuser> uuser1 = reposit.findByemail(email);
        if(uuser1.isPresent()){
            throw new RuntimeException("이미 있잖아");
        }
    }

    @Transactional
    public Uuser login(UserDto.Dto dto){
        Optional<Uuser> optionalUser = reposit.findByemail(dto.getEmail());

        if(optionalUser.isPresent()){
            Uuser uuser = optionalUser.get();
            if(bCryptPasswordEncoder.matches(dto.getPass(), uuser.getPass())){
                System.out.println("토큰 발급중");
                String token = Tokengiver.createjwt(uuser);
                System.out.println("토큰이 발급완료");

                uuser.setJwt(token);
                reposit.save(uuser);

                return optionalUser.get();
            }
            else {
                throw new RuntimeException("비번이 틀렸쥬");
            }
        }
        else {
            System.out.println("누구세요?");
            return null;
        }
    }

    public String kakaoConnect(){

        StringBuffer url = new StringBuffer();
        url.append("https://kauth.kakao.com/oauth/authorize?");
        url.append("client_id=").append(restAPI);
        url.append("&redirect_uri=").append(redirectURL);
        url.append("&response_type=" + "code");

        System.out.println("까까오에 연결중");

        return url.toString();
    }

    public void Kakaologin(String code, HttpSession session){

        try {
            System.out.println("\n카카오 코드:" + code);

            JsonNode token = getToken(code);
            JsonNode access_token = token.get("access_token");
            JsonNode re_token = token.get("refresh_token");
            session.setAttribute("access_token",access_token.asText());


            System.out.println("니 토큰: "+ access_token.asText()+"\n니 리프레시 토큰: "+ re_token.asText());


            JsonNode userinfo = getInfo(access_token);
            JsonNode properties = userinfo.path("properties");
            JsonNode kakaoinfo = userinfo.path("kakao_account");
            String enpass = bCryptPasswordEncoder.encode(userinfo.get("id").asText());

            System.out.println("-------------도용당한 니 정보----------------");

            System.out.println("PK: " + userinfo.get("id").asText());
            System.out.println("이름 : " + properties.path("nickname").asText());
            System.out.println("니얼굴 : " + properties.path("profile_image").asText());
            System.out.println("니이메일 : " + kakaoinfo.path("email").asText());

            if(reposit.findByemail(kakaoinfo.get("email").asText()).isPresent()){
                System.out.println("까까오로그인 완료");
            }
            else {
                uuser.setEmail(kakaoinfo.path("email").asText());
                uuser.setName(properties.path("nickname").asText());
                uuser.setPass(enpass);
                uuser.setPhoto(properties.path("profile_image").asText());
                uuser.setJwt(access_token.asText());
                uuser.setRe_token(re_token.asText());
                reposit.save(uuser);
                System.out.println("까까오 회원가입 완료");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public JsonNode getToken(String code){
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
            System.out.println("토큰이 요기잉네");

            return jsonResponse(response);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public JsonNode getInfo(JsonNode accessToken){
        final String requestUrl = "https://kapi.kakao.com/v2/user/me";

        try{
            post = new HttpPost(requestUrl);
            post.addHeader("Authorization", "Bearer "+ accessToken);

            final HttpResponse response = client.execute(post);

            System.out.println("니정보 빼옴");

            return jsonResponse(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public JsonNode jsonResponse(HttpResponse response){
        try{
            JsonNode returnNode = null;
            ObjectMapper mapper = new ObjectMapper();
            returnNode = mapper.readTree(response.getEntity().getContent());

            return  returnNode;
        } catch (Exception e){
            e.printStackTrace();
        } return null;
    }

    public List<Uuser> findall() {
        List<Uuser> memlist = reposit.findAll();
        List<Uuser> memedto = new ArrayList<>();
        for(Uuser uuser1: memlist){
            memedto.add(UserDto.listofUser(uuser1));
        }
        return memedto;
    }

    public Uuser findByid(int id) {
        Optional<Uuser> byid = reposit.findById(id);
        if(byid.isPresent()){
            return UserDto.listofUser(byid.get());
        }
        else {return null;}
    }

    @Transactional
    public void deleteById(int id) {
        reposit.deleteById(id);
        System.out.println("잘가시게" + id);
    }

    @Transactional
    public void unAuthorize(int id) {
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