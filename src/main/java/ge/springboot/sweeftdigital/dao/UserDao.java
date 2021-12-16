package ge.springboot.sweeftdigital.dao;

import ge.springboot.sweeftdigital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User,Integer> {

    @Query("select u from User u where u.email = :email")
    User findUserByEmail(@Param("email") String email);

    @Query("select u from User u order by u.id desc ")
    List<User> findAllUsers();

    User findUserById(Integer integer);

    @Query("select u from User u where u.server.id =: id")
    User findUserByServerId(@Param("id") Integer id);
}
