package vk;

import database.DBWorker;
import parser.ImageChecker;
import beans.Product;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import parser.ParserGlavbritva;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;


public class VKWorker {

    private static final String apiURL = "https://api.vk.com/method/";
    private static final String groupId = "98658033";
    private static final BigDecimal priceMultiplyer = new BigDecimal(1.15);

    private static String token = Authorization.getToken();
    private static int categoryId = 703; // Красота и здоровье -> Приборы и аксессуары

    public static Product addProduct(Product product){

        ParserGlavbritva.sleep(400);
        System.out.print("\nAdding product \"" + product.getName() + "\" to VK group... ");

        // Проверяем фото на валидность и соответствие размеру (не мене 400х400)
        String imageLink = product.getImageLink();
        product.setVkPhotoId(addImage(ImageChecker.getImageFile(imageLink)));

        // Создаём запрос на добавление нового товара
        HttpPost httppost = new HttpPost("https://api.vk.com/method/market.add");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("owner_id", "-" + groupId)
                .addTextBody("access_token", token)
                .addTextBody("name", cropName(product.getName()), ContentType.create("text/plain", Charset.forName("UTF-8")))
                .addTextBody("description", product.getName(), ContentType.create("text/plain", Charset.forName("UTF-8")))
                .addTextBody("category_id", String.valueOf(categoryId))
                .addTextBody("price", String.valueOf(product.getPrice().multiply(priceMultiplyer).setScale(0, RoundingMode.HALF_UP)))
                .addTextBody("deleted", String.valueOf(product.getDeleted()))
                .addTextBody("main_photo_id", String.valueOf(product.getVkPhotoId()))
                .build();
        httppost.setEntity(httpEntity);

        // Отправляем запрос
        String response = POSTSender.send(httppost);

        // Проверяем, добавился ли товар
        if (response != null && response.contains("market_item_id")) {
            System.out.println("success!");
            JsonParser JSONparser = new JsonParser();
            int vkID = JSONparser.parse(response).getAsJsonObject().getAsJsonObject("response").get("market_item_id").getAsInt();
            product.setVkId(vkID);
            return product;

        } else {
            System.out.println("fail");
            System.out.println("VK response: " + response);
            return null;
        }

    }

    public static boolean updateProduct(Product product){

        ParserGlavbritva.sleep(400);
        System.out.println("---\nUpdating product \"" + product.getName() + "\" in VK group:");

        product = updateDbVkPhotoId(product);

        // Создаём запрос на редактирование товара
        HttpPost httppost = new HttpPost("https://api.vk.com/method/market.edit");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("owner_id", "-" + groupId)
                .addTextBody("access_token", token)
                .addTextBody("item_id", String.valueOf(product.getVkId()))
                .addTextBody("name", cropName(product.getName()), ContentType.create("text/plain", Charset.forName("UTF-8")))
                .addTextBody("description", product.getName(), ContentType.create("text/plain", Charset.forName("UTF-8")))
                .addTextBody("category_id", String.valueOf(categoryId))
                .addTextBody("price", String.valueOf(product.getPrice().multiply(priceMultiplyer).setScale(0, RoundingMode.HALF_UP)))
                .addTextBody("deleted", String.valueOf(product.getDeleted()))
                .addTextBody("main_photo_id", String.valueOf(product.getVkPhotoId()))
                .build();
        httppost.setEntity(httpEntity);

        // Отправляем запрос
        String response = POSTSender.send(httppost);

        if (response.equals("{\"response\":1}")) {
            System.out.println("Product \"" + product.getName() + "\" successfully updated in VK group!");
            return true;
        } else {
            System.out.println("fail.");
            System.out.println("VK server response: " + response);
            return false;
        }

    }

    public static boolean deleteProduct(Product product){

        ParserGlavbritva.sleep(100);

        System.out.print("Deleting product \"" + product.getName() + "\" from VK group... ");

        // Создаём запрос на удаление товара
        HttpPost httppost = new HttpPost("https://api.vk.com/method/market.delete");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("access_token", token)
                .addTextBody("owner_id", "-" + groupId)
                .addTextBody("item_id", String.valueOf(product.getVkId()))
                .build();
        httppost.setEntity(httpEntity);

        // Отправляем запрос
        String response = POSTSender.send(httppost);

        // Проверяем, добавился ли товар
        if (response != null && response.contains("{\"response\":1}")) {
            System.out.println("success!");
            return true;
        } else {
            System.out.println("fail");
            System.out.println("VK response: " + response + "\n");
            return false;
        }

    }

    public static boolean reorderProduct(String albumId, String vkId, String afterId){

        ParserGlavbritva.sleep(200);

        System.out.print("Reordering product \"" + vkId + "\" position ... ");

        // Создаём запрос на перемещение продукта в подборке
        HttpPost httppost = new HttpPost("https://api.vk.com/method/market.reorderItems");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("access_token", token)
                .addTextBody("owner_id", "-" + groupId)
                .addTextBody("album_id", albumId)
                .addTextBody("item_id", vkId)
                .addTextBody("after", afterId)
                .build();
        httppost.setEntity(httpEntity);

        // Отправляем запрос
        String response = POSTSender.send(httppost);

        // Проверяем, добавился ли товар
        if (response != null && response.contains("{\"response\":1}")) {
            System.out.println("success!");
            return true;
        } else {
            System.out.println("fail");
            System.out.println("VK response: " + response + "\n");
            return false;
        }

    }

    public static boolean setCategory(Product product, int categoryId){

        ParserGlavbritva.sleep(200);

        System.out.print("Adding product \"" + product.getName() + "\" to VK album... ");

        // Создаём запрос на удаление товара
        HttpPost httppost = new HttpPost("https://api.vk.com/method/market.addToAlbum");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("access_token", token)
                .addTextBody("owner_id", "-" + groupId)
                .addTextBody("item_id", String.valueOf(product.getVkId()))
                .addTextBody("album_ids", String.valueOf(categoryId))
                .build();
        httppost.setEntity(httpEntity);

        // Отправляем запрос
        String response = POSTSender.send(httppost);

        // Проверяем на успешность операции
        if (response != null && response.contains("{\"response\":1}")) {
            System.out.println("success!");
            return true;
        } else {
            System.out.println("fail");
            System.out.println("VK response: " + response + "\n");
            return false;
        }

    }

    private static int addImage(File imageFile){

        // 1 шаг - получаем URL для загрузки фото у метода photos.getMarketUploadServer
        //String response = makeGet("https://api.vk.com/method/photos.getMarketUploadServer?group_id=120338153&main_photo=1&access_token=" + token);

        HttpPost httppost = new HttpPost(apiURL + "photos.getMarketUploadServer");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("group_id", groupId)
                .addTextBody("main_photo", "1")
                .addTextBody("access_token", token)
                .build();
        httppost.setEntity(httpEntity);
        String response = POSTSender.send(httppost);

        // Парсим JSON и получаем URL
        JsonParser JSONparser = new JsonParser();
        JsonObject mainJSONobject = JSONparser.parse(response).getAsJsonObject();
        JsonObject response2  = mainJSONobject.getAsJsonObject("response");
        JsonObject url2 = response2.getAsJsonObject();
        String postUrl = url2.get("upload_url").getAsString();

        // 2 шаг - отправляем фото POST запросом на полученный URL
        String JSONresponse = POSTImage(postUrl, imageFile);

        // Парсим JSON ответ
        JSONparser = new JsonParser();
        mainJSONobject = JSONparser.parse(JSONresponse).getAsJsonObject();

        String server = mainJSONobject.get("server").getAsString();
        String photo = mainJSONobject.get("photo").getAsString();
        String hash = mainJSONobject.get("hash").getAsString();
        String crop_data = mainJSONobject.get("crop_data").getAsString();
        String crop_hash = mainJSONobject.get("crop_hash").getAsString();

        // 3 шаг - отправляем полученные данные методу photos.saveMarketPhoto
        httppost = new HttpPost(apiURL + "photos.saveMarketPhoto");
        httpEntity = MultipartEntityBuilder.create()
                .addTextBody("group_id", groupId)
                .addTextBody("server", server)
                .addTextBody("photo", photo)
                .addTextBody("hash", hash)
                .addTextBody("crop_data", crop_data)
                .addTextBody("crop_hash", crop_hash)
                .addTextBody("access_token", token)
                .build();
        httppost.setEntity(httpEntity);
        String photoJSONdata = POSTSender.send(httppost);

        // Парсим и возвращаем идентификатор фотографии
        JSONparser = new JsonParser();
        mainJSONobject = JSONparser.parse(photoJSONdata).getAsJsonObject();

        return mainJSONobject.getAsJsonArray("response").get(0).getAsJsonObject().get("pid").getAsInt();

    }

    private static String POSTImage(String url, File imageFile){

        HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", imageFile, ContentType.create("application/octet-stream"), imageFile.getName()).build();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);
        return POSTSender.send(httppost);
    }

    private static String cropName (String name){

        int length = name.length();
        int escapedLength = name.replace("\"","\\\"").length();

        if (escapedLength > 95){
            return name.substring(0, 95-(escapedLength-length))+"...";
        } else {
            return name;
        }

    }

    private static Product updateDbVkPhotoId (Product product){

        System.out.print("Retrieving product \"" + product.getName() + "\" photo ID from VK group... ");

        // Создаём запрос на получение товара
        HttpPost httppost = new HttpPost("https://api.vk.com/method/market.getById");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("access_token", token)
                .addTextBody("item_ids", "-" + groupId + "_" + String.valueOf(product.getVkId()))
                .addTextBody("extended", "1")
                .build();
        httppost.setEntity(httpEntity);

        // Отправляем запрос
        String response = POSTSender.send(httppost);

        CharSequence successMask = "pid";

        if (response != null && response.contains(successMask)) {
            // Парсим id фотографии
            JsonParser JSONparser = new JsonParser();
            JsonObject jsonObject = JSONparser.parse(response).getAsJsonObject();
            jsonObject = jsonObject.getAsJsonArray("response").get(1).getAsJsonObject();
            jsonObject = jsonObject.getAsJsonArray("photos").get(0).getAsJsonObject();
            int vkPhotoId = jsonObject.get("pid").getAsInt();

            System.out.println("success! Photo id = " + vkPhotoId);

            product.setVkPhotoId(vkPhotoId);
            DBWorker.updateProduct(product);

            return product;

        } else {
            System.out.println("fail");
            System.out.println("VK server response: " + response);
            return product;
        }



    }

}