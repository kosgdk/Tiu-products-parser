package beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {

    private String id;
    private int vkId;
    private String name;
    private String link;
    private String imageLink;
    private int deleted;
    private BigDecimal price;
    private int vkPhotoId;


    public Product() {}

    public static Product createProduct(ResultSet resultSet){

        Product product = new Product();

        try {
            product.setId(resultSet.getString("id"));
            product.setName(resultSet.getString("name"));
            product.setPrice(resultSet.getBigDecimal("price"));
            product.setImageLink(resultSet.getString("imagelink"));
            product.setDeleted(resultSet.getInt("deleted"));
            product.setVkId(resultSet.getInt("vkid"));
            product.setVkPhotoId(resultSet.getInt("vkphotoid"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVkId() {
        return vkId;
    }

    public void setVkId(int vkId) {
        this.vkId = vkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getVkPhotoId() {
        return vkPhotoId;
    }

    public void setVkPhotoId(int vkPhotoId) {
        this.vkPhotoId = vkPhotoId;
    }

    @Override
    public String toString() {
        return "name: " + name + "\n" +
                "price: " + price + "\n" +
                "id: " + id + "\n" +
                "image: " + imageLink + "\n" +
                "vkid: " + vkId + "\n" +
                "vkImageId: " + vkPhotoId + "\n" +
                "deleted: " + deleted
                ;
    }
}
