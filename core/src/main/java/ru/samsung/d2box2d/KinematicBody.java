package ru.samsung.d2box2d;

import static ru.samsung.d2box2d.Main.WORLD_WIDTH;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class KinematicBody {
    public float x, y;
    public float width, height;
    private Body body;
    private float vx = 2;

    public KinematicBody(World world, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);

        body.createFixture(shape, 0);
        shape.dispose();

        body.setLinearVelocity(vx, 0);
    }

    public void move(){
        if(body.getPosition().x < 0 || body.getPosition().x > WORLD_WIDTH){
            vx = -vx;
            body.setLinearVelocity(vx, 0);
        }
    }
}
