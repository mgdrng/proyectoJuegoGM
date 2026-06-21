package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import java.util.Random;

public class PlayFlechas extends BaseScreen {
    private Main juego;
    private SpriteBatch lote;
    private ShapeRenderer dibujoPaneles;
    private BitmapFont font;
    private Texture flechaArriba;
    private Texture flechaAbajo;
    private Texture flechaIzquierda;
    private Texture flechaDerecha;

    private Array<Flechas> flechas = new Array<Flechas>();
    private float spawnTimer = 0f;
    private Random rng = new Random();

    private ModoDificultad dificultad;
    private Canciones cancionNivel;

    private float tamanio = 45f;
    private float spacing = 55f;
    private float inicioX;
    private float golpeY  = 80f;
    private float panelW  = 65f;
    private float panelX  = 10f;

    private int vidas  = 3;

    private float margenGolpe = 30f;

    private boolean herido = false;
    private int tiempoHerido    = 0;
    private int tiempoHeridoMax = 50;
    private float tiempoCancion = 0f;
    private float duracionCancion = 0f;
    private boolean partidaTerminada = false;

    public PlayFlechas(Main juego, ModoDificultad dificultad, Canciones cancionNivel) {
        this.juego = juego;
        this.dificultad = dificultad;
        this.cancionNivel = cancionNivel;
    }

    @Override
    public void show() {
        GestorPuntaje.getInstance().setPuntajeActual(0);
        lote = new SpriteBatch();
        dibujoPaneles = new ShapeRenderer();
        font = new BitmapFont();

        flechaArriba = new Texture("flechaUp.png");
        flechaAbajo = new Texture("flechaDown.png");
        flechaIzquierda = new Texture("flechaLeft.png");
        flechaDerecha = new Texture("flechaRight.png");

        float screenWidth = Gdx.graphics.getWidth();
        float centerX = screenWidth / 2f;
        float totalCarriles = (4 * tamanio) + (3 * spacing);
        inicioX = centerX - (totalCarriles / 2f);

        tiempoCancion = 0f;
        duracionCancion = obtenerDuracionCancion(cancionNivel.getArchivo());
        GestorAudio.getInstance().reproducir(cancionNivel.getArchivo());
    }

    @Override
    protected void update(float tiempoFrame) {
        if (partidaTerminada) {
            return;
        }

        tiempoCancion += tiempoFrame;

        if (vidas <= 0) {
            finalizarPartida();
            return;
        }

        if (tiempoCancion >= duracionCancion) {
            finalizarPartida();
            return;
        }

        if (GestorAudio.getInstance().isCancionTerminada()) {
            finalizarPartida();
            return;
        }

        if (herido) {
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }

        spawnTimer += tiempoFrame;
        if (spawnTimer >= dificultad.getIntervaloSpawn()) {
            spawnTimer = 0f;
            int carril = rng.nextInt(4);
            flechas.add(new FlechaBuilder()
                .setTipo(carril)
                .setX(carrilX(carril))
                .setY(Gdx.graphics.getHeight())
                .build());
        }

        boolean izq    = Gdx.input.isKeyJustPressed(Input.Keys.LEFT);
        boolean abajo  = Gdx.input.isKeyJustPressed(Input.Keys.DOWN);
        boolean arriba = Gdx.input.isKeyJustPressed(Input.Keys.UP);
        boolean der    = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT);

        for (int i = flechas.size - 1; i >= 0; i--) {
            Flechas f = flechas.get(i);
            f.mover(dificultad.getVelocidad() * tiempoFrame);

            if (f.getY() + tamanio < 0) {
                flechas.removeIndex(i);
                dañar();
                continue;
            }

            if (Math.abs(f.getY() - golpeY) <= margenGolpe) {
                if (teclaCorrecta(f.getTipo(), izq, abajo, arriba, der)) {
                    flechas.removeIndex(i);
                    GestorPuntaje.getInstance().setPuntajeActual(
                        GestorPuntaje.getInstance().getPuntajeActual() + 10);

                    // cambia de dificultad cada 150/300 puntos según el modo (Strategy)
                    int puntos = GestorPuntaje.getInstance().getPuntajeActual();
                    int ciclo = puntos % 600;
                    if (ciclo >= 300) {
                        dificultad = new ModoDificil();
                    } else if (ciclo >= 150) {
                        dificultad = new ModoNormal();
                    } else {
                        dificultad = new ModoFacil();
                    }
                }
            }
        }
    }

    @Override
    protected void draw(float tiempoFrame) {
        dibujoPaneles.begin(ShapeRenderer.ShapeType.Filled);
        dibujoPaneles.setColor(0.12f, 0.12f, 0.12f, 1f);
        for (int carril = 0; carril < 4; carril++) {
            dibujoPaneles.rect(carrilX(carril) - panelX, 0, panelW, Gdx.graphics.getHeight());
        }
        dibujoPaneles.end();

        lote.begin();

        font.draw(lote, "Puntos: " + GestorPuntaje.getInstance().getPuntajeActual(), 5, Gdx.graphics.getHeight() - 5);
        font.draw(lote, "Vidas: "  + vidas,  Gdx.graphics.getWidth() - 120,
            Gdx.graphics.getHeight() - 5);

        if (!herido || (tiempoHerido % 6 < 3)) {
            for (int i = 0; i < flechas.size; i++) {
                Flechas flecha = flechas.get(i);
                lote.draw(texFor(flecha.getTipo()),
                    flecha.getX(), flecha.getY(), tamanio, tamanio);
            }
        }

        boolean izqPress    = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean abajoPress  = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean arribaPress = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean derPress    = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        lote.setColor(1f, 0f, 1f, 1f); // Morado
        dibujarFlechaBase(flechaIzquierda, carrilX(0), izqPress);

        lote.setColor(0f, 1f, 1f, 1f); // Celeste
        dibujarFlechaBase(flechaAbajo, carrilX(1), abajoPress);

        lote.setColor(0f, 1f, 0f, 1f); // Verde
        dibujarFlechaBase(flechaArriba, carrilX(2), arribaPress);

        lote.setColor(1f, 0f, 0f, 1f); // Rojo
        dibujarFlechaBase(flechaDerecha, carrilX(3), derPress);

        lote.setColor(1f, 1f, 1f, 1f); // Volver al color blanco base
        lote.end();
    }

    @Override
    public void dispose() {
        lote.dispose();
        dibujoPaneles.dispose();
        font.dispose();
        flechaArriba.dispose();
        flechaAbajo.dispose();
        flechaIzquierda.dispose();
        flechaDerecha.dispose();

        GestorAudio.getInstance().dispose();
    }

    private void dañar() {
        vidas--;
        herido       = true;
        tiempoHerido = tiempoHeridoMax;
    }

    private boolean teclaCorrecta(int tipo, boolean izq, boolean abajo, boolean arriba, boolean der) {
        switch (tipo) {
            case 0:  return izq;
            case 1:  return abajo;
            case 2:  return arriba;
            default: return der;
        }
    }

    private float carrilX(int carril) {
        return inicioX + carril * (tamanio + spacing);
    }

    private Texture texFor(int tipo) {
        switch (tipo) {
            case 0:  return flechaIzquierda;
            case 1:  return flechaAbajo;
            case 2:  return flechaArriba;
            default: return flechaDerecha;
        }
    }
    private void setColorPorTipo(int tipo) {
        switch (tipo) {
            case 0: lote.setColor(1f, 0f, 1f, 1f); break; // Izquierda
            case 1: lote.setColor(0f, 1f, 1f, 1f); break; // Abajo
            case 2: lote.setColor(0f, 1f, 0f, 1f); break; // Arriba
            default: lote.setColor(1f, 0f, 0f, 1f); break; // Derecha
        }
    }

    private void dibujarFlechaBase(Texture tex, float x, boolean presionada) {
        if (presionada) {

            float nuevoTamanio = tamanio * 0.8f;
            float offset = (tamanio - nuevoTamanio) / 2f;
            lote.setColor(lote.getColor().r * 0.6f, lote.getColor().g * 0.6f, lote.getColor().b * 0.6f, 1f);
            lote.draw(tex, x + offset, golpeY + offset, nuevoTamanio, nuevoTamanio);
        } else {

            lote.draw(tex, x, golpeY, tamanio, tamanio);
        }
    }
    private float obtenerDuracionCancion(String archivo) {
        if (archivo.equals("FireForce.mp3")) {
            return 91f;
        }

        if (archivo.equals("Testeo.mp3")) {
            return 12f;
        }

        if (archivo.equals("BlackClover.mp3")) {
            return 90f;
        }

        return 60f;
    }

    private void finalizarPartida() {
        if (partidaTerminada) {
            return;
        }

        partidaTerminada = true;

        GestorAudio.getInstance().detener();
        flechas.clear();

        juego.setScreen(new Menu(juego));
    }
}
