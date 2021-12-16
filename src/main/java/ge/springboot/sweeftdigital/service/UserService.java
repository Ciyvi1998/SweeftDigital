package ge.springboot.sweeftdigital.service;

import ge.springboot.sweeftdigital.entity.Role;
import ge.springboot.sweeftdigital.entity.Server;
import ge.springboot.sweeftdigital.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    void deleteUser(User user);
    List<User> findAllUsers();
    User findUserByEmail(String email);
    void registerRole(Role role);
    void addRoleToUser(String email, String roleName);
    User findUserById(Integer id);
    void addServerToUser(String userEmail, String serverName);
    void lockUser(String email);
    void setAdminRole(String email) throws Exception;
    void removeAdminRole(String email) throws Exception;
    User findUserByServerId(Integer id);
}
