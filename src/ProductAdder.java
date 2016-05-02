import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import java.io.File;
import java.nio.charset.Charset;

public class ProductAdder extends Config{

    private String token;
    String response;

    public ProductAdder() {
        // Получаем access token
        this.token = Authorization.getToken();

    }

    public void addProduct(Product product){

        String productName = product.getName();
        String productDescription = productName + "\n" + product.getLink();
        int categoryId = product.getCategoryId();
        double productPrice = product.getPrice();
        int isDeleted = product.isDeleted();
        String imageLink = product.getImageLink();

        // Проверяем фото на валидность и соответствие размеру (не мене 400х400)
        ImageChecker imageChecker = new ImageChecker();
        int photoId = addImage(imageChecker.getImageFile(imageLink));

        // Создаём новый товар
        HttpPost httppost = new HttpPost("https://api.vk.com/method/market.add");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("owner_id", "-" + groupId)
                .addTextBody("access_token", token)
                .addTextBody("name", productName, ContentType.create("text/plain", Charset.forName("UTF-8")))
                .addTextBody("description", productDescription, ContentType.create("text/plain", Charset.forName("UTF-8")))
                .addTextBody("category_id", String.valueOf(categoryId))
                .addTextBody("price", String.valueOf(productPrice))
                .addTextBody("deleted", String.valueOf(isDeleted))
                .addTextBody("main_photo_id", String.valueOf(photoId))
                .build();

        httppost.setEntity(httpEntity);
        String JSONProductId = POSTSender.send(httppost);

        CharSequence successMask = "market_item_id";
        System.out.println(JSONProductId.contains(successMask) ? " - Success" : "Fail");
    }

    private int addImage(File imageFile){

        // 1 шаг - получаем URL для загрузки фото у метода photos.getMarketUploadServer
        //String response = makeGet("https://api.vk.com/method/photos.getMarketUploadServer?group_id=120338153&main_photo=1&access_token=" + token);

        HttpPost httppost = new HttpPost(apiURL + "photos.getMarketUploadServer");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("group_id", groupId)
                .addTextBody("main_photo", "1")
                .addTextBody("access_token", token)
                .build();
        httppost.setEntity(httpEntity);
        response = POSTSender.send(httppost);

        // Парсим JSON и выдираем URL
        JsonParser JSONparser = new JsonParser();
        JsonObject mainJSONobject = JSONparser.parse(response).getAsJsonObject();
        JsonObject response2  = mainJSONobject.getAsJsonObject("response");
        JsonObject url2 = response2.getAsJsonObject();
        String postUrl = url2.get("upload_url").getAsString();

        //DEBUG
        //System.out.println("URL для отправки фото: " + postUrl);

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

        //DEBUG
        /*
        System.out.println("Данные для загрузки фото:");
        System.out.println("server: " + server +
                "\nphoto: " + photo +
                // "group_id: " + group_id +
                "\nhash: " + hash +
                "\ncrop_data: " + crop_data +
                "\ncrop_hash: " + crop_hash);
        */

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

        // Парсим идентификатор фотографии
        JSONparser = new JsonParser();
        mainJSONobject = JSONparser.parse(photoJSONdata).getAsJsonObject();
        int photoId = mainJSONobject.getAsJsonArray("response").get(0).getAsJsonObject().get("pid").getAsInt();

        //System.out.println("Идентификатор фотографии: " + photoId);
        return photoId;
    }

    private String POSTImage(String url, File imageFile){

        HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", imageFile, ContentType.create("application/octet-stream"), imageFile.getName()).build();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);
        return POSTSender.send(httppost);
    }

}