/*
 *  File: Renderer.java
 *  Package: pong
 *  Description: The Renderer class.
 */

package brick_breaker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;


public class ConfigRenderer extends JPanel{

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        
        ControlConfig.ctrlConfig.render((Graphics2D) g);  //*only because  Graphics in Java is Graphics2D in default (extend)
    }
    
    
}
