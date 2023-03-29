package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		/*
		 * ADD YOUR CODE HERE
		 */
		Color[][] board_colors = board.flatten();
		int count = 0;
		int row = board_colors.length;
		int col = board_colors[0].length;
		for(int i = 0; i < row; i++){
			if(board_colors[i][0] == this.targetGoal) count++;
			if(board_colors[i][col-1] == this.targetGoal) count++;
		}
		for(int j = 0; j < col; j++){
			if(board_colors[0][j] == this.targetGoal) count++;
			if(board_colors[row-1][j] == this.targetGoal) count++;
		}
		return count;
	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
