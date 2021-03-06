package es.ucm.fdi.tp.practica5.ataxx;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import es.ucm.fdi.tp.basecode.bgame.control.ConsolePlayer;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.DummyAIPlayer;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.AIAlgorithm;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.bgame.views.GenericConsoleView;

/**
 * <p>
 * Factoria para la creacion de juegos Ataxx. Vease {@link ConnectNRules}
 * para la descripcion del juego.
 */
public class AtaxxFactory implements GameFactory{

	private int dim;
	private int obs;
	
	public AtaxxFactory(Integer numObs, int numPlayers){
		this(7,numObs,numPlayers);
	}
	
	public AtaxxFactory(int dim,Integer numObstacles, int numPlayers){
		obs = numObstacles;
		if(obs >= (dim * dim)-(numPlayers*2)){
			throw new GameError("The number of obstacles musn`t exceed the dimension of the board " );
		}
		if (dim*dim%2==0) {
			throw new GameError("Dimension must be odd: " + dim);
		} 
		else if (dim < 5) {
			throw new GameError("Dimension must be at least 5: " + dim);
		} else {
			this.dim = dim;
		}
	}
	
	@Override
	public GameRules gameRules() {
		return new AtaxxRules(dim,obs);
	}
	
	
	@Override
	public Player createConsolePlayer(){
		ArrayList<GameMove>possibleMoves = new ArrayList<GameMove>();
		possibleMoves.add(new AtaxxMove());
		return new ConsolePlayer(new Scanner(System.in), possibleMoves);
	}
	
	@Override 
	public Player createRandomPlayer(){
		return new AtaxxRandomPlayer();
	}
	
	@Override
	public Player createAIPlayer(AIAlgorithm alg) {
		return new DummyAIPlayer(createRandomPlayer(), 1000);
	}
	
	@Override
	public List<Piece> createDefaultPieces() {
		List<Piece> pieces = new ArrayList<Piece>();
		pieces.add(new Piece("X"));
		pieces.add(new Piece("O"));
		return pieces;
	}

	@Override
	public void createConsoleView(Observable<GameObserver> g, Controller c) {
		new GenericConsoleView(g, c);
	}

	@Override
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
			Player random, Player ai) {
		throw new UnsupportedOperationException("There is no swing view");
	}
	
	
}
