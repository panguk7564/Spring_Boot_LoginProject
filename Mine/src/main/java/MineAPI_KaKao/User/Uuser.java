package MineAPI_KaKao.User;


import MineAPI_KaKao.Util.StringArrayConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_tb")
public class Uuser { //-- 사용자 테이블

    @Id // -- PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email; // -- 이메일(로그인 ID)

    @Column
    private String name; // -- 이름

    @Column(nullable = false)
    private String pass; // -- Password

    @Column
    private String photo; // -- 프로필 사진

    @Column(length = 30)
    @Convert(converter = StringArrayConverter.class)
    private List<String> roles = new ArrayList<>(); // -- 유저 권한

    @Column
    private String jwt; // --- JWT 저장 컬럼

    @Column
    private String re_token; // -- 리프레시 토큰


    @Builder
    public Uuser(String jwt,String re_token,int id, String email, String pass, String name, String photo, List<String> roles) { //-- 빌더구현
        this.id = id;
        this.email = email;
        this.pass = pass;
        this.name = name;
        this.photo= photo;
        this.roles = roles;
        this.jwt = jwt;
        this.re_token = re_token;
    }

}
