package es.ucm.fdi.tp.practica5.attt;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica5.Gráficos.RectBoardSwingView;
import es.ucm.fdi.tp.practica5.ataxx.AtaxxRandomPlayer;

@SuppressWarnings("serial")
public class AdvancedTTTSwingView extends RectBoardSwingView {
	private AdvancedTTTSwingPlayer player;
	public AdvancedTTTSwingView(Observable<GameObserver> g, Controller c, Piece localPiece,
			Player randPlayer, Player aiPlayer){
			super(g,c,localPiece,randPlayer,aiPlayer);
			player = new AdvancedTTTSwingPlayer();
	}
	@Override
	protected void handleMouseClick(int row, int col, int mouseButton){
		if(player.getMov() == true){
			player.setIniMove(row, col);
			decideMakeManualMove(player);
		}else {
			player.setMove(row, col);
			
		}
	}
	@Override
	protected void activateBoard(){

		
	}
	@Override
	protected void desActivateBoard(){


		
	}
	protected void decideMakeRandomMove(Piece turn){
		decideMakeManualMove(new AtaxxRandomPlayer());
	}

}
