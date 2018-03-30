import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Laser extends _GameObject
{
    public Laser(BufferedImage image, double scale, Vector2 position)
    {
        super(image, scale);
        addFixture(Geometry.createRectangle(image.getWidth() * scale, image.getHeight() * scale));
        getTransform().setTranslation(new Vector2(position));
        setMass(MassType.INFINITE);
        setBullet(true);
    }
}
