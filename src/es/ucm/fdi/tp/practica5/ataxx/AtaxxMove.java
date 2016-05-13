package es.ucm.fdi.tp.practica5.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxMove extends GameMove {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Fila en la que se coloca la ficha de partida del movimiento devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	private int row;
	
	/**
	 * Columna en la que se coloca la ficha de partida del movimiento devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	private int col;
	

	/**
	 * Fila en la que se coloca la ficha de destino del movimiento devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int rowd;
	
	
	/**
	 * Columna en la que se coloca la ficha de destino del movimiento devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int cold;

	
	/**
	 * Solo se debe usar este constructor para obtener objetos de
	 * {@link ConnectNMove} para generar movimientos a partir de strings usando
	 * el metodo {@link #fromString(String)}
	 */
	public AtaxxMove(){};
	
	/**
	 * Construye un movimiento para colocar una ficha del tipo referenciado por
	 * {@code p} en la posicion ({@code row},{@code col}).
	 * 
	 * @param row
	 *            Numero de fila.
	 * @param col
	 *            Numero de columna.
	 * @param p
	 *            Ficha a colocar en ({@code row},{@code col}).
	 */
	public AtaxxMove(int row, int col,int rowd, int cold, Piece p) {
		super(p);
		this.row = row;
		this.col = col;
		this.rowd = rowd;
		this.cold = cold;
	}
	
	public int getRowd() {
		return rowd;
	}

	public int getCold() {
		return cold;
	}

	@Override
	public void execute(Board board, List<Piece> pieces) {
		if(board.getPosition(row, col) == getPiece()){ //Si la posición de inicio es del tipo correspondiente
			if (board.getPosition(rowd, cold) == null){//Si la posición de destino está vacia
				if((Math.abs(this.rowd - this.row) < 3) && (Math.abs(this.cold - this.col) < 3)) {  //Y está en el rango permitido	
					board.setPosition(rowd, cold, getPiece());
					board.setPieceCount(getPiece(), board.getPieceCount(getPiece())+1);
					if((Math.abs(this.rowd - this.row)  > 1) || (Math.abs(this.cold - this.col) > 1)) { //Si es una posición no aledaña
						board.setPosition(row, col, null);
						board.setPieceCount(getPiece(), board.getPieceCount(getPiece())-1);
					}
					for(int i= (rowd-1); i<= (rowd+1); i++){
						for(int j= (cold-1); j<= (cold+1); j++){
							if((i>=0 && j>=0) && (i<=board.getRows()-1 && j<=board.getCols()-1)){
								if(board.getPosition(i, j) != null){
									if(board.getPosition(i, j).getId() != "*"){
										board.setPieceCount(getPiece(), board.getPieceCount(getPiece())+1);
										board.setPieceCount(board.getPosition(i, j), board.getPieceCount(board.getPosition(i, j))-1);
										board.setPosition(i, j, getPiece());
									}
								}	
							}
						}
					}
				} else throw new GameError("position (" + rowd + "," + cold + ") exceeds the range limit!");
			} else throw new GameError("position (" + rowd + "," + cold + ") is already occupied!");
		} else throw new GameError("position (" + row + "," + col + ") It's not of the type indicated");
	}
	
	@Override
	public GameMove fromString(Piece p, String str) { //Hay que modificar esto para cambiar el movimiento del juego
		String[] words = str.split(" ");
		if (words.length != 4) {
			return null;
		}

		try {
			int rowi, coli, rowd, cold;
			rowi = Integer.parseInt(words[0]);
			coli = Integer.parseInt(words[1]);
			rowd = Integer.parseInt(words[2]);
			cold = Integer.parseInt(words[3]);
			return createMove(rowi, coli,rowd,cold, p);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	protected GameMove createMove(int rowi, int coli,int rowd, int cold, Piece p) {
		return new AtaxxMove(rowi, coli,rowd,cold, p);
	}
	
	@Override
	public String help() {
		return "Row and column for origin and for destination, separated by spaces (four numbers)";
	}

	@Override
	public String toString() {
		if (getPiece() == null) {
			return help();
		} else {
			return "Place a piece '" + getPiece() + "' at (" + row + "," + col + ")";
		}
	}

}
