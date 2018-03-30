import framework.Game;
import framework.GameKeyListener;
import javafx.scene.control.Button;
import org.dyn4j.Listener;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SpaceOrdnance extends Game
{
    public static void main(String[] args)
    {
        /*
        JFrame frame = new JFrame("Space Ordnance");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setContentPane(new Main());
        frame.pack();
        frame.setVisible(true);*/
        new SpaceOrdnance().run();
    }

    // INSTELLINGEN
    private static final int FPS = 144;
    private static final double WORLD_SCALE = 100; //50
    private static final boolean ANTI_ALIASING = true;
    private static final String ASTEROID_IMAGE_RSC_PATH = "/images/Asteroids";

    // DEBUG
    private boolean debug;

    // FRAMEWORK
    ///private World world;
    private GameKeyListener keyListener;

    // GAME OBJECTS
    private SpaceShip ship;
    private ArrayList<Asteroid> asteroids = new ArrayList<>();

    // GAME STATE
    private int level;
    // GRAPHICS
    private ArrayList<BufferedImage> smallAsteroidImages = new ArrayList<>();
    private ArrayList<BufferedImage> mediumAsteroidImages = new ArrayList<>();
    private ArrayList<BufferedImage> largeAsteroidImages = new ArrayList<>();
    private ArrayList<ExplosionAnimation> explosions = new ArrayList<>();

    private BufferedImage[] explosionImages;
    private BufferedImage background;
    private BufferedImage shipImage;

    // GAME START
    public SpaceOrdnance()
    {
        super("Space Ordnance", 100);
        super.antiAliasing = true;
        super.addKeyboardListener(keyListener = new GameKeyListener());
        world.setGravity(new Vector2(0,0));
    }

    @Override
    protected void loadContent()
    {
        loadGraphics();

        try
        {
            ImageIO.read(getClass().getResource("/images/FX/projectiles/yellow-laser.png"));
        }
        ship = new SpaceShip(shipImage, 0.007, 3);
        reset();
    }

    @Override
    protected void update(double deltaTime)
    {
        world.update(deltaTime);

        for (Iterator<ExplosionAnimation> iterator = explosions.iterator(); iterator.hasNext();)
        {
            ExplosionAnimation explosion = iterator.next();
            if (explosion.finished())
            {
                iterator.remove();
                continue;
            }
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

                //ship.setVisible(false);
                ship.setLinearVelocity(0, 0);
                ship.setAngularVelocity(0);
                //ship.getTransform().setTranslation(1920/2 * , 1080/2); // Zet het ruimteschip weer in het midden
                ship.setPosition(new Vector2(getWidth() / 2, getHeight() / 2));
                ship.setLives(ship.getLives()-1);
                System.out.printf("collision ship lives: "+ship.getLives());
            }
        }


        final Vector2 shipRotation = new Vector2(ship.getTransform().getRotation() + Math.PI * 0.5); // Voorkant schip

        if (keyListener.isKeyDown(KeyEvent.VK_UP))
        {
            final double force = 300 * deltaTime;
            Vector2 productForce = shipRotation.product(force);
            //Vector2 p = ship.getWorldCenter().sum(shipRotation.product(-0.9));
            ship.applyForce(productForce);
        }
        if (keyListener.isKeyDown(KeyEvent.VK_DOWN))
        {
            final double force = 300 * deltaTime;
            Vector2 f = shipRotation.product(-force);
            ship.applyForce(f);
        }
        if (keyListener.isKeyDown(KeyEvent.VK_RIGHT))
        {
            final double force = 10 * deltaTime;
            Vector2 f1 = shipRotation.product(force ).left();
            Vector2 f2 = shipRotation.product(force ).right();

            ship.applyImpulse(force);
        }

        if (keyListener.isKeyDown(KeyEvent.VK_LEFT))
        {
            final double force = -10 * deltaTime;
            Vector2 f1 = shipRotation.product(force).right();
            Vector2 f2 = shipRotation.product(force).left();

            ship.applyImpulse(force);
        }

        if (keyListener.isKeyPressed(KeyEvent.VK_P))
        {
            debug = !debug;
        }

        keyListener.update();
    }

    @Override
    protected void draw(Graphics2D g2d)
    {
        g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);

        for (Asteroid asteroid : asteroids)
        {
            asteroid.draw(g2d, worldScale);
        }
        ship.draw(g2d, worldScale);

        for (ExplosionAnimation explosion : explosions)
        {
            explosion.draw(g2d, worldScale);
        }
        if (debug)
        {
             DebugDraw.draw(g2d,world, worldScale);
        }
    }

    private void respawn()
    {
        //ship.setPosition(new Vector2(getWidth()/2, getHeight()/2));
        ship.getTransform().setTranslation(new Vector2((getWidth()/2)/worldScale, (getHeight()/2)/worldScale));
        ship.getTransform().setRotation(Math.toRadians(180));
    }
    private void reset()
    {
        world.removeAllBodiesAndJoints();
        respawn();
        world.addBody(ship);
        level = 1;
    }


    // GRAFISCHE OBJECTEN INLADEN
    private void loadGraphics()
    {
        explosionImages = new BufferedImage[64];
        try
        {
            background = ImageIO.read(getClass().getResource("/images/Backgrounds/bg5.jpg"));
            shipImage = ImageIO.read(getClass().getResource("/images/Ships/pack/1.png"));

            //File asteroidDir = new File(String.valueOf(getClass().getResource("/images/Asteroids")));
            File asteroidDir = new File("D:\\Libraries\\Documents\\GitHub\\SpaceOrdnance\\resources\\images\\Asteroids");
            File[] dirListing = asteroidDir.listFiles();
            for (File dir : dirListing)
            {
                File[] categoryDir = dir.listFiles();
                for (File file : categoryDir)
                {
                    if (dir.getName().equals("small-asteroids"))
                        smallAsteroidImages.add(ImageIO.read(getClass().getResource(String.format("%s/small-asteroids/%s", ASTEROID_IMAGE_RSC_PATH, file.getName()))));
                    else if (dir.getName().equals("medium-asteroids"))
                        mediumAsteroidImages.add(ImageIO.read(getClass().getResource(String.format("%s/medium-asteroids/%s", ASTEROID_IMAGE_RSC_PATH, file.getName()))));
                    else if (dir.getName().equals("large-asteroids"))
                        largeAsteroidImages.add(ImageIO.read(getClass().getResource(String.format("%s/large-asteroids/%s", ASTEROID_IMAGE_RSC_PATH, file.getName()))));
                    break;
                }
            }

            BufferedImage shipExplosionImages = ImageIO.read(getClass().getResource("/images/FX/explosions/explosion_4.png"));
            for (int i = 0; i < 64; i++)
            {
                explosionImages[i] = shipExplosionImages.getSubimage(512 * (i%8), 512 * (i/8), 512, 512);
            }
        }
        catch (IOException ex) { ex.printStackTrace(); }
    }
}
