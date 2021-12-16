package ge.springboot.sweeftdigital.security.config;

import ge.springboot.sweeftdigital.entity.Server;
import ge.springboot.sweeftdigital.entity.User;
import ge.springboot.sweeftdigital.service.ServerServiceImp;
import ge.springboot.sweeftdigital.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    private final ServerServiceImp serverServiceImp;
    private final UserServiceImp userServiceImp;
    Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);

    @Autowired
    public ScheduleConfig(ServerServiceImp serverServiceImp, UserServiceImp userServiceImp) {
        this.serverServiceImp = serverServiceImp;
        this.userServiceImp = userServiceImp;
    }

    //სრულდება ყოველ დღე 00:01 საათზე
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduleFixedDelayTask() {
        logger.info("Starting scheduled task...");
        List<Server> servers = serverServiceImp.findAllServers();
        if (servers.size() != 0) {
            for (Server server : servers) {
                if (server.getExpirationDate().before(new Date())) {
                    User user = userServiceImp.findUserByServerId(server.getId());
                    if (user != null) {
                        user.setServer(null);
                        userServiceImp.saveUser(user);
                    }
                    logger.info("Deleting expired server");
                    serverServiceImp.removeServer(server);
                    logger.info("Expired server successfully deleted");
                }
            }
        }
        logger.info("Scheduled task finished.");
    }
}
