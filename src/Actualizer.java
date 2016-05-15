import database.DBWorker;
import beans.Product;
import vk.VKWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Actualizer {

    public static void actualize(HashMap<String, Product> parsedProducts){

        TreeMap<String, Product> dbProducts = DBWorker.getAllProducts();
        ArrayList<Product> updatedProducts = new ArrayList<>();
        ArrayList<Product> addedProducts = new ArrayList<>();
        ArrayList<Product> errorProducts = new ArrayList<>();

        // Проверяем, есть ли вообще у поставщика наши товары
        for (Map.Entry<String, Product> dbProductEntry : dbProducts.entrySet()) {

            Product dbProduct = dbProductEntry.getValue();

            // Если у поставщика больше нет нашего товара, который доступен в группе ВК
            if (!parsedProducts.containsKey(dbProduct.getId()) & dbProduct.getDeleted() == 0) {
                dbProduct.setDeleted(2);
                //Убираем товар в группе ВК из наличия. Если успешно...
                if(VKWorker.updateProduct(dbProduct)){
                    //Обновляем статус товара в БД
                    DBWorker.updateProduct(dbProduct);
                }
            }

        }

        // Проверяем остальные товары БД на актуальность (цена и наличие)
        for (Map.Entry<String, Product> parsedProductEntry : parsedProducts.entrySet()) {

            Product parsedProduct = parsedProductEntry.getValue();

            // Если продукт уже есть в базе
            if (dbProducts.containsKey(parsedProduct.getId())) {

                Product dbProduct = dbProducts.get(parsedProduct.getId());

                BigDecimal parsedProductPrice = parsedProduct.getPrice();
                BigDecimal dbProductPrice = dbProduct.getPrice();
                int parsedProductDeleted = parsedProduct.getDeleted();
                int dbProductDeleted = dbProduct.getDeleted();

                // Если цена или наличие в базе не актуальны
                if (!parsedProductPrice.equals(dbProductPrice) || parsedProductDeleted != dbProductDeleted) {
                    dbProduct.setPrice(parsedProductPrice);
                    dbProduct.setDeleted(parsedProductDeleted);

                    // Обновляем товар в группе ВК. Если успешно...
                    if(VKWorker.updateProduct(dbProduct)){
                        // Обновляем товар в БД
                        DBWorker.updateProduct(dbProduct);
                        updatedProducts.add(dbProduct);
                    } else {
                        errorProducts.add(dbProduct);
                    }
                }
            }
            // Если продукт новый и его нет в базе
            else {
                // Добавляем продукт в группу ВК. Если успешно...
                Product addedProduct = VKWorker.addProduct(parsedProduct);
                if (addedProduct != null) {
                    // Добавляем продукт в БД.
                    DBWorker.addProduct(addedProduct);
                    addedProducts.add(addedProduct);
                } else {
                    errorProducts.add(parsedProduct);
                }
            }

        }

        System.out.println("Products updated: " + updatedProducts.size());
        System.out.println("New products added: " + addedProducts.size());

        if (errorProducts.size() > 0){
            System.out.println("There were problems with following products: " + errorProducts.size());
            errorProducts.forEach(product -> System.out.println("\t" + product.getName()));
        }

    }

}
