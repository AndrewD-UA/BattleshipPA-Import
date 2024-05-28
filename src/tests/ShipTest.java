package tests;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import javafx.geometry.Point2D;
import model.battleship.Ship;
import model.battleship.ShipCollection;
import model.board.Direction;

public class ShipTest {
	
	
	public void testAll() {
		testSetUp();
		testInitialState();
		testCollectionInit();
		testDamages();
		testShipDamage();
		testEmptyCollection();
		testDirection();
	}
	
    @Test
    public void testSetUp() {
        Point2D start = new Point2D(3, 4); 
        Direction facing = Direction.UP; 
        int size = 3;
        Ship s = new Ship(start, facing, size, false);
        assertEquals(false, s.isSunk());
    }

    @Test
    public void testInitialState() {
    	Point2D startPos = new Point2D(3, 4);
    	Ship ship = new Ship(startPos, Direction.UP, 3, false);
        assertEquals(Direction.UP, ship.getFacingDirection());
        assertEquals(false, ship.isSunk());
        assertEquals(3 , ship.getLength());
        assertEquals("Ship of length 3 at: 3, 4\nDamages: [false, false, false]", ship.toString());
        assertEquals(startPos, ship.getStartPos());
        
        assertEquals(false, ship.hitShip(-1, false));
        assertEquals(true, ship.hitShip(0, false));
        assertEquals(true, ship.hitShip(1, false));
        assertEquals(true, ship.hitShip(2, false));
        assertEquals(true, ship.isSunk());
    }
    
    @Test
    public void testShipDamage() {
    	Ship ship = new Ship(new Point2D(3, 4), Direction.UP, 3, false);
        
        assertEquals(false, ship.hitShip(-1, false));
        assertEquals(false, ship.hitShip(3, false));
        
        assertEquals(true, ship.hitShip(0, false));
        assertEquals(true, ship.hitShip(1, false));
        assertEquals(true, ship.hitShip(2, false));
        assertEquals(true, ship.isSunk());
    }
    
    //SHIP COLLECTION TESTS TO FOLLOW
    
    Ship[] ships = new Ship[] {
    		new Ship(new Point2D(0, 0), Direction.DOWN, 2, false),
    		new Ship(new Point2D(1, 0), Direction.DOWN, 3, false),
    		new Ship(new Point2D(2, 0), Direction.DOWN, 3, true),
    		new Ship(new Point2D(3, 0), Direction.DOWN, 4, false),
    		new Ship(new Point2D(4, 0), Direction.DOWN, 5, false)
    };
    
    String[] shipPaths = new String[] {
    		"/img/patrolTDL.png",
    		"/img/destroyerTDL.png",
    		"/img/subTopDownLeft.png",
    		"/img/battleTDL.png",
    		"/img/carrTrans.png"
    };

    @Test
    public void testCollectionInit() {
    	ShipCollection sc = new ShipCollection(ships);
    	assertEquals(5, sc.size());
    	
    	int index = 0;
    	for (Ship s: sc) {
    		assertEquals(s.getImagePath(), shipPaths[index]);
    		if (index != 2) {
    			assertEquals(false, s.isSub());
    		} else {
    			assertEquals(true, s.isSub());
    		}
    		index++;
    	}
    	
    	assertEquals(false, sc.isEmpty());
    	assertEquals(2, sc.getShip(0).getLength());
    	assertEquals(false, sc.addShip(new Ship(new Point2D(0, 0), Direction.UP, 3, false)));
    	
    	System.out.println(sc.toString());    	
    }
    
    @Test
    public void testEmptyCollection() {
    	ShipCollection sc = new ShipCollection();
    	assertEquals(0, sc.size());
    	sc.addShip(ships[0]);
    	assertEquals(1, sc.size());
    	sc.toString();
    }
    
    @Test
    public void testDamages() {
    	ShipCollection sc = new ShipCollection(ships);
    	
    	assertEquals(false, sc.tryMove(new Point2D(10, 10), false));
    	
    	assertEquals(true, sc.tryMove(new Point2D(0, 0), false));
    	assertEquals(true, sc.tryMove(new Point2D(0, 1), false));
    	assertEquals(4, sc.size());                    
    	
    	assertEquals(true, sc.tryMove(new Point2D(1, 0), false));
    	assertEquals(true, sc.tryMove(new Point2D(1, 1), false));
    	assertEquals(true, sc.tryMove(new Point2D(1, 2), false));
    	assertEquals(3, sc.size());                   
    	
    	assertEquals(true, sc.tryMove(new Point2D(2, 0), false));
    	assertEquals(true, sc.tryMove(new Point2D(2, 1), false));
    	assertEquals(true, sc.tryMove(new Point2D(2, 2), false));
    	assertEquals(2, sc.size());                    
    	
    	assertEquals(true, sc.tryMove(new Point2D(3, 0), false));
    	assertEquals(true, sc.tryMove(new Point2D(3, 1), false));
    	assertEquals(true, sc.tryMove(new Point2D(3, 2), false));
    	assertEquals(true, sc.tryMove(new Point2D(3, 3), false));
    	assertEquals(1, sc.size());                    
    	
    	assertEquals(true, sc.tryMove(new Point2D(4, 0), false));
    	assertEquals(true, sc.tryMove(new Point2D(4, 1), false));
    	assertEquals(true, sc.tryMove(new Point2D(4, 2), false));
    	assertEquals(true, sc.tryMove(new Point2D(4, 3), false));
    	assertEquals(true, sc.tryMove(new Point2D(4, 4), false));
    	assertEquals(0, sc.size());
    }
    
    @Test
    public void testDirection() {
    	Direction d = Direction.UP;
    	assertEquals(Direction.RIGHT, d.rotateRight());
    }
}
