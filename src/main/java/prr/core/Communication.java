package prr.core;

import prr.core.exceptions.InvalidInvocationException;

/**
* This class represents a communication (text or voice) made between
* two terminals.
**/
public class Communication {
    private CommunicationType type;
    private Terminal from;
    private Terminal to;
    private int size; // for SMS: characters/100 round up; for VOICE: seconds
    private double cost;
    private boolean ended = false;

    private Communication(CommunicationType type, Terminal to, Terminal from) {
        this.type = type;
        this.from = from;
        this.to = to;
    }

    public static Communication textCommunication(Terminal to, Terminal from, int length) {
        Communication c = new Communication(CommunicationType.SMS, to, from);
        c.size = (length + 99) / 100;
        return c;
    }

    public static Communication textCommunication(Terminal to, Terminal from) {
        return textCommunication(to, from, 0);
    }

    public static Communication voiceCommunication(Terminal to, Terminal from, int length) {
        Communication c = new Communication(CommunicationType.VOICE, to, from);
        c.size = (length + 99) / 100;
        return c;
    }

    public static Communication voiceCommunication(Terminal to, Terminal from) {
        return voiceCommunication(to, from, 0);
    }

    public void duration(int duration) {
        if (type != CommunicationType.VOICE)
            throw new InvalidInvocationException("duration only for voice");
        this.size = duration;
    }

    public Terminal to() { return to; }
    public Terminal from() { return from; }

    double computeCost() {
        // cost in cents
        if (size == 0) {
            cost = 0;
        } else if (size < 10) {
            cost = (from.getPoints() > 100) ? 1 : 2;
        } else if (size < 120) {
            if (from.getPoints() < 75) {
                cost = (type == CommunicationType.SMS) ? 6 : 12;
            } else {
                if (type == CommunicationType.SMS) {
                    cost = 4;
                } else {
                    cost = (from.numberOfFriends() < 4) ? 8 : 5;
                }
            }
        } else {
            cost = (from.getPoints() < 150) ? 15 : 12;
        }
        return cost;
    }

    public double getCost() {
        if (!ended)
            throw new InvalidInvocationException("Cost only available after end");
        return cost;
    }

    // mark ended and apply balances
    void end() {
        ended = true;
        computeCost();
        from.charge(cost);
        to.credit(0);
    }
}