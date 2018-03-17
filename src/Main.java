import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends JPanel implements ActionListener, KeyListener, MouseListener
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Space Ordnance");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setContentPane(new Main());
        frame.setVisible(true);
    }

    public static final double SCALE = 1;
    public static final int FPS = 60;
    public static final boolean ANTI_ALIASING = true;

    private JCheckBox showDebug;

    private BufferedImage background;
    private BufferedImage asteroidImage;
    private World world;

    private SpaceShip ship;


    public Main()
    {
        add(showDebug = new JCheckBox("Debug"));

        world = new World();
        world.setGravity(new Vector2(0, 0));

        //double aspectRatio = background.getWidth() / (double)background.getHeight();

        BufferedImage shipImage = null;
        try
        {
            asteroidImage = ImageIO.read(getClass().getResource("/images/Asteroids/large-asteroids/Asteroid-A-09-000.png"));
            background = ImageIO.read(getClass().getResource("/images/Backgrounds/bg1.jpg"));
            shipImage = ImageIO.read(getClass().getResource("/images/Ships/pack/1.png"));
        }
        catch (IOException ex) { ex.printStackTrace(); }


        Asteroid ast = new Asteroid(asteroidImage, 1, new Vector2(800, 1440/2), Asteroid.Size.LARGE);
        world.addBody(ast);
        ship = new SpaceShip(shipImage, 0.7, new Vector2(2560/2, 1440/2), 3);
        world.addBody(ship);

        addKeyListener(this);
        addMouseListener(this);
        lastTime = System.nanoTime();
        new Timer(1000/60, this).start();
    }

    private void reset()
    {

    }

    private long lastTime;
    @Override
    public void actionPerformed(ActionEvent e)
    {
        long time = System.nanoTime();
        double deltaTime = ( time - lastTime ) / 1000000000.0;
        lastTime = time;

        world.update(deltaTime);

        repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        if (ANTI_ALIASING)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);

        for (Body b : world.getBodies())
        {
            _GameObject gameObject = (_GameObject)b;
            gameObject.draw(g2d);
        }

        if (showDebug.isSelected())
        {
            DebugDraw.draw(g2d,world, 1);
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {

    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        System.out.println("force");
        ship.applyForce(new Vector2(-50000000, 0));
    }

    @Override
    public void mousePressed(MouseEvent e)
    {

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }
}
