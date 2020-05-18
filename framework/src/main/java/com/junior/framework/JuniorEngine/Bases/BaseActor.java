package com.junior.framework.JuniorEngine.Bases;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

/**
 * Extends functionality of the LibGDX Actor class.
 * by adding support for textures/animation, 
 * collision polygons, movement, world boundaries, and camera scrolling. 
 * Most game objects should extend this class; lists of extensions can be retrieved by stage and class name.
 * @see #Actor
 * @author Lee Stemkoski
 */
public class BaseActor extends Group
{

    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private boolean animationPaused;

    private Vector2 velocityVec;
    private Vector2 accelerationVec;
    private float acceleration;
    private float maxSpeed;
    private float deceleration;

    private Polygon boundaryPolygon;

    // stores size of game world for all actors
    private static Rectangle worldBounds;
    
    public BaseActor(float x, float y, Stage s)
    {
        // call constructor from Actor class
        super();

        // perform additional initialization tasks
        setPosition(x,y);
        s.addActor(this);

        // initialize animation data
        animation = null;
        elapsedTime = 0;
        animationPaused = false;

        // initialize physics data
        velocityVec = new Vector2(0,0);
        accelerationVec = new Vector2(0,0);
        acceleration = 0;
        maxSpeed = 1000;
        deceleration = 0;

        boundaryPolygon = null;
    }

    public boolean isWithinDistance(float distance, BaseActor other){
        Polygon poly1= this.getBoundaryPolygon();
        float scaleX= (this.getWidth()+2*distance)/this.getWidth();
        float scaleY= (this.getHeight()+ 2*distance)/ this.getHeight();
        poly1.setScale(scaleX,scaleY);

        Polygon poly2= other.getBoundaryPolygon();
        if(!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;
        return Intersector.overlapConvexPolygons(poly1,poly2);
    }


    public void centerAtPosition(float x, float y)
    {
        setPosition( x - getWidth()/2 , y - getHeight()/2 );
    }

    public void centerAtActor(BaseActor other)
    {
        centerAtPosition( other.getX() + other.getWidth()/2 , other.getY() + other.getHeight()/2 );
    }

    public void setAnimation(Animation<TextureRegion> anim)
    {
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize( w, h );
        setOrigin( w/2, h/2 );

        if (boundaryPolygon == null)
            setBoundaryRectangle();
    }

    public Animation<TextureRegion> loadAnimationFromSheet(TextureRegion texture, int rows, int cols, float frameDuration, boolean loop){
        int frameWidth = texture.getRegionWidth() / cols;
        int frameHeight = texture.getRegionHeight() / rows;

        TextureRegion[][] temp = texture.split(frameWidth, frameHeight);

        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                textureArray.add( temp[r][c] );

        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)
            setAnimation(anim);

        return anim;
    }

    public Animation<TextureRegion> loadTexture(TextureRegion texture){
       return loadAnimationFromSheet(texture, 1, 1, 1, true);
    }

    /**
     *  Set the pause state of the animation.
     *  @param pause true to pause animation, false to resume animation
     */
    public void setAnimationPaused(boolean pause)
    {
        animationPaused = pause;
    }

    /**
     *  Checks if animation is complete: if play mode is normal (not looping)
     *      and elapsed time is greater than time corresponding to last frame.
     *  @return 
     */
    public boolean isAnimationFinished()
    {
        return animation.isAnimationFinished(elapsedTime);
    }

    /**
     *  Sets the opacity of this actor.
     *  @param opacity value from 0 (transparent) to 1 (opaque)
     */
    public void setOpacity(float opacity)
    {
        this.getColor().a = opacity;
    }
    // ----------------------------------------------
    // physics/motion methods
    // ----------------------------------------------

    /**
     *  Set acceleration of this object.
     *  @param acc Acceleration in (pixels/second) per second.
     */
    public void setAcceleration(float acc)
    {
        acceleration = acc;
    }

    /**
     *  Set deceleration of this object.
     *  Deceleration is only applied when object is not accelerating.
     *  @param dec Deceleration in (pixels/second) per second.
     */
    public void setDeceleration(float dec)
    {
        deceleration = dec;
    }

    /**
     *  Set maximum speed of this object.
     *  @param ms Maximum speed of this object in (pixels/second).
     */
    public void setMaxSpeed(float ms)
    {
        maxSpeed = ms;
    }

    /**
     *  Set the speed of movement (in pixels/second) in current direction.
     *  If current speed is zero (direction is undefined), direction will be set to 0 degrees.
     *  @param speed of movement (pixels/second)
     */
    public void setSpeed(float speed)
    {
        // if length is zero, then assume motion angle is zero degrees
        if (velocityVec.len() == 0)
            velocityVec.set(speed, 0);
        else
            velocityVec.setLength(speed);
    }

    /**
     *  Calculates the speed of movement (in pixels/second).
     *  @return speed of movement (pixels/second)
     */
    public float getSpeed()
    {
        return velocityVec.len();
    }

    /**
     *  Determines if this object is moving (if speed is greater than zero).
     *  @return false when speed is zero, true otherwise
     */
    public boolean isMoving()
    {
        return (getSpeed() > 0);
    }

    /**
     *  Sets the angle of motion (in degrees).
     *  If current speed is zero, this will have no effect.
     *  @param angle of motion (degrees)
     */
    public void setMotionAngle(float angle)
    {
        velocityVec.setAngle(angle);
    }

    /**
     *  Get the angle of motion (in degrees), calculated from the velocity vector.
     *  <br>
     *  To align actor image angle with motion angle, use <code>setRotation( getMotionAngle() )</code>.
     *  @return angle of motion (degrees)
     */
    public float getMotionAngle()
    {
        return velocityVec.angle();
    }

    /**
     *  Update accelerate vector by angle and value stored in acceleration field.
     *  Acceleration is applied by <code>applyPhysics</code> method.
     *  @param angle Angle (degrees) in which to accelerate.
     *  @see #acceleration
     *  @see #applyPhysics
     */
    public void accelerateAtAngle(float angle)
    {
        accelerationVec.add( 
            new Vector2(acceleration, 0).setAngle(angle) );
    }

    /**
     *  Update accelerate vector by current rotation angle and value stored in acceleration field.
     *  Acceleration is applied by <code>applyPhysics</code> method.
     *  @see #acceleration
     *  @see #applyPhysics
     */
    public void accelerateForward()
    {
        accelerateAtAngle( getRotation() );
    }

    /**
     *  Adjust velocity vector based on acceleration vector, 
     *  then adjust position based on velocity vector. <br>
     *  If not accelerating, deceleration value is applied. <br>
     *  Speed is limited by maxSpeed value. <br>
     *  Acceleration vector reset to (0,0) at end of method. <br>
     *  @param dt Time elapsed since previous frame (delta time); typically obtained from <code>act</code> method.
     *  @see #acceleration
     *  @see #deceleration
     *  @see #maxSpeed
     */
    public void applyPhysics(float dt)
    {
        // apply acceleration
        velocityVec.add( accelerationVec.x * dt, accelerationVec.y * dt );

        float speed = getSpeed();

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0)
            speed -= deceleration * dt;

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0, maxSpeed);

        // update velocity
        setSpeed(speed);

        // apply velocity
        moveBy( velocityVec.x * dt, velocityVec.y * dt );

        // reset acceleration
        accelerationVec.set(0,0);
    }

    // ----------------------------------------------
    // Collision polygon methods
    // ----------------------------------------------

    /**
     *  Set rectangular-shaped collision polygon.
     *  This method is automatically called when animation is set,
     *   provided that the current boundary polygon is null.
     *  @see #setAnimation
     */
    public void setBoundaryRectangle()
    {
        float w = getWidth();
        float h = getHeight(); 

        float[] vertices = {0,0, w,0, w,h, 0,h};
        boundaryPolygon = new Polygon(vertices);
    }

    /**
     *  Replace default (rectangle) collision polygon with an n-sided polygon. <br>
     *  Vertices of polygon lie on the ellipse contained within bounding rectangle.
     *  Note: one vertex will be located at point (0,width);
     *  a 4-sided polygon will appear in the orientation of a diamond.
     *  @param numSides number of sides of the collision polygon
     */
    public void setBoundaryPolygon(int numSides)
    {
        float w = getWidth();
        float h = getHeight();

        float[] vertices = new float[2*numSides];
        for (int i = 0; i < numSides; i++)
        {
            float angle = i * 6.28f / numSides;
            // x-coordinate
            vertices[2*i] = w/2 * MathUtils.cos(angle) + w/2;
            // y-coordinate
            vertices[2*i+1] = h/2 * MathUtils.sin(angle) + h/2;
        }
        boundaryPolygon = new Polygon(vertices);

    }

    /**
     *  Returns bounding polygon for this BaseActor, adjusted by Actor's current position and rotation.
     *  @return bounding polygon for this BaseActor
     */
    public Polygon getBoundaryPolygon()
    {
        boundaryPolygon.setPosition( getX(), getY() );
        boundaryPolygon.setOrigin( getOriginX(), getOriginY() );
        boundaryPolygon.setRotation( getRotation() );
        boundaryPolygon.setScale( getScaleX(), getScaleY() );        
        return boundaryPolygon;
    }


    public boolean overlaps(BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()) )
            return false;

        return Intersector.overlapConvexPolygons( poly1, poly2 );
    }

    /**
     *  Implement a "solid"-like behavior:
     *  when there is overlap, move this BaseActor away from other BaseActor
     *  along minimum translation vector until there is no overlap.
     *  @param other BaseActor to check for overlap
     *  @return direction vector by which actor was translated, null if no overlap
     */
    public Vector2 preventOverlap(BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()) )
            return null;

        MinimumTranslationVector mtv = new MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if ( !polygonOverlap )
            return null;

        this.moveBy( mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth );
        return mtv.normal;
    }

    /**
     *  Set world dimensions for use by methods boundToWorld() and scrollTo().
     *  @param width width of world
     *  @param height height of world
     */
    public static void setWorldBounds(float width, float height)
    {
        worldBounds = new Rectangle( 0,0, width, height );
    }

    public static Rectangle getWorldBounds(){
        return worldBounds;
    }


    public static void setWorldBounds(BaseActor ba)
    {
        setWorldBounds( ba.getWidth(), ba.getHeight() );
    }   

    /**
     * If an edge of an object moves past the world bounds, 
     *   adjust its position to keep it completely on screen.
     */
    public void boundToWorld()
    {
        if (getX() < 0)
            setX(0);
        if (getX() + getWidth() > worldBounds.width)    
            setX(worldBounds.width - getWidth());
        if (getY() < 0)
            setY(0);
        if (getY() + getHeight() > worldBounds.height)
            setY(worldBounds.height - getHeight());
    }
    
    /**
     *  Center camera on this object, while keeping camera's range of view 
     *  (determined by screen size) completely within world bounds.
     */
    public void alignCamera()
    {
        Camera cam = this.getStage().getCamera();
        Viewport v = this.getStage().getViewport();

        // center camera on actor
        cam.position.set( this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0 );

        // bound camera to layout
        cam.position.x = MathUtils.clamp(cam.position.x, cam.viewportWidth/2,  worldBounds.width -  cam.viewportWidth/2);
        cam.position.y = MathUtils.clamp(cam.position.y, cam.viewportHeight/2, worldBounds.height - cam.viewportHeight/2);
        cam.update();
    }
    
    // ----------------------------------------------
    // Instance list methods
    // ----------------------------------------------


    public static ArrayList<BaseActor> getList(Stage stage, String className)
    {
        ArrayList<BaseActor> list = new ArrayList<BaseActor>();
        
        Class theClass = null;
        try
        {  theClass = Class.forName(className);  }
        catch (Exception error)
        {  error.printStackTrace();  }
        
        for (Actor a : stage.getActors())
        {
            if ( theClass.isInstance( a ) )
                list.add( (BaseActor)a );
        }

        return list;
    }

    /**
     *  Returns number of instances of a given class (that extends BaseActor).
     *  @param className name of a class that extends the BaseActor class
     *  @return number of instances of the class
     */
    public static int count(Stage stage, String className)
    {
        return getList(stage, className).size();
    }
    
    // ----------------------------------------------
    // Actor methods: act and draw
    // ----------------------------------------------

    /**
     *  Processes all Actions and related code for this object; 
     *  automatically called by act method in Stage class.
     *  @param dt elapsed time (second) since last frame (supplied by Stage act method)
     */
    public void act(float dt)
    {
        super.act( dt );

        if (!animationPaused)
            elapsedTime += dt;
    }


    public void draw(Batch batch, float parentAlpha)
    {
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if ( animation != null && isVisible() ) {
            batch.draw(animation.getKeyFrame(elapsedTime),
                    getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }

        super.draw( batch, parentAlpha );

    }
}