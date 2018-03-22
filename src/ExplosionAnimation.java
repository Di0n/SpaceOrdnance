import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class ExplosionAnimation
{
    private BufferedImage[] explosionImages;
    private Point2D explosionLocation;
    private double scale;
    private boolean explode;
    private int animationIndex;
    private int animationDelay;

    public ExplosionAnimation(BufferedImage[] explosionImages, Point2D explosionLocation, double scale)
    {
        this.explosionImages = explosionImages;
        this.explosionLocation = new Point2D.Double(explosionLocation.getX(), explosionLocation.getY());
        this.scale = scale;
        this.explode = false;
    }

    public void start()
    {
        explode = true;
        animationIndex = 0;
    }

    public void update()
    {
        if (explode && animationIndex == explosionImages.length)
            explode = false;
    }
    public void draw(Graphics2D g2d, double worldScale)
    {
        if (!explode) return;

        BufferedImage explosion = explosionImages[animationIndex++];

        AffineTransform tx = new AffineTransform();

        tx.translate(explosionLocation.getX() / 2 * worldScale, explosionLocation.getY() / 2 * worldScale);
        //tx.translate(explosionLocation.getX() * worldScale - explosion.getWidth()*scale / 2, explosionLocation.getY() * worldScale - explosion.getHeight()*scale / 2);
        //tx.rotate(Math.toRadians(getTransform().getRotation()));
        tx.scale(scale * worldScale,scale * worldScale);

        g2d.drawImage(explosion, tx, null);
    }

    public boolean finished()
    {
        return animationIndex == explosionImages.length;
    }
}
