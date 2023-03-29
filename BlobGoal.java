package assignment3;

import javafx.util.Pair;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		/*
		 * ADD YOUR CODE HERE
		 */
		Color[][] board_colors = board.flatten();
		int count = 0;
		for(int i = 0; i < board_colors.length; i++){
			for(int j = 0; j < board_colors[0].length; j++){
				boolean[][] visited = new boolean[board_colors.length][board_colors[0].length];
				count = Math.max(count, this.undiscoveredBlobSize(i, j, board_colors, visited));
			}
		}
		return count;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal) 
		+ " blocks, anywhere within the block";
	}


	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		/*
		 * ADD YOUR CODE HERE
		 */
		if(unitCells[i][j] != this.targetGoal){
			return 0;
		}
		Queue<int[]> q = new ArrayDeque<>();
		int[] a = {i, j};
		q.add(a);
		int count = 0;
		int[] dx = {-1, 0, 1, 0};
		int[] dy = {0, -1, 0, 1};
		while(!q.isEmpty()) {
			int[] t = q.poll();
			for(int k = 0; k < 4; k++){
				int nx = t[0] + dx[k];
				int ny = t[1] + dy[k];
				if(nx >= 0 && nx < unitCells.length && ny >= 0 && ny < unitCells[0].length && !visited[nx][ny] && unitCells[nx][ny] == this.targetGoal){
					visited[nx][ny] = true;
					count++;
					q.add(new int[]{nx, ny});
				}
			}
		}
		return count;
	}

}
