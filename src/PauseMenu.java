import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.stream.IntStream;

public class PauseMenu extends GameState {

    protected String[] options;
    protected int selected;

    @Override
    protected void init() {
        this.selected = 0;
        this.options = new String[]{
                "Back to the game",
                "Exit"
        };
        //                TODO show message that the current state won't be saved
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics graphics) {
        drawBackground(graphics);
        drawOptions(graphics, options, selected);
    }

    static void drawBackground(Graphics graphics) {
        graphics.setColor(new Color(54, 51, 51));
        graphics.fillRect(0, 0, Window.WIDTH, Window.HEIGHT);

        graphics.setColor(new Color(255, 255, 255, 255));
        graphics.drawRect(750, 50, 300, 450);
        graphics.setColor(new Color(50, 47, 47, 255));
        graphics.fillRect(751, 51, 299, 449);
    }

    static void drawOptions(Graphics graphics, String[] options, int selected) {
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Roboto", Font.PLAIN + Font.ITALIC, 25));

        IntStream.range(0, options.length).forEachOrdered(i -> {
            if (selected == i) {
                graphics.setColor(Color.GREEN);
            } else graphics.setColor(Color.WHITE);
            drawCenteredString(options[i], i, graphics);
        });
    }

    static void drawCenteredString(String option, int diffBetweenLines, Graphics graphics) {
        FontMetrics fm = graphics.getFontMetrics();
        int x = (Window.WIDTH - fm.stringWidth(option)) / 2;
        int y = (fm.getAscent() + (200 + 200 * diffBetweenLines - (fm.getAscent() + fm.getDescent())) / 2);
        graphics.drawString(option, x, y);
    }

    @Override
    public void keyPressed(int key) {
        if (key == KeyEvent.VK_UP) {
            selected--;
            if (selected < 0) selected = options.length - 1;
        } else if (key == KeyEvent.VK_DOWN) {
            selected = (selected + 1) % options.length;
        } else if (key == KeyEvent.VK_ESCAPE) {
            Game.STATE_MANAGER.backToPrevious();
        } else if (key == KeyEvent.VK_ENTER) {
            if (selected == 0) {
                Game.STATE_MANAGER.backToPrevious();
            } else if (selected == 1) {
                Game.STATE_MANAGER.clear();
                Game.STATE_MANAGER.changeState(new MainMenu());
            }
        }
    }

    @Override
    public void keyReleased(int key) {

    }


}
