package parser;

import beans.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserGlavbritva {

    private static String[] excludedProducts = new String[]{
            "http://glavbritva.ru/p50083274-poroshok-stiralnyj-burti.html",
            "http://glavbritva.ru/p112290298-poroshok-stiralnyj-kontsentrat.html",
            "http://glavbritva.ru/p50618163-poroshok-stiralnyj-burti.html",
            "http://glavbritva.ru/p50717139-sredstvo-dlya-stirki.html",
            "http://glavbritva.ru/p73336862-sredstvo-dlya-stirki.html"};

    private static String host = "http://glavbritva.ru";
    private static String url = host + "/product_list";
    private static HashMap<String, Product> products = new HashMap<>();

    private static int n = 0;

    public static HashMap<String, Product> parse(){

        try {
            Document doc = Jsoup.connect(host + "/product_list").get();

            Elements categories = doc.select("li[class=b-product-groups__item]");
            System.out.println("Total categories: " + categories.size());

            for (Element element : categories) {
                String categoryUrl = host + element.select("a").first().attr("href");
                System.out.println("Parsing category: " + element.select("a").first().attr("title"));
                parseCategory(categoryUrl);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return products;
    }

    private static void parseCategory(String url) {

        n = 0;

//        url = url + "?product_items_per_page=48";

        // Открываем первую страницу с товарами
        try {
            Document doc=Jsoup.connect(url + "?product_items_per_page=48").get();

            // Проверяем, есть ли на странице paging
            Elements pagingElements = doc.select("div[class=b-pager ]");

            if (pagingElements.size() > 0) {
                String pagingLinks[] = parsePaging(url, pagingElements.first());
                int pageCounter = 1;
                for (String pageUrl : pagingLinks) {
                    System.out.println("Parsing page " + pageCounter + "/" + pagingLinks.length);
                    pageCounter++;
                    parseItems(pageUrl);
                    sleep(1000);
                }
            } else {
                parseItems(url + "?product_items_per_page=48");
                sleep(1000);
            }

            System.out.println("Items parsed in category: " + n);

        }catch (IOException e){
            System.out.println("Some problems opening page: " + url);
            e.printStackTrace();
        }

//        System.out.println("Total products parsed: " + products.size());
    }

    private static String[] parsePaging (String url, Element element){

        // Получаем количество страниц с товарами
        int pagesCount = (Integer.parseInt(element.select("a[class=b-pager__link]").last().text()));

        System.out.println("Pages: " + (pagesCount));

        // Генерируем ссылки на страницы с товарами
        String[] pagesLinks = new String[pagesCount];

        for (int i = 0; i < (pagesCount); i++) {
            pagesLinks[i] = url + "/page_" + (i+1) + "?product_items_per_page=48";
            //System.out.println(pagesLinks[i]);
        }

        return pagesLinks;

    }

    private static void parseItems(String url) {

        // Парсим товары
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements firstStage = doc.select("div[class=b-layout__clear]");
        Elements items = firstStage.first().select("div[class~=(b-product-line b-product-line_type_gallery js-partner-criteo|b-product-line b-product-line_type_gallery b-product-line_pos_last js-partner-criteo)]");

        for (Element item:items) {

//            String id = item.select("span").first().attr("title");
            String link = item.select("a").first().attr("href");
            Element imageElement = item.select("img").first();
            String imageLink = imageElement.attr("src");

            // Парсим изображение
            CharSequence imageMask = "_w200_h200_";
            if (!imageLink.contains(imageMask)){
                imageLink = imageElement.attr("longdesc");
            }
            imageLink = imageLink.replaceAll("_w200_h200_", "_w640_h640_");

            String name = imageElement.attr("alt");
            String stringPrice = item.select("div[class=b-product-line__price-bar]").select("div").last().text();
            BigDecimal price =  priceToBigDecimal(stringPrice).setScale(2);
            String strAvailable = item.select("span[class=b-product-line__state]").first().text();
            int deleted = strAvailable.equals("В наличии") ? 0 : 2;

//            System.out.println( "ID: "+ id + "\n" +
//                    "Link: " + link + "\n" +
//                    "Image: " + imageLink + "\n" +
//                    "Name: " + name + "\n" +
//                    "Price: " + price + "\n" +
//                    "Available: " + strAvailable + "\n"
//            );

            Product product = new Product();
//            product.setId(id);
            product.setId(link);
            product.setName(name);
            product.setImageLink(imageLink);
            product.setDeleted(deleted);
            product.setPrice(price);

            products.put(link, product);

            n++;
        }
    }

    private static BigDecimal priceToBigDecimal(String price){

        price = price.replaceAll("[\\u00A0|\\s]","");
        String price3 = price.replace(',','.');
        Pattern pattern = Pattern.compile(".*(?=ру)");
        Matcher matcher = pattern.matcher(price3);
        BigDecimal bdPrice = new BigDecimal(1);

        if (matcher.find()){
            String stringPrice = matcher.group(0);
            bdPrice = new BigDecimal(stringPrice);
        }

        return bdPrice;
    }

    private static void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
