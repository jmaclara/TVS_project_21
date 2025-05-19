package prr.core;
import ppr.core.Communication;

import prr.exceptions.InvalidInvocationException;

public enum TerminalMode { OFF, NORMAL, SILENT, BUSY; }

public class Terminal {
    private final String id;
    private Client client;
    private int balance = 0; // cents
    private TerminalMode mode = TerminalMode.OFF;
    private TerminalMode prevMode;
    private Communication ongoing;

    // creates a terminal with a given identifier and associated to the given client.
    public Terminal(String id, Client client) {
        if (id == null || id.isEmpty() || client == null)
            throw new InvalidInvocationException("Invalid constructor args");
        this.id = id;
        this.client = client;
    }
    // Returns the mode of this terminal
    public final TerminalMode getMode() { return mode; }

    public final Client getClient() { return client; }
    public void setClient(Client c) {
        if (c == null)
            throw new InvalidInvocationException("Invalid client");
        this.client = c;
    }

    // Decreases the debt of this terminal by the given amount. The amount must be a number greater than 5 cents
    public void pay(int amount) {
        if (mode != TerminalMode.OFF || amount < 5)
            throw new InvalidInvocationException("Pay only off and >=5");
        balance -= amount;
    }

    // returns the balance of this terminal
    public int balance() {
        if (mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Balance unavailable in BUSY");
        return balance;
    }

    // send a SMS to terminal to with text msg. Returns if the SMS was successfully delivered.
    public boolean sendSMS(Terminal to, String msg) {
        if (mode == TerminalMode.OFF || mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Cannot send SMS now");

        if (to.getMode() == TerminalMode.OFF)
            return false;
        if (to.getMode() == TerminalMode.SILENT && !to.getClient().hasFriend(client))
            return false;
        
        receiveSMS(this, msg);
        return true;
    }

    // receives a SMS from terminal from with text msg
    public void receiveSMS(Terminal from, String msg) {
        // Does something with the message
    }

    // start a voice call with tetminal to
    public void makeVoiceCall(Terminal to) {
        if (mode == TerminalMode.OFF || mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Cannot start call");
        if (to.getMode() != TerminalMode.NORMAL)
            throw new InvalidInvocationException("Target unavailable");

        Communication c = Communication.voiceCommunication(this, to);
        prevMode = mode;
        mode = TerminalMode.BUSY;
        ongoing = c;
    }

    // to invoke over the receiving terminal of a voice call (represented by c). The voice
    // call is established if the terminal accepts the call, otherwise it throws an exception.
    void acceptVoiceCall(Communication c) {
        if (mode != TerminalMode.NORMAL)
            throw new InvalidInvocationException("Cannot accept call");
        prevMode = mode;
        mode = TerminalMode.BUSY;
        ongoing = c;
    }

    // turns on this terminal
    public void turnOn() {
        if (mode != TerminalMode.OFF)
            throw new InvalidInvocationException("Already on");
        mode = TerminalMode.NORMAL;
    }

    // turns off this terminal
    public void turnOff() {
        if (mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Cannot turn off while busy");
        mode = TerminalMode.OFF;
    }

    // toggles the On mode: normal to silent or silent to normal
    public void toggleOnMode() {
        if (mode == TerminalMode.NORMAL)
            mode = TerminalMode.SILENT;
        else if (mode == TerminalMode.SILENT)
            mode = TerminalMode.NORMAL;
        else
            throw new InvalidInvocationException("Can only toggle when on and idle");
    }

    // Ends the ongoing communication.
    public void endOngoingCommunication() {
        if (ongoing == null)
            throw new InvalidInvocationException("No ongoing call");

        ongoing.end();
        this.mode = prevMode;
        //client.updatePoints(0); // no change
        Terminal peer = (ongoing.from == this) ? ongoing.to : ongoing.from;
        peer.mode = peer.prevMode;
        this.ongoing = null;
        peer.ongoing = null;
    }
}

