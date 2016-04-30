import com.sun.org.apache.xpath.internal.SourceTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserGlavbritva {

    private String host = "http://glavbritva.ru";
    private String url = host + "/product_list?product_items_per_page=48";
    private ArrayList<Product> products = new ArrayList<>();

    public ArrayList<Product> parse() {

        // Открываем первую страницу с товарами
        try {
            Document doc=Jsoup.connect(url).get();
            Elements firstStage = doc.select("div[class=b-pager]");

            // Получаем количество страниц с товарами
            int pagesCount = Integer.parseInt(firstStage.first().select("a[class= b-pager__link ]").last().text());
            //System.out.println("Total pages: " + pagesCount);



            // Генерируем ссылки на страницы с товарами
            String pagesLinks[] = new String[pagesCount];
            for (int i = 0; i < pagesCount; i++) {
                pagesLinks[i] = host + "/product_list/page_" + (i+1) + "?product_items_per_page=48";
                //System.out.println(pagesLinks[i]);
            }

            System.out.println("Parsing page:");
            int i = 1;
            // Парсим товары с каждой страницы
            for (String pagesLink : pagesLinks) {
                System.out.println(i++ + " of " + pagesCount);
                parseItems(pagesLink);
            }

        }catch (IOException e){
            System.out.println("Some problems opening page: " + url);
            e.printStackTrace();
        }

        return products;
    }

    private void parseItems(String url) throws IOException {

        // Парсим товары
        Document doc = Jsoup.connect(url).get();
        Elements firstStage = doc.select("div[class=b-layout__clear]");
        Elements items = firstStage.first().select("div[class~=(b-product-line b-product-line_type_gallery js-partner-criteo|b-product-line b-product-line_type_gallery b-product-line_pos_last js-partner-criteo)]");

        for (Element item:items) {
            String id = item.select("span").first().attr("title");
            String link = item.select("a").first().attr("href");
            Element imageElement = item.select("img").first();
            String imageLink = imageElement.attr("longdesc");
            imageLink = imageLink.replaceAll("_w200_h200_", "_w640_h640_");
            String name = imageElement.attr("alt");
            float price = priceToFloat(item.select("div[class=b-product-line__price]").first().text());
            String strAvailable = item.select("span[class=b-product-line__state]").first().text();
            boolean available = strAvailable.equals("В наличии");

            /*
            System.out.println( "ID: "+ id + "\n" +
                    "Link: " + link + "\n" +
                    "Image: " + imageLink + "\n" +
                    "Description: " + name + "\n" +
                    "Price: " + price + "\n" +
                    "Available: " + strAvailable + "\n"
            );
            */

            products.add(new Product(id, name, link, imageLink, available, price));
        }
    }


    private float priceToFloat(String price){

        Pattern pattern = Pattern.compile("(\\d*)(\\.*)(\\d*)");
        Matcher matcher = pattern.matcher(price.replace(',','.'));
        float floatPrice = 0;

        if (matcher.find()){
            floatPrice = Float.parseFloat(matcher.group(0));
        }

        return floatPrice;
    }

}
