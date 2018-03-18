import javafx.scene.control.Button;
import org.dyn4j.Listener;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
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
        frame.pack();
        frame.setVisible(true);
    }


    private static final int FPS = 60;
    private static final double WORLD_SCALE = 1;
    private static final boolean ANTI_ALIASING = true;

    private JCheckBox showDebug;

    private BufferedImage background;
    private BufferedImage asteroidImage;
    private World world;

    private SpaceShip ship;

    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    private ArrayList<ExplosionAnimation> explosions = new ArrayList<>();
    BufferedImage[] explosionImages;


    public Main()
    {
        add(showDebug = new JCheckBox("Debug"));
        setFocusable(true);

        world = new World();
        world.setGravity(new Vector2(0, 0));

        //double aspectRatio = background.getWidth() / (double)background.getHeight();

        BufferedImage shipImage = null;
        explosionImages = new BufferedImage[64];
        try
        {
            asteroidImage = ImageIO.read(getClass().getResource("/images/Asteroids/large-asteroids/Asteroid-A-09-000.png"));
            background = ImageIO.read(getClass().getResource("/images/Backgrounds/bg1.jpg"));
            shipImage = ImageIO.read(getClass().getResource("/images/Ships/pack/1.png"));

            BufferedImage shipExplosionImages = ImageIO.read(getClass().getResource("/images/FX/explosions/explosion_4.png"));
            for (int i = 0; i < 64; i++)
            {
                explosionImages[i] = shipExplosionImages.getSubimage(512 * (i%8), 512 * (i/8), 512, 512);
            }
        }
        catch (IOException ex) { ex.printStackTrace(); }

        Asteroid ast = new Asteroid(asteroidImage, 1, new Vector2(800, 1440/2), Asteroid.Size.LARGE);
        world.addBody(ast);
        asteroids.add(ast);

        ship = new SpaceShip(shipImage, 0.7, new Vector2(2560/2, 1440/2), 3);
        ship.getTransform().setRotation(Math.toRadians(90));

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
        ship.update();
        for (ExplosionAnimation explosion : explosions)
        {
            explosion.update();
        }
        for (Asteroid asteroid : asteroids)
        {
            if (ship.isInContact(asteroid))
            {
                ExplosionAnimation ea = new ExplosionAnimation(explosionImages, new Point2D.Double(ship.getTransform().getTranslationX(),
                        ship.getTransform().getTranslationY()), ship.getScale());
                ea.start();
                explosions.add(ea);

                ship.setVisible(false);
                ship.setLinearVelocity(0, 0);
                ship.getTransform().setTranslation(getWidth()/2, getHeight()/2); // Zet het ruimteschip weer in het midden
                ship.setLives(ship.getLives()-1);
                System.out.printf("collision ship lives: "+ship.getLives());
            }
        }

        repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        if (ANTI_ALIASING)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);

        for (Asteroid asteroid : asteroids)
        {
            asteroid.draw(g2d, WORLD_SCALE);
        }
        ship.draw(g2d, WORLD_SCALE);

        for (ExplosionAnimation explosion : explosions)
        {
            explosion.draw(g2d, WORLD_SCALE);
        }
        if (showDebug.isSelected())
        {
            DebugDraw.draw(g2d,world, WORLD_SCALE);
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        //System.out.println(Math.toDegrees(ship.getTransform().getRotation()));
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            Vector2 velocity = ship.getLinearVelocity();
            double rotation = ship.getTransform().getRotation();
            
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {

        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {

        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {

        }

    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        System.out.println("force");
        ship.applyForce(new Vector2(-50000000, 0));

        //ship.getTransform().setRotation(Math.toRadians(-20));
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

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
