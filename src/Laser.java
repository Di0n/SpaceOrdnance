import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

import java.awt.image.BufferedImage;

public class Laser extends GameObject
{
    private long timeToLive;
    private long creationTime;
    private int maxTravelDistance = 2000; // 1 pixel is 100 meter

    public Laser(BufferedImage image, double scale, long timeToLive)
    {
        super(image, scale);
        addFixture(Geometry.createRectangle(image.getWidth() * scale, image.getHeight() * scale));
        setMass(MassType.NORMAL);
        //setBullet(true);
        this.timeToLive = timeToLive;
        this.creationTime = System.currentTimeMillis();
        getFixture(0).setFilter(new CategoryFilter(CollisionFilter.COLLISION_LASERS, CollisionFilter.COLLISION_ASTEROIDS)); // Laat lazers niet met schepen en andere lazers botsen
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
