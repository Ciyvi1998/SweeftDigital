package ge.springboot.sweeftdigital.dao;

import ge.springboot.sweeftdigital.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ConfirmationTokenDao extends JpaRepository<ConfirmationToken, String> {

    ConfirmationToken findByConfirmationToken(String confirmationToken);

    @Transactional
    @Modifying
    @Query("delete from ConfirmationToken c where c.user.id = :id")
    void deleteTokenByUserId(@Param("id") Integer id);

}
