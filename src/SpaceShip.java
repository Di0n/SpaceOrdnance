import javafx.scene.control.Button;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
        addFixture(Geometry.createRectangle(image.getWidth()-45, image.getHeight()-45));
        getTransform().setTranslation(new Vector2(position));
        setMass(MassType.NORMAL);
        getFixture(0).setRestitution(0.5);
    }

    public void update()
    {

    }

    public void shoot()
    {

    }

    public int getLives()
    {
        return lives;
    }

    public void setLives(int lives)
    {
        this.lives = lives;
    }
}
