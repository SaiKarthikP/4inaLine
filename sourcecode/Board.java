
import java.util.ArrayList;

public class Board {

	private final int N = 8;
	private char[][] board = new char[N][N];
	private boolean losing = false, winning = false;
	private Position[] positions2 = new Position[2];

	public Board(char[][] state){
		board = state;
	}

	public void insertPiece(Position p, int player){
		if (player == 1)
			board[p.x][p.y] = 'O';
		else
			board[p.x][p.y] = 'X';
	}

	public void undoPiece(Position p){ //deletes move from board
		board[p.x][p.y] = '-';
	}

	public ArrayList<Position> findNeighbors(){ //depending on the win/lose situation, add potential moves to the evaluation set
		ArrayList<Position> neighbors = new ArrayList<>();
		if (couldWin1() || couldLose1()){
			if (positions2[0].x != -1 && positions2[0].y != -1){
				neighbors.add(positions2[0]);               
			}
			if (positions2[1].x != -1 && positions2[1].y != -1){
				neighbors.add(positions2[1]);              
			}
		}
		else if (couldWin2() || couldLose2()){
			if (positions2[0].x != -1 && positions2[0].y != -1)
			{
				neighbors.add(positions2[0]);               
			}
			if (positions2[1].x != -1 && positions2[1].y != -1)
			{
				neighbors.add(positions2[1]);              
			}
		}
		else{
			for (int i = 0; i < N; i++){
				for (int j = 0; j < N; j++)
				{
					if (board[i][j] == '-')
					{
						neighbors.add(new Position(i,j));
					}
				}
			}  
		}
		return neighbors;
	}

	public int eval(){ //checks consecutive pieces in each adjacent row and column. Adds scores based on consecutive pieces
		int score = 0;        
		int counter = 0;
		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){
				int point = 0;
				if (board[i][j] == '-'){
					int r = i;
					int c = j;
					counter = 0;

					c++;
					while ((c < N) && (board[r][c] == 'O')){
						counter++;
						if (counter == 1)
							point -= 1;
						if (counter == 2)
							point -= 5;                                                       
						if (counter == 3)
							point -= 50;
						c++;
					}

					if (j - 1 >= 0)
						c = j - 1;
					while ((c >= 0) && (board[r][c] == 'O')){
						counter++;
						if (counter == 1)
							point -= 1;
						if (counter == 2)
							point -= 50;                           
						if (counter == 3)
							point -= 50;                           
						c--;
					}

					counter = 0;
					c = j;
					r++;

					while ((r < N) && (board[r][c] == 'O')){
						counter++;
						if (counter == 1)
							point -= 1;
						if (counter == 2)
							point -= 5;                          
						if (counter == 3)
							point -= 50;                            
						r++;
					}

					if (i - 1 >= 0)
						r = i - 1;
					while ((r >= 0) && (board[r][c] == 'O')){
						counter++;
						if (counter == 1)
							point -= 1;
						if (counter == 2)
							point -= 5;                           
						if (counter == 3)
							point -= 50;                           
						r--;
					}

					r = i;
					c = j;
					counter = 0;

					c++;
					while ((c < N) && (board[r][c] == 'X')){
						counter++;
						if (counter == 1)
							point += 1;
						if (counter == 2)
							point += 5;
						if (counter == 3)
							point += 50;
						c++;
					}

					if (j - 1 >= 0)
						c = j - 1;
					while ((c >= 0) && (board[r][c] == 'X')){
						counter++;
						if (counter == 1)
							point += 1;
						if (counter == 2)
							point += 5;
						if (counter == 3)
							point += 50;
						c--;
					}

					counter = 0;
					c = j;
					r++;

					while ((r < N) && (board[r][c] == 'X')){
						counter++;
						if (counter == 1)
							point += 1;
						if (counter == 2)
							point += 5;
						if (counter == 3)
							point += 50;
						r++;
					}

					if (i - 1 >= 0)
						r = i - 1;
					while ((r >= 0) && (board[r][c] == 'X')){
						counter++;
						if (counter == 1)
							point += 1;
						if (counter == 2)
							point += 5;
						if (counter == 3)
							point += 50;
						r--;
					}
				}
				else if (board[i][j] == 'O'){
					int r = i;
					int c = j;
					counter = 0;
					while ((c < N) && (board[r][c] == 'O')){
						counter++;
						if (counter == 4)
							point -= 1000;
						c++;
					}
					counter = 0;
					c = j;
					while ((r < N) && (board[r][c] == 'O')){
						counter++;
						if (counter == 4)
							point -= 1000;
						r++;
					}                                      
				}
				else if (board[i][j] == 'X'){
					int r = i;
					int c = j;
					counter = 0;
					while ((c < N) && (board[r][c] == 'X')){
						counter++;
						if (counter == 4)
							point += 1000;
						c++;
					}
					counter = 0;
					c = j;
					while ((r < N) && (board[r][c] == 'X')){
						counter++;
						if (counter == 4)
							point += 1000;
						r++;
					}                                    
				}
				score += point;
			}
		}       
		return score;
	}

	public boolean couldLose1(){ //computer checks if there are any possible moves player could make that forces a loss
		int counter;
		positions2[0] = new Position(-1,-1);
		positions2[1] = new Position(-1,-1);
		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){               
				int r = i;
				int c = j;
				counter = 0;

				//checks if player has the ability to set 3 in a column, trapping both sides

				while ((c < N) && (board[r][c] == 'O')){
					counter++;			
					if (counter == 3){
						if ((c + 1 < N ) && (board[r][c + 1] == '-')){
							losing = true;
							positions2[0].x = r;
							positions2[0].y = c + 1;                            
						}
						else if ((c - 3 >= 0) && (board[r][c - 3] == '-')){
							losing = true;
							positions2[1].x = r;
							positions2[1].y = c - 3;
						}
					}                        
					c++;
				}                   

				counter = 0;
				c = j;

				//checks if player has the ability to set 3 in a row, trapping both sides

				while ((r < N) && (board[r][c] == 'O')){
					counter++;
					if (counter == 3){
						if ((r + 1 < N ) && (board[r + 1][c] == '-')){                                
							losing = true;
							positions2[0].x = r + 1;
							positions2[0].y = c;
						}
						else if ((r - 3 >= 0) && (board[r - 3][c] == '-')){
							losing = true;
							positions2[1].x = r - 3;
							positions2[1].y = c;
						}                          
					}                  
					r++;
				}               
			}
		}
		return losing;
	}

	public boolean couldLose2(){ //additional force loss checks with two moves
		int counter;
		positions2[0] = new Position(-1,-1);
		positions2[1] = new Position(-1,-1);
		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){               
				int r = i;
				int c = j;
				counter = 0;

				//checks 2 moves in advance, if player has two in a column and can set up a 3 in a column trap

				while ((c < N) && (board[r][c] == 'O')){
					counter++;
					if (counter == 2){                            
						if (((c + 1 < N ) && (board[r][c + 1] == '-'))
								&& ((c - 2 >= 0) && (board[r][c - 2] == '-'))){
							losing = true;
							positions2[0].x = r;
							positions2[0].y = c + 1;
							positions2[1].x = r;
							positions2[1].y = c - 2;
						}
					}			  
					c++;
				}

				counter = 0;
				c = j;

				//checks 2 moves in advance, if player has two in a row and can set up a 3 in a row trap

				while ((r < N) && (board[r][c] == 'O')){
					counter++;
					if (counter == 2){                            
						if (((r + 1 < N ) && (board[r + 1][c] == '-'))
								&& ((r - 2 >= 0) && board[r - 2][c] == '-')){
							losing = true;
							positions2[0].x = r + 1;
							positions2[0].y = c;
							positions2[1].x = r - 2;
							positions2[1].y = c;
						}
					}			
					r++;
				}                   
			}
		}
		return losing;
	}

	public boolean couldWin1(){ //checks to see if there are any moves that guarantee win for computer
		int counter;
		positions2[0] = new Position(-1,-1);
		positions2[1] = new Position(-1,-1);

		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){
				int r = i;
				int c = j;
				counter = 0;

				//checks if there is any opportunity to set up a three trap

				while ((c < N) && (board[r][c] == 'X')){
					counter++;			
					if (counter == 3){
						if ((c + 1 < N ) && (board[r][c + 1] == '-')){
							winning = true;
							positions2[0].x = r;
							positions2[0].y = c + 1;                            
						}
						else if ((c - 3 >= 0) && (board[r][c - 3] == '-')){
							winning = true;
							positions2[1].x = r;
							positions2[1].y = c - 3;
						}
					}
					if (counter == 2){
						if ((c + 1 < N) && (c + 2 < N) && 
								(board[r][c + 1] == '-') && (board[r][c + 2] == 'X')){
							winning = true;
							positions2[0].x = r;
							positions2[0].y = c + 1;
						}
						else if ((c - 2 >= 0) && (c - 3 >= 0) && 
								(board[r][c - 2] == '-') && (board[r][c - 3] == 'X')){
							winning = true;
							positions2[1].x = r;
							positions2[1].y = c - 2;
						}
					}
					c++;
				}

				counter = 0;
				c = j;

				//same check for rows

				while ((r < N) && (board[r][c] == 'X')){
					counter++;			
					if (counter == 3){
						if ((r + 1 < N ) && (board[r + 1][c] == '-')){                                
							winning = true;
							positions2[0].x = r + 1;
							positions2[0].y = c;
						}
						else if ((r - 3 >= 0) && (board[r - 3][c] == '-')){
							winning = true;
							positions2[1].x = r - 3;
							positions2[1].y = c;
						}                          
					}
					if (counter == 2){
						if ((r + 1 < N) && (r + 2 < N) && 
								(board[r + 1][c] == '-') && (board[r + 2][c] == 'X')){
							winning = true;
							positions2[0].x = r + 1;
							positions2[0].y = c;
						}
						else if ((r - 2 >= 0) && (r - 3 >= 0) && 
								(board[r - 2][c] == '-') && (board[r - 3][c] == 'X')){
							winning = true;
							positions2[1].x = r - 2;
							positions2[1].y = c;
						}
					}
					r++;
				}               
			}
		}
		return winning;       
	}

	public boolean couldWin2() //additional win checks for two moves
	{
		int counter;
		positions2[0] = new Position(-1,-1);
		positions2[1] = new Position(-1,-1);

		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){
				int r = i;
				int c = j;
				counter = 0;

				//checks two moves ahead to see if any 2 or 3 traps are available

				while ((c < N) && (board[r][c] == 'X')){
					counter++;
					if (counter == 2){                            
						if (((c + 1 < N ) && (board[r][c + 1] == '-'))
								&& ((c - 2 >= 0) && (board[r][c - 2] == '-'))){
							winning = true;
							positions2[0].x = r;
							positions2[0].y = c + 1;
							positions2[1].x = r;
							positions2[1].y = c - 2;
						}
						else if ((c + 1 < N ) && (board[r][c + 1] == '-')){
							winning = true;
							positions2[0].x = r;
							positions2[0].y = c + 1;
						}
						else if ((c - 2 >= 0) && (board[r][c - 2] == '-')){
							winning = true;
							positions2[1].x = r;
							positions2[1].y = c - 2;
						}                                
					}			
					c++;
				}

				counter = 0;
				c = j;

				//same 2 move check for rows

				while ((r < N) && (board[r][c] == 'X')){
					counter++;
					if (counter == 2){                            
						if (((r + 1 < N ) && (board[r + 1][c] == '-'))
								&& ((r - 2 >= 0) && board[r - 2][c] == '-')){
							winning = true;
							positions2[0].x = r + 1;
							positions2[0].y = c;
							positions2[1].x = r - 2;
							positions2[1].y = c;
						}
						else if ((r + 1 < N ) && (board[r + 1][c] == '-')){
							winning = true;
							positions2[0].x = r + 1;
							positions2[0].y = c;
						}
						else if ((r - 2 >= 0) && (board[r - 2][c] == '-')){
							winning = true;
							positions2[1].x = r - 2;
							positions2[1].y = c;
						}                     
					}			
					r++;
				}
			}
		}
		return winning;
	}

	public boolean checkWin(char car){ //checks if the current position has anyone with a winning scenario (X/O)
		int counter;

		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){
				int r = i;
				int c = j;
				counter = 0;

				//checks for 4 in a column

				while ((c < N) && (board[r][c] == car)){
					counter++;			
					if (counter == 4){
						return true;
					}
					c++;
				}

				counter = 0;
				c = j;

				//checks for 4 in a row

				while ((r < N) && (board[r][c] == car)){
					counter++;			
					if (counter == 4){
						return true;
					}               
					r++;
				}
			}
		}
		return false;
	}

	public boolean checkDraw(){ //checks for draw if stalemate
		int counter = 0;
		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++)
			{
				if (board[i][j] == '-')
					counter++;
			}
		}
		return counter == 0;
	}

	public boolean validMove(Position p){ //validates if move is possible
		return board[p.x][p.y] == '-';
	}

	public void printBoard() //outputs board to screen
    {
        String index = "ABCDEFGH";

        System.out.println("\t1\t2\t3\t4\t5\t6\t7\t8");

        for (int i = 0; i < board.length; i++)
        {
            System.out.print(index.charAt(i) + "\t");
            for (int j = 0; j < board[i].length; j++)
            {
                System.out.print(board[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

	public char[][] getBoard(){
		return board;   
	}
}
