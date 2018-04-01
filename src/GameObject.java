import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.activation.ActivationGroup_Stub;

public abstract class GameObject extends Body
{
    protected BufferedImage image;
    protected double scale;
    protected boolean visible;
    protected boolean borderCheck;

    public GameObject(BufferedImage image, double scale)
    {
        this.image = image;
        this.scale = scale;
        this.visible = true;
        this.borderCheck = true;
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }
    public double getScale()
    {
        return scale;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public BufferedImage getImage()
    {
        return image;
    }

    public void draw(Graphics2D g2d, double worldScale)
    {
        if(image == null || !visible)
            return;

        AffineTransform tx = new AffineTransform();
        tx.translate(this.getTransform().getTranslationX() * worldScale, this.getTransform().getTranslationY() * worldScale);
        tx.rotate(this.getTransform().getRotation());
        tx.scale(scale * worldScale, -scale * worldScale);
        tx.translate(0, 0);

        tx.translate(-image.getWidth()/2, -image.getHeight()/2);
        g2d.drawImage(image, tx, null);
    }


    public void update(double deltaTime) {};

    public void setBorderCheck(boolean check)
    {
        borderCheck = check;
    }
    public boolean checkForBorders()
    {
        return borderCheck;
    }
}
