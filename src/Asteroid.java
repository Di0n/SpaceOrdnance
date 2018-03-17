import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.awt.image.BufferedImage;

public class Asteroid extends _GameObject
{
    public enum Size
    {
        SMALL,
        MEDIUM,
        LARGE
    }
    public Asteroid(BufferedImage image, double scale, Vector2 position, Size size)
    {
        super(image, scale);
        addFixture(Geometry.createCircle(Math.max(image.getWidth() - 65, image.getHeight()-65)));
        getTransform().setTranslation(new Vector2(position));
        setMass(MassType.NORMAL);

        switch (size)
        {
            case SMALL:
                setMass(new Mass(new Vector2(position), 10, 5));
                break;
            case MEDIUM:
                setMass(new Mass(new Vector2(position), 30, 15));
                break;
            case LARGE:
                setMass(new Mass(new Vector2(position), 5000, 25));
                break;
        }
    }
}
