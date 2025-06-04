package Animations;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author leo
 */
import org.pushingpixels.radiance.animation.api.Timeline;
import org.pushingpixels.radiance.animation.api.ease.Spline;
import javax.swing.*;
import java.awt.*;
import javax.swing.text.JTextComponent;

public class AnimatorUtils {
    
    private static final  Color bgColor = new Color(19, 23, 24);
    private static final Color visibleColor = new Color(255, 255, 255); 

    public static void animateFadeInFromLeft(JComponent component, Point targetLocation, int duration, int delay) {

        Point startLocation = new Point(targetLocation.x - 300, targetLocation.y);
        component.setLocation(startLocation);
        component.setVisible(true);

        if (component instanceof JTextComponent textComp) {
            textComp.setForeground(new Color(0, 0, 0, 0));
        } else {
            component.setForeground(new Color(component.getForeground().getRed(),
                    component.getForeground().getGreen(),
                    component.getForeground().getBlue(), 0));
        }

        var timeline = Timeline.builder(component)
                .addPropertyToInterpolate("location", startLocation, targetLocation)
                .addPropertyToInterpolate("foreground",bgColor,visibleColor)
                .setEase(new Spline(0f, 1.0f, 0f, 1.0f))
                .setDuration(duration)
                .setInitialDelay(delay)
                .build();
        timeline.play();

    }
    
    public static void animateFadeInFromTop(JComponent component, Point targetLocation, int duration, int delay) {
        
        Point startLocation = new Point(targetLocation.x , targetLocation.y - 100);
        component.setLocation(startLocation);
        component.setVisible(true);

        if (component instanceof JTextComponent textComp) {
            textComp.setForeground(new Color(0, 0, 0, 0));
        } else {
            component.setForeground(new Color(component.getForeground().getRed(),
                    component.getForeground().getGreen(),
                    component.getForeground().getBlue(), 0));
        }

        var timeline = Timeline.builder(component)
                .addPropertyToInterpolate("location", startLocation, targetLocation)
                .addPropertyToInterpolate("foreground",bgColor,visibleColor)
                .setEase(new Spline(0f, 1.0f, 0f, 1.0f))
                .setDuration(duration)
                .setInitialDelay(delay)
                .build();
        timeline.play();

    }

    public static void animateSequentially(JComponent[] components, Point[] targetLocations, int duration, int delayBetween) {
        for (int i = 0; i < components.length; i++) {
            int delay = i * delayBetween;
            animateFadeInFromLeft(components[i], targetLocations[i], duration, delay);
        }
    }

}
