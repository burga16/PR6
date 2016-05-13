package es.ucm.fdi.tp.practica5.attt;

import java.util.List;

import es.ucm.fdi.tp.basecode.attt.AdvancedTTTMove;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class AdvancedTTTSwingPlayer extends Player{
	private int rowDestino;
	private int colDestino;
	private int rowInicial;
	private int colInicial;
	private boolean mov ;
	
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules){
		return new AdvancedTTTMove(rowInicial,colInicial,rowDestino, colDestino,p);
		 
	}
	public boolean getMov(){
		return this.mov;
	}
	public void setIniMove(int row, int col){
		this.rowDestino = row;
		this.colDestino = col;
		this.mov = false;
	}
	public void setMove(int row, int col){
		this.rowInicial = row;
		this.colInicial = col;
		this.mov = true;
	}
	protected GameMove createMove(int row,int col,Piece p){
		return new AdvancedTTTMove(row,col,rowInicial,colInicial,p);
	}
}
