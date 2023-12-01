package MineAPI_KaKao.User;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;

@Data
public class UserDto { // --  데이터 전송 객체

        @NotEmpty // == NOT NULL
        private int id;

        @NotEmpty
        private String name;

        @NotEmpty
        private String pass;

        @NotEmpty
        private String email;

        private String photo;

        @NotEmpty
        private String roles;

        private String jwt;

        private String re_token;

        public Uuser toEntity() { // --- DTO 정보를 객체화
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


    public static Uuser listofUser(Uuser uuser) { // -- 회원 목록 출력
        Uuser user = new Uuser();
        user.setId(uuser.getId());
        user.setEmail(uuser.getEmail());
        user.setPass(uuser.getPass());
        user.setName(uuser.getName());
        user.setPhoto(uuser.getPhoto());

        return user;
    }
}