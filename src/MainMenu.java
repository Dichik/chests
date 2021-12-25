import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.stream.IntStream;

public class MainMenu extends GameState {

    protected String[] options;
    protected int selected;


    @Override
    protected void init() {
        options = new String[]{
                "StartGame",
                "Exit"
        };
        selected = 0;

        System.out.println("[MainMenu] created successfully.");
    }

    @Override
    public void tick() {
    }

    @Override
    public void render(Graphics graphics) {
        PauseMenu.drawBackground(graphics);
        PauseMenu.drawOptions(graphics, options, selected);
    }

    @Override
    public void keyPressed(int key) {
        if (key == KeyEvent.VK_UP) {
            selected--;
            if (selected < 0) selected = options.length - 1;
        } else if (key == KeyEvent.VK_DOWN) {
            selected = (selected + 1) % options.length;
        } else if (key == KeyEvent.VK_ENTER) {
            if(selected == options.length - 1) {
                System.exit(1);
            } else {
                Game.STATE_MANAGER.changeState(new PlayingState());
            }
        }
    }

    @Override
    public void keyReleased(int key) {
    }

}
