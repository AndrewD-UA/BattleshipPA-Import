package controller_view.board;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import controller_view.AllProperties;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * A viewer for the 10x10 game board.
 * 
 * This board is intended to be used with both the ShipPicker and actual game screens.
 * It generates a list of 10x10 buttons that are represent the board's tiles.
 * 
 * @author Andrew Dennison, Tom Giallanza
 */
public class BoardViewer extends BorderPane{
	
	/**
	 * Stores the number of rows and columns on the square board, which are the same.
	 */
	private static final int BOARD_SPACES = 10;
	
	/**
	 * The object used to notify listeners of changes
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	/**
	 * The GridPane contained in the center of this boardviewer
	 */
	private GridPane boardGrid;
	
	private double cellSize;
	
	private boolean showPreview;
		
	/**
	 * Create a new square board with a side length of size.
	 * @param size			The length of one cell on the board
	 * @param isAI  		True if we are viewing an AI's board, false otherwise
	 * @param showPreview 	True if this board should fire mouseOver events, false otherwise
	 */
	public BoardViewer(double size, boolean isAI, boolean showPreview) {
		super();
		cellSize = size;
		this.showPreview = showPreview;
		initGrid();	
	}
	
	/**
	 * Build the graphical representation of the GridPane used to directly show the Board
	 */
	private void initGrid() {
		boardGrid = new GridPane();	

		initializeOverlay();
		boardGrid.setAlignment(Pos.CENTER);		
		
		setCenter(boardGrid);	
	}
	
	/**
	 * Iteratively generate all the buttons
	 */
	private void initializeOverlay() {
		for (int row = BOARD_SPACES - 1; row >= 0; row--) {
			for (int col = BOARD_SPACES - 1; col >= 0; col--) {
				BoardButton gridButton = new BoardButton(col, row, this);
				boardGrid.add(gridButton, col, row);
			}
		}
	}

	/**
	 * Add a listener to this BoardViewer
	 * @param pcl	The listener to add.
	 */
	public void addListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}		
	
	private static final BorderWidths SELECTED_WIDTH = new BorderWidths(2, 2, 2, 2);
	
	private static final Border SELECTED_BORDER = new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY, SELECTED_WIDTH));

	private static final Border UNSELECTED_BORDER = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY, SELECTED_WIDTH));	
		
	/**
	 * An inner class used to represent each square on the board.
	 * 
	 * When a BoardButton is moused over, it sets its background image to the "overlay.png" file.
	 * Then, when the mouse exits the button, it resets it to be transparent.
	 * 
	 * Can perform actions using setOnAction.
	 * 
	 * @author Andrew Dennison
	 */
	private class BoardButton extends Button{
		
		/**
		 * Create a new BoardButton
		 * @param x	The X coordinate this button represents
		 * @param y	The Y coordinate this button represents
		 */
		public BoardButton(int x, int y, BoardViewer parent) {
			super();
			setBorder(UNSELECTED_BORDER);
			setBackground(null);
			setMinHeight(cellSize);
			setMaxHeight(cellSize);
			setMinWidth(cellSize);
			setMaxWidth(cellSize);
			setOnAction((event)->{
				pcs.firePropertyChange(new PropertyChangeEvent(parent, AllProperties.BOARD_CLICK.property(), null, new Point2D(x, y)));
			});
			setOnMouseEntered((event)->{
				setBorder(SELECTED_BORDER);
				if (showPreview) {
					pcs.firePropertyChange(AllProperties.MOUSE_OVER_EVENT.property(), null, new Point2D(x, y));
				}
			});				
			
			setOnMouseExited((event)->{
				setBorder(UNSELECTED_BORDER);
			});
		}
	}
}
