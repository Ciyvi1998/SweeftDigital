package ge.springboot.sweeftdigital.dao;

import ge.springboot.sweeftdigital.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface RoleDao extends JpaRepository<Role, Integer> {
    Role findRoleByName(String name);
}
