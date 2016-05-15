import beans.Product;
import database.DBConnectionManager;
import database.DBWorker;
import parser.ParserGlavbritva;
import vk.VKWorker;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        HashMap<String, Product> parsedProducts = ParserGlavbritva.parse();
        Actualizer.actualize(parsedProducts);

        ArrayList<Product> unaddedProducts = DBWorker.getUnaddedProducts();

        if (unaddedProducts.size() > 0) {
            System.out.println("Trying to add not added before products into VK group:");
            for (Product product : unaddedProducts) {
                Product addedProduct = VKWorker.addProduct(product);
                if (addedProduct != null) {
                    DBWorker.updateProduct(addedProduct);
                }
            }
        }

        DBConnectionManager.closeConnection();


//        Product testProduct = new Product();
//        testProduct.setId("http://glavbritva.ru/p27883003-stanok-dlya-britya.html");
//        testProduct.setVkId(211859);
//        testProduct.setName("Станок для бритья одноразовый DORCO TG-711 c 2 лезвиями, плавающей головкой и удлиненной ручкой, 5 шт.");
//        testProduct.setPrice(new BigDecimal(88));
//        testProduct.setImageLink("http://images.ru.prom.st/50065926_w640_h640_tg71141p2.png");
//        testProduct.setDeleted(0);
//        testProduct.setVkPhotoId(415383583);
//
//        VKWorker.updateProduct(testProduct);


    }

}
