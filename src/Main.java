import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {

        String host = "http://glavbritva.ru";
        String url = host + "/product_list?product_items_per_page=48";
        Document doc = Jsoup.connect(url).get();
        Elements firstStage = doc.select("div[class=b-pager]");
        int pagesCount = Integer.parseInt(firstStage.first().select("a[class= b-pager__link ]").last().text());
        System.out.println("Total pages: " + pagesCount);

        String pagesLinks[] = new String[pagesCount];

        for (int i = 0; i < pagesCount; i++) {
            pagesLinks[i] = host + "/product_list/page_" + (i+1) + "?product_items_per_page=48";
            System.out.println(pagesLinks[i]);
        }

        int n = 0;

        for (String pagesLink : pagesLinks) {
            n += parseItems(pagesLink);
        }

        System.out.println("Total items: " + n);
    }

    static int parseItems(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements firstStage = doc.select("div[class=b-layout__clear]");
        Elements items = firstStage.first().select("div[class~=(b-product-line b-product-line_type_gallery js-partner-criteo|b-product-line b-product-line_type_gallery b-product-line_pos_last js-partner-criteo)]");

        int n = 0;

        for (Element item:items) {
            n++;
            String id = item.select("span").first().attr("title");
            String link = item.select("a").first().attr("href");
            Element imageElement = item.select("img").first();
            String image = imageElement.attr("src");
            String description = imageElement.attr("alt");
            float price = priceToFloat(item.select("div[class=b-product-line__price]").first().text());
            String strAvailable = item.select("span[class=b-product-line__state]").first().text();
            //int available ? !strAvailable.equals("В наличии"),1,0;

            System.out.println( "ID: "+ id + "\n" +
                    "Link: " + link + "\n" +
                    "Image: " + image + "\n" +
                    "Description: " + description + "\n" +
                    "Price: " + price + "\n" +
                    "Available: " + strAvailable + "\n"
            );
        }
        return n;
    }


    static float priceToFloat(String price){
        Pattern pattern = Pattern.compile("(\\d*)(\\.*)(\\d*)");
        Matcher matcher = pattern.matcher(price.replace(',','.'));
        float floatPrice = 0;

        if (matcher.find()){
           floatPrice = Float.parseFloat(matcher.group(0));
        }

        return floatPrice;
    }

}
