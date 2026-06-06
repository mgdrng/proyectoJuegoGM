package puppy.code;

/*Se aplica Singleton en GestorPuntaje, ya que asegura que solo exista un objeto para mantener
el puntaje consistente entre partidas
 */

public class GestorPuntaje {
    private static GestorPuntaje gestor;
    private int puntajeMaximo = 0;
    private int puntajeActual = 0;

    private GestorPuntaje(){}

    public static GestorPuntaje getInstance() {
        if (gestor == null) {
            gestor = new GestorPuntaje();
        }
        return gestor;
    }

    public void setPuntajeActual(int puntos) {
        this.puntajeActual = puntos;
        if (puntos > puntajeMaximo) {
            puntajeMaximo = puntos;
        }
    }

    public int getPuntajeActual(){
        return puntajeActual;
    }
    public int getPuntajeMaximo(){
        return puntajeMaximo;
    }
}
