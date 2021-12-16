package ge.springboot.sweeftdigital.entity;

import ge.springboot.sweeftdigital.enums.ServerStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int capacity_MB;
    private Date expirationDate;
    @Enumerated(value = EnumType.STRING)
    private ServerStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }


    public int getCapacity_MB() {
        return capacity_MB;
    }

    public void setCapacity_MB(int capacity_MB) {
        this.capacity_MB = capacity_MB;
    }
}
