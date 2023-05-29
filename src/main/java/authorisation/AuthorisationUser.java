package authorisation;

import java.sql.Timestamp;

public class AuthorisationUser {
    public static final String admin_password = "1234";
    private final int id;
    private final String username;
    private final String email;
    private final String password_hash;
    private final String role;
    private final Timestamp created_at;
    private final Timestamp updated_at;

    public AuthorisationUser(int id, String username, String email, String password_hash, String role, Timestamp created_at, Timestamp updated_at) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password_hash = password_hash;
        this.role = role;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public void show() {
        System.out.println(id + " " + role + " " + username + " " + email);
    }

    public void fullShow() {
        System.out.println("id: " + id);
        System.out.println("username: " + username);
        System.out.println("email: " + email);
        System.out.println("password: " + password_hash);
        System.out.println("role: " + role);
        System.out.println("Создан: " + created_at);
        System.out.println("Последняя активность: " + updated_at);
        System.out.println();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public String getRole() {
        return role;
    }
}