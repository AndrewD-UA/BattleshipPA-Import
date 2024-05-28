package model.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javafx.geometry.Point2D;  

public class ModerateAI extends BattleshipAI{
      protected HashSet<Point2D> movesLeft;
      private final Random r;

      public ModerateAI() {    
          r = new Random();    
          movesLeft = new HashSet<>();
             for (int i = 0; i < BOARD_SIZE; i++) {               
                 for (int j = 0; j < BOARD_SIZE; j++){                    
                      movesLeft.add(new Point2D(i, j));
                 }
             }
      }

      public Point2D nextMove() {     
          int randomIndex = r.nextInt(movesLeft.size());
          Point2D guess = new ArrayList<>(movesLeft).get(randomIndex);    
          movesLeft.remove(guess);   
          return guess;
      } 
} 