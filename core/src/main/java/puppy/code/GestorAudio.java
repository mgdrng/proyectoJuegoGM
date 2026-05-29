package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;

public class GestorAudio {
    private static GestorAudio instancia;
    private Array<Canciones> repertorio;
    private Music musicaActual;
    private boolean cancionYaEmpezo = false;
    private float ultimaPosicion = 0f;
    private int framesAtascado = 0;

    private GestorAudio() {
        repertorio = new Array<Canciones>();

        repertorio.add(new Canciones("Fire Force OP1", "FireForce.mp3"));
        repertorio.add(new Canciones("Testeo ", "Testeo.mp3"));
        repertorio.add(new Canciones("Trap Hardcore", "trap.mp3"));
    }

    public static GestorAudio getInstance() {
        if (instancia == null) {
            instancia = new GestorAudio();
        }
        return instancia;
    }

    public Array<Canciones> getRepertorio() {
        return repertorio;
    }

    public void reproducir(String rutaArchivo) {

        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual.dispose();
        }

        cancionYaEmpezo = false;
        ultimaPosicion = 0f;
        framesAtascado = 0;

        musicaActual = Gdx.audio.newMusic(Gdx.files.internal(rutaArchivo));

        musicaActual.setLooping(false);

        musicaActual.play();
    }

    public void detener() {
        if (musicaActual != null) {
            musicaActual.stop();
        }
    }

    public void dispose() {
        if (musicaActual != null) {
            musicaActual.dispose();
        }
    }
    public boolean isCancionTerminada() {
        if (musicaActual != null) {

            if (musicaActual.isPlaying()) {
                cancionYaEmpezo = true;
            }

            if (cancionYaEmpezo) {
                // Caso 1: El motor funciona bien y avisa que se detuvo
                if (!musicaActual.isPlaying()) {
                    return true;
                }

                float posActual = musicaActual.getPosition();

                // Caso 2: Bug de LibGDX (La canción terminó y se reinició a 0 misteriosamente)
                if (ultimaPosicion > 10f && posActual < 1f) {
                    return true;
                }

                // Caso 3: Bug de LibGDX (La canción se quedó congelada al final del archivo)
                if (posActual > 0f && posActual == ultimaPosicion) {
                    framesAtascado++;
                    if (framesAtascado > 15) { // Si el tiempo no avanza por 15 fotogramas
                        return true;
                    }
                } else {
                    framesAtascado = 0; // Si el tiempo avanza normal, reseteamos
                }

                ultimaPosicion = posActual;
            }
        }
        return false;
    }
}
