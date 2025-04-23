package ru.samsung.d2box2d;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class DynamicBodyTriangle {
    public float x, y;
    public float width, height;
    public Body body;

    public DynamicBodyTriangle(World world, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.set(new float[]{0, height/2, -width/2, -height/2, width/2, -height/2});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.6f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public boolean hit(Vector3 point) {
        float x = point.x;
        float y = point.y;

        float x1 = this.x;
        float y1 = this.y + height / 2;
        float x2 = this.x - width / 2;
        float y2 = this.y - height / 2;
        float x3 = this.x + width / 2;
        float y3 = this.y - height / 2;

        float area = Math.abs((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
        float area1 = Math.abs((x1 - x) * (y2 - y) - (x2 - x) * (y1 - y));
        float area2 = Math.abs((x2 - x) * (y3 - y) - (x3 - x) * (y2 - y));
        float area3 = Math.abs((x3 - x) * (y1 - y) - (x1 - x) * (y3 - y));

        return (area == area1 + area2 + area3);
    }
}
