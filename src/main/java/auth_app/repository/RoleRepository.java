//package auth_app.repository;
//
//public class RoleRepository {
//}

package auth_app.repository;

import auth_app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}