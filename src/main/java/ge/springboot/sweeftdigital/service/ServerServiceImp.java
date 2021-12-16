package ge.springboot.sweeftdigital.service;

import ge.springboot.sweeftdigital.dao.ServerDao;
import ge.springboot.sweeftdigital.entity.Server;
import ge.springboot.sweeftdigital.enums.ServerStatus;
import ge.springboot.sweeftdigital.request.ServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ServerServiceImp implements ServerService {

    private final ServerDao serverDao;
    Logger logger = LoggerFactory.getLogger(ServerServiceImp.class);

    @Autowired
    public ServerServiceImp(ServerDao serverDao) {
        this.serverDao = serverDao;
    }

    @Override
    public Server findServerByName(String name) {
        return serverDao.findServerByName(name);
    }

    @Override
    public void addServer(ServerRequest request) {

        logger.info("Adding Server...");
        Server server = new Server();
        server.setName(request.getName());
        server.setExpirationDate(new Date(request.getExpirationDateString()));
        server.setCapacity_MB(request.getCapacity_MB());
        if (request.getServerStatusString().toLowerCase().equals("free")) {
            server.setStatus(ServerStatus.FREE);
        } else {
            server.setStatus(ServerStatus.BUSY);
        }
        serverDao.save(server);
        logger.info("Server successfully added.");


    }

    @Override
    public void removeServer(Server server) {
        logger.info("Deleting server with name: " + server.getName());
        serverDao.delete(serverDao.findServerByName(server.getName()));
        logger.info("Server successfully deleted.");
    }

    @Override
    public void changeServerStatus(Server server) {
        Server attachedServer = serverDao.findServerByName(server.getName());
        if (attachedServer.getStatus() == ServerStatus.BUSY) {
            attachedServer.setStatus(ServerStatus.FREE);
        } else {
            attachedServer.setStatus(ServerStatus.BUSY);
        }
        logger.info("Server status successfully changed.");
        serverDao.save(attachedServer);
    }

    @Override
    public List<Server> findFreeServers() {
        List<Server> servers = serverDao.findAllServers();
        List<Server> necessaryServers = new ArrayList<>();
        for (Server server : servers) {
            if (server.getStatus() == ServerStatus.FREE) {
                necessaryServers.add(server);
            }
        }
        return necessaryServers;
    }

    @Override
    public void saveServer(Server server) {
        serverDao.save(server);
    }

    @Override
    public List<Server> findAllServers() {
        return serverDao.findAllServers();
    }
}
