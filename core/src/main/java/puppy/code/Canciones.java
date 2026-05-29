package puppy.code;

public class Canciones {
    private String nombre;
    private String archivoFila;

    public Canciones(String nombre, String archivoFila) {
        this.nombre = nombre;
        this.archivoFila = archivoFila;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArchivo() {
        return archivoFila;
    }
}
