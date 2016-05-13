package es.ucm.fdi.tp.practica5.ataxx;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.FiniteRectBoard;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;

/**
 * Reglas del juego Ataxx.
 * <ul>
 * <li>El juego se juega en un tablero NxN (con N>=5).</li>
 * <li>El numero de jugadores esta entre 2 y 4.</li>
 * <li>Los jugadores juegan en el orden proporcionado, cada uno colocando una
 * ficha en una casilla vacia. El ganador es el que termine con más fichas de entre todos los jugadores
 * o el que deje sin fichas al resto
 * </li>
 * </ul>
 */

public class AtaxxRules implements GameRules {
	
	protected final Pair<State, Piece> gameInPlayResult = new Pair<State, Piece>(State.InPlay, null);
	
	private int dim;
	private int obs;
	
	public AtaxxRules(int dim, int numObs){
		obs = numObs;
		if (dim*dim%2==0) {
			throw new GameError("Dimension must be odd: " + dim);
		} 
		else if (dim < 5) {
			throw new GameError("Dimension must be at least 5: " + dim);
		} else {
			this.dim = dim;
		}
	}
	
	public String gameDesc() {
		return "Ataxx " + dim + "x" + dim; 
	}
	
	public Board createBoard(List<Piece> pieces) {
		Piece obstacle = new Piece("*");
		int newDim= dim-1;
		Board board = new FiniteRectBoard(dim, dim);
		board.setPosition(0, 0, pieces.get(0));
		board.setPosition(newDim, newDim, pieces.get(0));
		board.setPosition(newDim, 0, pieces.get(1));
		board.setPosition(0, newDim, pieces.get(1));
		board.setPieceCount(pieces.get(0), 2);
		board.setPieceCount(pieces.get(1), 2);
		if(pieces.size()>2){
			board.setPosition(newDim/2, 0, pieces.get(2));
			board.setPosition(newDim/2, newDim, pieces.get(2));
			board.setPieceCount(pieces.get(2), 2);
			if(pieces.size()==4){
				board.setPosition(0, newDim/2, pieces.get(3));
				board.setPosition(newDim, newDim/2, pieces.get(3));
				board.setPieceCount(pieces.get(3), 2);
			}
		}
		int i=0;
		int row,col;
		while(i<obs){
			row = Utils.randomInt(dim);
			col = Utils.randomInt(dim);
			if(board.getPosition(row, col) == null){
				board.setPosition(row, col, obstacle);
				i++;
			}
		}
	
		return board;
	}
	
	public Piece initialPlayer(Board board, List<Piece> playersPieces) {
		Piece p;
		int index = playersPieces.size()-1;
		p = nextPlayer(board,playersPieces,playersPieces.get(index));
		return p;
	}

	public int minPlayers() {
		return 2;
	}

	public int maxPlayers() {
		return 4;
	}
	public Pair<State, Piece> updateState(Board board, List<Piece> playersPieces, Piece lastPlayer) {
		int num_players = playersPieces.size();
		if(nextPlayer(board,playersPieces,lastPlayer) == null){
			int mayor = 0;
			int num_mayor = 1;
			for(int i=0; i<num_players; i++){
				int pieza_actual = board.getPieceCount(playersPieces.get(i));
				if(pieza_actual > mayor){
					mayor = pieza_actual;
					num_mayor = 1;
				}else if(pieza_actual == mayor){
					num_mayor++;
				}
			}
			if(num_mayor > 1)return new Pair<State, Piece>(State.Draw, null);
			else{
				int i=0;
				while(i<num_players){
				if(board.getPieceCount(playersPieces.get(i))== mayor) return new Pair<State, Piece>(State.Won, playersPieces.get(i));
				else i++;
				}
			}	
			
		}
		int i=0;
		int piezas_tablero=0;
		while(i < num_players){
			piezas_tablero = piezas_tablero + board.getPieceCount(playersPieces.get(i));
			i++;
		}
		int j=0;
		while(j < num_players){
			if(piezas_tablero == board.getPieceCount(playersPieces.get(j)))return new Pair<State, Piece>(State.Won, playersPieces.get(j));
			j++;
		}
		return gameInPlayResult;
	}
	public Piece nextPlayer(Board board, List<Piece> playersPieces, Piece lastPlayer) {
		List<Piece> pieces = playersPieces;
		int i = pieces.indexOf(lastPlayer);
		int paso_turno=0;
		i++;
		Piece pieza_actual = pieces.get((i ) % pieces.size());
		Integer contadorPieza = board.getPieceCount(pieza_actual);
		while(paso_turno <= playersPieces.size()){
			if(contadorPieza.equals(0)){
				playersPieces.remove(contadorPieza);
			}
			if(validMoves(board,playersPieces,pieza_actual) == null){
				paso_turno++;
				i++;
				pieza_actual = pieces.get((i ) % pieces.size());
				contadorPieza = board.getPieceCount(pieces.get((i ) % pieces.size()));
			}
			else return pieces.get((i) % pieces.size());
		}
		return null;
	}

	public double evaluate(Board board, List<Piece> pieces, Piece turn, Piece p){
		return 0;
	}

	public List<GameMove> validMoves(Board board, List<Piece> playersPieces, Piece turn) {
		List<GameMove> moves = new ArrayList<GameMove>();
		if (board.isFull()) {
			return null;
		}
		int rows = board.getRows();
		int cols = board.getCols();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board.getPosition(i, j) != null) {
					if(board.getPosition(i, j).getId() != "*"){
						if(board.getPosition(i, j).equals(turn)){
							moves.addAll(tieneMovimiento(i, j, board, rows,cols,turn));
						}
					}
				}
			}
		}
		if(moves.size()==0)return null;
		return moves;
	}
	private List<GameMove> tieneMovimiento(int row, int col, Board board, int rows, int cols,Piece piece){
		List<GameMove> moves = new ArrayList<GameMove>();
		for(int i=(row-2); i<=(row+2); i++){
			for(int j=(col-2); j<=(col+2); j++){
				 if((i>=0 && j>=0) && (i<=rows-1 && j<=cols-1)){
					if(board.getPosition(i, j) == null){ //Si no está ocupada la posición y no es un obstaculo
						moves.add(new AtaxxMove(row,col,i,j,piece));
					}
				 }
			 }
		 }
		 return moves;
	}

}
