import database.DBWorker;
import beans.Product;
import vk.VKWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Actualizer {

    private static HashMap<String, Product> dbProducts = DBWorker.getAllProducts();
    private static ArrayList<Product> productsToUpdate = new ArrayList<>();
    private static ArrayList<Product> updatedProducts = new ArrayList<>();
    private static ArrayList<Product> productsToAdd = new ArrayList<>();
    private static ArrayList<Product> addedProducts = new ArrayList<>();
    private static ArrayList<Product> productsToDelete = new ArrayList<>();
    private static ArrayList<Product> deletedProducts = new ArrayList<>();
    private static ArrayList<Product> errorProducts = new ArrayList<>();

    public static void actualize(HashMap<String, Product> parsedProducts){

        // Добавляем в ВК товары, которые есть в БД, но нет в ВК
        ArrayList<Product> unAddedProducts = DBWorker.getUnaddedProducts();

        if (unAddedProducts.size() > 0) {
            System.out.println("Продукты, не опубликованные с прошлого раза: " + unAddedProducts.size() + " шт.");
            productsToAdd.addAll(unAddedProducts);
        }

        // Проверяем, есть ли вообще у поставщика наши товары
        for (Map.Entry<String, Product> dbProductEntry : dbProducts.entrySet()) {

            Product dbProduct = dbProductEntry.getValue();

            // Если у поставщика больше нет нашего товара, который доступен в группе ВК
            if (!parsedProducts.containsKey(dbProduct.getId()) & dbProduct.getDeleted() == 0) {
                dbProduct.setDeleted(2);
                productsToDelete.add(dbProduct);
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
                    productsToUpdate.add(dbProduct);
                }
            }
            // Если продукт новый и его нет в базе
            else {
                productsToAdd.add(parsedProduct);
            }

        }


        System.out.println("\nNew products to add: " + productsToAdd.size());
        productsToAdd.forEach(product -> System.out.println(" - " + product.getName() + "   price: " + product.getPrice() + "   available: " + product.isAvailable()));

        System.out.println("Products to update: " + productsToUpdate.size());
        productsToUpdate.forEach(product -> System.out.println(" - " + product.getName() + "   price: " + product.getPrice() + "   available: " + product.isAvailable()));

        System.out.println("Products to delete: " + productsToDelete.size());
        productsToDelete.forEach(product -> System.out.println(" - " + product.getName()));

        System.out.print("Enter something:");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();

        productsToAdd.forEach(Actualizer::addNewProduct);
        productsToUpdate.forEach(Actualizer::updateProduct);
        productsToDelete.forEach(Actualizer::deleteProduct);

        System.out.println("\nNew products added: " + addedProducts.size());
        addedProducts.forEach(product -> System.out.println(" - " + product.getName() + "   price: " + product.getPrice() + "   available: " + product.isAvailable()));

        System.out.println("Products updated: " + updatedProducts.size());
        updatedProducts.forEach(product -> System.out.println(" - " + product.getName() + "   price: " + product.getPrice() + "   available: " + product.isAvailable()));

        System.out.println("Products deleted: " + deletedProducts.size());
        deletedProducts.forEach(product -> System.out.println(" - " + product.getName()));


        if (errorProducts.size() > 0){
            System.out.println("There were problems with following products: " + errorProducts.size());
            errorProducts.forEach(product -> System.out.println("\t" + product.getName()));
        }

    }

    private static void addNewProduct(Product product){
        // Публикуем продукт в группу ВК.
        Product publishedProduct = VKWorker.addProduct(product);

        if (publishedProduct != null) {
            // Добавляем опубликованный продукт c заполненными VkId и VkPhotoId в БД.
            DBWorker.addProduct(publishedProduct);
            // Добавляем опубликованный продукт в соответствующую коллекцию
            addedProducts.add(publishedProduct);
        } else {
            // Добавляем неопубликованный продукт в БД
            DBWorker.addProduct(product);
            // Добавляем неопубликованный продукт в соответствующую коллекцию
            errorProducts.add(product);
        }
    }

    private static void updateProduct(Product product){
        // Обновляем товар в группе ВК. Если успешно...
        if(VKWorker.updateProduct(product)){
            // Обновляем товар в БД
            DBWorker.updateProduct(product);
            updatedProducts.add(product);
        } else {
            errorProducts.add(product);
        }
    }

    private static void deleteProduct(Product product){
        //Убираем товар в группе ВК из наличия. Если успешно...
        if(VKWorker.updateProduct(product)){
            //Обновляем статус товара в БД
            DBWorker.updateProduct(product);
            deletedProducts.add(product);
        } else {
            errorProducts.add(product);
        }
    }

}
