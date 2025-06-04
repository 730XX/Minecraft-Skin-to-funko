
import Animations.AnimatorUtils;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import raven.toast.Notifications;

/**
 * ProyectoFunko es una aplicación Java con interfaz gráfica (GUI) que permite convertir una skin de Minecraft en una figura estilo Funko Pop.
 *
 * <p>
 * El usuario puede:
 * <ul>
 * <li>Cargar una imagen de skin en formato PNG (64x64 píxeles estándar de Minecraft).</li>
 * <li>Visualizar en tiempo real cómo se ve la skin montada en una plantilla de Funko.</li>
 * <li>Guardar la imagen generada como archivo PNG o exportarla en formato PDF.</li>
 * <li>Refrescar manualmente la vista presionando la tecla F5.</li>
 * </ul>
 *
 * <p>
 * Utiliza la librería PDFBox para la exportación en PDF y `BufferedImage` para la manipulación de imágenes.
 *
 * <p>
 * El diseño está enfocado en mantener el estilo pixel-art original mediante técnicas como escalado con "nearest neighbor" y reflejo horizontal para reutilizar partes de la skin.
 *
 * @author Leo
 */
public class ProyectoFunko extends JFrame {

    //private ImageIcon icon = new ImageIcon(
    //      "svg/xtreme.svg");
    FlatSVGIcon inconSVG = new FlatSVGIcon("svg/tail.svg");
    private BufferedImage skinImage;
    private BufferedImage funkoTemplate;
    private JLabel imageLabel;
    private BufferedImage finalImage;
    private AnimatorUtils animatorUtils;

    private JLabel labelJugador;
    private JTextField nombreJugadorField;
    private JButton btnDesdeArchivo;
    private JButton saveButton;
    private JButton btnDesdeNombre;
    private JLabel lblDedicatoria;
    private static int i = 0;

    public ProyectoFunko() {
        initUI();
        Notifications.getInstance().setJFrame(this);
    }

    /**
     * Inicializa la interfaz gráfica del usuario, incluyendo botones, eventos de teclado y carga la plantilla del Funko y la skin predeterminada.
     */
    private void initUI() {
        setTitle("Minecraft Skin To Funko Pop");

        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(inconSVG.getImage());
        Font fontGrande = new Font("Segoe UI", Font.PLAIN, 18); // o la fuente que prefieras
        getContentPane().setBackground(new Color(19, 23, 24));
        setLayout(new BorderLayout());

        // Panel izquierdo directo
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(new Color(19, 23, 24));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 10, 10)); // margen interno
        labelJugador = new JLabel("               Cargar Skin:    ");
        labelJugador.setForeground(Color.WHITE);
        labelJugador.setAlignmentX(Component.LEFT_ALIGNMENT);
        //para animacion
        labelJugador.setVisible(false);
        controlPanel.add(labelJugador);

        nombreJugadorField = new JTextField(15);
        nombreJugadorField.setMaximumSize(new Dimension(300, 30));
        nombreJugadorField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nombreJugadorField.putClientProperty("JTextField.placeholderText", "Ingresa el nombre...");
        //para animaicion
        //nombreJugadorField.setVisible(false);
        controlPanel.add(nombreJugadorField);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 11)));

        btnDesdeNombre = new JButton("Desde jugador");
        btnDesdeNombre.setMaximumSize(new Dimension(300, 40));
        btnDesdeNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        //para animacion
        //btnDesdeNombre.setVisible(false);
        btnDesdeNombre.addActionListener(e -> {
            String nombreJugador = nombreJugadorField.getText().trim();
            if (!nombreJugador.isEmpty()) {
                try {
                    BufferedImage original = SkinFetch.obtenerSkin(nombreJugador);
                    if (original.getWidth() < 1920 || original.getHeight() < 1080) {
                        skinImage = scaleImageNearestNeighbor(original, 1920, 1080);
                    } else if (original.getWidth() == 1920 && original.getHeight() == 1080) {
                        skinImage = original;
                    } else {
                        JOptionPane.showMessageDialog(this, "Imagen incompatible. Debe ser 1920x1080 o menor.");
                        return;
                    }
                    processSkin();
                } catch (IOException ex) {
                    Notifications.getInstance().
                            show(Notifications.Type.ERROR,
                                    Notifications.Location.BOTTOM_RIGHT,
                                    3500, "Error al cargar la skin (Verifica si el nombre esta correcto): ");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor ingresa un nombre de jugador.");
            }
        });

        labelJugador.setFont(fontGrande);
        nombreJugadorField.setFont(fontGrande);
        btnDesdeNombre.setFont(fontGrande);

        controlPanel.add(btnDesdeNombre);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnDesdeArchivo = new JButton("Desde archivo");
        btnDesdeArchivo.setMaximumSize(new Dimension(300, 40));
        btnDesdeArchivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDesdeArchivo.addActionListener(e -> loadSkin());
        //Para animacion
        //btnDesdeArchivo.setVisible(false);
        controlPanel.add(btnDesdeArchivo);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        saveButton = new JButton("Guardar Funko Pop");
        saveButton.setMaximumSize(new Dimension(300, 40));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setBackground(new Color(33, 150, 243));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        //Para animacion
        //saveButton.setVisible(false);
        saveButton.addActionListener(e -> saveFunkoPopImage());
        controlPanel.add(saveButton);
        controlPanel.add(Box.createVerticalGlue());

        lblDedicatoria = new JLabel("@730XX, una idea de l30_997");
        lblDedicatoria.setMaximumSize(new Dimension(300, 40));
        lblDedicatoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDedicatoria.setBackground(new Color(33, 150, 243));
        lblDedicatoria.setForeground(Color.WHITE);
        lblDedicatoria.setFont(fontGrande);
        //para animacion
        //lblDedicatoria.setVisible(false);
        controlPanel.add(lblDedicatoria);

        btnDesdeArchivo.setFont(fontGrande);
        saveButton.setFont(fontGrande);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(19, 23, 24));
        imagePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.NORTH);

        add(controlPanel, BorderLayout.WEST);
        add(imagePanel, BorderLayout.CENTER);

        try {
            funkoTemplate = ImageIO.read(new File("src/main/resources/moldes/molde groover.png"));
            //funkoTemplate = ImageIO.read(new File("moldes/molde groover.png"));
            loadDefaultSkin();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                animateComponents();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    processSkin();
                    initUI();
                    System.out.println("He presionado f5");
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {

                    animateComponents();

                    System.out.println("Animacion : " + i++);
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    public void animateComponents() {
        int delayStep = 0;
        int duration = 550;
        AnimatorUtils.animateFadeInFromLeft(nombreJugadorField, new Point(35, 80), duration, delayStep);
        AnimatorUtils.animateFadeInFromLeft(btnDesdeNombre, new Point(35, 120), duration, delayStep + 40);
        AnimatorUtils.animateFadeInFromLeft(btnDesdeArchivo, new Point(35, 160), duration, delayStep + 80);
        AnimatorUtils.animateFadeInFromLeft(saveButton, new Point(35, 200), duration, delayStep + 120);
        AnimatorUtils.animateFadeInFromLeft(lblDedicatoria, new Point(20, 920), duration, delayStep + 160);
        AnimatorUtils.animateFadeInFromTop(labelJugador, new Point(40, 40), duration, delayStep + 60);
    }

    /**
     * Carga una skin predeterminada desde una ruta fija y genera la imagen Funko. Se utiliza para facilitar pruebas rápidas sin selección manual.
     *
     */
    private void loadDefaultSkin() {
        try {
            //skinImage = ImageIO.read(new File("C:\\Users\\leo\\Desktop\\leo\\l30997\\testInput\\true\\scala.png"));
            skinImage = ImageIO.read(new File("src/main/resources/moldes/scala.png"));
            processSkin();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar la skin default" + e.getMessage());
        }
    }

    /**
     * Abre un diálogo para que el usuario seleccione una imagen PNG de skin de Minecraft, valida su tamaño, la escala si es necesario y la procesa como Funko.
     *
     */
    private void loadSkin() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes (*.png, *.jpg)", "png", "jpg"));

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage original = ImageIO.read(selectedFile);

                // Si la imagen es menor que 1920x1080, escalar con Nearest Neighbor
                if (original.getWidth() < 1920 || original.getHeight() < 1080) {
                    skinImage = scaleImageNearestNeighbor(original, 1920, 1080);
                } else if (original.getWidth() == 1920 && original.getHeight() == 1080) {
                    skinImage = original;
                } else {
                    JOptionPane.showMessageDialog(null, "Imagen incompatible. Debe ser 1920x1080 o menor.");
                    return; // No continuar si es inválida
                }
                processSkin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Guarda la imagen generada del Funko como archivo PNG o PDF, utilizando PDFBox para exportar en formato PDF con tamaño personalizado.
     *
     */
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
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, 3500, "Funko Guardado Correctamente");
                    //JOptionPane.showMessageDialog(this, "Imagen guardada con éxito.");
                } catch (IOException e) {
                    e.printStackTrace();
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, 3500, "Error al Guardar ");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay imagen para guardar.");
        }
    }

    /**
     * Escala una imagen utilizando el algoritmo de interpolación "nearest neighbor", que conserva los píxeles originales, ideal para mantener el estilo pixel-art.
     *
     * @param original imagen original a escalar.
     * @param newWidth ancho nuevo.
     * @param newHeight alto nuevo.
     * @return imagen escalada.
     *
     */
    public static BufferedImage scaleImageNearestNeighbor(BufferedImage original, int newWidth, int newHeight) {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return scaledImage;
    }

    /**
     * Procesa la skin cargada y genera una imagen final combinando partes recortadas de la skin con la plantilla del Funko. Esta imagen es mostrada en pantalla y puede ser exportada.
     *
     */
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
        g.drawImage(rotatedCab, 215, 537, null);

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

        //=========Sección piernas=========//
        //pierna izquierda (lado delantero 1) done      
        BufferedImage piernaIzquierdaAdelante = getSection(600, 876, 120, 204);
        BufferedImage escalarPiernaIzquierdaAdelante
                = scaleImage(piernaIzquierdaAdelante, 33, 65);
        BufferedImage piernaIzquierdaAdelanteRotada = rotateImage(escalarPiernaIzquierdaAdelante, 90);
        BufferedImage piernaIzquierdaAdelanteRotadaMirror = mirrorImage(piernaIzquierdaAdelanteRotada);
        g.drawImage(piernaIzquierdaAdelanteRotadaMirror, 86, 423, null);

        //pierna izquierda (lado derecho 2 Ancho) done   
        BufferedImage piernaIzquierdaDerecha = getSection(720, 876, 120, 204);
        BufferedImage escalarPiernaIzquierdaDerecha
                = scaleImage(piernaIzquierdaDerecha, 57, 65);
        BufferedImage piernaIzquierdaDerechaRotada = rotateImage(escalarPiernaIzquierdaDerecha, 90);
        BufferedImage piernaIzquierdaDerechaRotadaMirror = mirrorImage(piernaIzquierdaDerechaRotada);
        g.drawImage(piernaIzquierdaDerechaRotadaMirror, 86, 455, null);

        //pierna izquierda (lado trasero 3) done         
        BufferedImage piernaIzquierdaAtras = getSection(840, 876, 120, 204);
        BufferedImage escalarPiernaIzquierdaAtras
                = scaleImage(piernaIzquierdaAtras, 33, 65);
        BufferedImage piernaIzquierdaAtrasRotada = rotateImage(escalarPiernaIzquierdaAtras, 90);
        BufferedImage piernaIzquierdaAtrasRotadaMirror = mirrorImage(piernaIzquierdaAtrasRotada);
        g.drawImage(piernaIzquierdaAtrasRotadaMirror, 86, 510, null);

        //pierna izquierda (lado izquierdo 4 Ancho ) done       
        BufferedImage piernaIzquierdaIzquierda = getSection(480, 876, 120, 204);
        BufferedImage escalarPiernaIzquierdaIzquierda
                = scaleImage(piernaIzquierdaIzquierda, 60, 65);
        BufferedImage piernaIzquierdaIzquierdaRotada = rotateImage(escalarPiernaIzquierdaIzquierda, 90);
        BufferedImage piernaIzquierdaIzquierdaRotadaMirror = mirrorImage(piernaIzquierdaIzquierdaRotada);
        g.drawImage(piernaIzquierdaIzquierdaRotadaMirror, 86, 543, null);

        //pierna izquierda (pie)    
        BufferedImage piernaIzquierdaPie = getSection(720, 810, 120, 68);
        BufferedImage escalarPiernaIzquierdaPie
                = scaleImage(piernaIzquierdaPie, 32, 59);
        BufferedImage piernaIzquierdaPieEspejo = mirrorImage(escalarPiernaIzquierdaPie);
        g.drawImage(piernaIzquierdaPieEspejo, 150, 544, null);

        //pierna izquierda (muslo)    
        BufferedImage piernaIzquierdaMuslo = getSection(600, 810, 120, 68);
        BufferedImage escalarPiernaIzquierdaMuslo
                = scaleImage(piernaIzquierdaMuslo, 32, 59);
        BufferedImage escalarPiernaIzquierdaMusloMirror = mirrorImage(escalarPiernaIzquierdaMuslo);

        g.drawImage(escalarPiernaIzquierdaMusloMirror, 55, 544, null);

        //=========Sección pierna derecha=========//
        // pierna derecha (lado delantero 1)
        BufferedImage piernaDerechaAdelante = getSection(120, 337, 120, 204);
        BufferedImage escalarPiernaDerechaAdelante = scaleImage(piernaDerechaAdelante, 33, 65);
        BufferedImage piernaDerechaAdelanteRotada = rotateImage(escalarPiernaDerechaAdelante, 90);
        //BufferedImage piernaDerechaAdelanteRotadaMirror = mirrorImage(piernaDerechaAdelanteRotada);
        g.drawImage(piernaDerechaAdelanteRotada, 86, 785, null);

        // pierna derecha (lado izquierdo 2 Ancho) Ahora acomodando
        BufferedImage piernaDerechaIzquierda = getSection(0, 337, 120, 204);
        BufferedImage escalarPiernaDerechaIzquierda = scaleImage(piernaDerechaIzquierda, 59, 65);
        BufferedImage piernaDerechaIzquierdaRotada = rotateImage(escalarPiernaDerechaIzquierda, 90);
        //BufferedImage piernaDerechaIzquierdaRotadaMirror = mirrorImage(piernaDerechaIzquierdaRotada);
        g.drawImage(piernaDerechaIzquierdaRotada, 86, 726, null);

        // pierna derecha (lado trasero 3) ahora
        BufferedImage piernaDerechaAtras = getSection(360, 337, 120, 204);
        BufferedImage escalarPiernaDerechaAtras = scaleImage(piernaDerechaAtras, 33, 65);
        BufferedImage piernaDerechaAtrasRotada = rotateImage(escalarPiernaDerechaAtras, 90);
        //BufferedImage piernaDerechaAtrasRotadaMirror = mirrorImage(piernaDerechaAtrasRotada);
        g.drawImage(piernaDerechaAtrasRotada, 86, 694, null);

        // pierna derecha (lado derecho 4 Ancho) Ahora
        BufferedImage piernaDerechaDerecha = getSection(240, 337, 120, 204);
        BufferedImage escalarPiernaDerechaDerecha = scaleImage(piernaDerechaDerecha, 60, 65);
        BufferedImage piernaDerechaDerechaRotada = rotateImage(escalarPiernaDerechaDerecha, -90);
        BufferedImage piernaDerechaDerechaRotadaMirror = mirrorImage(piernaDerechaDerechaRotada);
        g.drawImage(piernaDerechaDerechaRotadaMirror, 85, 635, null);

        // pierna derecha (pie)
        BufferedImage piernaDerechaPie = getSection(240, 270, 120, 68);
        BufferedImage escalarPiernaDerechaPie = scaleImage(piernaDerechaPie, 32, 59);
        BufferedImage escalarPiernaDerechaPieRotada = rotateImage(escalarPiernaDerechaPie, 180);
        //BufferedImage piernaDerechaPieEspejo = mirrorImage(escalarPiernaDerechaPie);
        g.drawImage(escalarPiernaDerechaPieRotada, 55, 727, null);

        // pierna derecha (muslo)
        BufferedImage piernaDerechaMuslo = getSection(120, 270, 120, 68);
        BufferedImage escalarPiernaDerechaMuslo = scaleImage(piernaDerechaMuslo, 32, 59);
        BufferedImage escalarPiernaDerechaMusloRotada = rotateImage(escalarPiernaDerechaMuslo, -180);
        //BufferedImage escalarPiernaDerechaMusloMirror = mirrorImage(escalarPiernaDerechaMusloRotada);
        g.drawImage(escalarPiernaDerechaMusloRotada, 150, 727, null);

        g.dispose();
        // Muestra la imagen modificada
        ImageIcon finalIcon = new ImageIcon(finalImage);
        imageLabel.setIcon(finalIcon);
        imageLabel.setText(""); // Remove any text
    }

    /**
     * Extrae una sección rectangular específica de la skin cargada.
     *
     * @param x coordenada X de inicio del recorte.
     * @param y coordenada Y de inicio del recorte.
     * @param width ancho del área a recortar.
     * @param height alto del área a recortar.
     * @return imagen recortada o null si está fuera de los límites.
     *
     */
    private BufferedImage getSection(int x, int y, int width, int height) {
        if (x + width <= skinImage.getWidth() && y + height <= skinImage.getHeight()) {
            return skinImage.getSubimage(x, y, width, height);
        } else {
            System.out.println("Intentando recorte: (" + x + "," + y + "," + width + "," + height + ")");
            System.err.println("Recorte fuera de rango: (" + x + "," + y + "," + width + "," + height + ")");
            return null;
        }
    }

    /**
     * Escala una imagen a un tamaño específico utilizando interpolación estándar.
     *
     * @param src imagen fuente.
     * @param width ancho deseado.
     * @param height alto deseado.
     * @return imagen escalada.
     *
     */
    private BufferedImage scaleImage(BufferedImage src, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(src, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    /**
     * Refleja horizontalmente una imagen, creando un efecto espejo.
     *
     * @param image imagen original.
     * @return imagen reflejada horizontalmente.
     *
     */
    public static BufferedImage mirrorImage(BufferedImage image) {
        // Escala negativa en X (Espejo)
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    /**
     * Rota una imagen alrededor de su centro por un ángulo especificado.
     *
     * @param image imagen original a rotar.
     * @param angle ángulo en grados.
     * @return imagen rotada.
     *
     */
    public static BufferedImage rotateImage(BufferedImage image, double angle) {
        int w = image.getWidth();
        int h = image.getHeight();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));

        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();

        // Ajustar la transformación al centro del nuevo lienzo
        AffineTransform transform = new AffineTransform();
        transform.translate((newWidth - w) / 2, (newHeight - h) / 2);
        transform.translate(w / 2, h / 2);
        transform.rotate(rads);
        transform.translate(-w / 2, -h / 2);

        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotatedImage;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                
                FlatDarkLaf.setup();
                FlatDarkLaf.registerCustomDefaultsSource("RavenTheme");
                //UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialize LaF");
            }
            new ProyectoFunko();
        });
    }
}
