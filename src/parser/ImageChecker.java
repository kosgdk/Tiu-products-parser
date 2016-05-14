package parser;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;

public class ImageChecker {

    private static File imageFile = null;

    public static File getImageFile(String url){
        return check(url) ? imageFile : new File("empty.jpg");
    }

    private static boolean check(String url) {

        imageFile = new File("temp.jpg");

        try {
            FileUtils.copyURLToFile(new URL(url), imageFile);
        } catch (MalformedURLException e){
            System.out.println("Something wrong with image URL");
            return false;
        } catch (IOException e){
            System.out.println("Something wrong with writing image file");
            return false;
        }

        try {
            BufferedImage bImg = ImageIO.read(imageFile);
            int width = bImg.getWidth();
            int height = bImg.getHeight();

            return (width > 400 & height > 400);

        } catch (IOException e){
            System.out.println("Something wrong with reading image file");
            return false;
        }

    }

}