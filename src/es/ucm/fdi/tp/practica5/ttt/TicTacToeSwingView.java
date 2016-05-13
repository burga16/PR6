package es.ucm.fdi.tp.practica5.ttt;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica5.Gráficos.RectBoardSwingView;

@SuppressWarnings("serial")
public class TicTacToeSwingView extends RectBoardSwingView{
	private TicTacToeSwingPlayer player;
	public TicTacToeSwingView(Observable<GameObserver> g, Controller c, Piece localPiece,
								Player randPlayer, Player aiPlayer){
		super(g,c,localPiece,randPlayer,aiPlayer);
		player = new TicTacToeSwingPlayer();
	}
	@Override
	protected void handleMouseClick(int row, int col, int mouseButton){
		player.setMove(row,col);
		decideMakeManualMove(player);
	}
	@Override
	protected void activateBoard(){

		
	}
	@Override
	protected void desActivateBoard(){


		
	}
}
