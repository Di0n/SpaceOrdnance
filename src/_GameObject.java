import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class _GameObject extends Body
{
    protected BufferedImage image;
    protected double scale;

    public _GameObject(BufferedImage image, double scale)
    {
        this.image = image;
        this.scale = scale;
    }

    public void draw(Graphics2D g2d)
    {
        if(image == null)
            return;

        AffineTransform tx = new AffineTransform();
        tx.translate(this.getTransform().getTranslationX() * Main.SCALE, this.getTransform().getTranslationY() * Main.SCALE);
        tx.rotate(this.getTransform().getRotation());
        tx.scale(scale, -scale);
        tx.translate(0, 0);

        tx.translate(-image.getWidth()/2, -image.getHeight()/2);
        g2d.drawImage(image, tx, null);
    }
}
