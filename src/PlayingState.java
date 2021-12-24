import java.awt.*;
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

        while(!deck.isEmpty()) {
            System.out.println(deck.pop());
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics graphics) {

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
        for(int i = 0; i < NUMBER_OF_CARDS_IN_DECK / 4; ++ i) {
            for(int j = 0; j < 4; ++ j) {
                //System.out.println(CardName.values()[i] + " " + CardColor.values()[j]);
                cardList.add(new Card(CardName.values()[i], CardColor.values()[j]));
            }
        }
        shuffleCards(cardList);
    }

}
