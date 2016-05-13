package es.ucm.fdi.tp.practica5.Gráficos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.control.*;
import es.ucm.fdi.tp.basecode.bgame.model.*;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;

/**
 * Clase abstracta para el control básico de los juegos de la que heredará RectBoardSwingView. 
 * Es la parte gáfica que comparten todos los juegos: JPanel, ComboBox, TextArea y JButton.
 * En ella se define la parte gráfica del control y su funcionalidad
 */

@SuppressWarnings("serial")
public abstract class SwingView extends JFrame implements GameObserver {
	enum PlayerMode {
		MANUAL("m", "Manual"), RANDOM("r", "Random"), AI("a", "Automatics"); //Tres tipos de jugadores

		private String id;
		private String desc;

		PlayerMode(String id, String desc) {
			this.id = id;
			this.desc = desc;
		}

		public String getId() {
			return id;
		}

		public String getDesc() {
			return desc;
		}

		@Override
		public String toString() {
			return desc;
		}
	}
	
	private Controller ctrl;
	protected Observable<GameObserver> game;
	
	private Piece localPiece;
	private Piece turn;
	private List<Piece> pieces;
	
	private Board board;
	
	private Player randPlayer;
	private Player aiPlayer;
	
	private Map<Piece,Color> pieceColors;
	Iterator <Color> colorsIter = Utils.colorsGenerator();
	
	private Map<Piece,PlayerMode> playerTypes;
	
	private boolean inPlay;
	private boolean inMoveExec;
	
	private JPanel boardPanel;
	private JPanel toolBarPanel;
	private JTextArea statusArea;
	private JButton randomButton;
	private JButton aiButton;
	
	private JComboBox<Piece> playerColorsCB;
	private JComboBox<PlayerMode> modesCB; 
	private JComboBox<Piece> playerModesCB; 
	
	/**
	 * Constructora de la Clase. Aquí añadiremos la vista como observador del juego
	 * e iniciaremos la parte gráfica del juego
	 * @param g
	 * Un observador para que avise sobre los eventos que se sucedan en el juego
	 * @param c
	 * Un controlador del juego para que la vista pueda pasar datos a este último
	 * @param localPiece
	 * Indica la pieza del juego a la que pertenece la vista
	 * @param randPlayer
	 * Jugador aleatorio
	 * @param aiPlayer
	 * Jugador inteligente
	 */
	
	public SwingView(Observable<GameObserver> g, Controller c,
		             Piece localPiece, Player randPlayer, Player aiPlayer){
		this.ctrl = c;
		this.game = g;
		this.localPiece = localPiece;
		this.randPlayer = randPlayer;
		this.aiPlayer = aiPlayer;
		this.inPlay = false;
		this.pieceColors = new HashMap<Piece,Color>();
		this.playerTypes = new HashMap<Piece,PlayerMode>();
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				game.addObserver(SwingView.this);
			}
		});
		initGUI();
	
	}
	
	final protected Piece getTurn(){return turn;}
	final protected Board getBoard(){return board;}
	final protected List<Piece> getPieces(){return pieces;}
	
	final protected void setBoardArea(JComponent c){
		boardPanel.add(c, BorderLayout.CENTER);
	}
	final protected void vaciarMsg(){
		statusArea.setText("");
	}
	final protected void addMsg(String msg){
		
		statusArea.append("* " + msg + "\n");
	}
	final protected void addToCtrlArea(JComponent c){
		toolBarPanel.add(c);
	}
	
	private void initGUI(){
		//Construir parte gráfica del control
		this.setTitle("Board Games");
		JPanel mainPanel = new JPanel(new BorderLayout());
			
			//Tablero
			boardPanel = new JPanel(new BorderLayout());
			mainPanel.add(boardPanel, BorderLayout.CENTER);
			initBoardGui();
			
			//Controles
			toolBarPanel = new JPanel();
			toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.Y_AXIS));
			mainPanel.add(toolBarPanel, BorderLayout.LINE_END);
			initControlGui();
			this.setContentPane(mainPanel);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setSize(1000,650);
			this.setVisible(true);
			this.addWindowListener(new WindowListener(){
				@Override
				public void windowClosing(WindowEvent e){
					quit();
				}
				@Override
				public void windowOpened(WindowEvent e) {} 
				@Override
				public void windowDeiconified(WindowEvent e) {}
				@Override
				public void windowIconified(WindowEvent arg0) {}
				@Override
				public void windowDeactivated(WindowEvent e) {}
				@Override
				public void windowClosed(WindowEvent e) {} 
				@Override
				public void windowActivated(WindowEvent e) {}			
			});
	}
	/**
	 * Este método inicia la parte de la vista referente al controlador de juego,
	 * es decir menus y listas desplegables
	 */
	
	final protected void initControlGui() {
		
		addStatusArea();
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createTitledBorder("Player information"));
		playerInfoTable = new PlayerInfoTableModel();
		JTable table = new JTable(playerInfoTable){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col){
				Component comp = super.prepareRenderer(renderer,row,col);
				comp.setBackground(pieceColors.get(pieces.get(row)));
				return comp;
			}
				
		};
		
		JScrollPane sp = new JScrollPane(table);
		mainPanel.setPreferredSize(new Dimension(100,100));
		mainPanel.add(sp);
		addToCtrlArea(mainPanel);
		JPanel panela =  new JPanel(new FlowLayout(FlowLayout.LEFT));
		panela.setBorder(BorderFactory.createTitledBorder("Piece Colors"));
		playerColorsCB = new JComboBox<Piece>();
		JButton setColorButton = new JButton("Choose Color");
		setColorButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				Piece p = (Piece) playerColorsCB.getSelectedItem();
				ColorChooser c = new ColorChooser(new JFrame(), "Selec Piece Color", pieceColors.get(p));
				if(c.getColor() != null){
					pieceColors.put(p, c.getColor());
					repaint();
				}
			}
			
		});
		panela.add(playerColorsCB);
		panela.add(setColorButton);
		
		addToCtrlArea(panela);
		
		JPanel panelb = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelb.setBorder(BorderFactory.createTitledBorder("Player Modes"));
		
		modesCB = new JComboBox<PlayerMode>();
		modesCB.addItem(PlayerMode.MANUAL);
		
		if(randPlayer != null){
			modesCB.addItem(PlayerMode.RANDOM);
		}
		if(aiPlayer != null){
			modesCB.addItem(PlayerMode.AI);
		}
		playerModesCB = new JComboBox<Piece>(new DefaultComboBoxModel<Piece>(){
			@Override
			public void setSelectedItem(Object o){
				super.setSelectedItem(o);
				if(playerTypes.get(o) != PlayerMode.MANUAL){
					modesCB.setSelectedItem(PlayerMode.AI);
				}else{
					modesCB.setSelectedItem(PlayerMode.MANUAL);
				}
			}
		});
		JButton setModeButton = new JButton("Set");
		setModeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				Piece p = (Piece)playerModesCB.getSelectedItem();
				PlayerMode m = (PlayerMode)modesCB.getSelectedItem();
				
				playerTypes.put(p, m);
				playerInfoTable.refresh();
			}
		});
		panelb.add(playerModesCB);
		panelb.add(modesCB);
		panelb.add(setModeButton);
		addToCtrlArea(panelb);
		
		JPanel panelMov = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelMov.setBorder(BorderFactory.createTitledBorder("Automatic Moves"));
		this.randomButton = new JButton("Random");
		randomButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				makeRandomMove();
			}
		});
		this.aiButton = new JButton("Intelligent");
		aiButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				makeAllMove();
			}
		});
		panelMov.add(randomButton);
		panelMov.add(aiButton);
		addToCtrlArea(panelMov);
		
		
		JPanel panelMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton setQuitButton = new JButton("Quit");
		setQuitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				quit();
			}
		});
		JButton setRestartButton = new JButton("Restart");
		setRestartButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				ctrl.restart();
			}
		});
		
		panelMenu.add(setQuitButton);
		panelMenu.add(setRestartButton);
		addToCtrlArea(panelMenu);
	}
	
	final protected void quit(){
		int n = JOptionPane.showOptionDialog(new JFrame(), "Are sure you want to quit?","Quit",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if(n==0){
			try{
				ctrl.stop();
			}catch(GameError _e){
				
			}
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}
	private PlayerInfoTableModel playerInfoTable;
		class PlayerInfoTableModel extends DefaultTableModel{
			private String[] colNames;
			PlayerInfoTableModel(){
				this.colNames = new String[]{"Player","Mode","#Pieces"};
			}
			@Override
			public String getColumnName(int col){
				return colNames[col];
			}
			@Override
			public int getColumnCount(){
				return colNames.length;
			}
			@Override
			public int getRowCount(){
				return pieces == null ? 0: pieces.size();
			}
			@Override
			public Object getValueAt(int rowIndex, int columnIndex){
				if(pieces == null){
					return null;
				}
				Piece p = pieces.get(rowIndex);
				if(columnIndex == 0){
					return p;
				}else if(columnIndex == 1){
					return playerTypes.get(p);
				}else{
					return board.getPieceCount(p);
				}
			}
			public void refresh(){
				fireTableDataChanged();
			}
			
		};
		private void addStatusArea(){
			JPanel p = new JPanel(new BorderLayout());
			p.setPreferredSize(new Dimension(100,150));
			p.setBorder(BorderFactory.createTitledBorder("Status Messages"));
				statusArea = new JTextArea(5,15);
				statusArea.setEditable(false);
				JScrollPane statusAreaScroll = new JScrollPane(statusArea);
				p.add(statusAreaScroll, BorderLayout.CENTER);
			addToCtrlArea(p);
		} 
		
	protected Color getPieceColor(Piece p) {
		Color c = pieceColors.get(p);
		if (c == null) { 
			c = colorsIter.next();
			pieceColors.put(p, c); 
			} 
		return c; 
	}
	final protected Color setPieceColor(Piece p, Color c){return pieceColors.put(p, c);}
	
	final protected void decideMakeManualMove(Player manualPlayer){
		//if(inMoveExec || inPlay) return;
		if(localPiece != null && !localPiece.equals(turn)) return;
		if (playerTypes.get(turn) != PlayerMode.MANUAL) return;
		makeManualMove(manualPlayer);
	}
	private void makeManualMove(Player manualPlayer){
		passMoveToCtroller(manualPlayer);
	}
	private void passMoveToCtroller(final Player p){
		/*SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
			*/
				try{
					ctrl.makeMove(p);
				}catch(GameError _e){
					
				}
				/*
			}
		});
		*/
	}
	@SuppressWarnings("unused")
	private void decideMakeAutomaticMove(){
		if(inMoveExec || !inPlay){
			return;
		}
		if(localPiece != null && !localPiece.equals(turn)){
			return;
		}
		switch(playerTypes.get(turn)){
		case AI:
				makeAllMove();
				break;
		case RANDOM:
				makeRandomMove();
				break;
		default:
				break;
		}
	}
	private void makeAllMove(){
		passMoveToCtroller(aiPlayer);
	}
	private void makeRandomMove(){
		passMoveToCtroller(randPlayer);
	}
	
	
	protected abstract void initBoardGui();
	protected abstract void activateBoard();
	protected abstract void desActivateBoard();
	protected abstract void redrawBoard();
	
	// Game Observer Methods
	public void onGameStart(final Board Board, final String gameDesc, final List<Piece> pieces, final Piece turn){
		SwingUtilities.invokeLater(new Runnable(){
			
			public void run(){
				handleGameStart(Board,gameDesc,pieces,turn);}
		});
	}
	public void onGameOver(final Board board, final State state, final Piece winner){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				addMsg("Partida terminada: " +state);
				addMsg("Victoria para: " +winner);
				handleGameOver(board,state, winner);}
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
			public void run(){
				handleOnChangeTurn(board,turn);}
		});
	}
	public void onError(String msg){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){handleOnError(msg);}
		});
	}
	private void handleGameStart(final Board Board,final String gameDesc, final List<Piece>pieces, final Piece turn){
		this.setTitle("Board Games: " + gameDesc + (localPiece == null ? "" : " (" + localPiece + ")"));
		vaciarMsg();
		this.turn = turn;
		this.board = Board;
		this.pieces = pieces;
		this.inPlay = true;
		for(Piece p: pieces){
			if(playerTypes.get(p)== null){
				playerTypes.put(p, PlayerMode.MANUAL);
				playerModesCB.addItem(p);	
			}	
		}
		
		playerColorsCB.removeAllItems();
		for(Piece p: pieces){
			if(pieceColors.get(p)==null){
				pieceColors.put(p, colorsIter.next()); 
			}
			playerColorsCB.addItem(p);
		}
		
		handleOnChangeTurn(Board,turn);
		setActive(true);
	
	}
	private void handleGameOver(Board board, State state, Piece winner){
		this.board = board;
	}
	private void handleOnMoveStart(Board board, Piece turn){
		this.board = board;
		this.turn = turn;
	}
	private void handleOnMoveEnd(Board board, Piece turn, boolean success){
		this.board = board;
		this.turn = turn;
		
		repaint();
	}
	private void handleOnChangeTurn(Board board, final Piece turn){
		this.board = board;
		this.turn = turn;
		addMsg("Turno para:" +turn);
		if(playerTypes.get(turn)== PlayerMode.RANDOM) this.makeRandomMove();
		else if(playerTypes.get(turn)== PlayerMode.AI) this.makeAllMove();
		if(this.localPiece != turn){
			this.randomButton.setEnabled(false);
			this.aiButton.setEnabled(false);
		}else{
			this.randomButton.setEnabled(true);
			this.aiButton.setEnabled(true);
		}
	}
	public void handleOnError(String msg) {
		
		if (! inPlay || localPiece == null || turn.equals(localPiece )) {
			JOptionPane.showMessageDialog(new JFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	protected abstract void setActive(boolean active);
}
