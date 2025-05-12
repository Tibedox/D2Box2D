package ru.samsung.d2box2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    public static final float WORLD_WIDTH = 16, WORLD_HEIGHT = 9;

    private SpriteBatch batch;
    private SpriteBatch batchText;
    private OrthographicCamera camera;
    private OrthographicCamera cameraText;
    private World world;
    private Box2DDebugRenderer renderer;
    BitmapFont font;

    Texture textureAtlas;
    TextureRegion imgColob;

    DynamicBodyCircle bird;
    DynamicBodyCircle pig;
    DynamicBodyBox box;
    DynamicBodyBox[] boxes = new DynamicBodyBox[4];
    Body bodyTouched;

    @Override
    public void create() {
        batch = new SpriteBatch();
        batchText = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        cameraText = new OrthographicCamera();
        cameraText.setToOrtho(false, WORLD_WIDTH*100, WORLD_HEIGHT*100);
        Box2D.init();
        world = new World(new Vector2(0, -10f), true);
        renderer = new Box2DDebugRenderer();
        Gdx.input.setInputProcessor(new MyInputProcessor());
        font = new BitmapFont(Gdx.files.internal("dscrystal50white.fnt"));

        textureAtlas = new Texture("colobog.png");
        imgColob = new TextureRegion(textureAtlas, 0, 0, 100, 100);

        StaticBody floor = new StaticBody(world, 8, 1, 15.5f, 0.3f);
        StaticBody wall1 = new StaticBody(world, 1, 5, 0.3f, 7f);
        StaticBody wall2 = new StaticBody(world, 15, 5, 0.3f, 7f);

        boxes[0] = new DynamicBodyBox(world, 12.5f, 3, 0.2f, 1f, "board0");
        boxes[1] = new DynamicBodyBox(world, 13.5f, 3, 0.2f, 1f, "board1");
        boxes[2] = new DynamicBodyBox(world, 13f, 4, 1f, 0.2f, "board2");
        boxes[3] = new DynamicBodyBox(world, 13f, 6, 0.2f, 1f, "board3");
        pig = new DynamicBodyCircle(world, 13, 2, 0.3f, "pig");

        box = new DynamicBodyBox(world, 3, 2, 0.8f, 0.8f, "box");
        bird = new DynamicBodyCircle(world, 3, 3, 0.3f, "bird");

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                Body bodyA = fixtureA.getBody();
                Body bodyB = fixtureB.getBody();

                // Проверяем, что оба тела динамические
                if (bodyA.getType() == BodyDef.BodyType.DynamicBody &&
                    bodyB.getType() == BodyDef.BodyType.DynamicBody) {

                    // Получаем пользовательские данные тел (если вы их задавали)
                    Object userDataA = bodyA.getUserData();
                    Object userDataB = bodyB.getUserData();

                    // Здесь можно обработать столкновение
                    //System.out.println("Dynamic bodies collided: " + userDataA + " and " + userDataB);
                    if(userDataA.equals("pig") || userDataB.equals("pig")){
                        pig.isDead = true;
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
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
        batch.draw(imgColob, bird.getX(), bird.getY(), bird.getWidth()/2, bird.getHeight()/2,
            bird.getWidth(), bird.getHeight(), 1, 1, bird.getRotation());
        batch.end();

        batchText.setProjectionMatrix(cameraText.combined);
        batchText.begin();
        font.draw(batchText, "Hello", 100, 800);
        batchText.end();

        world.step(1/60f, 6, 2);

        if(pig != null && pig.isDead){
            world.destroyBody(pig.body);
            pig = null;
        }
    }

    @Override
    public void dispose() {
        textureAtlas.dispose();
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

            if (bird.hit(touchDown)) {
                bodyTouched = bird.body;
            }

            for (DynamicBodyBox b : boxes) {
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
