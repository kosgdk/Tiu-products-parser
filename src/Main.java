import beans.Product;
import database.DBWorker;
import parser.ParserGlavbritva;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {



        ArrayList<Product> parsdProducts = ParserGlavbritva.parse();
        TreeMap<String, Product> dbProducts = DBWorker.getProducts();
        Actualizer.actualize(parsdProducts, dbProducts);


//        String token = vk.Authorization.getToken();
        /*
        System.out.println("\n-----------------------------------\n\nTOTAL: " + products.size() + " products");

        database.DBWorker.checkTable();
        database.DBWorker.addProducts(products);
        */

        /*
        vk.ProductAdder productAdder = new vk.ProductAdder();



        System.out.println("Adding products to vk:");

        int k = 0;
        int n = 10;

        for(int i=k ; i<n ; i++){
            System.out.print(i+1 + " of " + n);
            productAdder.addProduct(products.get(i));
        }
        */

        /*
        beans.Product testProduct = new beans.Product(  "Venus Swirl-2",
                                            "Кассета для станков для бритья GILLETTE Swirl (типа Embrace), 2 шт.",
                                            "http://glavbritva.ru/p185411388-kasseta-dlya-stankov.html",
                                            "http://images.ru.prom.st/265417830_w640_h640_1014274783.jpg",
                                            true,
                                            99.85);

        vk.ProductAdder productAdder = new vk.ProductAdder();
        productAdder.addProduct(testProduct);
        */
    }

}
