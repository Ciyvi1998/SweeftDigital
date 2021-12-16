package ge.springboot.sweeftdigital.service;

import ge.springboot.sweeftdigital.entity.Server;
import ge.springboot.sweeftdigital.request.ServerRequest;

import java.util.List;

public interface ServerService {
    void addServer(ServerRequest request);
    void removeServer(Server server);
    void changeServerStatus(Server server);
    List<Server> findFreeServers();
    Server findServerByName(String name);
    void saveServer(Server server);
    List<Server> findAllServers();
}
