package com.example.joseaherrero.inventoryapp;

import java.util.Locale;

/**
 * Created by Jose A. Herrero on 20/01/2017.
 */

public class Utils {
    public static String formatQuantity(int quantity) { return Integer.toString(quantity); }
    public static String formatPrice(float price) {
        return String.format(Locale.getDefault(), "$ %.2f", price);
    }
}
