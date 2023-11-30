package MineAPI_KaKao.User;


import MineAPI_KaKao.Util.StringArrayConverter;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_tb")
public class Uuser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String name;

    @Column(nullable = false)
    private String pass;

    @Column
    private String photo;

    @Column(length = 30)
    @Convert(converter = StringArrayConverter.class)
    private List<String> roles = new ArrayList<>();

    @Column
    private String jwt;

    @Column
    private String re_token;


    @Builder
    public Uuser(String jwt,String re_token,int id, String email, String pass, String name, String photo, List<String> roles) {
        this.id = id;
        this.email = email;
        this.pass = pass;
        this.name = name;
        this.photo= photo;
        this.roles = roles;
        this.jwt = jwt;
        this.re_token = re_token;
    }

    public static UserDto showuser(Uuser uuser) {
        UserDto userDto = new UserDto();
        userDto.setId(uuser.getId());
        userDto.setEmail(uuser.getEmail());
        userDto.setPass(uuser.getPass());
        userDto.setName(uuser.getName());
        userDto.setPhoto(uuser.getPhoto());

        return userDto;

    }
}
