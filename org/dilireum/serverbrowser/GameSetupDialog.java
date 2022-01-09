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

import java.io.File;
import org.eclipse.swt.custom.StackLayout;
import org.dilireum.logging.SysLogger;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import java.util.*;

public class GameSetupDialog extends Dialog {

	private boolean		firstTime;
	private String			hostOs;
	private Tree			newTree;
	private TabFolder		gameTab;
	private TabItem		engineTab;
	private TabItem		masterTab;
	private GameMap		newMap;
	private GameMap		returnMap;
	@SuppressWarnings("unused")
	private ResourceBundle	resources;
	private TreeItem		selectedTreeItem;
	private TreeItem		previousTreeItem;
	private GameNode		selectedItemNode;
	private Game			selectedGame;
	private Mod			selectedMod;
	private Button			okButton;
	private Button			cancelButton;
	private Composite		rightComp;

	private Composite		gameComp;
	private Button			gameInstalled;
	private Button			gameDisplayed;
	private Composite		masterGroup;
	private Composite		engineGroup;
	private Label			gEngineLabel;
	private Combo			gameEngine;
	private Label			gwdLabel;
	private Text			gWorkingDir;
	private Button			wDirButton;
	private Label			gscLabel;
	private Text			gStartCmd;
	private Button			sCmdButton;
	private Label			gpLabel;
	private Text			gParms;
	private Button			New;
	private Button			Remove;
	
	private Label			urlLabel;
	private Combo			masterUrl;
	private Button			masterEnabled;
	private Label			masterTypeLabel;
	private Text			masterType;
	private Label			queryNameLabel;
	private Text			queryName;
	private Label			protLabel;
	private Text			protocols;
	private Label			portLabel;
	private Text			port;
	private Button			NewMaster;
	private Button			RemoveMaster;

	private Label			modSpec;
	private Text			modText;
	
	private Composite		modComp;
	private Button			modInstalled;
	private Button			modDisplayed;
	private Composite		modGroup;
	private Label			mwdLabel;
	private Text			mWorkingDir;
	private Button			mwdButton;
	private Label			mscLabel;
	private Text			mStartCmd;
	private Button			mscButton;
	private Label			mpLabel;
	private Text			mParms;
	private StackLayout	stack;
	private Shell			shell;
	

	public GameSetupDialog(Shell parent, String hostOs, ResourceBundle resources) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.resources = resources;
		this.hostOs = hostOs;
		returnMap = null;
	}

	/**
	 * Opens the dialog and returns the revised game tree
	 *
	 * @return Tree
	 */
	public GameMap open(GameMap games) {
		firstTime = true;
		// Create the dialog window
		shell = new Shell(getParent(), getStyle());
		shell.setText("Configure Games");
		createContents(games);
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				newMap = null;
			}
		});
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return returnMap;
	}

	private void createContents(GameMap games) {
		FormData	fdata;
		FormLayout	fl;
		GridData	gdata;
		GridLayout	gl;
		Composite	leftComp;
		Composite	bottomComp;

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		shell.setLayout(gl);

		leftComp	= new Composite(shell, SWT.BORDER);
		gdata = new GridData();
		gdata.widthHint = 200;
		gdata.heightHint = 345;
		leftComp.setLayoutData(gdata);
		leftComp.setLayout(new FillLayout());
		
		rightComp	= new Composite(shell, SWT.BORDER);
		stack = new StackLayout();
		rightComp.setLayout(stack);
		gdata = new GridData();
		gdata.widthHint = 400;
		gdata.heightHint = 345;
		rightComp.setLayoutData(gdata);
		
		gameComp		= new Composite(rightComp, SWT.NONE);
		gl = new GridLayout(2, false);
		gl.verticalSpacing = 8;
		gameComp.setLayout(gl);
		fdata = new FormData();
		fdata.top	= new FormAttachment(0,0);
		fdata.bottom	= new FormAttachment(100, 0);
		fdata.left	= new FormAttachment(0,0);
		fdata.right	= new FormAttachment(100,0);
		gameComp.setLayoutData(fdata);
		buildGameParmsWindow(gameComp);
		
		modComp	= new Composite(rightComp, SWT.NONE);
		gl = new GridLayout(2, false);
		gl.verticalSpacing = 8;
		modComp.setLayout(gl);
		fdata = new FormData();
		fdata.top	= new FormAttachment(0,0);
		fdata.bottom	= new FormAttachment(100, 0);
		fdata.left	= new FormAttachment(0,0);
		fdata.right	= new FormAttachment(100,0);
		modComp.setLayoutData(fdata);
		buildModParmsWindow(modComp);

		bottomComp = new Composite(shell, SWT.BORDER);
		gdata = new GridData(GridData.FILL_BOTH);
		gdata.horizontalSpan = 2;
		//gdata.heightHint = 40;
		bottomComp.setLayoutData(gdata);

		fl = new FormLayout();
		fl.spacing = 30;
		fl.marginBottom = 10;
		fl.marginTop = 10;
		fl.marginLeft = 5;
		fl.marginRight = 10;
		bottomComp.setLayout(fl);
		
		cancelButton = new Button(bottomComp, SWT.PUSH);
		cancelButton.setText("&Cancel");
		fdata = new FormData();
		fdata.top		= new FormAttachment(0,0);
		fdata.bottom	= new FormAttachment(100,0);
		fdata.right	= new FormAttachment(100,0);
		cancelButton.setLayoutData(fdata);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		okButton = new Button(bottomComp, SWT.PUSH);
		okButton.setText("&Ok");
		fdata = new FormData();
		fdata.top		= new FormAttachment(0,0);
		fdata.bottom	= new FormAttachment(100,0);
		fdata.right	= new FormAttachment(cancelButton,0);
		okButton.setLayoutData(fdata);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveNewSettings(selectedItemNode);
				returnMap = newMap;
				for (Game game = returnMap.getFirst(); game != null; game = returnMap.getNext(game.nodeID)) {
					SysLogger.logMsg(4, "Game: " + game.nodeID + " Installed: " + game.isInstalled + " Displayed: " + game.isDisplayed);
				}
				shell.close();
			}
		});
		shell.setDefaultButton(okButton);

		newTree = new Tree(leftComp, SWT.SINGLE | SWT.NONE);
		newTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SysLogger.logMsg(9, "Game tree item explicitly selected");
				treeItemSelected();
			}
		});
		newTree.addTreeListener(new TreeListener() {
			public void treeExpanded(TreeEvent e) {
				SysLogger.logMsg(9, "Game tree item expanded");
				treeItemSelected();
			}
			
			public void treeCollapsed(TreeEvent e) {
				SysLogger.logMsg(9, "Game tree item collapsed");
				treeItemSelected();
			}
		});
		
		newMap = new GameMap();

		newTree.setRedraw(false);

		for (Game game = games.getFirst(); game != null; game = games.getNext(game.nodeID)) {
			Game newGame = game.copyGame();
			newMap.put(game.nodeID, newGame);
			buildGameTree(newGame);			
		}
		newTree.setRedraw(true);
	}

	private void treeItemSelected() {
		if (newTree.getSelectionCount() > 0) {
			selectedTreeItem = newTree.getSelection()[0];
			if (firstTime) {
				firstTime = false;
			} else {
				saveNewSettings(selectedItemNode);
			}
			selectedItemNode = (GameNode) selectedTreeItem.getData(AllKnowingMind.NODE);
			if (previousTreeItem != selectedTreeItem) {
				SysLogger.logMsg(4, selectedItemNode.nodeType + " selected: " +  selectedTreeItem.getText());
				if (selectedItemNode.nodeType.equals(GameNode.GAME_NODE)) {
					selectedGame = (Game) selectedItemNode;
					stack.topControl = gameComp;
				} else {
					selectedMod = (Mod) selectedItemNode;
					stack.topControl = modComp;
				}
				rightComp.layout();
				displayItem(selectedItemNode);
				previousTreeItem = selectedTreeItem;
			}
		}
	}

	private void buildGameParmsWindow(Composite p) {
		GridData	g;
		
		gameInstalled = new Button(p, SWT.CHECK);
		gameInstalled.setText("Game Installed");
		g = new GridData();
		g.horizontalSpan = 2;
		g.horizontalAlignment = GridData.BEGINNING;
		gameInstalled.setLayoutData(g);
		gameInstalled.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedGame.isInstalled = gameInstalled.getSelection();
				SysLogger.logMsg(4, "Game.isInstalled set to " + selectedGame.isInstalled);
				for (int i = 0; i < selectedGame.childList.getCount(); i++) {
					Mod mod = (Mod) selectedGame.childList.get(i);
					mod.isInstalled = true;
				}
			}
		});

		gameDisplayed = new Button(p, SWT.CHECK);
		gameDisplayed.setText("Show Game");
		g = new GridData();
		g.horizontalSpan = 2;
		g.horizontalAlignment = GridData.BEGINNING;
		gameDisplayed.setLayoutData(g);
		gameDisplayed.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedGame.isDisplayed = gameDisplayed.getSelection();
				SysLogger.logMsg(4, "Game.isDisplayed set to " + selectedGame.isDisplayed);
				for (int i = 0; i < selectedGame.childList.getCount(); i++) {
					Mod mod = (Mod) selectedGame.childList.get(i);
					mod.isDisplayed = true;
				}
			}
		});
		
		gameTab = new TabFolder(p, SWT.TOP);
		g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 2;
		gameTab.setLayoutData(g);

		engineTab = new TabItem(gameTab, SWT.NONE);
		engineTab.setText("Engine Info");
		engineGroup = new Composite(gameTab, SWT.NONE);
		engineTab.setControl(engineGroup);
		
		GridLayout gl = new GridLayout(3, false);
		gl.horizontalSpacing = 10;
		engineGroup.setLayout(gl);
		g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 2;
		engineGroup.setLayoutData(g);

		gEngineLabel = new Label(engineGroup, SWT.LEFT);
		gEngineLabel.setText("Engine");
		g = new GridData();
		gEngineLabel.setLayoutData(g);

		gameEngine = new Combo(engineGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 2;
		gameEngine.setLayoutData(g);
		gameEngine.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectEngine(gameEngine.getText());
				SysLogger.logMsg(4, "Engine selected: " + gameEngine.getText());
			}
		});
		gameEngine.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				SysLogger.logMsg(9, "User clicked on the Engine combo control");
				saveEngineInfo(selectedGame);
			}
		});

		gwdLabel = new Label(engineGroup, SWT.LEFT);
		gwdLabel.setText("Working Directory");
		g = new GridData(GridData.FILL);
		gwdLabel.setLayoutData(g);

		gWorkingDir = new Text(engineGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData(GridData.FILL_HORIZONTAL);
		gWorkingDir.setLayoutData(g);
		
		wDirButton = new Button(engineGroup, SWT.PUSH);
		wDirButton.setText("Browse...");
		g = new GridData();
		wDirButton.setLayoutData(g);
		wDirButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(shell);

				dlg.setFilterPath(gWorkingDir.getText());
				dlg.setText("Game Working Directory");

				dlg.setMessage("Select a directory");
				String dir = dlg.open();
				if (dir != null) {
					gWorkingDir.setText(dir);
					selectedGame.workingDir.put(gameEngine.getText(), new VarEntry(gameEngine.getText(), dir));
				}
			}
		});

		gscLabel = new Label(engineGroup, SWT.LEFT);
		gscLabel.setText("Start Command");
		g = new GridData(GridData.FILL);
		gscLabel.setLayoutData(g);

		gStartCmd = new Text(engineGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData(GridData.FILL_HORIZONTAL);
		gStartCmd.setLayoutData(g);
		
		sCmdButton = new Button(engineGroup, SWT.PUSH);
		sCmdButton.setText("Browse...");
		g = new GridData();
		sCmdButton.setLayoutData(g);
		sCmdButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String[] WIN_NAMES = {"Executable Files (*.exe)", "All Files (*.*)"};
				String[] WIN_EXTS = {"*.exe", "*.*"};
				String[] UNIX_NAMES = {"All Files (*)"};
				String[] UNIX_EXTS = {"*"};
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				String[] NAMES;
				String[] EXTS;
				if (hostOs.startsWith("win")) {
					NAMES = WIN_NAMES;
					EXTS = WIN_EXTS;
				} else {
					NAMES = UNIX_NAMES;
					EXTS = UNIX_EXTS;
				}
				dlg.setText("Game Start Command");
				dlg.setFilterNames(NAMES);
				dlg.setFilterExtensions(EXTS);
				dlg.setFilterPath(gWorkingDir.getText());
				String fn = dlg.open();
				if (fn != null) {
					gStartCmd.setText(fn);
					selectedGame.startCommand.put(gameEngine.getText(), new VarEntry(gameEngine.getText(), fn));
				}
			}
		});


		gpLabel = new Label(engineGroup, SWT.LEFT);
		gpLabel.setText("Start Parameters");
		g = new GridData(GridData.FILL);
		gpLabel.setLayoutData(g);

		gParms = new Text(engineGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 2;
		gParms.setLayoutData(g);
		
		New = new Button(engineGroup, SWT.PUSH);
		New.setText("&New");
		g = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_END);
		g.horizontalSpan = 2;
		New.setLayoutData(g);
		New.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				NewEngineDialog ned;
				String			engineString = "";
				String[]		engineArray;
				String[]		newEngine;
				VarEntry		v;
				
				for (v = selectedGame.startCommand.getFirst(); v != null; v = selectedGame.startCommand.getNext(v.getKey())) {
					engineString += v.getKey() + " ";
				}
				engineArray = engineString.split(" ");
				ned = new NewEngineDialog(shell, engineArray, hostOs);
				if ((newEngine = ned.open()) != null) {
					if (engineArray.length == 1) {
						selectedGame.workingDir.put(newEngine[4], new VarEntry(newEngine[4], selectedGame.workingDir.get("default").getValue()));
						selectedGame.startCommand.put(newEngine[4], new VarEntry(newEngine[4], selectedGame.startCommand.get("default").getValue()));
						selectedGame.startParms.put(newEngine[4], new VarEntry(newEngine[4], selectedGame.startParms.get("default").getValue()));
						selectedGame.workingDir.remove("default");
						selectedGame.startCommand.remove("default");
						selectedGame.startParms.remove("default");
					}
					selectedGame.workingDir.put(newEngine[0], new VarEntry(newEngine[0], newEngine[1]));
					selectedGame.startCommand.put(newEngine[0], new VarEntry(newEngine[0], newEngine[2]));
					selectedGame.startParms.put(newEngine[0], new VarEntry(newEngine[0], newEngine[3]));
					gameEngine.removeAll();
					for (v = selectedGame.startCommand.getFirst(); v != null; v = selectedGame.startCommand.getNext(v.getKey())) {
						gameEngine.add(v.getKey());
					}
					gameEngine.select(0);
					selectEngine(gameEngine.getText());
				}
			}
		});
		
		Remove = new Button(engineGroup, SWT.PUSH);
		Remove.setText("&Remove");
		g = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_END);
		Remove.setLayoutData(g);
		Remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (gameEngine.getItemCount() == 1) {
					MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					mb.setText("Error");
					mb.setMessage("Can't delete the default engine.");
					mb.open();
					return;
				}
				String selEngine = gameEngine.getText();
				selectedGame.workingDir.remove(selEngine);
				selectedGame.startCommand.remove(selEngine);
				selectedGame.startParms.remove(selEngine);
				if (gameEngine.getItemCount() == 2) {
					VarEntry	v = selectedGame.workingDir.getFirst();
					String		remEngine = v.getKey();
					
					selectedGame.workingDir.put("default", new VarEntry("default", selectedGame.workingDir.get(remEngine).getValue()));
					selectedGame.startCommand.put("default", new VarEntry("default", selectedGame.startCommand.get(remEngine).getValue()));
					selectedGame.startParms.put("default", new VarEntry("default", selectedGame.startParms.get(remEngine).getValue()));
					selectedGame.workingDir.remove(remEngine);
					selectedGame.startCommand.remove(remEngine);
					selectedGame.startParms.remove(remEngine);
				}
				gameEngine.removeAll();
				for (VarEntry v = selectedGame.startCommand.getFirst(); v != null; v = selectedGame.startCommand.getNext(v.getKey())) {
					gameEngine.add(v.getKey());
				}
				gameEngine.select(0);
				selectEngine(gameEngine.getText());
			}
		});

		masterTab = new TabItem(gameTab, SWT.NONE);
		masterTab.setText("Master Server Info");
		masterGroup = new Composite(gameTab, SWT.NONE);
		masterTab.setControl(masterGroup);

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = 10;
		masterGroup.setLayout(gl);
		g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 2;
		masterGroup.setLayoutData(g);

		urlLabel = new Label(masterGroup, SWT.LEFT);
		urlLabel.setText("Master Server URL");
		g = new GridData();
		urlLabel.setLayoutData(g);

		masterUrl = new Combo(masterGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		g = new GridData(GridData.FILL_HORIZONTAL);
		//g.horizontalSpan = 2;
		masterUrl.setLayoutData(g);
		masterUrl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setMaster(masterUrl.getText());
				SysLogger.logMsg(4, "Master server selected: " +  masterUrl.getText());
			}
		});
		masterUrl.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				SysLogger.logMsg(9, "User clicked on the Master URL combo control");
				saveMasterInfo(selectedGame);
			}
		});

		masterEnabled = new Button(masterGroup, SWT.CHECK);
		masterEnabled.setText("Master Server Enabled");
		g = new GridData();
		g.horizontalSpan = 2;
		g.horizontalAlignment = GridData.BEGINNING;
		masterEnabled.setLayoutData(g);
		
		masterTypeLabel = new Label(masterGroup, SWT.LEFT);
		masterTypeLabel.setText("Master Server Type");
		g = new GridData();
		masterTypeLabel.setLayoutData(g);
		
		masterType = new Text(masterGroup, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		g = new GridData();
		g.widthHint = 100;
		masterType.setLayoutData(g);
		
		queryNameLabel = new Label(masterGroup, SWT.LEFT);
		queryNameLabel.setText("Query Name");
		g = new GridData();
		queryNameLabel.setLayoutData(g);
		
		queryName = new Text(masterGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData();
		g.widthHint = 150;
		queryName.setLayoutData(g);
		
		protLabel = new Label(masterGroup, SWT.LEFT);
		protLabel.setText("Protocols");
		g = new GridData();
		protLabel.setLayoutData(g);
		
		protocols = new Text(masterGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData();
		g.widthHint = 150;
		protocols.setLayoutData(g);
		
		portLabel = new Label(masterGroup, SWT.LEFT);
		portLabel.setText("Master Server Port");
		g = new GridData();
		portLabel.setLayoutData(g);
		
		port = new Text(masterGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData();
		g.widthHint = 45;
		port.setLayoutData(g);

		NewMaster = new Button(masterGroup, SWT.PUSH);
		NewMaster.setText("&New");
		g = new GridData(GridData.HORIZONTAL_ALIGN_END);
		//g.horizontalSpan = 2;
		NewMaster.setLayoutData(g);
		NewMaster.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				NewMasterServerDialog msd = new NewMasterServerDialog(shell, selectedGame);
				MasterServer ms;
				if ((ms = msd.open()) != null) {
					for (int i = 0; i < selectedGame.masterList.getCount(); i++) {
						if (ms.getMasterUrl().equalsIgnoreCase(selectedGame.masterList.get(i).getMasterUrl())) {
							MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
							mb.setText("Duplicate URL");
							mb.setMessage("Master server URL cannot be used more than once.");
							mb.open();
							return;
						}
					}
					
					selectedGame.masterList.add(ms);
					
					masterUrl.removeAll();
					for (int i = 0; i < selectedGame.masterList.getCount(); i++) {
						if (selectedGame.masterList.get(i).getMasterType().equals("q3m")) {
							Q3MasterServer qms = (Q3MasterServer) selectedGame.masterList.get(i);
							masterUrl.add(qms.masterUrl);
						} else if (selectedGame.masterList.get(i).getMasterType().equals("http")) {
							ETQWMasterServer ems = (ETQWMasterServer) selectedGame.masterList.get(i);
							masterUrl.add(ems.masterURL);
						}
					}

					masterUrl.select(0);
					String url;
					if ((url = masterUrl.getText()) != null) {
						setMaster(url);
					}
				}
			}
		});
		
		RemoveMaster = new Button(masterGroup, SWT.PUSH);
		RemoveMaster.setText("&Remove");
		g = new GridData();
		g.horizontalAlignment = GridData.CENTER;
		RemoveMaster.setLayoutData(g);
		RemoveMaster.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (masterUrl.getItemCount() > 1) {
					for (int i = 0; i < selectedGame.masterList.getCount(); i++) {
						MasterServer ms = selectedGame.masterList.get(i);
						if (ms.getMasterUrl().equalsIgnoreCase(masterUrl.getText())) {
							selectedGame.masterList.remove(i);
							break;
						}
					}
					masterUrl.remove(masterUrl.getSelectionIndex());
					masterUrl.select(0);
					String url;
					if ((url = masterUrl.getText()) != null) {
						setMaster(url);
					}

				} else {
					MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					mb.setText("Error");
					mb.setMessage("Can't delete the last master server.");
					mb.open();
				}
			}
		});

		modSpec = new Label(p, SWT.LEFT);
		modSpec.setText("Default Mod Spec");
		g = new GridData();
		modSpec.setLayoutData(g);
		
		modText = new Text(p, SWT.SINGLE | SWT.BORDER);
		g = new GridData(GridData.FILL_HORIZONTAL);
		modText.setLayoutData(g);
	}

	private void buildModParmsWindow(Composite p) {
		GridData	g;

		modInstalled = new Button(p, SWT.CHECK);
		modInstalled.setText("Mod Installed");
		g = new GridData();
		g.horizontalSpan = 2;
		g.horizontalAlignment = GridData.BEGINNING;
		modInstalled.setLayoutData(g);
		modInstalled.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedMod.isInstalled = modInstalled.getSelection();
				SysLogger.logMsg(4, "Mod.isInstalled set to " + selectedMod.isInstalled);
			}
		});

		modDisplayed = new Button(p, SWT.CHECK);
		modDisplayed.setText("Show Mod");
		g = new GridData();
		g.horizontalSpan = 2;
		g.horizontalAlignment = GridData.BEGINNING;
		modDisplayed.setLayoutData(g);
		modDisplayed.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedMod.isDisplayed = modDisplayed.getSelection();
				SysLogger.logMsg(4, "Mod.isDisplayed set to " + selectedMod.isDisplayed);
			}
		});
		
		modGroup = new Composite(p, SWT.BORDER);
		GridLayout gl = new GridLayout(3, false);
		gl.horizontalSpacing = 10;
		modGroup.setLayout(gl);
		g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 2;
		modGroup.setLayoutData(g);

		mwdLabel = new Label(modGroup, SWT.LEFT);
		mwdLabel.setText("Working Directory");
		g = new GridData(GridData.FILL);
		mwdLabel.setLayoutData(g);

		mWorkingDir = new Text(modGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData(GridData.FILL_HORIZONTAL);
		mWorkingDir.setLayoutData(g);

		mwdButton = new Button(modGroup, SWT.PUSH);
		mwdButton.setText("Browse...");
		g = new GridData();
		mwdButton.setLayoutData(g);
		mwdButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(shell);

				dlg.setFilterPath(mWorkingDir.getText());
				dlg.setText("SWT's DirectoryDialog");

				dlg.setMessage("Select a directory");
				String dir = dlg.open();
				if (dir != null) {
					mWorkingDir.setText(dir);
				}
			}
		});

		mscLabel = new Label(modGroup, SWT.LEFT);
		mscLabel.setText("Start Command");
		g = new GridData(GridData.FILL);
		mscLabel.setLayoutData(g);

		mStartCmd = new Text(modGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData(GridData.FILL_HORIZONTAL);
		mStartCmd.setLayoutData(g);
		
		mscButton = new Button(modGroup, SWT.PUSH);
		mscButton.setText("Browse...");
		g = new GridData();
		mscButton.setLayoutData(g);
		mscButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String[] WIN_NAMES = {"Executable Files (*.exe)", "All Files (*.*)"};
				String[] WIN_EXTS = {"*.exe", "*.*"};
				String[] UNIX_NAMES = {"All Files (*)"};
				String[] UNIX_EXTS = {"*"};
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				String[] NAMES;
				String[] EXTS;
				if (hostOs.startsWith("win")) {
					NAMES = WIN_NAMES;
					EXTS = WIN_EXTS;
				} else {
					NAMES = UNIX_NAMES;
					EXTS = UNIX_EXTS;
				}
				dlg.setFilterNames(NAMES);
				dlg.setFilterExtensions(EXTS);
				dlg.setFilterPath(mWorkingDir.getText());
				String fn = dlg.open();
				if (fn != null) {
					mStartCmd.setText(fn);
				}
			}
		});


		mpLabel = new Label(modGroup, SWT.LEFT);
		mpLabel.setText("Start Parameters");
		g = new GridData(GridData.FILL);
		mpLabel.setLayoutData(g);

		mParms = new Text(modGroup, SWT.SINGLE | SWT.BORDER);
		g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 2;
		mParms.setLayoutData(g);
	}

	private void displayItem(GameNode node) {
		if (node.nodeType.equals(GameNode.GAME_NODE))
			displayItem((Game) node);
		else
			displayItem((Mod) node);
	}

	private String makeProtocolString(ArrayList<Integer> protocols) {
		String prots = "";
		for (int i = 0; i < protocols.size(); i++) {
			int theInt = protocols.get(i);
			prots += ((prots.length() == 0) ? "" : ", ") + theInt;
		}
		return prots;
	}
	
	private void selectEngine(String eng) {
		if (selectedGame.workingDir.get(eng).getValue() != null)
			gWorkingDir.setText(selectedGame.workingDir.get(eng).getValue());
		if (selectedGame.startCommand.get(eng).getValue() != null)
			gStartCmd.setText(selectedGame.startCommand.get(eng).getValue());
		if (selectedGame.startParms.get(eng).getValue() != null)
			gParms.setText(selectedGame.startParms.get(eng).getValue());
	}
	
	private ArrayList<Integer> makeProtocolArray(String prots) {
		ArrayList<Integer>	protocols = new ArrayList<Integer>();
		String[]	splits;
		splits = prots.split(",");
		
		for (int i = 0; i < splits.length; i++) {
			try {
				if (!splits[i].equals("")) {
					protocols.add(Integer.parseInt(splits[i].trim()));
				}
			} catch (NumberFormatException e) {
				return protocols;
			}
		}
		return protocols;
	}
	
	private void displayItem(Game game) {
		VarEntry v;

		gameInstalled.setSelection(game.isInstalled);
		gameDisplayed.setSelection(game.isDisplayed);
		gameEngine.removeAll();
		for (v = game.startCommand.getFirst(); v != null; v = game.startCommand.getNext(v.getKey())) {
			gameEngine.add(v.getKey());
		}
		gameEngine.select(0);
		if (game.workingDir.getFirst().getValue() != null)
			gWorkingDir.setText(game.workingDir.getFirst().getValue());
		if (game.startCommand.getFirst().getValue() != null)
			gStartCmd.setText(game.startCommand.getFirst().getValue());
		if (game.startParms.getFirst().getValue() != null)
			gParms.setText(game.startParms.getFirst().getValue());
		if (game.defaultModParms != null)
			modText.setText(game.defaultModParms);

		masterUrl.removeAll();
		for (int i = 0; i < game.masterList.getCount(); i++) {
			if (game.masterList.get(i).getMasterType().equals("q3m")) {
				Q3MasterServer qms = (Q3MasterServer) game.masterList.get(i);
				masterUrl.add(qms.masterUrl);
			} else if (game.masterList.get(i).getMasterType().equals("http")) {
				ETQWMasterServer ems = (ETQWMasterServer) game.masterList.get(i);
				masterUrl.add(ems.masterURL);
			}
		}

		masterUrl.select(0);
		String url;
		if ((url = masterUrl.getText()) != null) {
			setMaster(url);
		}
	}

	private void displayItem(Mod mod) {
		modInstalled.setSelection(mod.isInstalled);
		modDisplayed.setSelection(mod.isDisplayed);
		if (mod.workingDir != null)
			mWorkingDir.setText(mod.workingDir);
		if (mod.startCommand != null)
			mStartCmd.setText(mod.startCommand);
		if (mod.startParms != null)
			mParms.setText(mod.startParms);
	}

	private void saveEngineInfo(Game game) {
		selectedGame.workingDir.put(gameEngine.getText(), new VarEntry(gameEngine.getText(), gWorkingDir.getText()));
		selectedGame.startCommand.put(gameEngine.getText(), new VarEntry(gameEngine.getText(), gStartCmd.getText()));
		selectedGame.startParms.put(gameEngine.getText(), new VarEntry(gameEngine.getText(), gParms.getText()));
	}

	private void saveMasterInfo(Game game) {
		String url = masterUrl.getText();

		for (int i = 0; i < selectedGame.masterList.getCount(); i++) {
			if (selectedGame.masterList.get(i).getMasterType().equals("q3m")) {
				Q3MasterServer qms = (Q3MasterServer) selectedGame.masterList.get(i);
				if (qms.masterUrl.equalsIgnoreCase(url)) {
					qms.pList = makeProtocolArray(protocols.getText());
					qms.isEnabled = masterEnabled.getSelection();
					if ((queryName.getText() != null) && (queryName.getText() != "")) {
						qms.queryGameName = queryName.getText();
					}
					try {
						qms.port = Integer.parseInt(port.getText());
					} catch (NumberFormatException e) {
						MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
						mb.setText("Number Format Error");
						mb.setMessage("Invalid port number " + port.getText());
						mb.open();
						qms.port = 0;
					}
					break;
				}
			} else if (selectedGame.masterList.get(i).getMasterType().equals("http")) {
				ETQWMasterServer ems = (ETQWMasterServer) selectedGame.masterList.get(i);
				if (ems.masterURL.equalsIgnoreCase(url)) {
					ems.isEnabled = masterEnabled.getSelection();
					break;
				}
			}
		}
	}
	
	private void saveNewSettings(GameNode node) {
		if (node.nodeType.equals(GameNode.GAME_NODE))
			saveNewSettings((Game) node);
		else
			saveNewSettings((Mod) node);
	}

	private void saveNewSettings(Game game) {
		saveEngineInfo(game);
		saveMasterInfo(game);
		selectedGame.defaultModParms = modText.getText();
	}
	
	private void saveNewSettings(Mod mod) {
		selectedMod.workingDir = mWorkingDir.getText();
		selectedMod.startCommand = mStartCmd.getText();
		selectedMod.startParms = mParms.getText();
	}

	private void buildGameTree(Game game) {
		TreeItem	g;
		TreeItem	m;
		Mod			mod;
		int			i;
		String		fileSep = File.separator;
		String		ext = (AllKnowingMind.hostOS.startsWith("win") ? ".gif" : ".png");

		g = new TreeItem(newTree, SWT.NONE);
		g.setText(game.nodeName);
		try {
			g.setImage(new Image(shell.getDisplay(), AllKnowingMind.gameIconDir + fileSep + game.iconFileName + ext));
		} catch (Exception e) {
			g.setImage(new Image(shell.getDisplay(), AllKnowingMind.gameIconDir + fileSep + "jollyroger" + ext));
		}
		g.setData(AllKnowingMind.NODE, game);
		
		for (i = 0; i < game.childList.getCount(); i++) {
			mod = (Mod) game.childList.get(i);
			if (!mod.nodeName.equalsIgnoreCase("unknown") && 
				!mod.nodeName.equalsIgnoreCase("other")) {
				m = new TreeItem(g, SWT.NONE);
				m.setText(mod.nodeName);
				m.setImage(new Image(shell.getDisplay(), AllKnowingMind.gameIconDir + fileSep + mod.iconFileName + ext));
				m.setData(AllKnowingMind.NODE, mod);
			}
		}
	}
	
	private void setMaster(String url) {
		for (int i = 0; i < selectedGame.masterList.getCount(); i++) {
			if (selectedGame.masterList.get(i).getMasterType().equals("q3m")) {
				Q3MasterServer qms = (Q3MasterServer) selectedGame.masterList.get(i);
				if (qms.masterUrl.equalsIgnoreCase(url)) {
					masterEnabled.setSelection(qms.isEnabled);
					masterType.setText("Quake 3");
					if (qms.queryGameName == null) {
						queryName.setText("");
					} else {
						queryName.setText(qms.queryGameName);
					}
					queryNameLabel.setVisible(true);
					queryName.setVisible(true);
					protLabel.setVisible(true);
					protocols.setVisible(true);
					portLabel.setVisible(true);
					port.setVisible(true);
					protocols.setText(makeProtocolString(qms.pList));
					port.setText(Integer.toString(qms.port));
					break;
				}
			} else if (selectedGame.masterList.get(i).getMasterType().equals("http")) {
				ETQWMasterServer ems = (ETQWMasterServer) selectedGame.masterList.get(i);
				if (ems.masterURL.equalsIgnoreCase(url)) {
					masterEnabled.setSelection(ems.isEnabled);
					masterType.setText("HTTP");
					queryNameLabel.setVisible(false);
					queryName.setVisible(false);
					protocols.setText("");
					port.setText("");
					protLabel.setVisible(false);
					protocols.setVisible(false);
					portLabel.setVisible(false);
					port.setVisible(false);
					break;
				}
			}
		}
	}
}
