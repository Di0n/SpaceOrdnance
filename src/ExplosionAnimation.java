import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ExplosionAnimation
{
    private BufferedImage[] explosionImages;
    private Vector2 explosionLocation;
    private double rotation;
    private double scale;
    private boolean explode;
    private int animationIndex;
    private int animationDelay = 1;
    private boolean draw;
    private long lastDrawTime;

    public ExplosionAnimation(BufferedImage[] explosionImages, Vector2 explosionLocation, double rotation, double scale)
    {
        this.explosionImages = explosionImages;
        this.explosionLocation = new Vector2(explosionLocation);
        this.rotation = rotation;
        this.scale = scale;
        this.explode = false;
    }

    public void start()
    {
        explode = true;
        animationIndex = 0;
    }

    private int test = 1;
    public void update(double deltaTime)
    {
        draw = false;
        final long currentTime = System.currentTimeMillis();
        if (explode && animationIndex == explosionImages.length) // Length
            explode = false;
        else if (currentTime - lastDrawTime > animationDelay) // baseren op deltatijd
            draw = true;
    }
    public void draw(Graphics2D g2d, double worldScale)
    {
        if (!explode || !draw) return;

        BufferedImage explosion = explosionImages[animationIndex++];

        AffineTransform tx = new AffineTransform();

        tx.translate(explosionLocation.x * worldScale, explosionLocation.y * worldScale);
        //tx.translate(explosionLocation.getX() * worldScale - explosion.getWidth()*scale / 2, explosionLocation.getY() * worldScale - explosion.getHeight()*scale / 2);
        tx.rotate(rotation);
        tx.scale(scale * worldScale,-scale * worldScale);
        tx.translate(0,0);
        tx.translate(-explosion.getWidth()/2, -explosion.getHeight()/2);

        g2d.drawImage(explosion, tx, null);
        lastDrawTime = System.currentTimeMillis();
    }

    public boolean finished()
    {
        return animationIndex == explosionImages.length;
    }
}
