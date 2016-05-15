package vk;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Authorization {

    private static String token = null;

    public static String getToken(){

         if (token == null){

             System.out.print("Retrieving access token... ");
             // Авторизируемся
             WebDriver driver = new FirefoxDriver();
             driver.get("https://oauth.vk.com/authorize?client_id=5437046&display=page&redirect_uri=http://google.com&scope=market,photos&response_type=token&v=5.52");

             WebDriverWait wait = new WebDriverWait(driver, 40);
             wait.until(ExpectedConditions.urlContains("access_token"));

             // Получаем URL с токеном
             String url = driver.getCurrentUrl();
             driver.close();

             // Парсим токен
             Pattern p = Pattern.compile("(?<=access_token=).*?(?=&)");
             Matcher m = p.matcher(url);
             m.find();
             token = m.group(0);
         }

        System.out.println("success!");
        System.out.println("Access token: " + token);
        return token;

     }
}
