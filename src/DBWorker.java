import beans.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DBWorker {

    private static Connection connection = DBConnectionManager.getInstance().getConnection();
    private static String tableName = "products";

    public static void checkTable(){

        String[] tableTypes = {"TABLE"};
        ResultSet rsTables = null;

        try {

            DatabaseMetaData dbMetaData = connection.getMetaData();
            rsTables = dbMetaData.getTables(null, "%", tableName, tableTypes);

            rsTables.last();

            if (rsTables.getRow() == 1){
                System.out.println("Database table \"products\" was successfully found.");
            } else {
                System.out.println("Database table \"products\" was not found. \n Creating new table...");
                createTable();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rsTables.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private static void createTable(){

        String SQL =
                "CREATE TABLE `gillette`.`" + tableName + "` (\n" +
                "  `id` NVARCHAR(50) NOT NULL,\n" +
                "  `vkid` INT(15) NULL,\n" +
                "  `name` NVARCHAR(300) NOT NULL,\n" +
                "  `price` DECIMAL NOT NULL,\n" +
                "  `link` NVARCHAR(300) NOT NULL,\n" +
                "  `imagelink` NVARCHAR(300) NOT NULL,\n" +
                "  `deleted` TINYINT(1) NOT NULL,\n" +
                "  PRIMARY KEY (`id`));";

        executeUpdate(SQL);
    }

    public static void addProducts(ArrayList<Product> products){

        Iterator<Product> iterator = products.iterator();

        while (iterator.hasNext()){
            addProduct(iterator.next());
        }

        /*
        Iterator iterator = products.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            Product product = (Product) entry.getValue();
            addProduct(product);
        }
        */

    }

    public static void addProduct(Product product){

        String id = product.getId();
        String name = product.getName();
        BigDecimal price = product.getPrice();

        String SQL = "INSERT INTO " + tableName + " (id, name, price, link, imagelink, deleted) VALUES ('" +
                product.getId() + "', '" +
                product.getName() + "', " +
                product.getPrice() + ", '" +
                product.getLink() + "', '" +
                product.getImageLink() + "', " +
                product.getDeleted() + ")";

        System.out.println(SQL);

        executeUpdate(SQL);

    }


    private static void executeUpdate(String SQL){

        try {
            Statement statement = connection.prepareStatement(SQL);
            statement.executeUpdate(SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
