package recipes.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import recipes.model.User;

@Repository
public interface RegistrationRepository extends CrudRepository<User, Integer> {
    public User getUserByEmail(String email);
    public User getUserById(int id);
 }
