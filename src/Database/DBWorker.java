package database;

import beans.Product;

import java.sql.*;
import java.util.*;

public class DBWorker {

    private static Connection connection = DBConnectionManager.getInstance().getConnection();
    private static String tableName = "products";

    static {
        checkTable();
    }

    private static void checkTable(){

        String[] tableTypes = {"TABLE"};
        ResultSet rsTables = null;

        try {

            DatabaseMetaData dbMetaData = connection.getMetaData();
            rsTables = dbMetaData.getTables(null, "%", tableName, tableTypes);

            rsTables.last();

            if (rsTables.getRow() == 1){
                System.out.println("database table \"products\" was successfully found.");
            } else {
                System.out.println("database table \"products\" was not found. \n Creating new table...");
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
                "  `id` NVARCHAR(300) NOT NULL,\n" +
                "  `vkid` INT(15) NULL,\n" +
                "  `name` NVARCHAR(300) NOT NULL,\n" +
                "  `price` DECIMAL(7,2) NOT NULL,\n" +
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

    }

    public static void addProduct(Product product){

        String SQL = "INSERT INTO " + tableName + " (id, name, price, imagelink, deleted) VALUES ('" +
                escapeSymbols(product.getId()) + "', '" +
                escapeSymbols(product.getName()) + "', " +
                product.getPrice() + ", '" +
                product.getImageLink() + "', " +
                product.getDeleted() + ")";

//        System.out.println(SQL);
        executeUpdate(SQL);

    }

    public static void updateProduct(Product product){

        String SQL = "UPDATE " + tableName + "SET " +
                "price = " + product.getPrice() + ", " +
                "deleted = " + product.getDeleted() +
                " WHERE id = '" + product.getId() + "'";

        System.out.println(SQL);
        executeUpdate(SQL);
    }

    public static TreeMap<String, Product> getProducts(){

        System.out.println("Reading data from dataase:");

        TreeMap<String, Product> products = new TreeMap<>();

        String SQL = "SELECT * FROM " + tableName + ";";
        Product productObject = null;

        try {

            Statement statement = connection.createStatement();
            ResultSet rsProducts = statement.executeQuery(SQL);

            while (rsProducts.next()) {

                productObject = new Product();

                productObject.setId(rsProducts.getString("id"));
                productObject.setName(rsProducts.getString("name"));
                productObject.setPrice(rsProducts.getBigDecimal("price"));
                productObject.setImageLink(rsProducts.getString("imagelink"));
                productObject.setDeleted(rsProducts.getInt("deleted"));

                products.put(productObject.getId(), productObject);
//                System.out.println(productObject + "\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Total products read from database: " + products.size());
        return products;
    }

    private static void executeUpdate(String SQL){

        try {
            Statement statement = connection.prepareStatement(SQL);
            statement.executeUpdate(SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static ResultSet executeQuery(String SQL){

        ResultSet resultSet = null;

        try {
            Statement statement = connection.prepareStatement(SQL);
            resultSet = statement.executeQuery(SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;

    }

    private static String escapeSymbols(String string){

        return string.replace("\'","\\\'");

    }

}
