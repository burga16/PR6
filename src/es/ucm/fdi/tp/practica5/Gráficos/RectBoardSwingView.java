package es.ucm.fdi.tp.practica5.Gráficos;

import java.awt.Color;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.control.*;
import es.ucm.fdi.tp.basecode.bgame.model.*;
import es.ucm.fdi.tp.practica5.Gráficos.BoardComponent.Shapes;

public abstract class RectBoardSwingView extends SwingView {
	private BoardComponent boardComp;
	private boolean active;
		public RectBoardSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, 
			Player randPlayer, Player aiPlayer){
		super(g,c,localPiece,randPlayer,aiPlayer);
		
	}
	@Override
	protected void initBoardGui(){
		boardComp = new BoardComponent(game){
			
			
			protected void mouseClicked(int row, int col, int mouseButton){
				if(active)handleMouseClick(row, col, mouseButton);
			}
			@Override
			protected Color getPieceColor(Piece p){
				return RectBoardSwingView.this.getPieceColor(p);
			}
			@Override
			protected Shapes getPieceShape(Piece p){
				return RectBoardSwingView.this.getPieceShape(p);
			}
		};
		setBoardArea(boardComp);
		
	}
	protected Shapes getPieceShape(Piece p){
		List<Piece>pieces = getPieces();
		if(pieces != null && pieces.contains(p)){
			return Shapes.CIRCLE;
		}else{
			return Shapes.RECTANGLE;
		}
		
	}
	public void setActive(boolean active){
		this.active = active;
	}

	protected Color getPieceColor(Piece p){
		return super.getPieceColor(p);
	}
	@Override
	protected void redrawBoard(){
		boardComp.redraw(super.getBoard());
	}
	protected abstract void handleMouseClick(int row, int col, int mouseButton);
}
