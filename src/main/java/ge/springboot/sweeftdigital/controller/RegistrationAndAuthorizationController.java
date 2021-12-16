package ge.springboot.sweeftdigital.controller;

import ge.springboot.sweeftdigital.dao.ConfirmationTokenDao;
import ge.springboot.sweeftdigital.entity.ConfirmationToken;
import ge.springboot.sweeftdigital.entity.User;
import ge.springboot.sweeftdigital.request.RegistrationRequest;
import ge.springboot.sweeftdigital.service.EmailSenderService;
import ge.springboot.sweeftdigital.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping(path = "/api/v1")
public class RegistrationAndAuthorizationController {

    private final UserServiceImp userServiceImp;
    private final ConfirmationTokenDao confirmationTokenDao;
    private final EmailSenderService emailSenderService;

    Logger logger = LoggerFactory.getLogger(RegistrationAndAuthorizationController.class);

    @Autowired
    public RegistrationAndAuthorizationController(UserServiceImp userServiceImp, ConfirmationTokenDao confirmationTokenDao, EmailSenderService emailSenderService) {
        this.userServiceImp = userServiceImp;
        this.confirmationTokenDao = confirmationTokenDao;
        this.emailSenderService = emailSenderService;
    }


    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signUp(@RequestBody RegistrationRequest request) {

        try {
            logger.info("Saving user with email: " + request.getEmail());
            userServiceImp.signUpUser(request);
            logger.info("User with email: " + request.getEmail() + " successfully saved. " +
                    "See the Email to activate user");
            return new ResponseEntity<>("User with email: " + request.getEmail() + " successfully saved \n" +
                    "See the Email to activate user", HttpStatus.OK);

        } catch (Exception e) {
           logger.error("Error while saving user...");
            return new ResponseEntity<>("Error while saving user...", HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(Model model, @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenDao.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userServiceImp.findUserByEmail(token.getUser().getEmail());
            user.setEnable(true);
            userServiceImp.saveUser(user);
            logger.info("User with email: " + user.getEmail() + " successfully enabled");
            return new ResponseEntity<>("User with email: " + user.getEmail() + " successfully enabled", HttpStatus.OK);
        } else {
            logger.error("link is invalid");
            return new ResponseEntity<>("link is invalid", HttpStatus.BAD_REQUEST);
        }

    }

}
