import framework.Game;
import framework.GameKeyListener;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpaceOrdnance extends Game
{
    public static void main(String[] args)
    {
        new SpaceOrdnance().run();
    }

    // INSTELLINGEN
    private static final int FPS = 144;
    private static final String ASTEROID_IMAGE_RSC_PATH = "/images/Asteroids";
    private static final int FIRE_TIMEOUT = 500;

    // DEBUG
    private boolean debug;

    // FRAMEWORK
    ///private World world;
    private GameKeyListener keyListener;

    // GAME OBJECTS
    private SpaceShip ship;
    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    private ArrayList<Laser> lasers = new ArrayList<>();

    // GAME STATE
    private int level;
    // GRAPHICS
    private ArrayList<BufferedImage> smallAsteroidImages = new ArrayList<>();
    private ArrayList<BufferedImage> largeAsteroidImages = new ArrayList<>();
    private ArrayList<ExplosionAnimation> explosions = new ArrayList<>();

    private BufferedImage[][] explosionImages;
    private BufferedImage background;
    private BufferedImage shipImage;
    private BufferedImage bbb[];

    // GAME START
    public SpaceOrdnance()
    {
        super("Space Ordnance", 100);
        super.antiAliasing = true;
        super.addKeyboardListener(keyListener = new GameKeyListener());
        world.setGravity(new Vector2(0,0));
    }

    @Override
    protected void update(double deltaTime)
    {
        world.update(deltaTime);

        List<Body> removeList = new ArrayList<>();
        for (Body body : world.getBodies())
        {
            GameObject gameObject = (GameObject)body;
            Transform transform = gameObject.getTransform();

            if (transform.getTranslationX() + (gameObject.getImage().getWidth()*gameObject.getScale() / 2) < 0 )
                transform.setTranslation(getWidth()/worldScale, transform.getTranslationY());
            else if (transform.getTranslationX() - (gameObject.getImage().getWidth() * gameObject.getScale() / 2)  > getWidth() / worldScale)
                transform.setTranslation(0, transform.getTranslationY());
            if (transform.getTranslationY() + (gameObject.getImage().getHeight() * gameObject.getScale() / 2) < 0)
                transform.setTranslation(transform.getTranslationX(), getHeight() / worldScale);
            else if (transform.getTranslationY() - (gameObject.getImage().getHeight() * gameObject.getScale() / 2) > getHeight() / worldScale)
                transform.setTranslation(transform.getTranslationX(), 0);

            if (gameObject instanceof Asteroid)
            {
                Asteroid asteroid = (Asteroid)gameObject;

                List<Body> bodies = asteroid.getInContactBodies(false);

                for (Body hitBody : bodies)
                {
                    if (hitBody instanceof SpaceShip)
                    {
                        BufferedImage[] img = explosionImages[0];
                        ExplosionAnimation ea = new ExplosionAnimation(img, ship.getTransform().getTranslation(), ship.getScale());
                        ea.start();
                        explosions.add(ea);
                        ship.setLives(ship.getLives()-1);
                        respawn();
                    }
                    else if (hitBody instanceof Laser)
                    {
                        Laser laser = (Laser)hitBody;
                        ExplosionAnimation ea = new ExplosionAnimation(bbb, new Vector2( asteroid.getTransform().getTranslationX(), ship.getTransform().getTranslationY()), asteroid.getScale());
                        ea.start();
                        explosions.add(ea);
                        lasers.remove(laser);
                        asteroids.remove(asteroid);

                        removeList.add(laser);
                        removeList.add(asteroid);
                    }
                }
            }
        }

        removeList.forEach(b -> world.removeBody(b));

        for (Iterator<ExplosionAnimation> iterator = explosions.iterator(); iterator.hasNext(); )
        {
            ExplosionAnimation explosion = iterator.next();
            if (explosion.finished())
            {
                iterator.remove();
                continue;
            }
            explosion.update();
        }

        for (Iterator<Laser> iterator = lasers.iterator(); iterator.hasNext(); )
        {
            Laser laser = iterator.next();
            if (System.currentTimeMillis() - laser.getCreationTime() > laser.getTimeToLive())
            {
                world.removeBody(laser);
                iterator.remove();
                continue;
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
            final double force = 5 * deltaTime;
            Vector2 f1 = shipRotation.product(force ).left();
            Vector2 f2 = shipRotation.product(force ).right();

            ship.applyImpulse(force);
        }

        if (keyListener.isKeyDown(KeyEvent.VK_LEFT))
        {
            final double force = -5 * deltaTime;
            Vector2 f1 = shipRotation.product(force).right();
            Vector2 f2 = shipRotation.product(force).left();

            ship.applyImpulse(force);
        }

        if (keyListener.isKeyDown(KeyEvent.VK_SPACE))
        {
            if ((System.currentTimeMillis() - ship.getLastShotFiredTime()) >= FIRE_TIMEOUT)
            {
                Laser laser = ship.shoot();
                world.addBody(laser);
                lasers.add(laser);
            }
        }
        if (keyListener.isKeyPressed(KeyEvent.VK_D))
        {
            debug = !debug;
        }
        if (keyListener.isKeyPressed(KeyEvent.VK_R))
        {
            respawn();
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

        for (Laser laser : lasers)
        {
            laser.draw(g2d, worldScale);
        }
        ship.draw(g2d, worldScale);



        for (ExplosionAnimation explosion : explosions)
        {
            explosion.draw(g2d, worldScale);
        }
        if (debug)
        {
            Color clr = g2d.getColor();
            g2d.setColor(Color.YELLOW);
            DebugDraw.draw(g2d,world, worldScale);
            g2d.setColor(clr);
        }
    }

    private void respawn()
    {
        //ship.setPosition(new Vector2(getWidth()/2, getHeight()/2));
        ship.clearForce();
        ship.setLinearVelocity(0,0);
        ship.getTransform().setTranslation(new Vector2((getWidth()/2)/worldScale, (getHeight()/2)/worldScale));
        ship.getTransform().setRotation(Math.toRadians(180));
    }
    private void reset()
    {
        world.removeAllBodiesAndJoints();
        Asteroid asteroid = new Asteroid(largeAsteroidImages.get(0), 0.015, Asteroid.Size.LARGE);
        asteroid.getTransform().setTranslation((getWidth() / 3)/worldScale, (getHeight() / 2)/worldScale);


        world.addBody(asteroid);
        asteroids.add(asteroid);
        respawn();
        world.addBody(ship);
        level = 1;
    }

    @Override
    protected void loadContent()
    {
        explosionImages = new BufferedImage[4][64];
        BufferedImage laserImage = null;

        try
        {
            background = ImageIO.read(getClass().getResource("/images/Backgrounds/bg5.jpg"));
            shipImage = ImageIO.read(getClass().getResource("/images/Ships/pack/1.png"));

            //File asteroidDir = new File(String.valueOf(getClass().getResource("/images/Asteroids")));

            /*File asteroidDir = new File("D:\\Libraries\\Documents\\GitHub\\SpaceOrdnance\\resources\\images\\Asteroids");
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
            }*/
            largeAsteroidImages.add(ImageIO.read(getClass().getResource("/images/Asteroids/large-asteroids/Asteroid-A-09-000.png")));



            for (int i = 0; i < 4; i++)
            {
                BufferedImage shipExplosionImages = ImageIO.read(getClass().getResource(String.format("/images/FX/explosions/explosion_%s.png", i+1)));
                //BufferedImage shipExplosionImages = ImageIO.read(getClass().getResource("/images/FX/explosions/explosion_4.png"));
                for (int j = 0; j < 64; j++)
                {
                    explosionImages[i][j] = shipExplosionImages.getSubimage(512 * (i % 8), 512 * (i / 8), 512, 512);
                }
            }
            bbb = new BufferedImage[64];
            BufferedImage shipExplosionImages = ImageIO.read(getClass().getResource("/images/FX/explosions/explosion_4.png"));
            for (int j = 0; j < 64; j++)
            {
                bbb[j] = shipExplosionImages.getSubimage(512 * (j % 8), 512 * (j / 8), 512, 512);
            }

            laserImage = ImageIO.read(getClass().getResource("/images/FX/projectiles/red_laser.png"));
        }
        catch (IOException ex) { ex.printStackTrace(); }


        ship = new SpaceShip(shipImage, 0.007, 3, laserImage);

        reset();
    }
}
