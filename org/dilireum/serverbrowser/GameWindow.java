/*
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.dilireum.serverbrowser;

import java.util.ResourceBundle;

import org.dilireum.logging.SysLogger;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;


public class GameWindow {

	public static final int	SIZE_PLAYER_TABLE_ARRAY		= 3;	// For 2 teams plus spec list.  May want to add for ref
	private static final int	GENERIC			= 0;
	private static final int	TEAM			= 1;
	private static final int	FFA				= 2;

	private Composite		window;
	private ResourceBundle	resources;
	private Composite		parentComp;
//	private Composite		mainComp;
	
	private StackLayout		stack;
	private Composite		gameWindowComp;
	private Composite		teamWindowComp;
	private Composite		ffaWindowComp;
	private GameWindowWidgets	gt[];
	private boolean			displayingExtendedInfo;
	private boolean			displayingTeamInfo;

	public GameWindow(Composite parent, ResourceBundle resources) {
		try {
			FormData			data;

			window				= parent;
			this.resources		= resources;
			parentComp = new Composite(window, SWT.NONE);
			stack = new StackLayout();
			parentComp.setLayout(stack);
			data = new FormData();
			data.top	= new FormAttachment(0,0);
			data.bottom	= new FormAttachment(100, 0);
			data.left	= new FormAttachment(0,0);
			data.right	= new FormAttachment(100,0);
			parentComp.setLayoutData(data);

			gameWindowComp		= new Composite(parentComp, SWT.NONE);
			gameWindowComp.setLayout(new FormLayout());
			data = new FormData();
			data.top	= new FormAttachment(0,0);
			data.bottom	= new FormAttachment(100, 0);
			data.left	= new FormAttachment(0,0);
			data.right	= new FormAttachment(100,0);
			gameWindowComp.setLayoutData(data);
			
			teamWindowComp	= new Composite(parentComp, SWT.NONE);
			teamWindowComp.setLayout(new FormLayout());
			data = new FormData();
			data.top	= new FormAttachment(0,0);
			data.bottom	= new FormAttachment(100, 0);
			data.left	= new FormAttachment(0,0);
			data.right	= new FormAttachment(100,0);
			teamWindowComp.setLayoutData(data);

			ffaWindowComp	= new Composite(parentComp, SWT.NONE);
			ffaWindowComp.setLayout(new FormLayout());
			data = new FormData();
			data.top	= new FormAttachment(0,0);
			data.bottom	= new FormAttachment(100, 0);
			data.left	= new FormAttachment(0,0);
			data.right	= new FormAttachment(100,0);
			ffaWindowComp.setLayoutData(data);
			
			displayingExtendedInfo = false;
			displayingTeamInfo = false;
			gt = new GameWindowWidgets[3];
			for (int i = 0; i < 3; i++) {
				gt[i] = new GameWindowWidgets();
			}
			
			gt[GENERIC].playerTable[0] = displayPlayerListHeader(gameWindowComp, true);
			displayExtendedHeader(teamWindowComp, true);
			displayExtendedHeader(ffaWindowComp, false);
			stack.topControl	= gameWindowComp;
			parentComp.layout();
		} catch (SWTException e) {
			SysLogger.logMsg(0, "SWTException caught");
			e.printStackTrace();
		}
	}

	private class GameWindowWidgets {
		//Unique sets are needed - one for each: ffa, team, generic
		public Composite[]		playerTableComp;
		public Composite[]		teamHdrComp;
		public Composite		specComp;
		public Composite		topComp;
		public Composite		clockComp;
		public Composite		serverComp;
		
		private Composite		playerComp;
		private Composite[]		teamComp;

		public Label			clockLabel;
		public Label			serverLabel;
		public Label[]			teamName;
		public Label[]			teamLabel;
		public Label[]			playersHdrLabel;
		public Label[]			playersLabel;
		public Label[]			scoreHdrLabel;
		public Label[]			scoreLabel;
		public Label[]			pingHdrLabel;
		public Label[]			pingLabel;
		public Table[]			playerTable;
		public Sash				specSash;

		public GameWindowWidgets () {
			teamName			= new Label[3];
			teamLabel			= new Label[3];
			playersHdrLabel 	= new Label[3];
			playersLabel		= new Label[3];
			scoreHdrLabel		= new Label[3];
			scoreLabel			= new Label[3];
			pingHdrLabel		= new Label[3];
			pingLabel			= new Label[3];

			playerTable			= new Table[SIZE_PLAYER_TABLE_ARRAY];
			teamComp			= new Composite[SIZE_PLAYER_TABLE_ARRAY];
			playerTableComp		= new Composite[SIZE_PLAYER_TABLE_ARRAY];
			teamHdrComp			= new Composite[SIZE_PLAYER_TABLE_ARRAY];
		}
	}
	
	public void displayServer(Server s) {
		int		i;
		int		limit;
		int		index;

		try {
			clearWindow();

			if (s.hasExtendedInfo) {
				if (s.isTeamGame) {
					stack.topControl = teamWindowComp;
					limit = 2;
					index = TEAM;
				} else {
					stack.topControl = ffaWindowComp;
					limit = 1;
					index = FFA;
				}
				for (i = 0; i < limit; i++) {
					displayTeamHeader(i, s);
					displayPlayerList(gt[index].playerTable[i], s.teams[i], true);
				}
				displayPlayerList(gt[index].playerTable[i], s.teams[i], false);
				gt[index].serverLabel.setText(s.gameType + " - " + s.mapName);
				if (s.gameClock != null) {
					gt[index].clockLabel.setText(resources.getString("gamewindow.clocklabel") + s.gameClock);
					//gt[index].clockLabel.pack();
				} else {
					gt[index].clockLabel.setText(" ");
					gt[index].clockLabel.pack();
				}
				gt[index].serverLabel.pack();
			} else {
				displayPlayerList(gt[GENERIC].playerTable[0], s.players, true);
				stack.topControl = gameWindowComp;
			}
			parentComp.layout();

			displayingExtendedInfo = s.hasExtendedInfo;
			displayingTeamInfo = s.isTeamGame;
		} catch (SWTException e) {
			SysLogger.logMsg(0, "Caught exception " + e);
		}
	}
	
	public void clearWindow(){
		try {
			int limit;
			if (displayingExtendedInfo) {
				int index;
				if (displayingTeamInfo) {
					limit = SIZE_PLAYER_TABLE_ARRAY;
					index = TEAM;
				} else {
					limit = 2;
					index = FFA;
				}

				for (int i = 0; i < limit; i++) {
					if (i < (limit - 1)) {
						gt[index].playersLabel[i].setText("");
						gt[index].scoreLabel[i].setText("");
						gt[index].pingLabel[i].setText("");
						gt[index].teamName[i].setText("");
						gt[index].clockLabel.setText("Game clock:");
						gt[index].serverLabel.setText("");
					}
					gt[index].playerTable[i].setRedraw(false);
					gt[index].playerTable[i].removeAll();
					gt[index].playerTable[i].setRedraw(true);
				}
			} else {
				gt[GENERIC].playerTable[0].setRedraw(false);
				gt[GENERIC].playerTable[0].removeAll();
				gt[GENERIC].playerTable[0].setRedraw(true);
			}
		} catch (SWTException e) {
			//e.printStackTrace();
		}
	}
	
	private Table displayPlayerListHeader(Composite parent, boolean displaySpec) {
		FormData	data;
		String[]	ColumnName = {  resources.getString("gamewindow.playername"), 
									resources.getString("gamewindow.playerscore"), 
									resources.getString("gamewindow.playerping"), " "};
		int[]		ColumnAlign = { SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.LEFT };
		int[]		ColumnWidth = { 150, 60, 60, 1 };
		boolean[]	ColumnResize = { true, true, true, true };
		Table			pt;
		TableColumn[]	tc;

		try {
			pt = new Table(parent, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
			data = new FormData();
			data.left	= new FormAttachment(0,0);
			data.right	= new FormAttachment(100,0);
			data.top	= new FormAttachment(0,0);
			data.bottom	= new FormAttachment(100,0);
			pt.setLayoutData(data);
			pt.setHeaderVisible(true);
			pt.setLinesVisible(false);
			pt.setRedraw(false);

			tc = new TableColumn[ColumnName.length];
			for (int i = 0; i < tc.length; i++) {
				if (!displaySpec && (i == 1)) continue;
				tc[i] = new TableColumn(pt, ColumnAlign[i]);
				tc[i].setText(ColumnName[i]);
				tc[i].setWidth(ColumnWidth[i]);
				tc[i].setResizable(ColumnResize[i]);
			}
			pt.setRedraw(true);
			return pt;
		} catch (Exception e) {
			return null;
		}
	}
	
	private void displayExtendedHeader(Composite parent, boolean teamGame) throws SWTException {
		try {
			FormData	data;
			int			i;
			int			limit;
			int			index;
			GridLayout	gl;
			GridData	gdata;
			
			if (teamGame) {
				limit = 2;
				index = TEAM;
			} else {
				limit = 1;
				index = FFA;
			}
			gt[index].topComp	= new Composite(parent, SWT.NONE);
			gt[index].specSash	= new Sash(parent, SWT.HORIZONTAL);
			gt[index].playerComp	= new Composite(parent, SWT.NONE);

			data = new FormData();
			data.top	= new FormAttachment(0, 0); 
			data.bottom	= new FormAttachment(gt[index].playerComp, 0);
			data.left	= new FormAttachment(0, 0);
			data.right	= new FormAttachment(100, 0);
			gt[index].topComp.setLayoutData(data);
			gt[index].topComp.setLayout(new GridLayout(2, true));

			gt[index].serverComp = new Composite(gt[index].topComp, SWT.NONE);
			gt[index].serverComp.setLayout(new FormLayout());
//		gdata = new GridData(GridData.FILL_HORIZONTAL);
//		gdata.grabExcessHorizontalSpace = true;
//		gdata.minimumWidth = gt[index].topComp.getClientArea().width / 2;
//		gdata.horizontalAlignment = GridData.BEGINNING;
			gt[index].serverComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			gt[index].serverLabel	= new Label(gt[index].serverComp, SWT.LEFT);
			data = new FormData();
			data.top	= new FormAttachment(0,0);
			data.bottom = new FormAttachment(100,0);
			data.left	= new FormAttachment(0,0);
			data.right	= new FormAttachment (100,0);
			gt[index].serverLabel.setLayoutData(data);
			gt[index].serverLabel.setText("");

			gt[index].clockComp = new Composite(gt[index].topComp,SWT.NONE);
			gt[index].clockComp.setLayout(new FormLayout());
			gt[index].clockComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			gt[index].clockLabel	= new Label(gt[index].clockComp, SWT.RIGHT);
			data = new FormData();
			data.top	= new FormAttachment(0,0);
			data.bottom = new FormAttachment(100,0);
			data.left	= new FormAttachment(0,0);
			data.right	= new FormAttachment (100,0);
			gt[index].clockLabel.setLayoutData(data);
			gt[index].clockLabel.setText(resources.getString("gamewindow.clocklabel"));

			data = new FormData();
			data.top	= new FormAttachment(60, 0); 
			data.left	= new FormAttachment(0, 0);
			data.right	= new FormAttachment(100, 0);
			gt[index].specSash.setLayoutData(data);
			gt[index].specSash.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					int index = displayingTeamInfo ? TEAM : FFA;
					((FormData) gt[index].specSash.getLayoutData()).top = new FormAttachment(0, event.y);
					gt[index].specSash.getParent().layout();
				}
			});

			gt[index].playerComp.setLayout(new FillLayout(SWT.HORIZONTAL));
			data = new FormData();
			data.top	= new FormAttachment(gt[index].topComp, 0); 
			data.bottom	= new FormAttachment(gt[index].specSash, 0); 
			data.left	= new FormAttachment(0, 0);
			data.right	= new FormAttachment(100, 0);
			gt[index].playerComp.setLayoutData(data);

			gt[index].specComp = new Composite(parent, SWT.NONE);
			gt[index].specComp.setLayout(new FormLayout());
			data = new FormData();
			data.top	= new FormAttachment(gt[index].specSash, 0); 
			data.bottom	= new FormAttachment(100, 0); 
			data.left	= new FormAttachment(0, 0);
			data.right	= new FormAttachment(100, 0);
			gt[index].specComp.setLayoutData(data);

			for (i = 0; i < limit; i++) {
				gt[index].teamComp[i] = new Composite(gt[index].playerComp, SWT.NONE);
				gdata = new GridData();
				gl = new GridLayout(1, true);
				gl.marginHeight = 0;
				gl.marginWidth = 0;
				gl.horizontalSpacing = 0;
				gl.verticalSpacing = 0;
				gt[index].teamComp[i].setLayout(gl);

				gt[index].teamHdrComp[i] = new Composite(gt[index].teamComp[i], SWT.BORDER);
				gl = new GridLayout(4, true);
				gl.marginHeight = 0;
				gl.marginWidth = 0;
				gl.horizontalSpacing = 0;
				gl.verticalSpacing = 0;
				gt[index].teamHdrComp[i].setLayout(gl);
				gt[index].teamHdrComp[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				gt[index].playerTableComp[i] = new Composite(gt[index].teamComp[i], SWT.NONE);
				gt[index].playerTableComp[i].setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL));
				gt[index].playerTableComp[i].setLayout(new FormLayout());

				gt[index].teamLabel[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER);
				gt[index].teamLabel[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
				gt[index].teamLabel[i].setText(resources.getString("gamewindow.team"));

				gt[index].playersHdrLabel[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER);
				gt[index].playersHdrLabel[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
				gt[index].playersHdrLabel[i].setText(resources.getString("gamewindow.players"));

				gt[index].scoreHdrLabel[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER);
				gt[index].scoreHdrLabel[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
				gt[index].scoreHdrLabel[i].setText(resources.getString("gamewindow.playerscore"));

				gt[index].pingHdrLabel[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER);
				gt[index].pingHdrLabel[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
				gt[index].pingHdrLabel[i].setText(resources.getString("gamewindow.avgping"));

				gt[index].teamName[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER | SWT.SHADOW_IN);
				gt[index].teamName[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
				gt[index].teamName[i].setText("");

				gt[index].playersLabel[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER | SWT.SHADOW_IN);
				gt[index].playersLabel[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
				gt[index].playersLabel[i].setText("");

				gt[index].scoreLabel[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER | SWT.SHADOW_IN);
				gt[index].scoreLabel[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
				gt[index].scoreLabel[i].setText("");

				gt[index].pingLabel[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER | SWT.SHADOW_IN);
				gt[index].pingLabel[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
				gt[index].pingLabel[i].setText("");

				gt[index].playerTable[i] = displayPlayerListHeader(gt[index].playerTableComp[i], true);
			}

			gt[index].teamComp[i] = new Composite(gt[index].specComp, SWT.NONE);
			data = new FormData();
			data.top	= new FormAttachment(0,0);
			data.bottom	= new FormAttachment(100,0);
			data.left	= new FormAttachment(0,0);
			data.right	= new FormAttachment(100,0);
			gt[index].teamComp[i].setLayoutData(data);
			gdata = new GridData();
			gl = new GridLayout(1, true);
			gl.marginHeight = gl.marginWidth = 0;
			gl.horizontalSpacing = 0;
			gl.verticalSpacing = 0;
			gt[index].teamComp[i].setLayout(gl);

			gt[index].teamHdrComp[i] = new Composite(gt[index].teamComp[i], SWT.BORDER);
			gl = new GridLayout(1, true);
			gl.marginHeight = gl.marginWidth = 0;
			gl.horizontalSpacing = 0;
			gl.verticalSpacing = 0;
			gt[index].teamHdrComp[i].setLayout(gl);
			gt[index].teamHdrComp[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			gt[index].playerTableComp[i] = new Composite(gt[index].teamComp[i], SWT.NONE);
			gt[index].playerTableComp[i].setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL));
			gt[index].playerTableComp[i].setLayout(new FormLayout());

			gt[index].teamLabel[i] = new Label(gt[index].teamHdrComp[i], SWT.CENTER);
			gdata = new GridData();
			gdata.horizontalSpan = 1;
			gdata.grabExcessHorizontalSpace = true;
			gdata.horizontalAlignment = GridData.CENTER;
			gt[index].teamLabel[i].setLayoutData(gdata);
			gt[index].teamLabel[i].setText("Spectators");

			gt[index].playerTable[i] = displayPlayerListHeader(gt[index].playerTableComp[i], false);
		} catch (SWTException e) {
			SysLogger.logMsg(0, "Caught SWTException");
			e.printStackTrace();
			throw e;
		}
	}

	private void displayTeamHeader(int i, Server s) throws SWTException {
		try {
			int	index = s.isTeamGame ? TEAM : FFA;
			gt[index].teamName[i].setText(s.teams[i].name);
			gt[index].playersLabel[i].setText(Integer.toString(s.teams[i].getCount()));
			//playersLabel[i].pack();
			if (s.hasTeamScore) {
				gt[index].scoreLabel[i].setText(Integer.toString(s.teamScore[i]));
				//scoreLabel[i].pack();
			} else {
				gt[index].scoreLabel[i].setText(Integer.toString(getTeamScore(s.teams[i])));
				//scoreLabel[i].pack();
			}
			gt[index].pingLabel[i].setText(Integer.toString(getTeamPing(s.teams[i])));
			//pingLabel[i].pack();
		} catch (SWTException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void displayPlayerList(Table pt, PlayerList pl, boolean displayScore) throws SWTException {
		try {
			pt.setRedraw(false);
			pt.removeAll();
			if (pl != null) {
				for (int loop = 0; loop < pl.getCount(); loop++) {
					Player	p = pl.get(loop);
					int		insertIndex;

					int c = 0;
					insertIndex = findPlayerInsertIndex(p, pt);
					TableItem item = new TableItem(pt, SWT.NONE, insertIndex);

					item.setText(c++, p.readableName);
					if (displayScore) item.setText(c++, Integer.toString(p.score));
					item.setText(c++, Integer.toString(p.pingTime));
					item.setText(c++, " ");
					item.setData(p);
				}
			}
			pt.setRedraw(true);
		} catch (SWTException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private int findPlayerInsertIndex(Player p, Table pt) throws SWTException {
		try {
			int			i;
			
			for (i = 0; i < pt.getItemCount(); i++) {
				if (p.score > ((Player) pt.getItem(i).getData()).score) return i;
			}
			return i;
		} catch (SWTException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private int getTeamScore(PlayerList team) {
		int		teamScore = 0;
		for (int i = 0; i < team.getCount(); i++) {
			teamScore += team.get(i).score;
		}
		return teamScore;
	}
	
	private int getTeamPing(PlayerList team) {
		int		teamPing = 0;
		for (int i = 0; i < team.getCount(); i++) {
			teamPing += team.get(i).pingTime;
		}
		return teamPing / ((team.getCount() == 0) ? 1: team.getCount());
	}
}
