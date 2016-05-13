package es.ucm.fdi.tp.practica5.ataxx;


import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica5.Gráficos.RectBoardSwingView;

@SuppressWarnings("serial")
public class AtaxxSwingView extends RectBoardSwingView {
	
	private AtaxxSwingPlayer player;
	public AtaxxSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
		player = new AtaxxSwingPlayer();
	}
	@Override
	protected void handleMouseClick(int row, int col, int mouseButton) {
		
		if(player.getMov() == true){
			player.setMove(row, col);
			addMsg("Ha seleccionado("+row +","+col +") como destino");
			decideMakeManualMove(player);
		}else {
			player.setIniMove(row, col);
			addMsg("Ha seleccionado("+row +","+col +") como origen");
			
		}
		
				
	}
	@Override
	protected void activateBoard() {
		
	}
	@Override
	protected void desActivateBoard() {
		
	}
	protected void decideMakeRandomMove(Piece turn){
		decideMakeManualMove(new AtaxxRandomPlayer());
	}

}
