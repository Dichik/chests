import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlayingState extends GameState {

    private Stack<Card> deck;
    private Map<Card, Integer> map;

    private List<Card> myCards;
    private List<Card> computerCards;

    private int INIT_NUMBER_OF_CARDS_TO_TAKE;
    private int NUMBER_OF_CARDS_IN_DECK;

    @Override
    protected void init() {
        INIT_NUMBER_OF_CARDS_TO_TAKE = 8;
        NUMBER_OF_CARDS_IN_DECK = 36;
        deck = new Stack<>();
        myCards = new ArrayList<>();
        computerCards = new ArrayList<>();
        map = new HashMap<>();
        generateCards();

        rozGive();
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics graphics) {
        drawBackground(graphics);
    }

    @Override
    public void keyPressed(int key) {

    }

    @Override
    public void keyReleased(int key) {

    }

    private void shuffleCards(List<Card> cardList) {
        Collections.shuffle(cardList);
        deck.addAll(new ArrayList<>(cardList));
    }

    private void rozGive() {
        for (int i = 0; i < INIT_NUMBER_OF_CARDS_TO_TAKE; ++i) {
            if (i % 2 == 0) {
                myCards.add(deck.pop());
            } else {
                computerCards.add(deck.pop());
            }
        }
    }

    private void makeMove(Card card) {
        int youTake = 0;
        List<Card> collect = computerCards.stream().filter(p -> points(p) == points(card))
                .collect(Collectors.toList());

    }

    private int points(Card card) {
        return map.get(card);
    }


    private void generateCards() {
        List<Card> cardList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_CARDS_IN_DECK / 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                cardList.add(new Card(CardName.values()[i], CardColor.values()[j]));
            }
        }
        shuffleCards(cardList);
    }


    private void drawBackground(Graphics graphics) {

        graphics.setColor(new Color(192, 213, 49));
        graphics.fillRect(0, 0, Window.WIDTH, 300);

        try {
            final BufferedImage image = ImageIO.read(new File("src/images/monkey.jpg"));
            graphics.drawImage(image, Window.WIDTH / 2 - image.getWidth() / 4, 0, image.getWidth() / 2, image.getHeight() / 2, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < computerCards.size(); ++i) {
            printComputerCard(computerCards.get(i), getStartPoint(100, computerCards.size()) + 100 * i, 200, graphics);
        }

        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 300, Window.WIDTH, Window.HEIGHT - 300);
        graphics.setColor(new Color(22, 255, 98));
        graphics.fillRect(1, 301, Window.WIDTH - 1, Window.HEIGHT - 300 - 1);

        Graphics2D g2D = (Graphics2D) graphics;
        g2D.rotate(0.2);
//        g2D.setColor(Color.BLACK);
//        g2D.drawRect(0,100,250, 1000);
        graphics.setColor(new Color(192, 213, 49));
        g2D.fillRect(1,100 + 1,250 - 2, 1000 - 2);

        g2D.rotate(-0.4);
        graphics.setColor(new Color(192, 213, 49));
        g2D.fillRect(Window.WIDTH - 250,300 + 1,250 - 2, 1000 - 2);

        g2D.rotate(0.2);

        for (int i = 0; i < myCards.size(); ++i) {
            printMyCard(myCards.get(i), getStartPoint(150, myCards.size()) + 150 * i, Window.HEIGHT - 300, graphics);
        }
//        TODO add borders of the table
    }

    // TODO create const variables for everything (Colors too)
    private int getStartPoint(int cardWidth, int size) {
        return Window.WIDTH / 2 - ((size % 2 == 0) ? (cardWidth * size / 2) : (size / 2 * cardWidth + cardWidth / 2));
    }

    private void printMyCard(Card card, int x, int y, Graphics graphics) {
        graphics.setColor(Color.BLACK);
        ((Graphics2D) graphics).draw(new RoundRectangle2D.Float(x, y, 150, 250, 10, 10));
        graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(x + 1, y + 1, 150 - 1, 250 - 1, 10, 10);

        if(getColorFromCardName(card) == Color.BLACK) {
            graphics.setColor(new Color(108, 108, 108, 255));
            graphics.fillRect(x + 150 / 2 - 50, y + 250 / 2 - 60, 100, 120);

            graphics.setColor(new Color(255, 150, 150, 255));
            graphics.fillRect(x + 150 / 2 - 40, y + 250 / 2 - 50, 80, 100);
        } else {
            graphics.setColor(new Color(255, 150, 150, 255));
            graphics.fillRect(x + 150 / 2 - 50, y + 250 / 2 - 60, 100, 120);

            graphics.setColor(new Color(108, 108, 108, 255));
            graphics.fillRect(x + 150 / 2 - 40, y + 250 / 2 - 50, 80, 100);
        }

        try {
            final BufferedImage colorCard = ImageIO.read(getColorCardURL(card));
            graphics.drawImage(colorCard, 150 / 2 + x - colorCard.getWidth() / 2, 250 / 2 + y - colorCard.getHeight() / 2, null);
            graphics.setColor(getColorFromCardName(card));
            graphics.setFont(new Font("Roboto", Font.PLAIN, 25));
            graphics.drawString(getNameFromCard(card), x + 10, y + 30);
            graphics.drawString(getNameFromCard(card), x + 150 - 30, y + 250 - 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNameFromCard(Card card) {
        switch (card.getCardName()) {
            case SIX:
                return "6";
            case KING:
                return "K";
            case SEVEN:
                return "7";
            case EIGHT:
                return "8";
            case NINE:
                return "9";
            case TEN:
                return "10";
            case JACK:
                return "J";
            case QUEEN:
                return "Q";
            default:
                return "A";
        }
    }

    private Color getColorFromCardName(Card card) {
        switch (card.getColor()) {
            case HEARTS:
            case DIAMONDS:
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }

    private File getColorCardURL(Card card) {
        switch (card.getColor()) {
            case CLUBS:
                return new File("src/images/clubs.png");
            case HEARTS:
                return new File("src/images/hearts.png");
            case SPADES:
                return new File("src/images/spades.png");
            case DIAMONDS:
            default:
                return new File("src/images/diamonds.png");
        }
    }

    private void printComputerCard(Card card, int x, int y, Graphics graphics) {
        graphics.setColor(Color.BLACK);
        ((Graphics2D) graphics).draw(new RoundRectangle2D.Float(x, y, 125, 220, 10, 10));
        graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(x + 1, y + 1, 125 - 1, 220 - 1, 10, 10);
        try {
            final BufferedImage image = ImageIO.read(new File("src/images/card_backside.jpg"));
            graphics.drawImage(image, x + 2, y + 2, 125 - 2, 220 - 2, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
