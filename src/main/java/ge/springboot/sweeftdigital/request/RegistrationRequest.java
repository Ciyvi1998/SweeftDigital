package ge.springboot.sweeftdigital.request;

public class RegistrationRequest {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final int age;
    private final String stringDate;
    private final String gender;

    public RegistrationRequest(String firstName, String lastName, String email, String password, int age, String stringDate, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.age = age;
        this.stringDate = stringDate;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getStringDate() {
        return stringDate;
    }
}
