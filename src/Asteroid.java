import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

import java.awt.image.BufferedImage;

public class Asteroid extends GameObject
{
    public enum Size
    {
        SMALL,
        MEDIUM,
        LARGE
    }

    private Size asteroidSize;

    public Asteroid(BufferedImage image, double scale, Size asteroidSize)
    {
        super(image, scale);
        this.asteroidSize = asteroidSize;
        addFixture(Geometry.createCircle(Math.max((image.getWidth()/2)*scale, (image.getHeight()/2)*scale)));
        setMass(MassType.NORMAL);
        getFixture(0).setFilter(new CategoryFilter(CollisionFilter.COLLISION_ASTEROIDS, CollisionFilter.COLLISION_LASERS | CollisionFilter.COLLISION_ASTEROIDS | CollisionFilter.COLLISION_SHIPS));
    }
}
