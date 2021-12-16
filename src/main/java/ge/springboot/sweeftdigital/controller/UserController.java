package ge.springboot.sweeftdigital.controller;

import ge.springboot.sweeftdigital.entity.Role;
import ge.springboot.sweeftdigital.entity.Server;
import ge.springboot.sweeftdigital.entity.User;
import ge.springboot.sweeftdigital.enums.ServerStatus;
import ge.springboot.sweeftdigital.request.ServerRequest;
import ge.springboot.sweeftdigital.service.ServerServiceImp;
import ge.springboot.sweeftdigital.service.UserServiceImp;
import ge.springboot.sweeftdigital.utils.SweeftDigitalErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserServiceImp userServiceImp;
    private final ServerServiceImp serverServiceImp;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserServiceImp userServiceImp, ServerServiceImp serverServiceImp) {
        this.userServiceImp = userServiceImp;
        this.serverServiceImp = serverServiceImp;
    }

    @RequestMapping(value = "/displayAllUsers", method = RequestMethod.GET)
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok().body(userServiceImp.findAllUsers());
    }

    @Transactional
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserById(@PathVariable(value = "id", required = false) Integer id) {

        try {
            logger.info("finding user with id: " + id);
            User user = userServiceImp.findUserById(id);
            if (user == null) {
                logger.info("User with Id: " + id + " wasn't found");
                return new ResponseEntity<>("User with Id: " + id + " wasn't found", HttpStatus.NOT_FOUND);
            }
            logger.info("deleting user with email: " + user.getEmail());
            userServiceImp.deleteUser(user);
            logger.info("user with email: " + user.getEmail() + " successfully deleted");
            return new ResponseEntity<>("user with email: " + user.getEmail() + " successfully deleted", HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error while deleting user...");
            return new ResponseEntity<>("Error while deleting user...", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/addServer", method = RequestMethod.POST)
    public ResponseEntity<?> addServer(@RequestBody ServerRequest request) {
        try {
            Server existingServer = serverServiceImp.findServerByName(request.getName());
            if (existingServer == null) {
                serverServiceImp.addServer(request);
                return new ResponseEntity<>("Server successfully saved.", HttpStatus.OK);
            } else {
                logger.info("Server with name: " + request.getName() + " already exists!");
                return new ResponseEntity<>("Server with name: " + request.getName() + " already exists!", HttpStatus.CREATED);
            }
        } catch (Exception e) {
            logger.error("Error while saving server " + e);
            return new ResponseEntity<>("Error while saving server ", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/displayFreeServers", method = RequestMethod.GET)
    public ResponseEntity<List<Server>> displayFreeServers() {
        return ResponseEntity.ok().body(serverServiceImp.findFreeServers());
    }

    @RequestMapping(value = "/addServerToUser", method = RequestMethod.POST)
    public ResponseEntity<?> addServerToUser(Principal principal,
                                             @RequestParam String serverName) {
        Server server = serverServiceImp.findServerByName(serverName);
        if (server.getStatus() != ServerStatus.BUSY) {
            userServiceImp.addServerToUser(principal.getName(), serverName);
        } else {
            return new ResponseEntity<>("Server already busy", HttpStatus.BAD_REQUEST);
        }
        if (userServiceImp.findUserByEmail(principal.getName()).getServer() != null) {
            return new ResponseEntity<>("Server with name: " + serverName + " " +
                    "successfully added to user with name: " + principal, HttpStatus.OK);
        } else return new ResponseEntity<>("Error while adding server to user", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @RequestMapping(value = "/removeServer", method = RequestMethod.POST)
    public ResponseEntity<String> removeServer(Principal principal) {
        try {
            User user = userServiceImp.findUserByEmail(principal.getName());
            if (user != null && user.getServer() != null) {
                Server server = serverServiceImp.findServerByName(user.getServer().getName());
                server.setStatus(ServerStatus.FREE);
                serverServiceImp.saveServer(server);
                user.setServer(null);
                userServiceImp.saveUser(user);
                logger.info("Server is successfully removed from user");
                return new ResponseEntity<>("Server is successfully removed from user", HttpStatus.OK);
            } else {
                logger.info("User hasn't related any server");
                return new ResponseEntity<>("User hasn't related any server", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error while removing serer from user");
            return new ResponseEntity<>("Error while removing serer from user", HttpStatus.BAD_REQUEST);
        }
    }

    // აღნიშნული ემთოდით შესაძ₾ებელია დაბლოკვაც და განბლოკვაც
    @RequestMapping(value = "/lockUser", method = RequestMethod.POST)
    public ResponseEntity<?> lockUser(@RequestParam String userEmail) {
        try {
            if (userServiceImp.findUserByEmail(userEmail) != null) {
                userServiceImp.lockUser(userEmail);
                return new ResponseEntity<>("User lock status successfully changed.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User with email: " + userEmail + " didn't find", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error while changing lock status", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/setAdminRole", method = RequestMethod.POST)
    public ResponseEntity<?> setAdminRole(@RequestParam String userEmail) {
        try {
            if (userServiceImp.findUserByEmail(userEmail) != null) {
                userServiceImp.setAdminRole(userEmail);
                logger.info("User admin role successfully added.");
                return new ResponseEntity<>("User admin role successfully added.", HttpStatus.OK);
            } else {
                logger.info("User with email: " + userEmail + "didn't find");
                return new ResponseEntity<>("User with email: " + userEmail + " didn't find", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("User already is an admin");
            return new ResponseEntity<>("User already is an admin", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/removeAdminRole", method = RequestMethod.POST)
    public ResponseEntity<?> removeAdminRole(@RequestParam String userEmail) {
        try {
            User user = userServiceImp.findUserByEmail(userEmail);

            if (user != null) {
                int counter = 0;
                for (Role role : user.getRoles()){
                    if (role.getName().equals("ADMIN")) {
                        counter++;
                    }
                }
                if (counter == 0) {
                    logger.info(SweeftDigitalErrorCode.USER_NOT_ADMIN_ROLE);
                    return new ResponseEntity<>(SweeftDigitalErrorCode.USER_NOT_ADMIN_ROLE, HttpStatus.BAD_REQUEST);
                }
                userServiceImp.removeAdminRole(userEmail);
                logger.info("User admin role successfully removed.");
                return new ResponseEntity<>("User admin role successfully removed.", HttpStatus.OK);
            } else {
                logger.info("User with email: " + userEmail + "didn't find");
                return new ResponseEntity<>("User with email: " + userEmail + " didn't find", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error while removing an admin role", HttpStatus.BAD_REQUEST);
        }
    }
}
