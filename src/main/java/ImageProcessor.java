
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageProcessor {

    public static BufferedImage processSkin(File skinFile) {

        BufferedImage skinImage = FileManager.loadImage(skinFile);
        BufferedImage funkoTemplate = FileManager.loadTemplate();

        if (skinImage == null || funkoTemplate == null) {
            return null;
        }

        // 🔹 Crear la imagen final
        BufferedImage finalImage = new BufferedImage(funkoTemplate.getWidth(), funkoTemplate.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        g.drawImage(funkoTemplate, 0, 0, null);

        // 🔹 Definir ángulos para rotaciones
        double angulo180 = 180;
        double anguloM180 = -180;
        double anguloM90 = -90;
        double angulo90 = 90;

        //=========Sección cabeza=========//
        // Cabeza frente
        BufferedImage cabezaFront = getSection(skinImage, 241, 136, 236, 134);
        BufferedImage escalarCabezaFront = scaleImage(cabezaFront, 158, 152);
        g.drawImage(escalarCabezaFront, 215, 385, null);

        // Cabeza derecha (rotada -90)
        BufferedImage cabezaDerecha = getSection(skinImage, 480, 136, 236, 134);
        BufferedImage escalarCabezaDerecha = scaleImage(cabezaDerecha, 164, 153);
        BufferedImage cabezaDerechaRotada = rotateImage(escalarCabezaDerecha, -90);
        g.drawImage(cabezaDerechaRotada, 367, 227, null);

        // Cabeza izquierda (rotada 180)
        BufferedImage cabezaIzquierda = getSection(skinImage, 0, 136, 236, 134);
        BufferedImage escalarCabezaIzquierda = scaleImage(cabezaIzquierda, 160, 158);
        BufferedImage cabezaIzquierdaRotada = rotateImage(escalarCabezaIzquierda, 180);
        g.drawImage(cabezaIzquierdaRotada, 58, 227, null);

        // Cabeza arriba
        BufferedImage cabezaArriba = getSection(skinImage, 241, 0, 236, 134);
        BufferedImage escalarCabezaArriba = scaleImage(cabezaArriba, 158, 158);
        g.drawImage(escalarCabezaArriba, 215, 227, null);

        // Cabeza atrás (rotada 90)
        BufferedImage cabezaAtras = getSection(skinImage, 720, 135, 236, 134);
        BufferedImage escalarCabezaAtras = scaleImage(cabezaAtras, 158, 158);
        BufferedImage cabezaAtrasRotada = rotateImage(escalarCabezaAtras, 90);
        g.drawImage(cabezaAtrasRotada, 209, 71, null);

        // Cabeza abajo (rotada 180)
        BufferedImage cabezaAbajo = getSection(skinImage, 480, 0, 236, 134);
        BufferedImage escalarCabezaAbajo = scaleImage(cabezaAbajo, 158, 158);
        BufferedImage cabezaAbajoRotada = rotateImage(escalarCabezaAbajo, 180);
        g.drawImage(cabezaAbajoRotada, 215, 539, null);

        //=========Sección torso=========//
        // Pecho delantero (rotado -90)
        BufferedImage pechoDelantero = getSection(skinImage, 480, 337, 360, 266);
        BufferedImage escalarPechoDelantero = scaleImage(pechoDelantero, 154, 126);
        BufferedImage pechoDelanteroRotado = rotateImage(escalarPechoDelantero, -90);
        g.drawImage(pechoDelanteroRotado, 353, 664, null);

        // Espalda (rotada 90)
        BufferedImage espalda = getSection(skinImage, 810, 337, 390, 266);
        BufferedImage escalarEspalda = scaleImage(espalda, 175, 126);
        BufferedImage espaldaRotada = rotateImage(escalarEspalda, 90);
        g.drawImage(espaldaRotada, 353, 529, null);

        // Capucha trasera (rotada -90)
        BufferedImage capucha = getSection(skinImage, 600, 270, 337, 67);
        BufferedImage escalarCapucha = scaleImage(capucha, 137, 59);
        BufferedImage capuchaRotada = rotateImage(escalarCapucha, -90);
        g.drawImage(capuchaRotada, 463, 586, null);

//
//        // Brazo izquierdo - múltiples lados
//        BufferedImage brazoizquierdoAdelante = scaleImage(getSection(1080, 876, 120, 204), 33, 64);
//        BufferedImage brazoizquierdoIzquierda = scaleImage(getSection(960, 876, 119, 204), 33, 64);
//        BufferedImage brazoizquierdoAtras = scaleImage(getSection(1321, 876, 120, 204), 33, 64);
//        BufferedImage brazoizquierdoDerecha = scaleImage(getSection(1200, 876, 120, 204), 33, 64);
//        BufferedImage brazoizquierdoMano = mirrorImage(scaleImage(getSection(1200, 809, 120, 68), 33, 33));
//        BufferedImage brazoizquierdoHombro = scaleImage(getSection(1080, 793, 120, 68), 33, 33);
//
//        g.drawImage(brazoizquierdoAdelante, 522, 90, null);
//        g.drawImage(brazoizquierdoIzquierda, 489, 90, null);
//        g.drawImage(brazoizquierdoAtras, 456, 90, null);
//        g.drawImage(brazoizquierdoDerecha, 423, 90, null);
//        g.drawImage(brazoizquierdoMano, 522, 154, null);
//        g.drawImage(brazoizquierdoHombro, 459, 57, null);
//
//        // Brazo derecho
//        BufferedImage brazoDerechoAdelante = scaleImage(getSection(1320, 337, 120, 204), 33, 64);
//        BufferedImage brazoDerechoDerecha = scaleImage(getSection(1440, 337, 120, 204), 33, 64);
//        BufferedImage brazoDerechoAtras = scaleImage(getSection(1560, 337, 120, 204), 33, 64);
//        BufferedImage brazoDerechoIzquierda = scaleImage(getSection(1200, 337, 120, 204), 33, 64);
//        BufferedImage brazoDerechoMano = mirrorImage(scaleImage(getSection(1320, 270, 120, 68), 34, 33));
//        BufferedImage brazoDerechoHombro = scaleImage(getSection(1440, 270, 120, 68), 33, 33);
//        g.drawImage(brazoDerechoAdelante, 148, 85, null);
//        g.drawImage(brazoDerechoDerecha, 49, 85, null);
//        g.drawImage(brazoDerechoAtras, 82, 85, null);
//        g.drawImage(brazoDerechoIzquierda, 115, 85, null);
//        g.drawImage(brazoDerechoMano, 85, 52, null);
//        g.drawImage(brazoDerechoHombro, 148, 149, null);
        //=========Sección piernas=========//
        // pierna izquierda (lado delantero)
        BufferedImage piernaIzquierdaAdelante = getSection(skinImage, 600, 876, 120, 204);
        BufferedImage escalarPiernaIzquierdaAdelante = scaleImage(piernaIzquierdaAdelante, 33, 64);
        BufferedImage piernaIzquierdaAdelanteRotada = rotateImage(escalarPiernaIzquierdaAdelante, -90);
        g.drawImage(piernaIzquierdaAdelanteRotada, 132, 350, null);

        // pierna izquierda (lado izquierdo)
        BufferedImage piernaIzquierdaIzquierda = getSection(skinImage, 480, 876, 120, 204);
        BufferedImage escalarPiernaIzquierdaIzquierda = scaleImage(piernaIzquierdaIzquierda, 33, 64);
        g.drawImage(escalarPiernaIzquierdaIzquierda, 489, 300, null);

        // pierna izquierda (lado trasero)
        BufferedImage piernaIzquierdaAtras = getSection(skinImage, 840, 876, 120, 204);
        BufferedImage escalarPiernaIzquierdaAtras = scaleImage(piernaIzquierdaAtras, 33, 64);
        g.drawImage(escalarPiernaIzquierdaAtras, 456, 300, null);

        // pierna izquierda (lado derecho)
        BufferedImage piernaIzquierdaDerecha = getSection(skinImage, 720, 876, 120, 204);
        BufferedImage escalarPiernaIzquierdaDerecha = scaleImage(piernaIzquierdaDerecha, 33, 64);
        g.drawImage(escalarPiernaIzquierdaDerecha, 423, 300, null);

        // pierna izquierda (pie)
        BufferedImage piernaIzquierdaPie = getSection(skinImage, 720, 810, 120, 68);
        BufferedImage escalarPiernaIzquierdaPie = scaleImage(piernaIzquierdaPie, 33, 33);
        BufferedImage piernaIzquierdaPieEspejo = mirrorImage(escalarPiernaIzquierdaPie);
        g.drawImage(piernaIzquierdaPieEspejo, 522, 364, null);

        // pierna izquierda (muslo)
        BufferedImage piernaIzquierdaMuslo = getSection(skinImage, 600, 810, 120, 68);
        BufferedImage escalarPiernaIzquierdaMuslo = scaleImage(piernaIzquierdaMuslo, 33, 33);
        g.drawImage(escalarPiernaIzquierdaMuslo, 459, 268, null);
        g.dispose();
        return finalImage;
    }

    private static void drawImage(Graphics2D g, BufferedImage img, int x, int y) {
        if (img != null) {
            g.drawImage(img, x, y, null);
        }
    }

    private static BufferedImage getSection(BufferedImage baseImage, int x, int y, int width, int height) {
        return baseImage.getSubimage(x, y, width, height);
    }

    public static BufferedImage scaleImage(BufferedImage img, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return scaled;
    }

    public static BufferedImage rotateImage(BufferedImage image, double angleDegrees) {
        double rads = Math.toRadians(angleDegrees);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = image.getWidth();
        int h = image.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);
        at.rotate(rads, w / 2.0, h / 2.0);
        g2d.setTransform(at);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return rotatedImage;
    }

    public static BufferedImage mirrorImage(BufferedImage image) {
        // Escala negativa en X (Espejo)
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }
}
