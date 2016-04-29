public class Product {
    private String id;
    private String name;
    private String link;
    private String imagelink;
    private boolean available;
    private float price;

    public Product(String id, String name, String link, String imagelink, boolean available, float price) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.imagelink = imagelink;
        this.available = available;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getImagelink() {
        return imagelink;
    }

    public boolean isAvailable() {
        return available;
    }

    public float getPrice() {
        return price;
    }

    public int isDeleted(){
        return available ? 0 : 1;
    }

    @Override
    public String toString() {
        return "id: " + id +
                "name: " + name +
                "link: " + link +
                "image: " + imagelink +
                "price: " + price +
                "available: " + available
                ;
    }
}
