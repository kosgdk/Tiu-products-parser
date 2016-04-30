import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {


        ProductAdder productAdder = new ProductAdder();

        ParserGlavbritva parser = new ParserGlavbritva();

        ArrayList<Product> products = parser.parse();

        System.out.println("Adding products to VK:");

        int i = 0;
        int n = 10;

        for(i=0 ; i<n ; i++){
            System.out.println(i+1 + " of " + n);
            productAdder.add(products.get(i));
        }


        System.out.println("Total products count: " + (i+1));


        /*
        Product testProduct = new Product(  "Venus Swirl-2",
                                            "Кассета для станков для бритья GILLETTE Swirl (типа Embrace), 2 шт.",
                                            "http://glavbritva.ru/p185411388-kasseta-dlya-stankov.html",
                                            "http://images.ru.prom.st/265417830_w640_h640_1014274783.jpg",
                                            true,
                                            99.85);

        ProductAdder productAdder = new ProductAdder();
        productAdder.add(testProduct);
        */
    }

}
