package prr.core;

import prr.core.exceptions.InvalidOperationException;
import prr.core.exceptions.InvalidInvocationException;

import org.testng.annotations.*;
import static org.testng.Assert.*;
import java.lang.reflect.*;

public class CommunicationTest {

    private Client from, to;
    private Terminal tFrom, tTo;

    @BeforeMethod
    public void setup() {
        tFrom = new Terminal("F1", new Client("From", 1, new Terminal("F0", null)));
        tTo   = new Terminal("T1", new Client("To",   2, new Terminal("T0", null)));
        from = tFrom.getClient();
        to   = tTo.getClient();
    }

    @Test
    public void testComputeCostSizeZero() throws Exception {
        Communication c = Communication.textCommunication(tTo, tFrom, 0);
        c.duration(0);
        Method compute = Communication.class.getDeclaredMethod("computeCost");
        compute.setAccessible(true);
        double cost = (double) compute.invoke(c);
        assertEquals(cost, 0.0);
    }

    @Test
    public void testComputeCostSmallSizes() throws Exception {
        from.updatePoints(101 - from.getPoints()); // set >100
        Communication c1 = Communication.textCommunication(tTo, tFrom, 5);
        Method compute = Communication.class.getDeclaredMethod("computeCost");
        compute.setAccessible(true);
        assertEquals((double)compute.invoke(c1), 1.0);
        from.updatePoints(-50); // now <=100
        Communication c2 = Communication.textCommunication(tTo, tFrom, 5);
        assertEquals((double)compute.invoke(c2), 2.0);
    }

    @Test
    public void testComputeCostMediumText() throws Exception {
        from.updatePoints(80 - from.getPoints()); // set >=75
        Communication c = Communication.textCommunication(tTo, tFrom, 50);
        Method compute = Communication.class.getDeclaredMethod("computeCost");
        compute.setAccessible(true);
        assertEquals((double)compute.invoke(c), 4.0);
    }

    @Test
    public void testComputeCostMediumVoiceFriendEffect() throws Exception {
        from.updatePoints(80 - from.getPoints()); // points>=75
        // add <4 friends
        for (int i = 0; i < 3; i++) from.addFriend(new Client("X"+i, 10+i, new Terminal("Z"+i, null)));
        Constructor<Communication> ctor = Communication.class.getDeclaredConstructor(CommunicationType.class, Terminal.class, Terminal.class);
        ctor.setAccessible(true);
        Communication cv = ctor.newInstance(CommunicationType.VOICE, tFrom, tTo);
        cv.duration(50);
        Method compute = Communication.class.getDeclaredMethod("computeCost");
        compute.setAccessible(true);
        assertEquals((double)compute.invoke(cv), 8.0);
        // add 1 more friend => 4 total
        from.addFriend(new Client("Y", 20, new Terminal("Z4", null)));
        assertEquals((double)compute.invoke(cv), 5.0);
    }

    @Test
    public void testComputeCostLargeSizes() throws Exception {
        from.updatePoints(140 - from.getPoints()); // <150
        Communication c1 = Communication.textCommunication(tTo, tFrom, 200);
        Method compute = Communication.class.getDeclaredMethod("computeCost");
        compute.setAccessible(true);
        assertEquals((double)compute.invoke(c1), 15.0);
        from.updatePoints(200 - from.getPoints()); // >=150
        assertEquals((double)compute.invoke(c1), 12.0);
    }
}

