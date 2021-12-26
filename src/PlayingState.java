import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.IntStream.*;

public class PlayingState extends GameState {

    private Stack<Card> deck;
    private Boolean gameOver;

    private List<Card> myCards;
    private List<Card> computerCards;

    private int INIT_NUMBER_OF_CARDS_TO_TAKE;
    private int NUMBER_OF_CARDS_IN_DECK;

    private int countMyChests;
    private int countComputersChests;

    private boolean myTurn;
    private String lastMoveCardName;
    private Set<String> lastMoveCardColors;

    private List<Card> rememberedCards;
    private CardName lastChosenCardName;

    @Override
    protected void init() {
        INIT_NUMBER_OF_CARDS_TO_TAKE = 8;
        NUMBER_OF_CARDS_IN_DECK = 36;

        countMyChests = countComputersChests = 0;
        rememberedCards = new ArrayList<>();

        deck = new Stack<>();
        myCards = new ArrayList<>();
        computerCards = new ArrayList<>();
        gameOver = false;
        generateCards();

        myTurn = true;
//        TODO toss-up who starts + add animation

        rozGive();
    }

    @Override
    public void tick() {
//        TODO add checking if the game is finished
        if (myTurn) {
            makeMyMove();
        } else {
            makeComputerMove();
        }

        if (gameOver || myCards.isEmpty() || computerCards.isEmpty()) {
            showMessageGameOver();
            Game.STATE_MANAGER.clear();
            Game.STATE_MANAGER.changeState(new MainMenu());
        }
    }

    private void showMessageGameOver() {
        String message;
        if (countMyChests > countComputersChests) {
            message = "Congrats! You've won!";
        } else {
            message = "Sorry, try next time... ";
        }
        JOptionPane.showMessageDialog(Window.window, message);
    }

    private void makeComputerMove() {

        int index = getCardIndex();
        lastChosenCardName = CardName.values()[index];

        String chosenCardName = CardName.values()[index].name();

        Set<String> chosenCardColors = new HashSet<>();

        Set<CardColor> alreadyExists = new HashSet<>();
        for (Card card : computerCards) {
            if (card.getCardName().name().equals(chosenCardName)) {
                alreadyExists.add(card.getColor());
            }
        }
        for (int i = 0; i < 4; ++i) {
            if (!alreadyExists.contains(CardColor.values()[i])) {
                chosenCardColors.add(CardColor.values()[i].name());
            }
        }

        makeComputerMove(chosenCardName, chosenCardColors);
        lastMoveCardName = chosenCardName;
        lastMoveCardColors = chosenCardColors;

        checkIfComputerChestExists();
        changeTurn();
    }

    private int getCardIndex() {
        List<Card> totalCards = new ArrayList<>();
        totalCards.addAll(rememberedCards);
        totalCards.addAll(computerCards);

        int[] count = new int[NUMBER_OF_CARDS_IN_DECK / 4];
        int max = 1;
        for (Card totalCard : totalCards) {
            if(getNumberInMyCards(totalCard.getCardName()) == 0) continue;

            if(lastChosenCardName != null && lastChosenCardName.equals(totalCard.getCardName()) )
                continue;

            count[totalCard.getCardName().getPosition()]++;
            max = Math.max(max, count[totalCard.getCardName().getPosition()]);
        }
        int index = -1;
        for (int i = 0; i < count.length; ++i) {
            if (count[i] == max) {
                if ((index == -1 || getNumberInMyCards(CardName.values()[i])
                        < getNumberInMyCards(CardName.values()[i]))
                        && getNumberInMyCards(CardName.values()[i]) != 0) {
                    index = i;
                }
            }
        }
        return index;
    }

    private int getNumberInMyCards(CardName c) {
        return Math.toIntExact(computerCards.stream()
                .filter(card -> card.getCardName().name().equals(c.name()))
                .count());
    }

    private void makeComputerMove(String chosenCardName, Set<String> chosenCardColors) {
        int numberOfTaken = 0;
        List<Card> removeFromMyCards = new ArrayList<>();
        for (Card myCard : myCards) {
            if (myCard.getCardName().name().equals(chosenCardName)
                    && chosenCardColors.contains(myCard.getColor().name())) {
                numberOfTaken++;
                computerCards.add(myCard);
                removeFromMyCards.add(myCard);
            }
        }
        if (numberOfTaken != chosenCardColors.size() && !deck.isEmpty()) {
            computerCards.add(deck.pop());
        }
        for (Card card : removeFromMyCards) {
            myCards.remove(card);
        }
    }

    private int rand(int limit) {
        return new Random().nextInt(limit);
    }

    @Override
    public void render(Graphics graphics) {
        drawBackground(graphics);

        drawHistoryOfMoves(graphics);
//        TODO check if we can press ESC when making our move
    }

    private void drawHistoryOfMoves(Graphics graphics) {
        graphics.setColor(new Color(169, 129, 168));
        graphics.fillRoundRect(Window.WIDTH - 355, 20, 300, 350, 10, 10);
        graphics.setColor(new Color(255, 149, 251, 255));
        graphics.fillRoundRect(Window.WIDTH - 350, 15, 300, 350, 10, 10);

        graphics.setColor(Color.BLACK);
        drawCenteredString(0, graphics);

        if (lastMoveCardName != null && lastMoveCardColors != null) {
            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("Ubuntu", Font.PLAIN, 25));
            if (myTurn) {
                graphics.drawString("Computer chose: ", Window.WIDTH - 300, 90);
            } else {
                graphics.drawString("You chose: ", Window.WIDTH - 300, 90);
            }
            graphics.drawString(lastMoveCardName, Window.WIDTH - 300, 120);

            int index = 0;
            for (String cardName : lastMoveCardColors) {
                try {
                    final BufferedImage image = ImageIO.read(Objects.requireNonNull(getColorCardURL(cardName)));
                    graphics.drawImage(image, Window.WIDTH - 300, 150 + index * 30, 50, 50, null);
                    index++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawCenteredString(int diffBetweenLines, Graphics graphics) {
        FontMetrics fm = graphics.getFontMetrics();
        int x = (Window.WIDTH - 350 + fm.stringWidth("History:") / 2);
        int y = (fm.getAscent() + (70 + 200 * diffBetweenLines - (fm.getAscent() + fm.getDescent())) / 2);
        graphics.drawString("History:", x, y);
    }

    @Override
    public void keyPressed(int key) {
//        TODO we should see chosenMenu - for card name and color
        if (key == KeyEvent.VK_ESCAPE) {
            Game.STATE_MANAGER.changeState(new PauseMenu());
        }
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

    private void makeMyMove() {
        String[] options = Stream.of(CardName.values())
                .map(CardName::name).toArray(String[]::new);
        String chosenCardName = (String) JOptionPane.showInputDialog(
                Window.window,
                "Choose Card Name from the list",
                "Choose Card Name",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        String[] number = new String[]{"1", "2", "3", "4"};
        int chosenNumber = Integer.parseInt((String) JOptionPane.showInputDialog(
                Window.window,
                "Make your choice:",
                "Choose amount of cards",
                JOptionPane.QUESTION_MESSAGE,
                null,
                number,
                number[0]
        ));

        Set<String> chosenCardColors = new HashSet<>();

        rangeClosed(1, chosenNumber).forEach(i -> {
            String[] cardColors = Stream.of(CardColor.values())
                    .map(CardColor::name)
                    .filter(name -> !chosenCardColors.contains(name))
                    .toArray(String[]::new);
            String chosenCardColor = (String) JOptionPane.showInputDialog(
                    Window.window,
                    "Make a choice: ",
                    "Choose color of " + i + " card",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    cardColors,
                    cardColors[0]
            );
            chosenCardColors.add(chosenCardColor);
        });

        makeMyMove(chosenCardName, chosenCardColors);
        lastMoveCardName = chosenCardName;
        lastMoveCardColors = chosenCardColors;
// TODO sort cards by color then by name (asc)
        changeTurn();
        checkIfMyChestExists();
    }

    private void makeMyMove(String chosenCardName, Set<String> chosenCardColors) {
        int numberOfTaken = 0;
        List<Card> removeFromComputerCards = new ArrayList<>();
        for (Card computerCard : computerCards) {
            if (computerCard.getCardName().name().equals(chosenCardName)
                    && chosenCardColors.contains(computerCard.getColor().name())) {
                numberOfTaken++;
                myCards.add(computerCard);
                removeFromComputerCards.add(computerCard);
            }
        }
        if (numberOfTaken != chosenCardColors.size() && !deck.isEmpty()) {
            myCards.add(deck.pop());
        }
        for (Card card : removeFromComputerCards) {
            rememberedCards.add(card);
            computerCards.remove(card);
        }
    }

    private void changeTurn() {
        myTurn = !myTurn;
    }

    private void checkIfMyChestExists() {
        for (int i = 0; i < myCards.size(); ++i) {
            int cntSameCardName = 1;
            for (int j = i + 1; j < myCards.size(); ++j) {
                if (myCards.get(i).getCardName().equals(myCards.get(j).getCardName())) {
                    cntSameCardName++;
                }
            }
            if (cntSameCardName == 4) {
                removeAndIncreaseMyChest(myCards.get(i).getCardName());
            }
        }
    }

    private void removeAndIncreaseMyChest(CardName cardName) {
        this.myCards = myCards.stream()
                .filter(card -> !card.getCardName().equals(cardName))
                .collect(Collectors.toList());
        countMyChests++;
    }

    private void checkIfComputerChestExists() {
        for (int i = 0; i < computerCards.size(); ++i) {
            int cntSameCardName = 1;
            for (int j = i + 1; j < computerCards.size(); ++j) {
                if (computerCards.get(i).getCardName().equals(computerCards.get(j).getCardName())) {
                    cntSameCardName++;
                }
            }
            if (cntSameCardName == 4) {
                removeAndIncreaseComputerChest(computerCards.get(i).getCardName());
            }
        }
    }

    private void removeAndIncreaseComputerChest(CardName cardName) {
        this.computerCards = computerCards.stream()
                .filter(card -> !card.getCardName().equals(cardName))
                .collect(Collectors.toList());
        countComputersChests++;
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
//        TODO divide into functions
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

//        TODO cards over rotated rectangles

        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 300, Window.WIDTH, Window.HEIGHT - 300);
        graphics.setColor(new Color(22, 255, 98));
        graphics.fillRect(1, 301, Window.WIDTH - 1, Window.HEIGHT - 300 - 1);

        Graphics2D g2D = (Graphics2D) graphics;
        g2D.rotate(0.2);
//        g2D.setColor(Color.BLACK);
//        g2D.drawRect(0,100,250, 1000);
        graphics.setColor(new Color(192, 213, 49));
        g2D.fillRect(1, 100 + 1, 250 - 2, 1000 - 2);

        g2D.rotate(-0.4);
        graphics.setColor(new Color(192, 213, 49));
        g2D.fillRect(Window.WIDTH - 250, 300 + 1, 250 - 2, 1000 - 2);

        g2D.rotate(0.2);
        if(!deck.isEmpty()) {
            drawDeckOfCard(graphics);
        }

        for (int i = 0; i < myCards.size(); ++i) {
            printMyCard(myCards.get(i), getStartPoint(150, myCards.size()) + 150 * i, Window.HEIGHT - 300, graphics);
        }

        graphics.setColor(Color.BLACK);
        graphics.drawRect(5, 5, 450, 40);
        graphics.setColor(new Color(131, 253, 95));
        graphics.fillRect(6, 6, 450 - 1, 40 - 1);

        graphics.setColor(new Color(0, 0, 0));
        graphics.setFont(new Font("Ubuntu", Font.PLAIN, 25));
        graphics.drawString(getScoreString(), 10, 30);
    }

    private String getScoreString() {
        return "My chests [ " + countMyChests + " ]:[ " + countComputersChests + " ] Computer's chests";
    }

    private void drawDeckOfCard(Graphics graphics) {
        try {
            final BufferedImage cardBack = ImageIO.read(new File("src/images/card_backside.jpg"));
            graphics.drawImage(cardBack, Window.WIDTH - 320, 450, 150, 220, null);
            String str = Integer.toString(deck.size());
            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("Ubuntu", Font.PLAIN, 25));
            graphics.drawString(str, Window.WIDTH - 320 + 150 / 2 - 15, 450);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        if (getColorFromCardName(card) == Color.BLACK) {
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
            final BufferedImage colorCard = ImageIO.read(getColorCardURL(card.getColor()));
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

    private File getColorCardURL(String cardColor) {
        for (int i = 0; i < 4; ++i) {
            if (CardColor.values()[i].name().equals(cardColor)) {
                return getColorCardURL(CardColor.values()[i]);
            }
        }
        return null;
    }

    private File getColorCardURL(CardColor cardColor) {
        switch (cardColor) {
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
