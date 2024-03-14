package org.util;

public class Common {
    public static int getRawTotal(String price){
        return Integer.parseInt(price.replaceAll("[^0-9]", ""));
    }

    public static String getHealth(){
        return "i am alive!";
    }
}