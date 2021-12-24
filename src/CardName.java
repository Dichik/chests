
public enum CardName {

    SIX(0),
    SEVEN(1),
    EIGHT(2),
    NINE(3),
    TEN(4),
    JACK(5),
    QUEEN(6),
    KING(7),
    ACE(8);

    private int num;

    CardName(int num) {
        this.num = num;
    }

    public CardName getCardName(int index) {
        return CardName.values()[index];
    }

    public int getPosition() {
        return this.num;
    }

    @Override
    public String toString() {
        return "CardName{" +
                "num=" + num +
                '}';
    }

}
