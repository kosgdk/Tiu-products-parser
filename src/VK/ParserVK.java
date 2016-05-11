package vk;

import beans.Product;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.HashMap;

public class ParserVK extends Config {

    private static HashMap<String, Product> productsVK = new HashMap<>();

    static HashMap<String, Product> getProducts (String token){

        HttpPost httppost = new HttpPost(apiURL + "market.get");
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("owner_id", "-" + groupId)
                .addTextBody("access_token", token)
                //.addTextBody("offset", "200")
                .build();
        httppost.setEntity(httpEntity);
        String response = POSTSender.send(httppost);
        System.out.println(response);

        /*
        // Парсим JSON и выдираем URL
        JsonParser JSONparser = new JsonParser();
        JsonObject mainJSONObject = JSONparser.parse(response).getAsJsonObject();
        JsonObject response2  = mainJSONObject.getAsJsonObject("response");
        JsonObject url2 = response2.getAsJsonObject();
        String postUrl = url2.get("upload_url").getAsString();
        */

        return productsVK;
        }

}
