package bankapp.models;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserDao extends CrudRepository<User, Long> {

    /**
    * This method will find an User instance in the database by its email.
    */
    public User findByEmail(String email);

}