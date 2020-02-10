package net.neczpal.bjcounter.cards;

import net.neczpal.bjcounter.countings.CountingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Shoe {

    private List<Card> cards;

    private double hiLoCount;
    private double omegaCount;
    private double wongHalvesCount;
    private double aceCount;

    private double[] allCount = new double[CountingType.values ().length];
    private CountingType[] allCountingTypes = CountingType.values ();

    private int numberOfDecks;

    public Shoe() {
        this(1);
    }

    public Shoe(int numberOfDecks) {
        this.numberOfDecks = numberOfDecks;
        resetShoe();
    }

    public Shoe(List<Card> cards) {
        this.cards = cards;
    }

    public void resetShoe() {
        cards  = new ArrayList<>();

        for(int i = 0; i < numberOfDecks; ++i) {
            for (CardValue value : CardValue.values()){
                for(SuitValue suit : SuitValue.values()) {
                    if(suit != SuitValue.ANY)
                        cards.add(new Card(value, suit));
                }
            }
        }
        hiLoCount = 0;
        omegaCount = 0;
        wongHalvesCount = 0;
        aceCount = 0;

        allCount = new double[CountingType.values ().length];

        shuffleCards();
    }

    public boolean removeCard (Card card) {
        return cards.remove(card);
    }

    //Fisher–Yates shuffle
    public void shuffleCards() {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = cards.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Card iCard = cards.get(index);
            cards.set(index, cards.get(i));
            cards.set(i, iCard);
        }
    }

    public Card dealCard() {
        Card iCard = cards.get(0);
        cards.remove(0);

        int cardValue = iCard.getValue().value;

        for (int i = 0; i < allCountingTypes.length; i++) {
            if(cardValue == 11) {
                allCount[i] += allCountingTypes[i].getValue (0);
            } else {
                allCount[i] += allCountingTypes[i].getValue (cardValue - 1);
            }
        }

        //HI LO COUNT
        if(cardValue >= 10) {
            hiLoCount--;
        } else
        if(cardValue <= 6) {
            hiLoCount++;
        }

        //OMEGA COUNT
        switch (cardValue){
            case 2:
            case 3:
            case 7:
                omegaCount += 1;
                break;
            case 4:
            case 5:
            case 6:
                omegaCount += 2;
                break;
            case 9:
                omegaCount -= 1;
                break;
            case 10:
                omegaCount -= 2;
                break;
        }

        //WONG HALVES COUNT
        switch (cardValue){
            case 2:
            case 7:
                wongHalvesCount += 0.5;
                break;
            case 3:
            case 4:
            case 6:
                wongHalvesCount += 1;
                break;
            case 5:
                wongHalvesCount += 1.5;
                break;
            case 9:
                wongHalvesCount -= 0.5;
                break;
            case 10:
            case 11:
                wongHalvesCount -= 1;
                break;
        }

        return iCard;
    }

    public Card dealSpecificCard(Card card) throws Exception{
        if (!cards.remove(card)){
            throw new Exception("Nincs mar az adott lap a pakliban!");
        }


        int cardValue = card.getValue().value;

        for (int i = 0; i < allCountingTypes.length; i++) {
            if(cardValue == 11) {
                allCount[i] += allCountingTypes[i].getValue (0);
            } else {
                allCount[i] += allCountingTypes[i].getValue (cardValue - 1);
            }
        }

        if (cardValue == 11) {
            aceCount++;
        }

        //HI LO COUNT
        if(cardValue >= 10) {
            hiLoCount--;
        } else
        if(cardValue <= 6) {
            hiLoCount++;
        }

        //OMEGA COUNT
        switch (cardValue){
            case 2:
            case 3:
            case 7:
                omegaCount += 1;
                break;
            case 4:
            case 5:
            case 6:
                omegaCount += 2;
                break;
            case 9:
                omegaCount -= 1;
                break;
            case 10:
                omegaCount -= 2;
                break;
        }

        //WONG HALVES COUNT
        switch (cardValue){
            case 2:
            case 7:
                wongHalvesCount += 0.5;
                break;
            case 3:
            case 4:
            case 6:
                wongHalvesCount += 1;
                break;
            case 5:
                wongHalvesCount += 1.5;
                break;
            case 9:
                wongHalvesCount -= 0.5;
                break;
            case 10:
            case 11:
                wongHalvesCount -= 1;
                break;
        }


        return card;
    }

    public double getCount(CountingType type) {
        return  allCount[type.ordinal ()] / (cards.size()/52.0);
//        return type.isBalanced () ? allCount[type.ordinal ()] / (cards.size()/52.0) : allCount[type.ordinal ()];
    }

    public double getHiLoCount(){
        return hiLoCount;
    }

    public double getHiLoTrueCount() {
        return hiLoCount / (cards.size()/52.0);
    }

    public double getOmegaCount(){
        return omegaCount;
    }

    public double getOmegaTrueCount() {
        return omegaCount / (cards.size()/52.0);
    }

    public double getWongHalvesCount(){
        return wongHalvesCount;
    }

    public double getWongHalvesTrueCount() {
        return wongHalvesCount / (cards.size()/52.0);
    }

    public double getAcesCount() {
        return (numberOfDecks * 4 - aceCount) / cards.size();
    }

    public boolean isInsuranceWorthIt() {
        double numTens = 0.;
        for (Card card : cards) {
            if (card.getValue().value == 10)
                numTens++;
        }

        return numTens / cards.size() > 0.3333333;
    }

    public int getCardsLeftCount () {
        return cards.size();
    }

    public void writeAllCards() {
        int[] counts = new int[10];

        for (Card card : cards) {
            counts[card.getValue().value - 2]++;
        }

        for (int i = 0; i < 10; i++) {
            System.out.print((i + 2) + ": " + counts[i] + "\n");
        }
        System.out.println();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Card card : cards) {
            stringBuilder.append(card + "\n");
        }

        return stringBuilder.toString();
    }

}
