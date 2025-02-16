//package auth_app.repository;
//
//public class UserRepository {
//}


package auth_app.repository;

import auth_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

//public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT u FROM \"user\" u WHERE u.email = :email") // Escape the table name
//    Optional<User> findByEmail(@Param("email") String email);
//}