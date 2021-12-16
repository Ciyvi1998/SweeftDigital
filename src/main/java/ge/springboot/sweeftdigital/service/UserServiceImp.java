package ge.springboot.sweeftdigital.service;

import ge.springboot.sweeftdigital.dao.ConfirmationTokenDao;
import ge.springboot.sweeftdigital.dao.RoleDao;
import ge.springboot.sweeftdigital.dao.ServerDao;
import ge.springboot.sweeftdigital.dao.UserDao;
import ge.springboot.sweeftdigital.entity.ConfirmationToken;
import ge.springboot.sweeftdigital.entity.Role;
import ge.springboot.sweeftdigital.entity.Server;
import ge.springboot.sweeftdigital.entity.User;
import ge.springboot.sweeftdigital.enums.Gender;
import ge.springboot.sweeftdigital.enums.ServerStatus;
import ge.springboot.sweeftdigital.request.RegistrationRequest;
import ge.springboot.sweeftdigital.security.EmailValidator;
import ge.springboot.sweeftdigital.utils.SweeftDigitalErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImp implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenDao confirmationTokenDao;
    private final EmailSenderService emailSenderService;
    private final ServerDao serverDao;
    @PersistenceContext
    private final EntityManager em;

    Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    public UserServiceImp(UserDao userDao, RoleDao roleDao, EmailValidator emailValidator, BCryptPasswordEncoder bCryptPasswordEncoder, ConfirmationTokenDao confirmationTokenDao, EmailSenderService emailSenderService, ServerDao serverDao, EntityManager em) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.emailValidator = emailValidator;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.confirmationTokenDao = confirmationTokenDao;
        this.emailSenderService = emailSenderService;
        this.serverDao = serverDao;
        this.em = em;
    }

    @Override
    public User findUserById(Integer id) {
        return userDao.findUserById(id);
    }

    @Override
    public void saveUser(User user) {
//        გავააქტიუროდ მხოლოდ CommandLineRunner ის გაშვებისას რადგან იწნააღმდეგ შემთხვევაშ მივიღებთ ორმაგ შიფრაციას
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        User attachedUser = findUserByEmail(user.getEmail());
        attachedUser.setRoles(null);
        userDao.save(attachedUser);
        confirmationTokenDao.deleteTokenByUserId(attachedUser.getId());
        userDao.delete(attachedUser);
    }

    @Override
    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    public void registerRole(Role role) {
        roleDao.save(role);
    }

    @Override
    @Transactional
    public void addRoleToUser(String email, String roleName) {
        User user = userDao.findUserByEmail(email);
        Role role = roleDao.findRoleByName(roleName);
        user.getRoles().add(role);
        // რადგან ვიყენებთ @Transactional ანოტაციას save() მეთოდის გამოყენება აღარ არის საჭირო.
        userDao.save(user);
    }

    @Transactional
    public void signUpUser(RegistrationRequest request) {
        try {


            boolean isValidEmail = emailValidator.
                    test(request.getEmail());

            if (!isValidEmail) {
                logger.error(SweeftDigitalErrorCode.EMAIL_NOT_VALID);
                throw new IllegalStateException(SweeftDigitalErrorCode.EMAIL_NOT_VALID);
            }
            User existingUser = userDao.findUserByEmail(request.getEmail());
            if (existingUser != null) {
                throw new Exception(SweeftDigitalErrorCode.EMAIL_ALREADY_TAKEN);
            } else {
                User user = new User();
                user.setName(request.getFirstName());
                user.setSurname(request.getLastName());
                user.setEmail(request.getEmail());
                user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
                user.setAge(request.getAge());
                user.setBirthDate(new Date(request.getStringDate()));
                if (request.getGender().toLowerCase().equals("male")) {
                    user.setGender(Gender.MALE);
                } else {
                    user.setGender(Gender.FEMALE);
                }
                List<Role> roles = new ArrayList<>();
                roles.add(roleDao.findRoleByName("USER"));
                user.setRoles(roles);
                logger.info("saving user with email: " + user.getEmail());
                userDao.save(user);
                logger.info("user successfully registered!");

                user = userDao.findUserByEmail(request.getEmail());
                ConfirmationToken confirmationToken = new ConfirmationToken(user);

                confirmationTokenDao.save(confirmationToken);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setSubject("რეგისტრაცია");
                mailMessage.setFrom("kakauridze.nika@gtu.ge");
                mailMessage.setText("ანგარიშის გასააქტიურებლად გადადით ბმულზე : "
                        + "http://localhost:8080/api/v1/confirm-account?token=" + confirmationToken.getConfirmationToken());

                emailSenderService.sendEmail(mailMessage);
                logger.info("email sent");
            }

        } catch (Exception e) {
            logger.error("Registration failed!", e);
        }

    }

    @Override
    @Transactional
    public void addServerToUser(String userEmail, String serverName) {
        try {
            User user = userDao.findUserByEmail(userEmail);
            Server server = serverDao.findServerByName(serverName);
            if (server != null && server.getStatus() == ServerStatus.FREE) {
                server.setStatus(ServerStatus.BUSY);
                serverDao.save(server);
                user.setServer(server);
                userDao.save(user);
                logger.info("Server with name: " + serverName + "" +
                        "successfully added to user with name: " + userEmail);
            } else {
                logger.info("Server didn't added.");
            }
        } catch (Exception e) {
            logger.error("Error while adding server to user " + e);
        }
    }

    @Override
    public void lockUser(String email) {
        User user = userDao.findUserByEmail(email);
        if (user != null) {
            if (user.isLocked()) {
                user.setLocked(false);
            } else {
                user.setLocked(true);
            }
            userDao.save(user);
            logger.info("User lock status is: " + user.isLocked());
        } else {
            logger.info("User with email: " + email + " didn't find.");
        }
    }

    @Override
    public void setAdminRole(String email) throws Exception {
        User user = userDao.findUserByEmail(email);
        if (user != null && (user.getRoles().size() != 0 && user.getRoles() != null)) {
            int counter = 0;
            List<Role> roles = user.getRoles();
            for (Role role : roles) {
                if (role.getName().equals("ADMIN")) {
                    counter++;
                }
            }
            if (counter != 0) {
                logger.info(SweeftDigitalErrorCode.USER_ALREADY_IS_ADMIN);
                throw new Exception(SweeftDigitalErrorCode.USER_ALREADY_IS_ADMIN);
            } else {
                roles.add(roleDao.findRoleByName("ADMIN"));
                user.setRoles(roles);
                userDao.save(user);
            }
        }
    }

    @Override
    public void removeAdminRole(String email) throws Exception {
        User user = userDao.findUserByEmail(email);
        if (user != null && (user.getRoles().size() != 0 && user.getRoles() != null)) {
            int counter = 0;
            List<Role> roles = user.getRoles();
            for (Role role : roles) {
                if (role.getName().equals("ADMIN")) {
                   roles.remove(counter);
                   break;
                }
                counter++;
            }
            user.setRoles(roles);
            userDao.save(user);
        }
    }

    @Override
    public User findUserByServerId(Integer id) {
        return findUserByServerId(id);
    }

    // Another way of same methods
    //      ---> OPTIONAL <---

    public void saveNewUser(User user) {
        em.merge(user);
    }

    public void removeUser(User user) {
        em.remove(em.find(User.class, user.getId()));
    }

    public List<User> getAllUsers() {
        return em.createQuery("select u from User u order by u.id desc ")
                .getResultList();
    }

}
