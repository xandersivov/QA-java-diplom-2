public class UserData {

    private String email;
    private String password;
    private String name;

    public String getEmail() {
        return email;
    }

    public UserData setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserData setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserData setName(String name) {
        this.name = name;
        return this;
    }
}