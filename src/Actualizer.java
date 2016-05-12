import database.DBWorker;
import beans.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TreeMap;

public class Actualizer {

    public static void actualize(ArrayList<Product> parsedProducts, TreeMap<String, Product> dbProducts){

        ArrayList<Product> updatedProducts = new ArrayList<>();
        ArrayList<Product> addedProducts = new ArrayList<>();

        for (Product parsedProduct : parsedProducts) {

            // Если спаршеный продукт уже есть в базе
            if (dbProducts.containsKey(parsedProduct.getId())) {

                Product dbProduct = dbProducts.get(parsedProduct.getId()); // Получаем соответствующий товар из БД
                String ppId = parsedProduct.getId();
                String dbpId = dbProduct.getId();

                BigDecimal ppPrice = parsedProduct.getPrice();
                BigDecimal dbpPrice = dbProduct.getPrice();
                int ppDeleted = parsedProduct.getDeleted();
                int dbpDeleted = dbProduct.getDeleted();

                if (!ppPrice.equals(dbpPrice) || ppDeleted != dbpDeleted) {
                    System.out.println("Updating product: " + parsedProduct.getId());
                    DBWorker.updateProduct(parsedProduct);
                    updatedProducts.add(parsedProduct);
                    // TODO: vk update product
                }
            }
            // Если продукт новый и его нет в базе
            else {
                System.out.println("Adding new product: " + parsedProduct.getId());
                DBWorker.addProduct(parsedProduct);
                addedProducts.add(parsedProduct);
                // TODO: vk add product
            }

        }

//        for (Product product : updatedProducts) {
//            System.out.println(product + "\n");
//        }
        System.out.println("Products updated: " + updatedProducts.size());

//        for (Product product : addedProducts) {
//            System.out.println(product + "\n");
//        }
        System.out.println("New products added: " + addedProducts.size());

    }


}
