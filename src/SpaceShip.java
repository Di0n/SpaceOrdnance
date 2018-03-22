import javafx.scene.control.Button;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpaceShip extends _GameObject
{
    private final int SHOOT_TIMEOUT = 50; // ms
    private int lives;


    public SpaceShip(BufferedImage image, double scale, Vector2 position, int lives)
    {
        super(image, scale);
        this.lives = lives;

        addFixture(Geometry.createRectangle(image.getWidth()*scale, image.getHeight()*scale));
        getTransform().setTranslation(new Vector2(position.x * scale, position.y*scale));
        setMass(MassType.NORMAL);
        //setMass(new Mass(new Vector2(0,0), 20, 10));
        setAngularDamping(5);
    }


    public void update(double deltaTime)
    {

    }

    public void shoot()
    {

    }

    public void explode()
    {

    }

    /*
    public void draw(Graphics2D g2d, double worldScale)
    {
        super.draw(g2d, worldScale); // Teken eerst het ruimteschip
        if (explosionAnimation == null || !explode) return;

        BufferedImage explosion = explosionAnimation[explosionIndex++];
        AffineTransform tx = new AffineTransform();

        tx.translate(this.getTransform().getTranslationX() * worldScale - explosion.getWidth()*scale / 2, this.getTransform().getTranslationY() * worldScale - explosion.getHeight()*scale / 2);
        tx.rotate(Math.toRadians(getTransform().getRotation()));
        tx.scale(scale,scale);
        g2d.drawImage(explosion, tx, null);

        //tx.scale(scale, -scale);

        //tx.translate(-image.getWidth()/2, -image.getHeight()/2);

        //g2d.drawImage(explosion, (int)getTransform().getTranslationX() - explosion.getWidth() / 2, (int)getTransform().getTranslationY() - explosion.getHeight() / 2, null);

        /*long time = System.currentTimeMillis();
        if ((lastExplosionDrawTime - time) > 100) // Test waarde
        {
            g2d.drawImage(explosionAnimation[explosionIndex++], (int)getTransform().getTranslationX(), (int)getTransform().getTranslationY(), null);
            lastExplosionDrawTime = time;
        }
    }*/

    public int getLives()
    {
        return lives;
    }

    public void setLives(int lives)
    {
        this.lives = lives;
    }
}
