public class Terminal {
    private String id;
    private Client client;
    private TerminalMode mode = TerminalMode.OFF;
    private int balance = 0; // cents
    private Communication ongoing;
    private TerminalMode prevMode;

    public Terminal(String id, Client client) {
        if (id == null || client == null)
            throw new InvalidOperationException("Invalid constructor args");
        this.id = id;
        this.client = client;
    }

    public final TerminalMode getMode() { return mode; }

    public void pay(int amount) {
        if (mode != TerminalMode.OFF || amount < 5)
            throw new InvalidInvocationException("Pay only off and >=5");
        balance -= amount;
    }

    public int balance() {
        if (mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Balance unavailable in BUSY");
        return balance;
    }

    public boolean sendSMS(Terminal to, String msg) {
        if (mode == TerminalMode.OFF || mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Cannot send SMS now");
        boolean delivered = to.receiveSMSInternal(this, msg);
        return delivered;
    }

    private boolean receiveSMSInternal(Terminal from, String msg) {
        if (mode == TerminalMode.OFF)
            return false;
        if (mode == TerminalMode.SILENT && !client.hasFriend(from.client))
            return false;
        // store or process msg
        return true;
    }

    public void receiveSMS(Terminal from, String msg) {
        throw new InvalidInvocationException("Use sendSMS to deliver");
    }

    public void makeVoiceCall(Terminal to) {
        if (mode == TerminalMode.OFF || mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Cannot start call");
        // check recipient can receive
        if (to.mode == TerminalMode.OFF || to.mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Target unavailable");
        Communication c = new Communication(CommunicationType.VOICE, this, to);
        this.prevMode = this.mode;
        to.prevMode = to.mode;
        this.mode = TerminalMode.BUSY;
        to.mode = TerminalMode.BUSY;
        this.ongoing = c;
        to.ongoing = c;
    }

    void acceptVoiceCall(Communication c) {
        if (mode != TerminalMode.NORMAL)
            throw new InvalidInvocationException("Cannot accept call");
        // already set BUSY in caller side
    }

    public void endOngoingCommunication() {
        if (ongoing == null)
            throw new InvalidInvocationException("No ongoing call");
        ongoing.end();
        this.mode = prevMode;
        client.updatePoints(0); // no change
        Terminal peer = (ongoing.from == this) ? ongoing.to : ongoing.from;
        peer.mode = peer.prevMode;
        this.ongoing = null;
        peer.ongoing = null;
    }

    public void turnOn() {
        if (mode != TerminalMode.OFF)
            throw new InvalidInvocationException("Already on");
        mode = TerminalMode.NORMAL;
    }

    public void turnOff() {
        if (mode == TerminalMode.BUSY)
            throw new InvalidInvocationException("Cannot turn off while busy");
        mode = TerminalMode.OFF;
    }

    public void toggleOnMode() {
        if (mode == TerminalMode.NORMAL)
            mode = TerminalMode.SILENT;
        else if (mode == TerminalMode.SILENT)
            mode = TerminalMode.NORMAL;
        else
            throw new InvalidInvocationException("Can only toggle when on and idle");
    }
}

