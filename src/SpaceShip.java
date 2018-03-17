import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpaceShip
{
    private Point2D position;
    private BufferedImage image;

    public SpaceShip(String imageResource, Point2D position)
    {
        this.position = position;

        try
        {
            image = ImageIO.read(getClass().getResource(imageResource));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void update()
    {

    }
    public void draw(Graphics2D g2d)
    {

    }
    public void setPosition(Point2D position)
    {
        this.position = position;
    }
    public Point2D getPosition()
    {
        return position;
    }
}
