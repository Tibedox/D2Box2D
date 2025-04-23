package ru.samsung.d2box2d;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class DynamicBodyCircle {
    public float x, y;
    public float radius;
    public Body body;

    public DynamicBodyCircle(World world, float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    boolean hit(Vector3 t){
        x = body.getPosition().x;
        y = body.getPosition().y;
        return (t.x-x)*(t.x-x) + (t.y-y)*(t.y-y) < radius*radius;
    }
}
