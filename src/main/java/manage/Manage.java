package manage;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Manage {
    public final static String defRoot = "root";
    public final static String defPw = "1234";
    public final static String defUrl = "jdbc:mysql://localhost:3306/base1";
    private final static byte[] secretKey = "66Rf1PHfoNSordt43kimp4gOh8Knlc0iJMqzztJwHc9imojdQh3YfHuHKJUPxS0qVrP0c6RVsRnIbSE5roE0ahLgSqngPqpRfq28".getBytes();

    public Manage(String jwt, String root, String pw, String url) throws SQLException {
        Connection connection = DriverManager.getConnection(url, root, pw);
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS dish ( " +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, description TEXT, " +
                "price DECIMAL(10, 2) NOT NULL, " +
                "quantity INT NOT NULL, " +
                "is_available BOOLEAN NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(), " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP());");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS us_order ( " +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, status VARCHAR(50) NOT NULL, " +
                "special_requests TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(), " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP());");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS order_dish ( " +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "order_id INT NOT NULL, dish_id INT NOT NULL, " +
                "quantity INT NOT NULL, price DECIMAL(10, 2) NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES us_order(id), " +
                "FOREIGN KEY (dish_id) REFERENCES dish(id));");
        Claims res = null;
        try {
            res = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            System.out.println("Невалидный jwt токен");
            connection.close();
            return;
        }
        ManageUser curUser = new ManageUser(
                res.get("id", Integer.class),
                res.get("username", String.class),
                res.get("email", String.class),
                res.get("role", String.class));
        Scanner in = new Scanner(System.in);
        if (curUser.getRole().equals("manager")) {
            label:
            while (true) {
                System.out.println("Для добавления блюда введите - 1, " +
                        "для просмотра блюд - 2, " +
                        "для просмотра заказов - 3, " +
                        "для выхода введите '-'");
                String ans = in.nextLine();
                switch (ans) {
                    case "1":
                        System.out.println("Введите название блюда:");
                        String name = in.nextLine();
                        System.out.println("Введите описание блюда:");
                        String description = in.nextLine();
                        System.out.println("Введите цену блюда:");
                        float price = in.nextFloat();
                        System.out.println("Введите количество блюд:");
                        int quantity = in.nextInt();
                        Dish curDish = new Dish(name, description, price, quantity);
                        statement.executeUpdate("INSERT INTO dish (name, description, price, is_available, quantity) values ('" +
                                curDish.getName() + "', '" +
                                curDish.getDescription() + "', '" +
                                curDish.getPrice() + "', '" +
                                curDish.isAvailable() + "', '" +
                                curDish.getQuantity() + "');");
                        System.out.println("Блюдо успешно добавлено");
                        break;
                    case "2": {
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM dish");
                        ArrayList<Dish> dishes = new ArrayList<Dish>();
                        while (resultSet.next()) {
                            dishes.add(new Dish(
                                    resultSet.getInt("id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("description"),
                                    resultSet.getFloat("price"),
                                    resultSet.getInt("quantity")));
                        }
                        System.out.println("Все блюда:");
                        for (Dish dish : dishes) {
                            dish.fullShow();
                        }
                        break;
                    }
                    case "3": {
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM us_order");
                        ArrayList<Order> orders = new ArrayList<Order>();
                        while (resultSet.next()) {
                            orders.add(new Order(
                                    resultSet.getInt("id"),
                                    resultSet.getInt("user_id"),
                                    resultSet.getString("status"),
                                    resultSet.getString("special_requests")));
                        }
                        System.out.println("Все заказы:");
                        for (Order order : orders) {
                            order.fullShow();
                        }
                        break;
                    }
                    case "-":
                        break label;
                }
            }
        } else if (curUser.getRole().equals("customer")) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dish WHERE quantity>0");
            ArrayList<Dish> menu = new ArrayList<Dish>();
            while (resultSet.next()) {
                menu.add(new Dish(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getFloat("price"),
                        resultSet.getInt("quantity")));
            }
            System.out.println("Меню:");
            for (Dish dish : menu) {
                dish.show();
            }
            System.out.println("Введите названия блюд, которые хотите заказать, затем введите '-':");
            ArrayList<Dish> order = new ArrayList<Dish>();
            String curName = in.nextLine();
            while (!curName.equals("-")) {
                for (Dish dish : menu) {
                    if (dish.getName().equals(curName)) {
                        order.add(dish);
                    }
                }
                curName = in.nextLine();
            }
            System.out.println("Ваши комментарии к заказу:");
            String comm = in.nextLine();
            System.out.println("Заказ:");
            Order curOrder = new Order(curUser.getId(), "in process", comm);
            statement.executeUpdate("INSERT INTO us_order (user_id, status, special_requests) values ('" +
                    curOrder.getUser_id() + "', '" +
                    curOrder.getStatus() + "', '" +
                    curOrder.getSpecial_requests() + "');");
            ResultSet resultSet2 = statement.executeQuery("SELECT * FROM us_order");
            ArrayList<Order> curOrders = new ArrayList<Order>();
            while (resultSet2.next()) {
                curOrders.add(new Order(
                        resultSet2.getInt("id"),
                        resultSet2.getInt("user_id"),
                        resultSet2.getString("status"),
                        resultSet2.getString("special_requests")));
            }
            for (Dish dish : order) {
                dish.show();
                statement.executeUpdate("INSERT INTO order_dish (order_id, dish_id, quantity, price) values ('" +
                        curOrders.get(curOrders.size() - 1).getId() + "', '" +
                        dish.getId() + "', '" +
                        1 + "', '" +
                        dish.getPrice() + "');");
            }
            System.out.println("Заказ выполняется...");
            ResultSet set = statement.executeQuery("SELECT * FROM us_order  WHERE status='in process'");
            while (!set.isClosed() && set.next()) {
                Order cur = new Order(
                        set.getInt("id"),
                        set.getInt("user_id"),
                        set.getString("status"),
                        set.getString("special_requests"));
                statement.executeUpdate("UPDATE us_order SET status='done' WHERE id=" + cur.getId());
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Заказ выполнен...");
        }
        connection.close();
    }
}
