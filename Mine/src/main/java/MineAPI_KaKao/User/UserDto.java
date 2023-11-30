package MineAPI_KaKao.User;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;

@Data
public class UserDto {

    private int id;
    private String name;
    private String pass;
    private String email;
    private String photo;

    @Data
    public static class Dto {

        private int id;

        @NotEmpty(message = "이름없음?")
        private String name;

        @NotEmpty(message = "패스워드적어야됨")
        private String pass;

        @NotEmpty(message = "이메일 어디갔노?")
        private String email;

        private String photo;

        private String roles;

        private String jwt;
        private String re_token;

        public Uuser toEntity() {
            return Uuser.builder()
                    .email(email)
                    .pass(pass)
                    .name(name)
                    .photo(photo)
                    .roles(Collections.singletonList("ROLE_USER"))
                    .jwt(jwt)
                    .re_token(re_token)
                    .build();
        }
    }

    public static Uuser listofUser(Uuser uuser) {
        Uuser user = new Uuser();
        user.setId(uuser.getId());
        user.setEmail(uuser.getEmail());
        user.setPass(uuser.getPass());
        user.setName(uuser.getName());
        user.setPhoto(uuser.getPhoto());

        return user;
    }
}