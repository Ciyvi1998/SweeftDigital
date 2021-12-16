package ge.springboot.sweeftdigital.request;

public class ServerRequest {

    private final String name;
    private final String expirationDateString;
    private final String serverStatusString;
    private final int capacity_MB;

    public ServerRequest(String name, String expirationDateString, String serverStatusString, int capacity_mb) {
        this.name = name;
        this.expirationDateString = expirationDateString;
        this.serverStatusString = serverStatusString;
        capacity_MB = capacity_mb;
    }

    public String getName() {
        return name;
    }

    public String getExpirationDateString() {
        return expirationDateString;
    }

    public String getServerStatusString() {
        return serverStatusString;
    }

    public int getCapacity_MB() {
        return capacity_MB;
    }
}
