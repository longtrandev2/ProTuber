package org.example.protuber.controller;

import javafx.scene.text.Font;

public class FontController {
    public static void fontLoader(){
        Font.loadFont(FontController.class.getResourceAsStream("/fonts/Poppins-Black.ttf"), 14);
        Font.loadFont(FontController.class.getResourceAsStream("/fonts/Geomanist-Regular.otf"), 14);
        System.out.println("Fonts loaded successfully!"); // Kiểm tra font đã nạp
    }
}
