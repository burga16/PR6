package es.ucm.fdi.tp.practica5.ataxx;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxRandomPlayer extends Player {
	
	private static final long serialVersionUID = 1L;
	
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
		List<GameMove> movesList = new ArrayList<GameMove>();
		if (board.isFull()) {
			throw new GameError("The board is full, cannot make a random move!!");
		}
		movesList = rules.validMoves(board, pieces, p);
		int rand = Utils.randomInt(movesList.size());
		return movesList.get(rand);
		
	}
		protected GameMove createMove(int row, int col, int rowd, int cold, Piece p) {
			return new AtaxxMove(row, col, rowd, cold, p);
		}
}
