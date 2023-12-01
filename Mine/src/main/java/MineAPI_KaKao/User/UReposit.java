package MineAPI_KaKao.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UReposit extends JpaRepository<Uuser, Integer> { // -- CRUD 구현
    Optional<Uuser> findByemail(String email); // -- 이메일 중복 확인
    Optional<Uuser> findById(int id); // -- ID 조회
}
