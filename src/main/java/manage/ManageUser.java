package manage;

public class ManageUser {
    private final int id;
    private final String role;

    public ManageUser(int id, String username, String email, String role) {
        this.id = id;
        this.role = role;
    }
    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}
