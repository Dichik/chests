import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlayingState extends GameState {

    private Stack<Card> cards;
    private Map<Card, Integer> map;

    private List<Card> myCards;
    private List<Card> computerCards;

    private final Integer INIT_NUMBER_OF_CARDS_TO_TAKE = 8;

    @Override
    protected void init() {
        cards = new Stack<>();
        shuffleCards();
        myCards = new ArrayList<>();
        computerCards = new ArrayList<>();
        map = new HashMap<>();
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

    private void shuffleCards() {
        List<Card> cardList = new ArrayList<>();
        Collections.shuffle(cardList);
        cards.addAll(new ArrayList<>(cardList));
    }

    private void rozGive() {
        for (int i = 0; i < INIT_NUMBER_OF_CARDS_TO_TAKE; ++i) {
            if (i % 2 == 0) {
                myCards.add(cards.pop());
            } else {
                computerCards.add(cards.pop());
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

}
