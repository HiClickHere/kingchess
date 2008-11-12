/*
 * Utils.java
 *
 * Created on October 30, 2008, 5:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author dong
 */
public class Utils {
    
    /**
     * Darken an input color to a specified percentage.
     * @param color the input color to be darkened
     * @return the darker color
     */
    public static int darkenColor(int color, int percentage) {
        int r = ((color & 0x00FF0000) >> 16),
                g = ((color & 0x0000FF00) >> 8),
                b = (color & 0x000000FF);
        r -= percentage * r / 100;
        g -= percentage * g / 100;
        b -= percentage * b / 100;
        return (((r) << 16) | ((g) << 8) | b);
    }
    
    
    /**
     * Lighten an input color to a specified percentage.
     * @param color the input color to be lightened
     * @return the lighter color
     */
    public static int lightenColor(int color, int percentage) {
        int r = ((color & 0x00FF0000) >> 16),
                g = ((color & 0x0000FF00) >> 8),
                b = (color & 0x000000FF);
        r += percentage * (255 - r) / 100;
        g += percentage * (255 - g) / 100;
        b += percentage * (255 - b) / 100;
        return (((r) << 16) | ((g) << 8) | b);
    }
    
    
}
