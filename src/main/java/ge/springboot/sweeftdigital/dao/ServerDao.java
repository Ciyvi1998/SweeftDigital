package ge.springboot.sweeftdigital.dao;

import ge.springboot.sweeftdigital.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerDao extends JpaRepository<Server,Integer> {

    @Query("select s from Server s where s.name = :name")
    Server findServerByName(@Param("name") String name);

    @Query("select s from Server s order by s.id desc ")
    List<Server> findAllServers();
}
