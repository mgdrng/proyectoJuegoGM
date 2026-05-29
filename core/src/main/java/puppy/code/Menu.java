package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Menu extends BaseScreen{
    private Main juego;
    private SpriteBatch batch;
    private BitmapFont font;
    private int indiceCancion = 0;
    private Array<Canciones> repertorio;

    public Menu(Main juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font  = new BitmapFont();
        repertorio = GestorAudio.getInstance().getRepertorio();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            indiceCancion++;
            if (indiceCancion >= repertorio.size) indiceCancion = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            indiceCancion--;
            if (indiceCancion < 0) indiceCancion = repertorio.size - 1;
        }

        Canciones cancionElegida = repertorio.get(indiceCancion);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            juego.setScreen(new PlayFlechas(juego, new ModoFacil(), cancionElegida));
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
            juego.setScreen(new PlayFlechas(juego, new ModoNormal(), cancionElegida));
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3))
            juego.setScreen(new PlayFlechas(juego, new ModoDificil(), cancionElegida));
    }
    @Override
    protected void draw(float delta) {
        batch.begin();
        font.draw(batch, "Selecciona dificultad:", 200, 340);
        font.draw(batch, "[1] Facil", 200, 310);
        font.draw(batch, "[2] Normal", 200, 280);
        font.draw(batch, "[3] Dificil", 200, 250);
        font.draw(batch, "Mejor puntaje: " + GestorPuntaje.getInstance().getPuntajeMaximo(), 200, 200);

        String nombrePista = repertorio.get(indiceCancion).getNombre();
        font.draw(batch, "< Pista: " + nombrePista + " >", 200, 150);
        font.draw(batch, "(Usa flechas <- e -> para cambiar musica)", 170, 120);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
