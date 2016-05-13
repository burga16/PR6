package es.ucm.fdi.tp.practica6;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.control.commands.PlayCommand;
import es.ucm.fdi.tp.basecode.bgame.control.commands.QuitCommand;
import es.ucm.fdi.tp.basecode.bgame.control.commands.RestartCommand;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class GameClient extends Controller implements Observable<GameObserver>{
	private String host;
	private int port;
	private List<GameObserver> observers;
	private Piece localPiece;
	private GameFactory gameFactory;
	private Connection connectionToServer;
	private boolean gameOver;
	
	public GameClient(String host, int port)throws Exception{
		super(null,null);
		this.host = host;
		this.port = port;
		connect();
	}
	public GameFactory getGameFactory(){return gameFactory;}
	public Piece getPlayerPiece(){return localPiece;}
	public void addObserver(GameObserver o){observers.add(o);}
	public void removeObserver(GameObserver o){observers.remove(o);}
	
	@Override
	public void makeMove(Player p){
		forwardCommand(new PlayCommand(p));
	}
	@Override
	public void stop(){
		forwardCommand(new QuitCommand());
	}
	@Override
	public void restart(){
		forwardCommand(new RestartCommand());
	}
	private void forwardCommand(Command cmd){
		if(gameOver == false){
			try {
				connectionToServer.sendObject(cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void connect()throws Exception{
		connectionToServer = new Connection(new Socket(host,port));
		connectionToServer.sendObject("Connect");
		Object response = connectionToServer.getObject();
		if(response instanceof Exception){
			throw (Exception)response;
		}
		try{
			gameFactory = (GameFactory) connectionToServer.getObject(); 
			localPiece = (Piece) connectionToServer.getObject();
		}catch(Exception e){
			throw new GameError("Unknown server response: " + e.getMessage());
		}
	}
	public void start(){
		this.observers.add(new GameObserver());
		gameOver = false;
		while(!gameOver){
			try{
				Response res = ...//read a response
				for(GameObserver o: observers){
					//execute the response of the observer o
				}
			}catch(ClassNotFoundException | IOException e){
			}
		}
	}
}
