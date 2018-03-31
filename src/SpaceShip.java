import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.awt.image.BufferedImage;

public class SpaceShip extends GameObject
{
    private final long SHOOT_TIMEOUT = 50; // ms
    private long lastShotFired;
    private int lives;
    private double maxSpeed = 10;
    private BufferedImage laserTexture;
    private boolean destroyed;
    private long destroyTime;


    public SpaceShip(BufferedImage image, double scale, int lives, BufferedImage laserTexture)
    {
        super(image, scale);
        this.lives = lives;
        this.lastShotFired = 0;
        this.laserTexture = laserTexture;
        this.destroyed = false;
        this.destroyTime = 0;

        addFixture(Geometry.createRectangle(image.getWidth()*scale, image.getHeight()*scale));
        setMass(MassType.NORMAL);
        setAngularDamping(5);
        getFixture(0).setFilter(new CategoryFilter(CollisionFilter.COLLISION_SHIPS, CollisionFilter.COLLISION_ASTEROIDS | CollisionFilter.COLLISION_SHIPS)); // Laat schepen niet op lazers botsen
    }

    @Override
    public void update(double deltaTime)
    {
        if (this.getLinearVelocity().getMagnitude() > maxSpeed) // Limiteer snelheid
            this.setLinearVelocity(this.getLinearVelocity().getNormalized().multiply(maxSpeed));
    }

    public Laser shoot()
    {
        final double shipFront = this.getTransform().getRotation() + Math.PI * 0.5;  // Voorkant schip
        final Vector2 shipRotation = new Vector2(shipFront);
        Laser laser = new Laser(laserTexture, 0.009, 1000);

        laser.getTransform().setTranslation(getTransform().getTranslation().copy());

        laser.getTransform().setRotation(shipFront);
        final double force = 80;
        Vector2 forceVec = shipRotation.product(force);
        laser.applyForce(forceVec);
        lastShotFired = System.currentTimeMillis();
        return laser;
    }

    public void setDestroyed(boolean destroyed)
    {
        this.destroyed = destroyed;
        destroyTime = destroyed ? System.currentTimeMillis() : 0;
    }
    public boolean isDestroyed()
    {
        return destroyed;
    }
    public long getDestroyTime()
    {
        return destroyTime;
    }

    public long getLastShotFiredTime()
    {
        return lastShotFired;
    }

    public int getLives()
    {
        return lives;
    }

    public void setLives(int lives)
    {
        this.lives = lives;
    }

    public double getMaxSpeed()
    {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed)
    {
        this.maxSpeed = maxSpeed;
    }


    /*
    public void draw(Graphics2D g2d, double worldScale)
    {
        super.draw(g2d, worldScale); // Teken eerst het ruimteschip
        if (explosionAnimation == null || !explode) return;

        BufferedImage explosion = explosionAnimation[explosionIndex++];
        AffineTransform tx = new AffineTransform();

        tx.translate(this.getTransform().getTranslationX() * worldScale - explosion.getWidth()*scale / 2, this.getTransform().getTranslationY() * worldScale - explosion.getHeight()*scale / 2);
        tx.rotate(Math.toRadians(getTransform().getRotation()));
        tx.scale(scale,scale);
        g2d.drawImage(explosion, tx, null);

        //tx.scale(scale, -scale);

        //tx.translate(-image.getWidth()/2, -image.getHeight()/2);

        //g2d.drawImage(explosion, (int)getTransform().getTranslationX() - explosion.getWidth() / 2, (int)getTransform().getTranslationY() - explosion.getHeight() / 2, null);

        /*long time = System.currentTimeMillis();
        if ((lastExplosionDrawTime - time) > 100) // Test waarde
        {
            g2d.drawImage(explosionAnimation[explosionIndex++], (int)getTransform().getTranslationX(), (int)getTransform().getTranslationY(), null);
            lastExplosionDrawTime = time;
        }
    }*/
}
