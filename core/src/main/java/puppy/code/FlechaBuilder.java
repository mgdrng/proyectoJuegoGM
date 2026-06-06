package puppy.code;

/*Se aplica Builder en FlechaBuilder, ya que permite
  construir objetos Flechas paso a paso, manteniendo la lógica
  de creación separada de PlayFlechas.
 */

public class FlechaBuilder {
    private int tipo;
    private float x;
    private float y;

    public FlechaBuilder setTipo(int tipo) {
        this.tipo = tipo;
        return this;
    }

    public FlechaBuilder setX(float x) {
        this.x = x;
        return this;
    }

    public FlechaBuilder setY(float y) {
        this.y = y;
        return this;
    }

    public Flechas build() {
        return new Flechas(tipo, x, y);
    }
}
