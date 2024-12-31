package utils;

import java.util.Random;

public class utils {
    public static String generateRandomString(String url){
        String SALTCHARS = "asdfghjkklqwertyuiopzxcvbnmnQWERTYYUIOPZXCVBNNMASDFGHJKL";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 7) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
