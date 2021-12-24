import lombok.ToString;

@ToString
public enum CardColor {

    SPADES(0),
    HEARTS(1),
    DIAMONDS(2),
    CLUBS(3);

    private int num;

    CardColor(int num) {
        this.num = num;
    }

    public CardColor getColorByIndex(int index) {
        return CardColor.values()[index];
    }

    public int getPosition() {
        return num;
    }

    @Override
    public String toString() {
        return "CardColor{" +
                "num=" + num +
                '}';
    }
}
