package es.ucm.fdi.tp.practica5.Gráficos;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;

@SuppressWarnings("serial")
public abstract class BoardComponent extends JComponent implements GameObserver {

	private int _CELL_HEIGHT = 50;
	private int _CELL_WIDTH = 50;

	private Board tablero;
	private int rows;
	private int cols;
	
	private HashMap<Piece,Color> pieceColors = new HashMap<Piece,Color>();
	private Iterator <Color> colorslter = Utils.colorsGenerator();
	public enum Shapes{
		CIRCLE,RECTANGLE
	}

	public BoardComponent(final Observable<GameObserver> game) {
		initGUI();
		initBoard(5,5);
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				game.addObserver(BoardComponent.this);
			}
		});
	}
	private void initBoard(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
	}
	

	private void initGUI() {

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("Mouse Released: " + "(" + e.getX() + ","
						+ e.getY() + ")");
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("Mouse Pressed: " + "(" + e.getX() + ","
						+ e.getY() + ")");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				System.out.println("Mouse Exited Component: " + "(" + e.getX()
						+ "," + e.getY() + ")");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				System.out.println("Mouse Entered Component: " + "(" + e.getX()
						+ "," + e.getY() + ")");
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				BoardComponent.this.mouseClicked(e.getY()/_CELL_HEIGHT , e.getX()/_CELL_WIDTH, e.getButton());
			}
		});
		this.setSize(new Dimension(rows * _CELL_HEIGHT, cols * _CELL_WIDTH));
		repaint();
	}
	
	protected abstract void mouseClicked(int i, int j, int button);
	protected abstract Color getPieceColor(Piece p);
	protected abstract Shapes getPieceShape(Piece p);
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		fillBoard(g);

		
	}
	private void fillBoard(Graphics g){
		_CELL_WIDTH = this.getWidth() / cols;
		_CELL_HEIGHT = this.getHeight() / rows;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				drawCell(i, j, g);
	}
	

	private void drawCell(int row, int col, Graphics g) {
		int x = col * _CELL_WIDTH;
		int y = row * _CELL_HEIGHT;
		if(tablero != null){
			if(tablero.getPosition(row, col) != null){
				if(tablero.getPosition(row, col).getId()== "*"){
					g.setColor(Color.BLACK);
					g.fillRect(x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4);
					
				}
				else{
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4);
					
					g.setColor(getPieceColor(tablero.getPosition(row, col)));
					g.fillOval(x + 4, y + 4, _CELL_WIDTH - 8, _CELL_HEIGHT - 8);
		
					g.setColor(Color.black);
					g.drawOval(x + 4, y + 4, _CELL_WIDTH - 8, _CELL_HEIGHT - 8);
				}
			}else{
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4);
			}
		}
	}
	public void redraw(Board b){
		this.tablero = b;
		repaint();
	}

	public void setBoardSize(int rows, int cols) {
		initBoard(rows, cols);
	}
	public void onGameStart(final Board Board, final String gameDesc, final List<Piece> pieces, final Piece turn){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				handleGameStart(Board,gameDesc,pieces,turn);
				setBoardSize(Board.getRows(), Board.getCols());
				}
		});
	}
	public void onGameOver(final Board board, final State state, final Piece winner){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){handleGameOver(board,state, winner);}
		});
	}
	public void onMoveStart(Board board, Piece turn){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){handleOnMoveStart(board,turn);}
		});
	}
	public void onMoveEnd(Board board, Piece turn, boolean success){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){handleOnMoveEnd(board, turn, success);}
		});
	}
	public void onChangeTurn(Board board, final Piece turn){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){handleOnChangeTurn(board,turn);}
		});
	}
	public void onError(String msg){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){handleOnError(msg);}
		});
	}
	private void handleGameStart(final Board Board,final String gameDesc, final List<Piece>pieces, final Piece turn){
		this.tablero = Board;
		redraw(Board);
		repaint();
	}
	private void handleGameOver(Board board, State state, Piece winner){
	
	}
	private void handleOnMoveStart(Board Board, Piece turn){
		this.tablero = Board;
		redraw(Board);
	}
	private void handleOnMoveEnd(Board board, Piece turn, boolean success){
		this.tablero = board;
		redraw(board);
	}
	private void handleOnChangeTurn(Board board, final Piece turn){
		this.tablero = board;
		redraw(board);
	}
	private void handleOnError(String msg){
		
	}
}
