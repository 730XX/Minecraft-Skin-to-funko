/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

/**
 *
 * @author leo
 */
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FileManager {

    public static final String SKIN_PATH = "C:\\Users\\leo\\Desktop\\leo\\l30997\\testInput\\true\\scala.png";
    private static final String TEMPLATE_PATH = "C:\\Users\\leo\\Desktop\\leo\\l30997\\testInput\\true\\molde groover.png";

    public static BufferedImage loadImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar la imagen: " + e.getMessage());
            return null;
        }
    }

    public static BufferedImage loadTemplate() {
        try {
            return ImageIO.read(new File(TEMPLATE_PATH));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el molde: " + e.getMessage());
            return null;
        }
    }

    public static BufferedImage loadDefaultSkin() {
        try {
            return ImageIO.read(new File(SKIN_PATH));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar la skin por defecto: " + e.getMessage());
            return null;
        }
    }

    

}
