import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductAdder {

    private int emptyPhotoId = 413141791;
    private String groupId = "120338153";
    private String token = null;

    public ProductAdder() {
        // Получаем access token
        this.token = getAccessToken();
    }

    public void add(Product product){

        createProduct(product);

    }


    private String getAccessToken(){

        // Авторизируемся
        WebDriver driver = new FirefoxDriver();
        driver.get("https://oauth.vk.com/authorize?client_id=5437046&display=page&redirect_uri=http://google.com&scope=market,photos&response_type=token&v=5.52");

        WebDriverWait wait = new WebDriverWait(driver, 40);
        wait.until(ExpectedConditions.urlContains("access_token"));
        System.out.println("OK");

        // Получаем URL с токеном
        String url = driver.getCurrentUrl();
        driver.close();

        // Парсим токен
        Pattern p = Pattern.compile("(?<=access_token=).*?(?=&)");
        Matcher m = p.matcher(url);
        m.find();
        String token = m.group(0);

        System.out.println("access token: " + token);

        return token;
    }

    private void createProduct(Product product){

        String productName = product.getName();
        String productDescription = productName + "\n" + product.getLink();
        int categoryId = product.getCategoryId();
        double productPrice = product.getPrice();
        int isDeleted = product.isDeleted();
        String imageLink = product.getImageLink();

        ImageChecker imageChecker = new ImageChecker(imageLink);

        int photoId = addImage(imageChecker.getImageFile());

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
        String JSONProductId = makePOST(httppost);

        System.out.println("Product added: " + JSONProductId);
    }

    private int addImage(File imageFile){

        // 1 шаг - получаем URL для загрузки фото у метода photos.getMarketUploadServer
        String response = makeGet("https://api.vk.com/method/photos.getMarketUploadServer?group_id=120338153&main_photo=1&access_token=" + token);

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
        //System.out.println("Данные для загрузки фото:");
        /*System.out.println("server: " + server +
                "\nphoto: " + photo +
                // "group_id: " + group_id +
                "\nhash: " + hash +
                "\ncrop_data: " + crop_data +

        */

        // 3 шаг - отправляем полученные данные методу photos.saveMarketPhoto
        HttpPost httppost = new HttpPost("https://api.vk.com/method/photos.saveMarketPhoto");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("group_id", groupId)
                .addTextBody("server", server)
                .addTextBody("photo", photo)
                .addTextBody("hash", hash)
                .addTextBody("crop_data", crop_data)
                .addTextBody("crop_hash", crop_hash)
                .addTextBody("access_token", token)
                .build();
        httppost.setEntity(httpEntity);
        String photoJSONdata = makePOST(httppost);

        // Парсим идентификатор фотографии
        JSONparser = new JsonParser();
        mainJSONobject = JSONparser.parse(photoJSONdata).getAsJsonObject();
        int photoId = mainJSONobject.getAsJsonArray("response").get(0).getAsJsonObject().get("pid").getAsInt();

        //System.out.println("Идентификатор фотографии: " + photoId);
        return photoId;
    }

    private String makeGet (String url){

        CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                public String handleResponse(final HttpResponse response) throws IOException {
                    HttpEntity entity = response.getEntity();
                    return EntityUtils.toString(entity);
                }
            };

        try {

            return httpclient.execute(httpget, responseHandler);

        } catch (IOException e){
            System.out.println("Something wrong with GET request.");
            e.printStackTrace();
        }

        return null;
    }

    private String POSTImage(String url, File imageFile){

        HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", imageFile, ContentType.create("application/octet-stream"), imageFile.getName()).build();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);
        return makePOST(httppost);
    }

    private String makePOST(HttpPost httppost){

        CloseableHttpClient httpclient = HttpClients.createDefault();

        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            public String handleResponse(final HttpResponse response) throws IOException {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            }
        };

        try {

            return httpclient.execute(httppost, responseHandler);

        } catch (IOException e){
            System.out.println("Something wrong with POST request.");
            e.printStackTrace();
        }

        return null;
    }

}