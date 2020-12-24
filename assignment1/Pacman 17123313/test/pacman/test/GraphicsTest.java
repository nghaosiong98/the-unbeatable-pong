// 
// Decompiled by Procyon v0.5.36
// 

package pacman.test;

import java.awt.Dimension;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JComponent;

public class GraphicsTest extends JComponent
{
    public static void main(final String[] args) {
        final JFrame frame = new JFrame("Graphics Test");
        frame.add(new GraphicsTest());
        frame.setDefaultCloseOperation(3);
        frame.pack();
        frame.setVisible(true);
    }
    
    @Override
    protected void paintComponent(final Graphics g) {
        final BufferedImage image = new BufferedImage(800, 600, 6);
        final Graphics2D other = (Graphics2D)image.getGraphics();
        other.setColor(Color.CYAN);
        other.fillRect(0, 0, 800, 600);
        final BufferedImage cutout = new BufferedImage(800, 600, 6);
        final Graphics2D cut = (Graphics2D)image.getGraphics();
        cut.setColor(Color.BLACK);
        cut.setColor(Color.WHITE);
        cut.fillRect(0, 350, 800, 100);
        final AlphaComposite ac = AlphaComposite.getInstance(1);
        other.setComposite(ac);
        other.drawImage(cutout, 0, 0, 800, 600, null);
        g.drawImage(image, 0, 0, 800, 600, null);
        super.paintComponent(g);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
}
