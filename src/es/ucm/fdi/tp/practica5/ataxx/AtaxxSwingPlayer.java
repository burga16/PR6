package es.ucm.fdi.tp.practica5.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.connectn.ConnectNMove;

public class AtaxxSwingPlayer extends Player {
	private int rowDestino;
	private int colDestino;
	private int rowInicial;
	private int colInicial;
	private boolean mov ;
	
	AtaxxSwingPlayer(){
	
	}
	public boolean getMov(){
		return this.mov;
	}
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules){
		//return createMove(rowDestino, colDestino, p);
		
		return new AtaxxMove(rowInicial,colInicial,rowDestino, colDestino,p);
		 
	}
	
	public void setMove(int row, int col){
		this.rowDestino = row;
		this.colDestino = col;
		this.mov = false;
	}
	public void setIniMove(int row, int col){
		this.rowInicial = row;
		this.colInicial = col;
		this.mov = true;
	}
	protected GameMove createMove(int row,int col,Piece p){
		return new AtaxxMove(row,col,rowInicial,colInicial,p);
	}
	

}
