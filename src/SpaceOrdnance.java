import framework.FrameRateCounter;
import framework.Game;
import framework.GameKeyListener;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
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
    private FrameRateCounter frameRateCounter;

    // GAME OBJECTS
    private SpaceShip ship;
    private AsteroidSpawner asteroidSpawner;
    private Hud hud;

    // GAME STATE
    private int level;
    private long respawnTime = 500; // MS
    private boolean startGame;

    // GRAPHICS
    //private ArrayList<BufferedImage> asteroidImages;
    private ArrayList<ExplosionAnimation> explosions;
    private BufferedImage[] asteroidImages;

    private BufferedImage[][] explosionImages;
    private BufferedImage background;
    private BufferedImage shipImage;



    // GAME START
    public SpaceOrdnance()
    {
        super("Space Ordnance", 100);
        super.antiAliasing = true;
        super.addKeyboardListener(keyListener = new GameKeyListener());
        world.setGravity(new Vector2(0,0));

        hud = new Hud();
        frameRateCounter = new FrameRateCounter();
        explosions = new ArrayList<>();
        startGame = false;
    }


    @Override
    protected void update(double deltaTime)
    {
        world.update(deltaTime);
        frameRateCounter.update(deltaTime);
        handleUserInput(deltaTime); // Verwerk gebruiker input

        if (asteroidSpawner.finished() && world.getBodyCount() == 1)
            asteroidSpawner.start(++level);

        asteroidSpawner.update(deltaTime, getWidth(), getHeight());


        for (Iterator<ExplosionAnimation> iterator = explosions.iterator(); iterator.hasNext(); )
        {
            ExplosionAnimation explosion = iterator.next();
            if (explosion.finished())
            {
                iterator.remove();
                continue;
            }
            explosion.update(deltaTime);
        }


        List<Body> removeBodies = new ArrayList<>();
        List<Joint> removeJoints = new ArrayList<>();

        for (Body body : world.getBodies())
        {
            GameObject gameObject = (GameObject)body;
            gameObject.update(deltaTime);

            boolean crossedBounds = handleBounds(gameObject);

            if (crossedBounds && !gameObject.getJoints().isEmpty())              // Voor joint requirement, joints moeten verwijderd zijn voordat ze naar de andere kant worden geteleport
                removeJoints.addAll(gameObject.getJoints());

            if (gameObject instanceof Laser)
            {
                Laser laser = (Laser)gameObject;
                if (System.currentTimeMillis() - laser.getCreationTime() > laser.getTimeToLive())
                    removeBodies.add(laser);
            }
            else if (gameObject instanceof Asteroid)
            {
                Asteroid asteroid = (Asteroid)gameObject;

                List<Body> bodies = asteroid.getInContactBodies(false);

                for (Body hitBody : bodies)
                {
                    if (hitBody instanceof Asteroid)
                    {
                        if (asteroid.isSticky() && asteroid.getJoints().size() == 0)
                        {
                            RopeJoint ropeJoint = new RopeJoint(asteroid, hitBody, asteroid.getTransform().getTranslation(), hitBody.getTransform().getTranslation());
                            world.addJoint(ropeJoint);
                        }
                        // Random maken
                       /* if (gameObject.getJoints().size() > 0 || hitBody.getJoints().size() > 0) continue;
                        RopeJoint rj = new RopeJoint(gameObject, hitBody, gameObject.getTransform().getTranslation(), hitBody.getTransform().getTranslation());
                        world.addJoint(rj);*/
                    }
                    else if (hitBody instanceof SpaceShip)
                    {
                        //BufferedImage[] img = explosionImages.get(0);
                        SpaceShip ss = (SpaceShip)hitBody;

                        Vector2 impactDirection = new Vector2(asteroid.getTransform().getTranslationX() - ss.getTransform().getTranslationX(), asteroid.getTransform().getTranslationY() - ss.getTransform().getTranslationY());

                        ExplosionAnimation ea = new ExplosionAnimation(explosionImages[1], new Vector2(ss.getTransform().getTranslationX(), ss.getTransform().getTranslationY()), impactDirection.getDirection(), ss.getScale());
                        ea.start();

                        explosions.add(ea);

                        ship.setLives(ship.getLives()-1);
                        ship.setDestroyed(true);

                        removeBodies.add(asteroid);
                    }
                    else if (hitBody instanceof Laser)
                    {
                        Laser laser = (Laser)hitBody;

                        ExplosionAnimation ea = new ExplosionAnimation(explosionImages[0], new Vector2( asteroid.getTransform().getTranslationX(), asteroid.getTransform().getTranslationY()), asteroid.getTransform().getRotation(),asteroid.getScale() );
                        ea.start();

                        explosions.add(ea);

                        removeBodies.add(laser);
                        removeBodies.add(asteroid);
                    }
                }
            }
        }

        removeBodies.forEach(b -> world.removeBody(b));
        removeJoints.forEach(j -> world.removeJoint(j));

        if (ship.getLives() <= 0)
        {
            final int textSize = 50;
            Hud.TextSettings ts = new Hud().new TextSettings("GAME OVER", 50, new Point2D.Double((getWidth()/2) - (textSize * 2) * 2 , getHeight()/2), "Magneto", Font.BOLD);
            hud.setText(ts, 2000);
            reset();
        }
        else if (ship.isDestroyed())
            respawn();
    }

    /**
     *
     * @param gameObject
     * @return Geeft een boolean terug of het game object over de grens is gegaan
     */
    boolean handleBounds(GameObject gameObject)
    {
        Transform transform = gameObject.getTransform();

        boolean crossedBounds = false;
        if (transform.getTranslationX() + (gameObject.getImage().getWidth() * gameObject.getScale() / 2) < 0)
        {
            crossedBounds = true;
            transform.setTranslation((getWidth() / worldScale) + gameObject.getImage().getWidth() * gameObject.getScale() / 2, transform.getTranslationY());
        }
        else if (transform.getTranslationX() - (gameObject.getImage().getWidth() * gameObject.getScale() / 2) > getWidth() / worldScale)
        {
            crossedBounds = true;
            transform.setTranslation(0 - gameObject.getImage().getWidth() * gameObject.getScale() / 2, transform.getTranslationY());
        }
        if (transform.getTranslationY() + (gameObject.getImage().getHeight() * gameObject.getScale() / 2) < 0)
        {
            crossedBounds = true;
            transform.setTranslation(transform.getTranslationX(), (getHeight() / worldScale) + gameObject.getImage().getHeight() * gameObject.getScale() / 2);
        }
        else if (transform.getTranslationY() - (gameObject.getImage().getHeight() * gameObject.getScale() / 2) > getHeight() / worldScale)
        {
            crossedBounds = true;
            transform.setTranslation(transform.getTranslationX(), 0 - gameObject.getImage().getHeight() * gameObject.getScale() / 2);
        }

        return crossedBounds;
    }

    void handleUserInput(double deltaTime)
    {
        final Vector2 shipRotation = new Vector2(ship.getTransform().getRotation() + Math.PI * 0.5); // Voorkant schip

        if (keyListener.isKeyDown(KeyEvent.VK_UP))
        {
            final double force = 300 * deltaTime;
            Vector2 productForce = shipRotation.product(force);
            //Vector2 p = ship.getWorldCenter().sum(shipRotation.product(-0.9));
            Vector2 linear = ship.getLinearVelocity();
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
            }
        }
        if (keyListener.isKeyPressed(KeyEvent.VK_BACK_SLASH))
        {
            debug = !debug;
        }
        if (keyListener.isKeyPressed(KeyEvent.VK_R))
        {
            reset();
        }
        if (keyListener.isKeyPressed(KeyEvent.VK_U))
        {
            System.out.println(ship.getLinearVelocity());
        }

        keyListener.update();
    }

    @Override
    protected void draw(Graphics2D g2d)
    {
        g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);

        for (Body body : world.getBodies())
            ((GameObject) body).draw(g2d, worldScale);

        for (ExplosionAnimation explosion : explosions)
        {
            explosion.draw(g2d, worldScale);
        }
        if (debug)
        {
            Color clr = g2d.getColor();
            g2d.setColor(Color.YELLOW);
            DebugDraw.draw(g2d,world, worldScale);
        }

        final int textSize = 30;
        Hud.drawTextThisFrame(g2d, "Level "+level, textSize, new Point2D.Double(getWidth()/2, textSize + 10), "Arial", Font.PLAIN);
        Hud.drawTextThisFrame(g2d, "Lives: "+ship.getLives(), textSize, new Point2D.Double(getWidth() - 150, textSize+10), "Arial", Font.PLAIN);
        Hud.drawTextThisFrame(g2d, "FPS: "+Math.round(frameRateCounter.getAverageFramesPerSecond()), textSize, new Point2D.Double(30 , textSize + 10), "Arial", Font.PLAIN);

        hud.draw(g2d);
    }

    void respawn()
    {
        //ship.setPosition(new Vector2(getWidth()/2, getHeight()/2));
        ship.clearForce();
        ship.setLinearVelocity(0,0);
        ship.setAngularVelocity(0.0);
        ship.getTransform().setTranslation(new Vector2((getWidth()/2)/worldScale, (getHeight()/2)/worldScale));
        ship.getTransform().setRotation(Math.toRadians(180));
        ship.setDestroyed(false);
        System.out.println(ship.getLives());
    }
    void reset()
    {
        destroyAllWorldObjects();
        world.addBody(ship);
        level = 1;
        ship.setLives(3);

        asteroidSpawner.start(level);
        respawn();
    }

    void destroyAllWorldObjects()
    {
        for (Body body : world.getBodies())
        {
            GameObject gameObject = (GameObject)body;
            ExplosionAnimation explosionAnimation = new ExplosionAnimation(explosionImages[0], gameObject.getTransform().getTranslation(),
                    gameObject.getTransform().getRotation(), gameObject.getScale());
            explosionAnimation.start();
            explosions.add(explosionAnimation);
        }
        world.removeAllBodiesAndJoints();
    }

    @Override
    protected void loadContent()
    {
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

            final int asteroidBufferSize = 10;
            asteroidImages = new BufferedImage[asteroidBufferSize];
            for (int i = 0; i < asteroidBufferSize; i++)
            {
                System.out.println(String.format("/images/Asteroids/large-asteroids/asteroid (%s).png", i+1));
                asteroidImages[i] = ImageIO.read(getClass().getResource(String.format("/images/Asteroids/large-asteroids/asteroid (%s).png", i+1)));
                //asteroidImages.add(ImageIO.read(getClass().getResource(String.format("/images/Asteroids/large-asteroids/Asteroid-A-09-00%s.png", i))));
            }
            //asteroidImages.add(ImageIO.read(getClass().getResource("/images/Asteroids/large-asteroids/Asteroid-A-09-000.png")));


            explosionImages = new BufferedImage[2][64];


            /* 4 Werkt alleen op laptop?!?*/
            for (int i = 0; i < 2; i++)
            {

                BufferedImage explosionSpriteSheet = ImageIO.read(getClass().getResource(String.format("/images/FX/explosions/explosion_%s.png", i+1)));

                //BufferedImage shipExplosionImages = ImageIO.read(getClass().getResource("/images/FX/explosions/explosion_4.png"));
                for (int j = 0; j < 64; j++)
                {
                    explosionImages[i][j] = explosionSpriteSheet.getSubimage(512 * (j % 8), 512 * (j / 8), 512, 512);
                    // animation[j] = explosionSpriteSheet.getSubimage(512 * (i % 8), 512 * (i / 8), 512, 512);
                }
            }

            laserImage = ImageIO.read(getClass().getResource("/images/FX/projectiles/red_laser.png"));
        }
        catch (IOException ex) { ex.printStackTrace(); }


        ship = new SpaceShip(shipImage, 0.007, 3, laserImage);
        asteroidSpawner = new AsteroidSpawner(world, worldScale, asteroidImages);

        reset();
    }
}
