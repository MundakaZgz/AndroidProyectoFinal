package com.example.joseaherrero.inventoryapp;

/**
 * Created by Jose A. Herrero on 20/01/2017.
 */

public class Utils {
    public static String formatQuantity(int quantity) { return Integer.toString(quantity); }
    public static String formatPrice(float price) {
        return String.format("$ %.2f", price);
    }
}
