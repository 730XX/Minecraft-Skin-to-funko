/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

/**
 *
 * @author leo
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Interfaz extends JFrame {

    private JLabel imageLabel;
    private BufferedImage finalImage;

    public Interfaz() {
        setTitle("Minecraft Skin");
        setSize(800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(19, 23, 24));

        setupUI();
        setupKeyListener();

        // 🔹 Cargar automáticamente la skin y el molde al iniciar
        loadDefaultSkin();

        setVisible(true);
    }

    private void setupUI() {
        JPanel panel = new JPanel();
        JButton loadButton = new JButton("Cargar Skin");
        JButton saveButton = new JButton("Guardar Funko Pop");

        //loadButton.addActionListener(e -> loadSkin());
        saveButton.addActionListener(e -> saveFunkoPopImage());

        imageLabel = new JLabel();
        panel.add(loadButton);
        panel.add(saveButton);
        panel.add(imageLabel);
        add(panel);
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    System.out.println("F5 presionado - Actualizando skin");
                    loadDefaultSkin(); // 🔄 Recargar la skin al presionar F5
                }
            }
        });
        setFocusable(true);
    }

    private void loadDefaultSkin() {
        File defaultSkinFile = new File(FileManager.SKIN_PATH);
        finalImage = ImageProcessor.processSkin(defaultSkinFile);

        if (finalImage != null) {
            imageLabel.setIcon(new ImageIcon(finalImage));
        } else {
            JOptionPane.showMessageDialog(this, "Error al cargar la skin predeterminada.");
        }
    }

    private void saveFunkoPopImage() {
        if (finalImage != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Imagen");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image", "png"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF", "pdf"));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String extension = "";

                // Asegurarse de que la extensión del archivo sea la correcta
                if (fileChooser.getFileFilter().getDescription().equals("PNG Image")) {
                    extension = "png";
                } else if (fileChooser.getFileFilter().getDescription().equals("PDF")) {
                    extension = "pdf";
                }

                // Asignar extensión si no hay una por defecto
                if (!fileToSave.getName().toLowerCase().endsWith("." + extension)) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + "." + extension);
                }

                try {
                    if (extension.equals("pdf")) {
                        // Guardar imagen como PDF usando PDFBox
                        File tempImageFile = File.createTempFile("tempImage", ".png");
                        ImageIO.write(finalImage, "png", tempImageFile);

                        PDDocument document = new PDDocument();

                        // Crear una página con tamaño personalizado (195 × 282 mm)
                        PDRectangle customSize = new PDRectangle(195 * 2.8346f, 282 * 2.8346f); // Convertir mm a puntos (1 mm = 2.8346 puntos)
                        PDPage page = new PDPage(customSize);
                        document.addPage(page);

                        PDImageXObject pdImage = PDImageXObject.createFromFile(tempImageFile.getAbsolutePath(), document);
                        PDPageContentStream contentStream = new PDPageContentStream(document, page);

                        // Ajustar la imagen al tamaño de la página 5 = 62
                        contentStream.drawImage(pdImage, 5, -5, customSize.getWidth(), customSize.getHeight());
                        contentStream.close();

                        document.save(fileToSave);
                        document.close();

                        // Borrar el archivo temporal
                        tempImageFile.delete();
                    } else {
                        // Guardar imagen como PNG
                        ImageIO.write(finalImage, extension, fileToSave);
                    }
                    JOptionPane.showMessageDialog(this, "Imagen guardada con éxito.");
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al guardar la imagen.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay imagen para guardar.");
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Interfaz::new);
    }
}

