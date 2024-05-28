package controller_view.board;

import java.beans.PropertyChangeListener;

import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import model.battleship.Ship;
import model.battleship.ShipCollection;
import model.board.Board;
import model.board.Direction;

/**
 * A container which holds a BoardViewer and a BoardOverlay at the same
 * positions.
 * 
 * @author Andrew Dennison
 */
public class BoardContainer extends StackPane {

	private BoardViewer bv;
	private BoardOverlay bd;

	private Board board;

	private double cellSize;

	private static final int BOARD_SPACES = 10;
	
	private SnapshotParameters params;
	private static final BackgroundRepeat BR = BackgroundRepeat.NO_REPEAT;
	
	/**
	 * Create a new BoardContainer of a given size to represent a given Board
	 * 
	 * @param size 			The size in pixels to make this container
	 * @param b    			The Board to represent in this container
	 * @param isAI			False if the board should not include highlight selections, True if it should
	 * @param showPreview	True if the container should fire mouseOverEvents, false otherwise
	 */
	public BoardContainer(double size, Board b, boolean isAI, boolean showPreview) {
		super();
		cellSize = size / BOARD_SPACES;
		board = b;

		setMaxWidth(size);
		setMinWidth(size);
		setMaxHeight(size);
		setMaxHeight(size);
		
		bv = new BoardViewer(cellSize, isAI, showPreview);
		bd = new BoardOverlay(cellSize);
		
		params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		
		getChildren().addAll(bv, bd);
	}

	/**
	 * Create a new BoardContainer with an empty Board
	 * 
	 * @param size			size in pixels of the container
	 * @param isAI			True if the board includes highlighting, false otherwise
	 * @param showPreview	True if the container should fire mouseOverEvents, false otherwise
	 */
	public BoardContainer(double size, boolean isAI, boolean showPreview) {
		this(size, new Board(new ShipCollection()), isAI, showPreview);
	}

	/**
	 * Initiate a move on the underlying Board at a given position
	 * 
	 * @param pos Point2D to make the move at
	 * @return 0 if false, 1 if true
	 */
	public int makeMoveOnBoard(Point2D pos) {
		int hit = board.enterMove(pos, false);

		// update the boardviewer
		refreshBoardViewer();

		// update the boardoverlay
		bd.playMoveAnimation(pos, hit);

		return hit;
	}

	/**
	 * Add a shield on underlying Board at a given position
	 * 
	 * @param pos Point2D to add Shield at
	 * @return 0 if false, -1 if true
	 */
	public int shieldMove(Point2D pos) {
		int shipBox = board.placeShield(pos);
		// update the boardviewer
		refreshBoardViewer();

		// update the boardoverlay
		bd.playMoveAnimation(pos, shipBox);
		return shipBox;
	}
	
	/**
	 * Generate the underlay of all ships at the correct positions on the board
	 * 
	 * @return Generated image
	 */
	private Background generateBackground(ShipCollection allShips) {
		if (allShips.size() == 0) {
			return null;
		}
		
		BackgroundImage[] imgs = new BackgroundImage[5];		
		for (int i = 0; i < 5; i++) {			
			// Get the image for the current ship
			Ship currentShip = allShips.getShip(i);
			
			if (currentShip == null || !currentShip.isRevealed()) {
				continue;
			}			
			
			imgs[i] = renderShip(currentShip);
		}
		
		return new Background(imgs);
	}
	
	/**
	 * Render a preview of a Ship that has not been placed on the board
	 * @param s		Ship to render
	 */
	public void renderPreviewAt(Ship s) {
		if (s == null) {
			return;
		}
		
		setBackground(new Background(renderShip(s)));
	}

	private BackgroundImage renderShip(Ship currentShip) {	
		String imgPath = getClass().getResource(currentShip.getImagePath()).toExternalForm();
		Image shipImage = new Image(imgPath, cellSize * currentShip.getLength(), cellSize, false, false);

		// Rotate the ship according to the ship's orientation
		ImageView imageView = new ImageView(shipImage);
		imageView.setRotate(currentShip.getFacingDirection().direction() * 90);

		// Determine where the ship's image should be placed on the board
		double xPos = currentShip.getStartPos().getX() * cellSize;
		if (currentShip.getFacingDirection() == Direction.LEFT) {
			Rotate flipRotation = new Rotate(180,Rotate.X_AXIS);
			imageView.getTransforms().addAll(flipRotation);
			xPos -= (currentShip.getLength() - 1) * cellSize;
		}

		double yPos = currentShip.getStartPos().getY() * cellSize;
		if (currentShip.getFacingDirection() == Direction.UP) {
			yPos -= (currentShip.getLength() - 1) * cellSize;
		}
		Image rotatedImage = imageView.snapshot(params, null);
		
		BackgroundPosition bgPosition = new BackgroundPosition(Side.LEFT, xPos, false, Side.TOP, yPos, false);
		// scaled ship image and position
		return new BackgroundImage(rotatedImage, BR, BR, bgPosition, BackgroundSize.DEFAULT);
	}

	/**
	 * Register a listener to the underlying Board
	 * 
	 * @param pcl Listener to add to the Board
	 */
	public void addListener(PropertyChangeListener pcl) {
		bv.addListener(pcl);
		board.addListener(pcl);
	}

	/**
	 * Add a Ship to the Board held by this BoardContainer
	 * 
	 * @param s Ship to add
	 * @return True if the ship was added, false if not
	 */
	public boolean addShipToBoard(Ship s) {
		boolean added = board.addShipToBoard(s);

		if (added) {
			refreshBoardViewer();
		}

		return added;
	}

	/**
	 * Rebuild the background if it was changed
	 */
	private void refreshBoardViewer() {
		bv.setBackground(generateBackground(board.getCollection()));
		bd.refreshOverlay();
	}

	/**
	 * Change the Board represented by this Container
	 * 
	 * @param newBoard Board to represent
	 */
	public void setBoard(Board newBoard) {
		board = newBoard;
		refreshBoardViewer();
	}

	/**
	 * Determine if the BoardContainer's Board has a full stock of ships
	 * 
	 * @return True if 5 ships are on the Board, false otherwise
	 */
	public boolean isBoardFull() {
		return board.size() == 5;
	}
	
	/**
	 * This method is intended to be used only for passing the Board between Screens.
	 * 
	 * It is not intended to allow direct manipulation of the Board, which could cause issues.
	 * @return	A reference to the Board contained in this object.
	 */
	public Board getBoard() {
		return board;
	}
	
	/**
	 * Check if this container contains a particular BoardViewer by reference
	 * 
	 * Mainly for usage in determining event origin.
	 * @param bv	BoardViewer to check
	 * @return		True if this BoardContainer holds the viewer, otherwise false
	 */
	public boolean containsBoardViewer(BoardViewer bv) {
		return this.bv == bv;
	}
	
	/**
	 * Reset this BoardContainer by clearing the Board, refreshing the background, and resetting the overlay
	 */
	public void reset() {
		board = new Board(new ShipCollection());
		refreshBoardViewer();
		bd.reset();
	}

	/**
	 * Notify the board the game is beginning
	 */
	public void notifyStarted() {
		board.startStats();
	}
	
	/**
	 * Check if this board will currently allow a special move
	 * @return	True if the board will, false otherwise
	 */
	public boolean checkStatusOfSpecial() {
		return board.canMakeShieldMove();
	}

	/**
	 * Attempt to make a special move at a given point
	 * @param specialButton	The special button that is currently selected
	 * @param playerMove	The x and y coordinates of the selected point
	 * @return				True if the special ability was made, false otherwise
	 */
	public boolean attemptSpecialMove(String specialButton, Point2D playerMove) {
		int[] result = board.attemptSpecialMove(specialButton, playerMove);
		boolean validMove = result.length != 0;
		
		if (validMove) {
			if (specialButton.equals("nuke")) {
				// Render the Nuke hits
				Point2D startPoint = new Point2D(playerMove.getX() - 1, playerMove.getY() - 1);
				
				for(int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						bd.playMoveAnimation(startPoint.add(i, j), result[(int) ((i * 3) + j)]);
					}
				}
			} 
			
			// Render a strafing run
			else if (specialButton.equals("strafing run")) {
				Point2D startPoint = new Point2D(playerMove.getX(), playerMove.getY() - 2);
				
				for (int i = 0; i < 5; i++) {
					bd.playMoveAnimation(startPoint.add(0, i), result[i]);
				}
			} 
			
			//pass through second chance
			else if (specialButton.equals("second chance")){
				makeMoveOnBoard(playerMove);
			}
		}
		
		refreshBoardViewer();
		
		return validMove;
	}

	/**
	 * Remove a ship from the board based off the name provided
	 * @param text	Name of the type of ship to remove, e.g. "Patrol"
	 */
	public void removeFromBoard(String text) {
		int length = 3;
		boolean isSub = false;
		
		switch(text) {
		case "Patrol": 		length = 2; 	break;
		case "Submarine": 	isSub = true; break;
		case "Battleship": 	length = 4; 	break;
		case "Carrier":		length = 5;		break;
		}
		
		Ship s = new Ship(new Point2D(0, 0), Direction.UP, length, isSub);	
		
		if (board.remove(s)) {
			refreshBoardViewer();		
		}
	}
}
