package authorisation;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Authorisation {
    public final static String defRoot = "root";
    public final static String defPw = "1234";
    public final static String defUrl = "jdbc:mysql://localhost:3306/base";
    private final static byte[] secretKey = "66Rf1PHfoNSordt43kimp4gOh8Knlc0iJMqzztJwHc9imojdQh3YfHuHKJUPxS0qVrP0c6RVsRnIbSE5roE0ahLgSqngPqpRfq28".getBytes();
    private String jwt = "";

    public Authorisation(String root, String pw, String url) throws SQLException {
        Connection connection = DriverManager.getConnection(url, root, pw);
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS user (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "role VARCHAR(10) NOT NULL CHECK (role IN ('customer', 'chef', 'manager')), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(), " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP());");
        ArrayList<AuthorisationUser> users;
        AuthorisationUser curUser = null;
        while (true) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM User");
            users = new ArrayList<AuthorisationUser>();
            while (resultSet.next()) {
                users.add(new AuthorisationUser(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email"), resultSet.getString("password_hash"), resultSet.getString("role"), resultSet.getTimestamp("created_at"), resultSet.getTimestamp("updated_at")));
            }
            Scanner in = new Scanner(System.in);
            System.out.println("Вход - 1\nРегистрация - 2\nВведите код операции:");
            String answer = in.nextLine();
            if (answer.equalsIgnoreCase("1")) {
                System.out.println("Вход:");
                System.out.println("Введите ник:");
                String username = in.nextLine();
                while (true) {
                    boolean flag = true;
                    for (AuthorisationUser user : users) {
                        if (username.equals(user.getUsername())) {
                            flag = false;
                            curUser = user;
                            break;
                        }
                    }
                    if (!flag) {
                        break;
                    }
                    System.out.println("Пользователя с таким ником не существует, попробуйте еще раз:");
                    username = in.nextLine();
                }
                System.out.println("Введите почту:");
                String email = in.nextLine();
                while (!email.equals(curUser.getEmail())) {
                    System.out.println("Неверное имя пользователя или почта, попробуйте еще раз:");
                    email = in.nextLine();
                }
                System.out.println("Введите пароль:");
                String password = in.nextLine();
                while (!password.equals(curUser.getPassword_hash())) {
                    System.out.println("Неверный пароль, попробуйте еще раз:");
                    password = in.nextLine();
                }
                System.out.println("Вход выполнен успешно\n");
                if (curUser.getRole().equals("manager")) {
                    System.out.println("Информация о текущем пользователе:");
                    curUser.fullShow();
                    System.out.println("Информация о всех пользователях:");
                    for (AuthorisationUser user : users) {
                        user.fullShow();
                    }
                } else {
                    System.out.println("Информация о текущем пользователе:");
                    curUser.show();
                    System.out.println("Если вы администратор и хотите получить доступ уровня manager для этого аккаунта введите пароль администратора, иначе введите '-':");
                    password = in.nextLine();
                    while ((!password.equals(AuthorisationUser.admin_password)) && (!password.equals("-"))) {
                        System.out.println("Неверный пароль, попробуйте еще раз или введите '-':");
                        password = in.nextLine();
                    }
                    if (!password.equals("-")) {
                        statement.executeUpdate("UPDATE User SET role='manager' WHERE username='" + curUser.getUsername() + "'");
                        System.out.println("Роль manager успешно получена");
                    }
                }
                break;
            } else if (answer.equalsIgnoreCase("2")) {
                System.out.println("Регистрация:");
                System.out.println("Введите ник:");
                String username = in.nextLine();
                while (true) {
                    boolean flag = true;
                    for (AuthorisationUser user : users) {
                        if (username.equals(user.getUsername())) {
                            flag = false;
                            break;
                        }
                    }
                    if (username.length() > 50) {
                        flag = false;
                    }
                    if (flag) {
                        break;
                    }
                    System.out.println("Пользователь с таким ником уже существует или длина более 50 символов, попробуйте еще раз:");
                    username = in.nextLine();
                }
                System.out.println("Введите почту:");
                String email = in.nextLine();
                while (true) {
                    boolean flag = true;
                    for (AuthorisationUser user : users) {
                        if (email.equals(user.getEmail())) {
                            flag = false;
                            break;
                        }
                    }
                    if (!email.matches("^.+@.+[.].+$")) {
                        flag = false;
                    }
                    if (email.length() > 100) {
                        flag = false;
                    }
                    if (flag) {
                        break;
                    }
                    System.out.println("Пользователь с такой почтой уже существует или почтовый адрес указан в неверном формате, попробуйте еще раз:");
                    email = in.nextLine();
                }
                System.out.println("Введите пароль:");
                String password = in.nextLine();
                statement.executeUpdate("INSERT INTO User (username, email, password_hash, role) values ('" +
                        username + "', '" +
                        email + "', '" +
                        password + "', '" +
                        "customer')");
                System.out.println("Пользователь успешно создан");
            } else {
                System.out.println("Выберите верный номер операции:");
            }
        }
        connection.close();
        jwt = Jwts.builder()
                .claim("id", curUser.getId())
                .claim("email", curUser.getEmail())
                .claim("username", curUser.getUsername())
                .claim("role", curUser.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey))
                .compact();
    }

    public String getJwt() {
        return jwt;
    }
}
