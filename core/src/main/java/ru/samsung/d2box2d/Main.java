package ru.samsung.d2box2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    public static final float WORLD_WIDTH = 16, WORLD_HEIGHT = 9;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer renderer;

    KinematicBody platform;
    DynamicBodyCircle[] balls = new DynamicBodyCircle[1];
    DynamicBodyBox[] boxes = new DynamicBodyBox[3];
    DynamicBodyTriangle[] triangles = new DynamicBodyTriangle[1];
    Body bodyTouched;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        Box2D.init();
        world = new World(new Vector2(0, -10f), true);
        renderer = new Box2DDebugRenderer();
        Gdx.input.setInputProcessor(new MyInputProcessor());

        StaticBody floor = new StaticBody(world, 8, 1, 15.5f, 0.3f);
        StaticBody wall1 = new StaticBody(world, 1, 5, 0.3f, 7f);
        //StaticBody wall2 = new StaticBody(world, 15, 5, 0.3f, 7f);

        /*for (int i = 0; i < balls.length; i++) {
            balls[i] = new DynamicBodyCircle(world, 8+MathUtils.random(-0.1f, 0.1f), 5+i, MathUtils.random(0.1f, 0.5f));
        }
        for (int i = 0; i < triangles.length; i++) {
            triangles[i] = new DynamicBodyTriangle(world, 10, 5+i, 0.7f, 0.7f);
        }*/
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new DynamicBodyBox(world, 12, 5+i, 0.2f, 0.8f);
        }
        balls[0] = new DynamicBodyCircle(world, 3, 2, 0.3f);
        triangles[0] = new DynamicBodyTriangle(world, 10, 5, 0.7f, 0.7f);
        platform = new KinematicBody(world, 0, 4, 5, 0.8f);
    }

    @Override
    public void render() {
        // события
        //platform.move();

        // отрисовка
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        renderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.end();
        world.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    class MyInputProcessor implements InputProcessor{
        Vector3 touchDown = new Vector3();
        Vector3 touchUp = new Vector3();

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            touchDown.set(screenX, screenY, 0);
            camera.unproject(touchDown);
            bodyTouched = null;
            for (DynamicBodyCircle b : balls) {
                if (b.hit(touchDown)) {
                    bodyTouched = b.body;
                }
            }
            for (DynamicBodyBox b : boxes) {
                if (b.hit(touchDown)) {
                    bodyTouched = b.body;
                }
            }
            for (DynamicBodyTriangle b : triangles) {
                if (b.hit(touchDown)) {
                    bodyTouched = b.body;
                }
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(bodyTouched != null) {
                touchUp.set(screenX, screenY, 0);
                camera.unproject(touchUp);
                Vector3 swipe = new Vector3(touchUp).sub(touchDown);
                bodyTouched.applyLinearImpulse(new Vector2(-swipe.x, -swipe.y), bodyTouched.getPosition(), true);
            }
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            return false;
        }
    }
}
