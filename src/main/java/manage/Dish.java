package manage;

public class Dish {
    private final String name;
    private final String description;
    private final float price;
    private final int quantity;
    private int id;

    public Dish(String name, String description, float price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public Dish(int id, String name, String description, float price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public void show() {
        System.out.println("Блюдо: " + name);
        System.out.println("Описание: " + description);
        System.out.println("цена: " + price);
        System.out.println();
    }

    public void fullShow() {
        System.out.println("Блюдо: " + name);
        System.out.println("Описание: " + description);
        System.out.println("цена: " + price);
        System.out.println("количество: " + quantity);
        System.out.println();
    }

    public int isAvailable() {
        if (quantity == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
