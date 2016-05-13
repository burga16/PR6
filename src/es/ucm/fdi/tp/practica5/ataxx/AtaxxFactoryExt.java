package es.ucm.fdi.tp.practica5.ataxx;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class AtaxxFactoryExt extends AtaxxFactory{
	public AtaxxFactoryExt(int dimRows, Integer numObs, int numPlayers){
		super(dimRows, numObs, numPlayers);
		
	}
	public AtaxxFactoryExt(Integer numObs, int numPlayers){
		super(numObs, numPlayers);
	}
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
			Player random, Player ai) {
			new AtaxxSwingView(g, c, viewPiece, random, ai);
	}
}
