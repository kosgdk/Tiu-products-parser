import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;

public class ImageChecker {

    private File imageFile = null;


    public ImageChecker(String url){

        System.out.println(url);

        imageFile = new File("temp.jpg");

        try {
            FileUtils.copyURLToFile(new URL(url), imageFile);
        } catch (MalformedURLException e){
            System.out.println("Something wrong with image URL");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("Something wrong with writing image file");
            e.printStackTrace();
        }
    }

    public File getImageFile(){
        return check() ? imageFile : new File("empty.jpg");
    }

    private boolean check() {

        try {
            BufferedImage bImg = ImageIO.read(imageFile);
            int width = bImg.getWidth();
            int height = bImg.getHeight();

            return (width > 400 || height > 400);

        } catch (IOException e){
            System.out.println("Something wrong with reading image file");
            e.printStackTrace();
        }

        return false;
    }


}