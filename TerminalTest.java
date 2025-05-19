package prr.core;

import org.testng.annotations.*;
import static org.testng.Assert.*;

public class TerminalTest {

    private Client client;
    private Terminal t1, t2;

    @BeforeMethod
    public void setup() {
        client = new Client("Alice", 111, new Terminal("T1", null));
        t1 = new Terminal("T1", client);
        t2 = new Terminal("T2", client);
    }

    @Test
    public void testTurnOnOff() {
        assertEquals(t1.getMode(), TerminalMode.OFF);
        t1.turnOn();
        assertEquals(t1.getMode(), TerminalMode.NORMAL);
        t1.turnOff();
        assertEquals(t1.getMode(), TerminalMode.OFF);
    }

    @Test(expectedExceptions = InvalidInvocationException.class)
    public void testInvalidTurnOn() {
        t1.turnOn();
        t1.turnOn();
    }

    @Test
    public void testPayAndBalance() {
        // pay >=5 in OFF
        t1.turnOff();
        t1.pay(10);
        assertEquals(t1.balance(), -10);
    }

    @Test(expectedExceptions = InvalidInvocationException.class)
    public void testInvalidPayAmount() {
        t1.pay(4);
    }

    @Test(expectedExceptions = InvalidInvocationException.class)
    public void testInvalidPayWhenOn() {
        t1.turnOn();
        t1.pay(10);
    }

    @Test
    public void testToggleMode() {
        t1.turnOn();
        assertEquals(t1.getMode(), TerminalMode.NORMAL);
        t1.toggleOnMode();
        assertEquals(t1.getMode(), TerminalMode.SILENT);
        t1.toggleOnMode();
        assertEquals(t1.getMode(), TerminalMode.NORMAL);
    }

    @Test(expectedExceptions = InvalidInvocationException.class)
    public void testInvalidToggle() {
        t1.toggleOnMode(); // OFF → invalid
    }

    @Test
    public void testSendSMSDelivered() {
        t1.turnOn();
        t2.turnOn();
        assertTrue(t1.sendSMS(t2, "Hello"));
    }

    @Test(expectedExceptions = InvalidInvocationException.class)
    public void testSendSMSWhenOff() {
        t1.sendSMS(t2, "Hi");
    }

    @Test
    public void testSendSMSSilentNotFriend() {
        t1.turnOn();
        t2.turnOn();
        t2.toggleOnMode(); // SILENT
        assertFalse(t1.sendSMS(t2, "Msg"));
    }

    @Test
    public void testSendSMSSilentFriend() {
        Client bob = new Client("Bob", 222, t2);
        client.addFriend(bob);
        t1.turnOn(); t2.turnOn(); t2.toggleOnMode();
        assertTrue(t1.sendSMS(t2, "Msg"));
    }

    @Test
    public void testVoiceCallAndBusyState() {
        t1.turnOn(); t2.turnOn();
        t1.makeVoiceCall(t2);
        assertEquals(t1.getMode(), TerminalMode.BUSY);
        assertEquals(t2.getMode(), TerminalMode.BUSY);
        assertThrows(InvalidInvocationException.class, () -> t1.balance());
        t1.endOngoingCommunication();
        assertNotEquals(t1.getMode(), TerminalMode.BUSY);
        assertNotEquals(t2.getMode(), TerminalMode.BUSY);
    }

    @Test(expectedExceptions = InvalidInvocationException.class)
    public void testVoiceCallWhenTargetOff() {
        t1.turnOn();
        // t2 remains OFF
        t1.makeVoiceCall(t2);
    }
}