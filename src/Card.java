import lombok.*;

public class Card {
    private CardName cardName;
    private CardColor color;

    public Card(CardName cardName, CardColor color) {
        this.cardName = cardName;
        this.color = color;
    }

    public CardName getCardName() {
        return cardName;
    }

    public void setCardName(CardName cardName) {
        this.cardName = cardName;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
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
