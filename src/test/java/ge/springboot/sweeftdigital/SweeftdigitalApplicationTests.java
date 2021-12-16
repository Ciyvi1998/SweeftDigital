package ge.springboot.sweeftdigital;

import ge.springboot.sweeftdigital.request.RegistrationRequest;
import ge.springboot.sweeftdigital.service.UserServiceImp;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SweeftdigitalApplicationTests {

    private final UserServiceImp userServiceImp;

    @Autowired
    SweeftdigitalApplicationTests(UserServiceImp userServiceImp) {
        this.userServiceImp = userServiceImp;
    }

    @Test
    @Order(1)
    public void registerUser() {
        RegistrationRequest request = new RegistrationRequest("Test", "Testadze", "test@gmail.com",
                "testPassword", 33, "02/09/1990", "male");
        userServiceImp.signUpUser(request);
        assertThat(userServiceImp.findAllUsers()).size().isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void testUsers() {
        assertEquals(33, userServiceImp.findUserByEmail("test@gmail.com").getAge());
    }

}
