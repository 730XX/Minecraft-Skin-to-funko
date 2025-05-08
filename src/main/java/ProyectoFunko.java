
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class ProyectoFunko extends JFrame {

    private ImageIcon icon = new ImageIcon(
            "C:\\Users\\leo\\Documents\\intellProyects\\MinecrfatSkinFunko\\src\\images\\mitico.png");
    private BufferedImage skinImage;
    private BufferedImage funkoTemplate;
    private JLabel imageLabel;
    private BufferedImage finalImage;

    public ProyectoFunko() {
        // Quita la interfaz de mierda de java
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.getMessage();
        }
        SwingUtilities.invokeLater(this::initUI);
    }

    private void initUI() {
        setTitle("Minecraft Skin to Funko Pop");
        setSize(800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.DARK_GRAY);
        setResizable(false);
        getContentPane().setBackground((new Color(19, 23, 24)));
        setIconImage(icon.getImage());

        JPanel panel = new JPanel();
        JButton loadButton = new JButton("Cargar Skin");
        loadButton.addActionListener(e -> loadSkin());

        JButton saveButton = new JButton("Guardar Funko Pop");
        saveButton.addActionListener(e -> saveFunkoPopImage());

        imageLabel = new JLabel();
        panel.add(loadButton);
        panel.add(saveButton);
        panel.add(imageLabel);

        add(panel);

        try {
            funkoTemplate = ImageIO
                    //.read(new File("C:\\Users\\leo\\Documents\\NetBeansProjects\\MinecraftSkinToFunkoPop\\src\\main\\java\\img\\funkoleo.png"));
                    .read(new File("C:\\Users\\leo\\Desktop\\leo\\l30997\\testInput\\true\\molde groover.png"));
            loadDefaultSkin();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setVisible(true);

        // KeyListener para forzar la actualización
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    System.out.println("He presionado el f5");// Presiona F5 para actualizar
                    processSkin();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        setFocusable(true); // Asegura que el JFrame pueda recibir eventos de teclado
    }

    // Skin predeterminada para ahorrar tiempo
    private void loadDefaultSkin() {
        try {
            skinImage = ImageIO.read(new File("C:\\Users\\leo\\Desktop\\leo\\l30997\\testInput\\true\\scala.png"));
            processSkin();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar la skin default" + e.getMessage());
        }
    }

    private void loadSkin() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image", "png"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPG Image", "jpg"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                skinImage = ImageIO.read(selectedFile);
                if (skinImage.getWidth() != 1920) {
                    JOptionPane.showMessageDialog(null, "Imagen incompatiblle");
                }
                processSkin();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void processSkin() {

        //angulos para rotar imagenes
        double angulo180 = Math.toRadians(180);
        double anguloM180 = Math.toRadians(-180);
        double anguloM90 = Math.toRadians(-90);
        double angulo90 = Math.toRadians(90);

        //17 pixeles es 1 px en alto
        //30 pixeles es 1 px en ancho
        if (skinImage == null || funkoTemplate == null) {
            return;
        }
        // objeto de la imagen final
        finalImage = new BufferedImage(
                funkoTemplate.getWidth(), funkoTemplate.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = finalImage.getGraphics();
        g.drawImage(funkoTemplate, 0, 0, null);

        AffineTransform transform = new AffineTransform();
        AffineTransform transformice = new AffineTransform();
        AffineTransform transformCA = new AffineTransform();
        AffineTransform transformCapucha = new AffineTransform();

        // posicion en pixeles de donde se recorta de la imagen original
        BufferedImage cabezaFront = getSection(241, 136, 236, 134);
        // scalar la imagen
        BufferedImage scaledHead = scaleImage(cabezaFront, 158, 152);
        // posicion en la que se inserta dentro del molde
        g.drawImage(scaledHead, 215, 385, null);

        // cabeza derecha rotada -90
        BufferedImage cabezaDerecha = getSection(480, 136, 236, 134);
        BufferedImage escalarCD = scaleImage(cabezaDerecha, 164, 153);
        transform.rotate(anguloM90, escalarCD.getWidth() / 2, escalarCD.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedCD = op.filter(escalarCD, null);
        g.drawImage(rotatedCD, 367, 227, null);

        // cabeza izquierda rotada 90
        BufferedImage cabezaIzquierda = getSection(0, 136, 236, 134);
        BufferedImage escalarCI = scaleImage(cabezaIzquierda, 160, 158);
        transform.rotate(angulo180, escalarCI.getWidth() / 2, escalarCI.getHeight() / 2);
        AffineTransformOp op2 = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedCI = op2.filter(escalarCI, null);
        g.drawImage(rotatedCI, 58, 227, null);

        // cabeza Arriba
        BufferedImage cabezaArriba = getSection(241, 0, 236, 134);
        BufferedImage escalarCAr = scaleImage(cabezaArriba, 158, 158);
        g.drawImage(escalarCAr, 215, 227, null);

        // cabeza Atras
        BufferedImage cabezaAtras = getSection(720, 135, 236, 134);
        BufferedImage escalarCAt = scaleImage(cabezaAtras, 158, 158);
        //g.drawImage(escalarCAt, 215, 69, null);
        transform.rotate(angulo90, escalarCAt.getWidth() / 2, escalarCAt.getHeight() / 2);
        AffineTransformOp op4 = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedCAt = op4.filter(escalarCAt, null);
        g.drawImage(rotatedCAt, 209, 71, null);

        // cabeza Abajo
        BufferedImage cabezaAbajo = getSection(480, 0, 236, 134);
        BufferedImage escalarCAb = scaleImage(cabezaAbajo, 158, 158);
        transformCA.rotate(anguloM180, escalarCAb.getWidth() / 2, escalarCAb.getHeight() / 2);
        AffineTransformOp op1 = new AffineTransformOp(transformCA, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedCab = op1.filter(escalarCAb, null);
        g.drawImage(rotatedCab, 215, 539, null);

        //pecho lado delantero       
        BufferedImage pecholD = getSection(480, 337, 360, 266);
        BufferedImage escalarPechoLD = scaleImage(pecholD, 154, 126);
        transform.rotate(anguloM90, escalarPechoLD.getWidth() / 2, escalarPechoLD.getHeight() / 2);
        AffineTransformOp op5 = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedPechoLD = op5.filter(escalarPechoLD, null);
        g.drawImage(rotatedPechoLD, 353, 664, null);

        //pecho lado trasero (Espalda)
        BufferedImage pecholT = getSection(810, 337, 390, 266);
        BufferedImage escalarPechoLT = scaleImage(pecholT, 175, 126);
        transformice.rotate(angulo90, escalarPechoLT.getWidth() / 2, escalarPechoLT.getHeight() / 2);
        AffineTransformOp op6 = new AffineTransformOp(transformice, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedPechoLT = op6.filter(escalarPechoLT, null);
        g.drawImage(rotatedPechoLT, 353, 529, null);

        //capucha trasera xd        '
        BufferedImage capucha = getSection(600, 270, 337, 67);
        BufferedImage escalarCapucha = scaleImage(capucha, 137, 59);
        transformCapucha.rotate(anguloM90, escalarCapucha.getWidth() / 2, escalarCapucha.getHeight() / 2);
        AffineTransformOp op7 = new AffineTransformOp(transformCapucha, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedCapucha = op7.filter(escalarCapucha, null);
        g.drawImage(rotatedCapucha, 463, 586, null);

        //=========Seccion brazo izquierdo=========//
        //brazoizquierdo (lado Delantero)       
        BufferedImage brazoizquierdoAdelante = getSection(1080, 876, 120, 204);
        BufferedImage escalarBrazoizquierdoAdelante
                = scaleImage(brazoizquierdoAdelante, 33, 64);
        g.drawImage(escalarBrazoizquierdoAdelante, 522, 90, null);

        //brazoizquierdo(lado Izquierdo)       
        BufferedImage brazoizquierdoIzquierda = getSection(960, 876, 119, 204);
        BufferedImage escalarBrazoizquierdoIzquierda
                = scaleImage(brazoizquierdoIzquierda, 33, 64);
        g.drawImage(escalarBrazoizquierdoIzquierda, 489, 90, null);

        //brazoizquierdo (lado trasero)         
        BufferedImage brazoizquierdoAtras = getSection(1321, 876, 120, 204);
        BufferedImage escalarBrazoizquierdoAtras
                = scaleImage(brazoizquierdoAtras, 33, 64);
        g.drawImage(escalarBrazoizquierdoAtras, 456, 90, null);

        //brazoizquierdo (lado Derecho)         
        BufferedImage brazoizquierdoDerecha = getSection(1200, 876, 120, 204);
        BufferedImage escalarBrazoizquierdoDerecha
                = scaleImage(brazoizquierdoDerecha, 33, 64);
        g.drawImage(escalarBrazoizquierdoDerecha, 423, 90, null);

        //brazoizquierdo (Mano)    
        BufferedImage brazoizquierdoMano = getSection(1200, 809, 120, 68);
        BufferedImage escalarBrazoizquierdoMano
                = scaleImage(brazoizquierdoMano, 33, 33);
        BufferedImage brazoIzquierdoManoEspejo = mirrorImage(escalarBrazoizquierdoMano);
        g.drawImage(brazoIzquierdoManoEspejo, 522, 154, null);

        //brazoizquierdo (Hombro)    
        BufferedImage brazoizquierdoHombro = getSection(1080, 793, 120, 68);
        BufferedImage escalarBrazoizquierdoHombro
                = scaleImage(brazoizquierdoHombro, 33, 33);
        g.drawImage(escalarBrazoizquierdoHombro, 459, 57, null);

        //=========Sección brazo Derecho=========//
         //brazo derecho (lado delantero)       
        BufferedImage brazoDerechoAdelante = getSection(1320, 337, 120, 204);
        BufferedImage escalarBrazoDerechoAdelante
                = scaleImage(brazoDerechoAdelante, 33, 64);
        g.drawImage(escalarBrazoDerechoAdelante, 148, 85, null);

          //brazo derecho (lado derecho)       
        BufferedImage brazoDerechoDerecha = getSection(1440, 337, 120, 204);
        BufferedImage escalarBrazoDerechoDerecha
                = scaleImage(brazoDerechoDerecha, 33, 64);
        g.drawImage(escalarBrazoDerechoDerecha, 49, 85, null);

         //brazo derecho (lado trasero)         
        BufferedImage brazoDerechoAtras = getSection(1560, 337, 120, 204);
        BufferedImage escalarBrazoDerechoAtras
                = scaleImage(brazoDerechoAtras, 33, 64);
        g.drawImage(escalarBrazoDerechoAtras, 82, 85, null);

        //brazo derecho (lado izquierdo)         
        BufferedImage brazoDerechoIzquierda = getSection(1200, 337, 120, 204);
        BufferedImage escalarBrazoDerechoIzquierda
                = scaleImage(brazoDerechoIzquierda, 33, 64);
        g.drawImage(escalarBrazoDerechoIzquierda, 115, 85, null);

        //brazo derecho (Hombro)    
        BufferedImage brazoDerechoMano = getSection(1320, 270, 120, 68);
        BufferedImage escalarBrazoDerechoMano
                = scaleImage(brazoDerechoMano, 34, 33);
        BufferedImage brazoDerechoManoEspejo = mirrorImage(escalarBrazoDerechoMano);
        g.drawImage(brazoDerechoManoEspejo, 85, 52, null);

        //brazo derecho (Mano)    
        BufferedImage brazoDerechoHombro = getSection(1440, 270, 120, 68);
        BufferedImage escalarBrazoDerechoHombro
                = scaleImage(brazoDerechoHombro, 33, 33);
        g.drawImage(escalarBrazoDerechoHombro, 148, 149, null);

        g.dispose();
        // Mostrar la imagen final en la interfaz
        ImageIcon finalIcon = new ImageIcon(finalImage);
        imageLabel.setIcon(finalIcon);
        imageLabel.setText(""); // Remove any text
    }

    private BufferedImage getSection(int x, int y, int width, int height) {
        if (x + width <= skinImage.getWidth() && y + height <= skinImage.getHeight()) {
            return skinImage.getSubimage(x, y, width, height);
        } else {
            System.err.println("Recorte fuera de rango: (" + x + "," + y + "," + width + "," + height + ")");
            return null;
        }
    }

    private BufferedImage scaleImage(BufferedImage src, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(src, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    public static BufferedImage mirrorImage(BufferedImage image) {
        // Escala negativa en X
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        // Traslada para mantener la imagen visible
        tx.translate(-image.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ProyectoFunko();
        });
    }
}
