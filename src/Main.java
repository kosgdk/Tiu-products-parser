import beans.Product;
import database.DBConnectionManager;
import database.DBWorker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.ParserGlavbritva;
import vk.VKWorker;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        // СИНХРОНИЗАЦИЯ ТОВАРОВ В ГРУППЕ ВК С САЙТОМ ПОСТАВЩИКА
        HashMap<String, Product> parsedProducts = ParserGlavbritva.parse();
        Actualizer.actualize(parsedProducts);
        DBConnectionManager.closeConnection();

          // УДАЛЕНИЕ ВСЕХ ТОВАРОВ ИЗ ГРУППЫ ВК
//        TreeMap<String, Product> dbProducts = DBWorker.getAllProducts();
//        dbProducts.forEach((s, product) -> VKWorker.deleteProduct(product));

          // ДОБАВЛЕНИЕ ТОВАРОВ В ПОДБОРКУ
//        ParserGlavbritva.parseCategory("http://glavbritva.ru/g3770505-podarochnye-nabory");
//        HashMap<String, Product> parsedPproducts = ParserGlavbritva.getProducts();
//        System.out.println(parsedPproducts.size());
//        HashMap<String, Product> dbPproducts = DBWorker.getAllProducts();
//
//        for (Map.Entry<String, Product> parsedProductEntry : parsedPproducts.entrySet()){
//            Product product = dbPproducts.get(parsedProductEntry.getKey());
//            VKWorker.setCategory(product, 39);
//        }


    }

}
