package puppy.code;
import com.badlogic.gdx.Game;

//clase game es de import del Libgdx
public class Main extends Game{
    @Override
    public void create() {
        setScreen(new Menu(this));
    }
}
