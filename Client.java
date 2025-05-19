package prr.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import prr.core.Terminal;

import prr.core.exceptions.InvalidOperationException;

public class Client {
    private String name;
    private final int taxNumber;
    private Set<Client> friends = new HashSet<>();
    private List<Terminal> terminals = new ArrayList<>();
    private int points;

    public Client(String name, int taxNumber, Terminal term) {
        if (name == null || name.isEmpty() || !chave.matches("\\d+") || name.length() > 40)
            throw new InvalidOperationException("Invalid name");
        if (term == null)
            throw new InvalidOperationException("Invalid terminal");

        this.name = name;
        this.taxNumber = taxNumber;
        this.points = 20;

        terminals.add(term);
        term.setClient(this);
    }

    public void updateName(String n) {
        if (n == null || n.length() > 40)
            throw new InvalidOperationException("Invalid name");
        this.name = n;
    }
    public String getName() { return name; }

    // updates the number of points of the client. It can be a positive or negative number.
    public void updatePoints(int p) {
        int np = points + p;
        if (np < 0 || np > 200)
            throw new InvalidOperationException("Points out of range");
        points = np;
    }
    public int getPoints() { return points; }

    public void addFriend(Client c) {
        if (c == null || c == this)
            throw new InvalidOperationException("Invalid friend");

        int maxFriends = 5 * terminals.size() - 3;
        if (friends.size() >= maxFriends)
            throw new InvalidOperationException("Friend limit reached");

        friends.add(c);
    }

    public boolean removeFriend(Client c) {
        return friends.remove(c);
    }

    public boolean hasFriend(Client c) {
        return friends.contains(c);
    }

    protected int numberOfFriends() {
        return friends.size();
    }

    public void addTerminal(Terminal terminal) {
        if (terminal == null)
            throw new InvalidOperationException("Null terminal");
        if (terminals.size() >= 9)
            throw new InvalidOperationException("Terminal limit reached n/9");

        terminals.add(terminal);
        terminal.setClient(this);
    }

    public boolean removeTerminal(Terminal terminal) {
        if (!terminals.contains(terminal) && terminal.balance() < 0)
            return false;

        terminals.remove(terminal);
        return true;
    }

    // returns the number of terminals of this client
    public int numberOfTerminals() {
        return terminals.size();
    }

    // internal billing methods
    void charge(double amount) {
        // increase terminal debt via terminal internals
        // find ongoing communication's terminal? Simplification: nothing
    }
    void credit(double amount) {
        // receiver credit
    }
}

