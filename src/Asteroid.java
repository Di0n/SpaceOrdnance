import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Asteroid extends _GameObject
{
    public enum Size
    {
        SMALL,
        MEDIUM,
        LARGE
    }

    private Size asteroidSize;

    public Asteroid(BufferedImage image, double scale, Vector2 position, Size size)
    {
        super(image, scale);
        asteroidSize = size;

        addFixture(Geometry.createCircle(Math.max((image.getWidth()/2)*scale, (image.getHeight()/2)*scale)));
        getTransform().setTranslation(new Vector2(position.x * scale, position.y * scale));
        setMass(MassType.NORMAL);

        /*switch (size)
        {
            case SMALL:
                setMass(new Mass(new Vector2(position.x*scale, position.y*scale), 150, 25));
                break;
            case MEDIUM:
                setMass(new Mass(new Vector2(position.x*scale, position.y*scale), 300, 15));
                break;
            case LARGE:
                setMass(new Mass(new Vector2(position.x*scale, position.y*scale), 600, 5));
                break;
        }*/
    }

}
