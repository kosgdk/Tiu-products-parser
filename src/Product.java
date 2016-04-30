public class Product {
    private String id;
    private String name;
    private String link;
    private String imageLink;
    private boolean available;
    private double price;

    private int categoryId = 703; // Красота и здоровье -> Приборы и аксессуары

    private int emptyPhotoId = 413141791;


    public Product(String id, String name, String link, String imageLink, boolean available, double price) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.imageLink = imageLink;
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

    public String getImageLink() {
        return imageLink;
    }

    public boolean isAvailable() {
        return available;
    }

    public double getPrice() {
        return price;
    }

    public int isDeleted(){
        return available ? 0 : 2;
    }

    public int getEmptyPhotoId() {
        return emptyPhotoId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public String toString() {
        return "id: " + id + "\n" +
                "name: " + name + "\n" +
                "link: " + link + "\n" +
                "image: " + imageLink + "\n" +
                "price: " + price + "\n" +
                "available: " + available
                ;
    }
}
