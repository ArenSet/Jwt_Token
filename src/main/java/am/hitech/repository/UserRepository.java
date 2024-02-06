package am.hitech.repository;

import am.hitech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
    User findById (int id);

    @Query(nativeQuery = true,value = "select * from `user` where `number` = ?1 or `email` = ?2")
    User getByNumberOrEmail(String number, String email);

}
