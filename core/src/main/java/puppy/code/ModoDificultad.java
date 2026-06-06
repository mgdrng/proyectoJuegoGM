package puppy.code;

/*Se aplica Strategy en ModoDificultad, ya que permite intercambiar
la dificultad del juego sin modificar la lógica principal de PlayFlechas.
*/

public interface ModoDificultad {
    float getVelocidad();
    float getIntervaloSpawn();
}
