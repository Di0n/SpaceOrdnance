import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Laser extends _GameObject
{
    private long timeToLive;
    private long creationTime;

    public Laser(BufferedImage image, double scale, long timeToLive)
    {
        super(image, scale);
        addFixture(Geometry.createRectangle(image.getWidth() * scale, image.getHeight() * scale));
        setMass(MassType.NORMAL);
        //setBullet(true);
        this.timeToLive = timeToLive;
        this.creationTime = System.currentTimeMillis();
        //setMass(new Mass(getTransform().getTranslation(), image.getWidth() * scale, 25));
        getFixture(0).setFilter(new CategoryFilter(2,2)); // Laat lazers niet met schip botsen
    }

    public long getTimeToLive()
    {
        return timeToLive;
    }

    public long getCreationTime()
    {
        return creationTime;
    }
}
