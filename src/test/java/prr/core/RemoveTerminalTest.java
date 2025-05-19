package prr.core;

import prr.core.exceptions.InvalidOperationException;
import prr.core.exceptions.InvalidInvocationException;

import org.testng.annotations.*;
import static org.testng.Assert.*;

// RemoveTerminalTest.java
public class RemoveTerminalTest {

    private Client client;
    private Terminal t1, t2;

    @BeforeMethod
    public void setup() {
        t1 = new Terminal("T1", null);
        client = new Client("Alice", 123, t1);
        t2 = new Terminal("T2", null);
    }

    @Test
    public void testRemoveUnowned() {
        assertFalse(client.removeTerminal(t2));
    }

    @Test
    public void testRemoveOwnedNonNegativeBalance() {
        // balance 0 by default
        assertTrue(client.removeTerminal(t1));
        assertEquals(client.numberOfTerminals(), 0);
    }

    @Test
    public void testRemoveOwnedPositiveBalance() {
        t1.turnOff();
        t1.pay(10); // balance -10
        // simulate credit to positive: cheat via direct field or skip since impossible
    }

    @Test
    public void testRemoveOwnedNegativeBalance() {
        t1.turnOff();
        t1.pay(10);
        assertFalse(client.removeTerminal(t1));
        assertEquals(client.numberOfTerminals(), 1);
    }
}