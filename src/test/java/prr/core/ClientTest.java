package prr.core;

import prr.core.exceptions.InvalidOperationException;

import org.testng.annotations.*;
import static org.testng.Assert.*;

public class ClientTest {
  private Terminal t1, t2;
  private Client alice, bob;

  @BeforeMethod
  public void setup() {
    t1 = new Terminal("T1", null);
    t2 = new Terminal("T2", null);
    alice = new Client("Alice", 111, t1);
    bob   = new Client("Bob",   222, t2);
  }

  // ───── Success cases ─────

  @Test
  public void ctorValid() {
    assertEquals(alice.getName(), "Alice");
    assertEquals(alice.numberOfTerminals(), 1);
    assertEquals(alice.getPoints(), 20);
  }

  @Test
  public void updatePointsWithinRange() {
    alice.updatePoints(30);
    assertEquals(alice.getPoints(), 50);
    alice.updatePoints(-20);
    assertEquals(alice.getPoints(), 30);
  }

  @Test
  public void addAndRemoveFriendValid() {
    alice.addFriend(bob);
    assertTrue(alice.hasFriend(bob));
    assertTrue(alice.removeFriend(bob));
    assertFalse(alice.hasFriend(bob));
  }

  @Test
  public void addAndRemoveTerminalValid() {
    Terminal t3 = new Terminal("T3", alice);
    alice.addTerminal(t3);
    assertEquals(alice.numberOfTerminals(), 2);
    assertTrue(alice.removeTerminal(t3));
    assertEquals(alice.numberOfTerminals(), 1);
  }

  // ───── Failure cases ─────

  @Test(expectedExceptions = InvalidOperationException.class)
  public void ctorInvalidNameNull() {
    new Client(null, 123, t1);
  }

  @Test(expectedExceptions = InvalidOperationException.class)
  public void updatePointsOutOfRange() {
    alice.updatePoints(-21);
  }

  @Test(expectedExceptions = InvalidOperationException.class)
  public void addFriendSelf() {
    alice.addFriend(alice);
  }

  @Test
  public void removeTerminalNegativeBalance() {
    // simulate negative balance
    Terminal debt = new Terminal("D1", alice);
    // (force internal state) ... assume we have a method: debt.pay(1000);
    debt.turnOff();
    debt.pay(1000);
    alice.addTerminal(debt);
    assertFalse(alice.removeTerminal(debt));
  }
}
