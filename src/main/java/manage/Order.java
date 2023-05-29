package manage;

public class Order {
    private final int user_id;
    private int id;
    private final String status;
    private final String special_requests;

    public Order(int user_id, String status, String special_requests) {
        this.user_id = user_id;
        this.status = status;
        this.special_requests = special_requests;
    }

    public Order(int id, int user_id, String status, String special_requests) {
        this.id = id;
        this.user_id = user_id;
        this.status = status;
        this.special_requests = special_requests;
    }

    public void fullShow() {
        System.out.println("id: " + id);
        System.out.println("user id: " + user_id);
        System.out.println("status: " + status);
        System.out.println("comment: " + special_requests);
        System.out.println();
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecial_requests() {
        return special_requests;
    }
}
