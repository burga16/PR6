package es.ucm.fdi.tp.practica6;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class GameServer extends Controller implements GameObserver{
	private int port;
	private int numPlayers;
	private int numOfConnectedPlayers;
	private GameFactory gameFactory;
	private List<Connection> clients;
	private List<Piece> pieces;
	
	volatile private ServerSocket server;
	volatile private boolean stopped;
	volatile private boolean gameOver;
	
	private JTextArea infoArea;
	
	public GameServer(GameFactory gameFactory, List<Piece> pieces, int port){
		super(new Game(gameFactory.gameRules()),pieces);
		this.port = port;
		this.numPlayers = pieces.size();
		this.pieces = pieces;
		game.addObserver(this);
	}
	
	@Override
	public synchronized void makeMove(Player player){
		try{super.makeMove(player);}catch(GameError e){}
	}
	@Override
	public synchronized void stop(){
		try{super.stop();}catch(GameError e){}
	}
	@Override
	public synchronized void restart(){
		try{super.restart();}catch(GameError e){}
	}
	private void controlGui(){
		try{
			SwingUtilities.invokeAndWait(new Runnable(){
				@Override
				public void run(){constructGUI();}
			});
		} catch(InvocationTargetException | InterruptedException e){
			throw new GameError("Something went wrong when constrructing the GUI");
		}
	}
	private void constructGUI(){
		//created text area for printting messages
		JFrame window = new JFrame("Game Server");
		JPanel a = new JPanel(new BorderLayout());
		a.setPreferredSize(new Dimension(100,150));
		a.setBorder(BorderFactory.createTitledBorder("Info Messages"));
		infoArea = new JTextArea(5,15);
		infoArea.setEditable(false);
		
	
		//quit button
		JButton quitButton = new JButton("Stop Server");
		Dimension preferredSize = new Dimension(500,500);
		window.setPreferredSize(preferredSize);
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
	private void log(String msg){
		//invokeLater
		infoArea.append("* " + msg + "\n");
	}
	private void startServer(){
		try{
			server = new ServerSocket(port);
		}catch(IOException e){
			throw new GameError("No se pudo iniciar el servidor");
		}
		stopped = false;
		//iniciamos un hilo para espera de sockets clientes
		while(!stopped){
			try{
				Socket s = server.accept();
				log("Got a player request");
				handleRequest(s);
				}catch(IOException e){
					if(!stopped){	
					log("error while waiting for a connection: " + e.getMessage());
				}
			}
		}
	}
	private void handleRequest(Socket s){
		try{
			Connection c = new Connection(s);
			Object clientRequest = c.getObject();
			if(!(clientRequest instanceof String) && !((String)clientRequest).equalsIgnoreCase("Connect")){
				c.sendObject(new GameError("Invalid Request"));
				c.stop();
				return;
			}
			if(numOfConnectedPlayers >= numPlayers ){
				c.sendObject(new GameError("The number of connected players exceeds the limit"));
				c.stop();
				return;
			}
			numOfConnectedPlayers++;
			clients.add(c);
			c.sendObject("OK");
			c.sendObject(gameFactory);
			c.sendObject(numOfConnectedPlayers-1);
			if(numOfConnectedPlayers == numPlayers){
				start();
			}//La primera vez start después restart
			startClientListener(c);
		}catch(IOException | ClassNotFoundException _e){}
	}
	private void startClientListener(Connection c){
		//lanzamos un hilo para cada cliente
		gameOver = false;
		Thread t = new Thread(){
			@SuppressWarnings("deprecation")
			@Override
			public void run(){
				while(!stopped && !gameOver){
					try{
						Command cmd;
						cmd = (Command)c.getObject();
						cmd.execute(GameServer.this);
					}catch(ClassNotFoundException | IOException e){
						if(!stopped && !gameOver){
							stopTheGame();
						}
					}
				}
			};
		};
		t.start();
	}
	void forwardNotification(Response r){
		try{
			for(Connection c: clients){
				c.sendObject(r);
			}
		}catch(IOException e){
			stopTheGame();
		}
	}
	private void stopTheGame(){
		stop();
		gameOver = true;
		for(Connection c: clients){
			try {
				c.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
	public void onGameStart(Board board, String gameDesc, List<Piece> pieces, Piece turn){
		forwardNotification(new GameStartResponse(board,gameDesc,pieces,turn));
	}
	public void onGameOver(Board board, State state, Piece winner){
		forwardNotification(new GameOverResponse(board, state, winner));
		stopTheGame();
	}
	public void onMoveStart(Board board, Piece turn){
		forwardNotification(new MoveStartResponse(board,turn));
	}
	public void onMoveEnd(Board board, Piece turn, boolean success){
		forwardNotification(new MoveEndResponse(board,turn,success));
	}
	public void onChangeTurn(Board board, Piece turn){
		forwardNotification(new ChangeTurnResponse(board,turn));
	}
	public void onError(String msg){
	forwardNotification(new ErrorResponse(msg));
	}
}
