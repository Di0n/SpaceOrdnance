import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
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
        BodyFixture fixture = new BodyFixture(Geometry.createCircle(Math.min((image.getWidth()/2)*scale, (image.getHeight()/2)*scale)));
        fixture.setFilter(new CategoryFilter(CollisionFilter.COLLISION_ASTEROIDS, CollisionFilter.COLLISION_LASERS | CollisionFilter.COLLISION_ASTEROIDS | CollisionFilter.COLLISION_SHIPS));
        fixture.setRestitution(1.0);
        addFixture(fixture);
        setMass(MassType.NORMAL);
    }
    //addFixture(Geometry.createCircle(Math.min((image.getWidth()/2)*scale, (image.getHeight()/2)*scale)));
    //getFixture(0).setFilter(new CategoryFilter(CollisionFilter.COLLISION_ASTEROIDS, CollisionFilter.COLLISION_LASERS | CollisionFilter.COLLISION_ASTEROIDS | CollisionFilter.COLLISION_SHIPS));

    @Override
    public void update(double deltaTime)
    {
        if (!checkForBorders()) return;

    }

    public Size getSize()
    {
        return asteroidSize;
    }
}
