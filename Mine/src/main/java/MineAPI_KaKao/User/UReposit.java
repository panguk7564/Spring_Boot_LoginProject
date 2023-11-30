package MineAPI_KaKao.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UReposit extends JpaRepository<Uuser, Integer> {
    Optional<Uuser> findByemail(String email);
    Optional<Uuser> findById(int id);
}
