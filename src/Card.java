import lombok.*;

@Getter
@Setter
public class Card {
    private CardName cardName;
    private CardColor color;

    public Card(CardName cardName, CardColor color) {
        this.cardName = cardName;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardName=" + cardName +
                ", color=" + color +
                '}';
    }
}
