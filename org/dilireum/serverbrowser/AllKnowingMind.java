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

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.SWT;
import org.apache.commons.cli.*;

import java.net.*;
import java.io.*;
import java.text.Collator;
import java.util.*;

import org.dilireum.logging.*;

public class AllKnowingMind {
	public AllKnowingMind			akm;
	public static final String		AKMversion			= "0.2";
	public static final String		XMLversion			= "0.2";
	private static final int		MAP_IMAGE_WIDTH		= 200;
	public static final String		NODE				= "NODE";
	public static final String		FOLDER				= "FOLDER";
	public static final String		SERVER_LIST_NODE	= "SERVERLIST";
	public static final String		SERVER_NODE			= "SERVER";
	
	public static final String		tokIpAddr			= "${ipaddr}";
	public static final String		tokPort				= "${port}";
	public static final String		tokModId			= "${modid}";
	
	private static final String		fileSep				= File.separator;
	private static String			homeDir;
	private static String			appDir;
	private static final String		iconDir				= "icons";
	private static final String		miscDir				= iconDir + fileSep + "misc";
	private static final String		toolbarDir			= iconDir + fileSep + "toolbar";
	private static final String		flagDir				= iconDir + fileSep + "flags";
	public static final String		gameIconDir			= iconDir + fileSep + "game";
	private static final String		imageDir			= "images";
	private static final String		mapsDir				= imageDir + fileSep + "maps";
	public static String			logDir;
	private static final String		dataDir				= "data";
	private static final String		resourceDir			= "resources";
	
	private static final String		resourcesBundle		= "resources.AKM";
	private static final String		mainConfigFile		= resourceDir + fileSep + "AKM_options.properties";
	
	private static final String		userLanguage		= "akm.lang";
	private static final String		queryConcurrency	= "akm.concurrency";
	private static final String		queryRetries		= "akm.retries";
	private static final String		splashEnabled		= "akm.splash";
	private static final String		checkUpdates		= "akm.check_updates";
	private static final String		onJoinAction		= "akm.join_action";
	
	public static String			language;
	public static Locale			locale;
	public static int				maxConcurrency;
	public static int				maxRetries;
	public static final int			DEFAULT_MAX_CONCURRENCY	= 100;
	public static final int			DEFAULT_MAX_RETRIES		= 3;
	public static final int			MAX_CONCURRENCY			= 300;
	public static final int			MAX_RETRIES				= 10;

	public static ArrayList<LanguageEntry>		languageList;

    public static final int			NEW_VERSION = 0;
    public static final int			CURRENT_VERSION = 1;
    public static final int			OLD_VERSION = 2;
    public static final int			UNKNOWN_VERSION = 3;
    //
    public static final int			REL_YEAR = 2009;
    public static final int			REL_MONTH = 6;
    public static final int			REL_DAY = 28;
    public static String			latestVersion;
    
    //private static final String		appImageFile		= imageDir + fileSep + "oracle128x128.gif";
	
	private static final String		appTitle			= "The All-Knowing Mind";
	private static final String		defLogFileName 		= "AllKnowingMind.log";
	private static final String		projectDownloadUrl	= "http://sourceforge.net/project/showfiles.php?group_id=243818&package_id=297179";
	private static String			logFileName;
	private static final int		defLogLevel			= 0;
	private static int				logLevel;

	private static final boolean	SORT_ASCENDING		= false;
	private static final boolean	SORT_DESCENDING		= true;
	public static String			unknownModIcon;
	public static final String		hostOS				= System.getProperty("os.name").toLowerCase();
	public static final String		hostArch			= System.getProperty("os.arch").toLowerCase();

	private int				sortColumn		= -1;
	private int				lastSortColumn	= -2;
	//private long			lastMinimized   = 0;
	private boolean			sortOrder		= SORT_DESCENDING; // init to desc so a click will sort ascending
	private Collator 		collator;
	//private Image			trayImage;
	private ImageList		flagIcons;
	private Image			pbImage;
	private Image			lockedImage;
	private Image			jollyRoger;
	private Image			blankImage;
	private ImageList		gameIcons;
	private MapImageList	mapImages;
	private Image			mapImage;

	private Image			rebuildImage;
	private Image			updateCurrentImage;
	private Image			pingCurrentImage;
	private Image			updateSelectedImage;
	private Image			pingImage;
	private Image			cancelImage;
	private Image			connectImage;
	private Image			findImage;
	private Image			optionsImage;
	private Image			gamesImage;
	private Image			watchImage;
	
	private Image			splashImage;
	private Shell			splash;
	private ProgressBar		splashBar;

	private int				loop;

	private CountryLookupService	ipLookup;
	private String 			lookupDir;
	private	Clipboard 		clip;

	private Display			display;
	private Shell			shell;
	//private Tray			tray;
	//private TrayItem		trayApp;
	private Composite		toolbarComp;
	private Composite		mainComp;
	private Composite		leftComp;
	private Composite		rightComp;
	private Composite		bottomComp;
	private Composite		gameComp;
	private Composite		statusComp;
	private GameWindow		gameWindow;

	private Label			statusLabel;
	private Label			gameInfo;
	private ProgressBar		pBar;

	private Sash			mainSash;
	private Sash			gameSash;

	private Tree			gameTree;
	private Table			serverTable;
	
	private Table			varTable;

	private Canvas			mapCanvas;

	private ToolBar			toolbar;
	private ToolItem		rebuildMaster;
	private ToolItem		updateCurrent;
	private ToolItem		pingCurrent;
	private ToolItem		updateSelected;
	private ToolItem		pingSelected;
	private ToolItem		cancelAction;
	private ToolItem		connectSelected;
//	private ToolItem		findPlayer;
	private ToolItem		configureGames;
	private ToolItem		watchServer;
	private ToolItem		configurePrefs;

	
	private Menu			mainMenu;	
	private Menu			fileMenu;
	private Menu			editMenu;
//	private Menu			viewMenu;
//	private Menu			gameMenu;
	private Menu			serverMenu;
	private Menu			helpMenu;

	private MenuItem		fileItem;
//	private MenuItem		fileStatsItem;
	private MenuItem		fileExitItem;
	
	private MenuItem		editItem;
//	private MenuItem		editAddServerItem;
//	private MenuItem		editRemoveServerItem;
//	private MenuItem		editAddMasterItem;
//	private MenuItem		editDeleteMasterItem;
	private MenuItem		editPreferencesItem;
	
//	private MenuItem		viewItem;
//	private MenuItem		viewToolbarItem;
//	private MenuItem		viewColumnsItem;
//	
//	private MenuItem		gameItem;
	private MenuItem		gameConfigureItem;
	
	private MenuItem		serverItem;
	private MenuItem		serverUpdateSelectedItem;
	private MenuItem		serverPingSelectedItem;
	private MenuItem		serverWatchSelectedItem;
	private MenuItem		serverConnectItem;			// Ask for password if needed (and one has not been provided) - store passwords
//	private MenuItem		serverRconItem;
//	private MenuItem		serverFindLanItem;
//	private MenuItem		serverAddTriggerItem;
//	private MenuItem		serverEditTriggerItem;
	
	private MenuItem		helpItem;
	private MenuItem		helpAboutItem;
	private MenuItem		helpCheckUpdates;
	
	private String			currentGamePopupType;	// One of values from GameNode.GAME, FOLDER, MOD, GAMETYPE
	
	private Menu			gamePopup;
	private MenuItem		gtRefreshGame;
	private MenuItem		gtUpdateGame;
	private MenuItem		gtPingAllItem;
	private MenuItem		gtCreateFolderItem;
	private MenuItem		gtDeleteFolderItem;
	private MenuItem		gtAddServer;

	private Menu			serverPopup;
	private MenuItem		stUpdateSelectedItem;
	private MenuItem		stPingSelectedItem;
	private MenuItem		stJoinSelectedItem;
	private MenuItem		stWatchSelected;
	private MenuItem		stCopyAddress;
	private MenuItem		stCopyServer;
	private MenuItem		stAddServerToFolder;
	private MenuItem		stRemoveFolderServers;

	private Game			selectedGame;
	private GameNode		selectedGameNode;
	private TreeItem		selectedGameItem;
	private TreeItem		previousGameItem;
	private ServerList		selectedNodeList;
	private ServerList		selectedServerList;
	private Server			selectedServer;
	private TableItem		previousServerItem;
	
	public static GameMap	games;
	private String			supportedGamesFile;
	private String			userGamesFile;
	private String			gameDefaultsFile;
	private String			languageFile;
	private String			appIconFile;
	//private String		trayImageFile;
	private String			folderImageFile;
	private String			serverIcon;
	private String			pbIconFile;
	private String			blankIconFile;
	private String			lockedIconFile;
	private String			unknownCountryFile;
	private String			splashImageFile;
	private String			imageExt;
	private String			oracleImageFile;
	
	private static Option	noSplash;
	private static Option	help;
	private static Option	version;
	private static Option	logfile;
	private static Option	debug;
	
	public static boolean	enableSplash;
	public static boolean	optEnableSplash;
	public static enum 		JoinAction {
		None("none"), 
		Minimize("minimize"), 
		Exit("exit");
		
		private String	value;
		private JoinAction(String val) {
			value = val;
		}
		
		public String toString() {
			return value;
		}
	};

	public static JoinAction	joinAction;
	public static boolean		areUpdatesChecked;
	
	// Declaration of configuration attribute
	// *************************************
	public static ResourceBundle resources;

	public static Properties options;

	// Static initializer to load language resources
	// *******************************
	static {
		try {
			optEnableSplash = true;
			options = new Properties();
			try {
				loadProperties(options, mainConfigFile);
			} catch (FileNotFoundException e) {
				SysLogger.logMsg(5, mainConfigFile + " not found, setting default values.");
			}
			locale = null;
			language = AllKnowingMind.options.getProperty(userLanguage);
			locale = ((language != null) && !language.equals("") ? new Locale(language) : Locale.getDefault());
			resources = ResourceBundle.getBundle(resourcesBundle, locale);
			AllKnowingMind.options.setProperty(userLanguage, locale.getLanguage());
			if ((language == null) || language.equals("")) {
				language = AllKnowingMind.options.getProperty(userLanguage);
			}

			String value;
			maxConcurrency	= DEFAULT_MAX_CONCURRENCY;
			maxRetries		= DEFAULT_MAX_RETRIES;
			
			if ((value = AllKnowingMind.options.getProperty(queryConcurrency)) != null) {
				maxConcurrency	= Integer.parseInt(value);
			}
			
			if ((value = AllKnowingMind.options.getProperty(queryRetries)) != null) {
				maxRetries		= Integer.parseInt(value);
			}

			joinAction			= JoinAction.Minimize;
			areUpdatesChecked	= true;
			enableSplash		= true;
			
			if ((value = AllKnowingMind.options.getProperty(splashEnabled)) != null) {
				enableSplash	= Boolean.parseBoolean(value);
			}

			if ((value = AllKnowingMind.options.getProperty(checkUpdates)) != null) {
				areUpdatesChecked	= Boolean.parseBoolean(value);
			}
			
			if ((value = AllKnowingMind.options.getProperty(onJoinAction)) != null) {
				if (value.equalsIgnoreCase(JoinAction.Exit.toString())) {
					joinAction = JoinAction.Exit;
				} else if (value.equalsIgnoreCase(JoinAction.Minimize.toString())) {
					joinAction = JoinAction.Minimize;
				} else {
					joinAction = JoinAction.None;
				}
			}

			saveProperties(options, mainConfigFile);
		} catch (MissingResourceException mre) {
			System.err.println(mre.getLocalizedMessage());
			System.exit(1);
		}
	}

	/**
	 * Load the main configuration file
	 * 
	 */
	
	public static void loadProperties(Properties options, String configFile) throws FileNotFoundException {
		InputStream is = null;
		try {
			is = new FileInputStream(configFile);
			options.load(is);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveProperties(Properties options, String configFile) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(configFile);
			AllKnowingMind.options.setProperty(userLanguage, locale.getLanguage());
			AllKnowingMind.options.setProperty(queryConcurrency, Integer.toString(maxConcurrency));
			AllKnowingMind.options.setProperty(queryRetries, Integer.toString(maxRetries));
			AllKnowingMind.options.setProperty(splashEnabled, Boolean.toString(enableSplash));
			AllKnowingMind.options.setProperty(checkUpdates, Boolean.toString(areUpdatesChecked));
			AllKnowingMind.options.setProperty(onJoinAction, joinAction.toString());
			AllKnowingMind.options.store(os, "All-Knowing Mind OPTIONS - DO NOT EDIT");
		} catch (FileNotFoundException e) {
			SysLogger.logMsg(0, configFile + " not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void updateProperties(Properties options, String configFile) {
		try {
			loadProperties(options, configFile);
			SysLogger.logMsg(9, "Properties updated");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args) {
		homeDir = System.getProperty("user.home");
		if (hostOS.startsWith("win")) {
			appDir = homeDir + fileSep + "Application Data" + fileSep + "Dilireum" + fileSep + "The All-Knowing Mind";
		} else {
			appDir = homeDir + fileSep + ".akm";
		}
		File appFile = new File(appDir);
		if (!appFile.exists()) {
			appFile.mkdir();
		}

		logDir = appDir + fileSep + "log";
		logFileName = logDir + fileSep + defLogFileName;
		logLevel = defLogLevel;

    	if (args.length > 0) {
    		processCommandLine(args);
    	}
		new AllKnowingMind().run();
	}
	
	@SuppressWarnings("static-access")
	private static void processCommandLine(String[] args) {
		noSplash	= new Option( "nosplash", "Disable the splash screen. Overrides user preference.");
		help		= new Option( "help", "Print this message." );
		version		= new Option( "version", "Print the version information and exit." );
		logfile		= OptionBuilder.withArgName( "file" )
						.hasArg()
						.withDescription(  "Use given file for log." )
						.create( "logfile" );
		
		debug		= OptionBuilder.withArgName("level")
						.hasArg()
						.withDescription("Print debugging information with specified granularity (0-9).")
						.create("debug");
		
		Options options = new Options();
		options.addOption(noSplash);
		options.addOption(help);
		options.addOption(version);
		options.addOption(debug);
		options.addOption(logfile);

		CommandLineParser parser = new GnuParser();
	    try {
	        CommandLine line = parser.parse( options, args );
	        if (line.hasOption("help")) {
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp( "akm", options );
	        	System.exit(1);
	        }
	        
	        if (line.hasOption("nosplash")) {
	        	optEnableSplash = false;
	        }

	        if (line.hasOption("version")) {
	        	System.out.println("AKMversion: " + AKMversion);
	        	System.out.println("XMLversion: " + XMLversion);
	        	System.exit(1);
	        }

		    if (line.hasOption("debug")) {
		    	try {
					logLevel = Integer.parseInt(line.getOptionValue("debug"));
				} catch (NumberFormatException e) {
					logLevel = defLogLevel;
				}
		    }
		    
		    if (line.hasOption("logfile")) {
		    	logFileName = line.getOptionValue("logfile");
		    }
	    } catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
    }
    
	public void errorWindow(String windowText, String errorText) {
		if (display == null) {
			display = new Display();
		}
		if (shell == null) {
			shell = new Shell(display);
		
			MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage(errorText);
			mb.setText(windowText);
		
			shell.pack();
		
			Rectangle shellRect = shell.getBounds();
			Rectangle displayRect = display.getPrimaryMonitor().getBounds();
			shell.setLocation((displayRect.width - shellRect.width) / 2, 
							  (displayRect.height - shellRect.height) / 2);

			shell.open();
			mb.open();
			shell.dispose();
		} else {
			MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage(errorText);
			mb.setText(windowText);
			mb.open();
		}

	}
	
    private void displaySplashScreen() {
		splashImage = new Image(display, splashImageFile);
		splash = new Shell(SWT.ON_TOP);
		splashBar = new ProgressBar(splash, SWT.NONE);
		splashBar.setMaximum(100);
		Label label = new Label(splash, SWT.NONE);
		label.setImage(splashImage);
		FormLayout layout = new FormLayout();
		splash.setLayout(layout);
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(100, 0);
		labelData.bottom = new FormAttachment(100, 0);
		label.setLayoutData(labelData);
		FormData progressData = new FormData();
		progressData.left = new FormAttachment(0, 5);
		progressData.right = new FormAttachment(100, -5);
		progressData.bottom = new FormAttachment(100, -5);
		splashBar.setLayoutData(progressData);
		splash.pack();
		Rectangle splashRect = splash.getBounds();
		Rectangle displayRect = display.getPrimaryMonitor().getBounds();
		splash.setLocation((displayRect.width - splashRect.width) / 2, 
						(displayRect.height - splashRect.height) / 2);
		if (optEnableSplash && enableSplash) {
			splash.open();
		}
    }

    private int checkForUpdates() {
    	int		versionCheck;
        BufferedReader bin;
        String strDate;
        String[] dateArray;
        //String dlUrl;
        String versionFileUrl = "http://allknowingmind.sourceforge.net/akmcurrentversion.txt";
        try {
            bin = new BufferedReader(new InputStreamReader(new URL(versionFileUrl).openStream()));

            latestVersion = bin.readLine();
            strDate = bin.readLine();
            SysLogger.logMsg(4, "Latest release date:" + strDate);
            //dlUrl = bin.readLine();
            dateArray = strDate.split(" ");
            int year = Integer.parseInt(dateArray[0]);
            int month = Integer.parseInt(dateArray[1]);
            int day = Integer.parseInt(dateArray[2]);

            Calendar thisRelDate = Calendar.getInstance();
            Calendar currRelDate = thisRelDate;
            thisRelDate.set(REL_YEAR, REL_MONTH - 1, REL_DAY);
            currRelDate.set(year,     month - 1,     day);

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM d, yyyy");
            SysLogger.logMsg(4, "The Installed version was released on: " + sdf.format(thisRelDate.getTime()));
            SysLogger.logMsg(4, "The most recent version was released on: " + sdf.format(currRelDate.getTime()));

            if (currRelDate.after(thisRelDate)) {
            	SysLogger.logMsg(4, "You do not have the most current release.");
                versionCheck = OLD_VERSION;
            } else if (thisRelDate.after(currRelDate)) {
            	SysLogger.logMsg(4, "You have a pre-release version installed.");
                versionCheck = NEW_VERSION;
            } else {
            	SysLogger.logMsg(4, "You have the current release.");
                versionCheck = CURRENT_VERSION;
            }
        } catch (Exception ex) {
            versionCheck = UNKNOWN_VERSION;
            SysLogger.logMsg(0, "AKM cannot determine the current version because there is a problem " +
                    "with your Internet connection. The error is:\n" + ex.getMessage());
        }
        return versionCheck;
    }

    private void run() {
		String version = System.getProperty("java.version");
		String versionSubstring = version.substring(0, 3);
		if (versionSubstring.startsWith("1.0") || versionSubstring.startsWith("1.1") || versionSubstring.startsWith("1.2") || 
			versionSubstring.startsWith("1.3") || versionSubstring.startsWith("1.4")) {
			System.out.println("Minumum Java version is 1.5");
			errorWindow("Configuration Error", "Java 1.5 is required");
			System.exit(1);
		}

    	imageExt = (hostOS.startsWith("win")) ? ".gif" : ".png";

    	supportedGamesFile		= dataDir + fileSep + "games.xml";
    	userGamesFile			= dataDir + fileSep + "usergames.xml";
    	gameDefaultsFile		= dataDir + fileSep + "gamedefaults.xml";
    	languageFile			= dataDir + fileSep + "languages.xml";
		appIconFile 		= "oracle16x16" + imageExt;
		serverIcon			= "kdesvnlog" + imageExt;
		pbIconFile			= "punkbuster_logo" + imageExt;
		blankIconFile		= "blank16x16" + imageExt;
		lockedIconFile		= "passworded_logo" + imageExt;
		unknownCountryFile	= "jollyroger" + imageExt;
		folderImageFile		= miscDir + fileSep + "folder" + imageExt; 
		unknownModIcon		= "help";
		splashImageFile		= imageDir + fileSep + "oracle_splash" + imageExt;
		//trayImageFile		= imageDir + fileSep + "oracle22x22" + imageExt;
		oracleImageFile		= imageDir + fileSep + "oracle32x32" + imageExt;
	    display	= new Display();
	    //tray = display.getSystemTray();
		displaySplashScreen();
		
		deleteLogFiles();
    	
    	SysLogger.setLogOutput(SysLogger.SCREEN | SysLogger.FILE);
    	//SysLogger.setLogOutput(SysLogger.FILE);
    	SysLogger.setOutputPath(logFileName);
    	SysLogger.setLogLevel(logLevel);
    	if (SysLogger.getLogOutput() == SysLogger.FILE) {
    		redirect(logFileName);
    	}

    	lookupDir = dataDir;

    	if (!(new File(supportedGamesFile)).exists()) {
    		errorWindow("Configuration Error", "Required file " + supportedGamesFile + " does not exist.  Exiting.");
    		System.exit(1);
    	}

    	if (!(new File(gameDefaultsFile)).exists()) {
    		errorWindow("Configuration Error", "Required file " + gameDefaultsFile + " does not exist.  Exiting.");
    		System.exit(1);
    	}
    	
    	if (!(new File(languageFile)).exists()) {
    		errorWindow("Configuration Error", "Required file " + languageFile + " does not exist.  Exiting.");
    		System.exit(1);
    	}
    	
		languageList = new ArrayList<LanguageEntry>();
		loadLanguages(4, languageList, languageFile);
		

    	collator = Collator.getInstance(Locale.getDefault());  // For sorting the server table

		shell	= new Shell(display);
		
		clip			= new Clipboard(display);
    	ipLookup		= new CountryLookupService(lookupDir);
    	//trayImage		= new Image(shell.getDisplay(), trayImageFile);
    	pbImage			= new Image(shell.getDisplay(), miscDir + fileSep + pbIconFile);
    	blankImage		= new Image(shell.getDisplay(), miscDir + fileSep + blankIconFile);
    	lockedImage		= new Image(shell.getDisplay(), miscDir + fileSep + lockedIconFile);
    	jollyRoger		= new Image(shell.getDisplay(), miscDir + fileSep + unknownCountryFile);
    	flagIcons		= loadFlagIcons(flagDir);
    	gameIcons		= loadGameIcons(gameIconDir);
    	mapImages		= new MapImageList();
    	mapImage		= new Image(shell.getDisplay(), 200, 200);
    	
    	splashBar.setSelection(10);

    	// If usergames.xml does not exist, build it using gamedefaults.xml and games.xml
    	if (!(new File(userGamesFile)).exists()) {
    		String osArch = "";
    		
    		if (hostOS.startsWith("win")) {
    			osArch = "win-" + hostArch;
    		} else if (hostOS.equals("linux")) {
    			osArch = hostOS + "-" + hostArch;
    		} else {
    			errorWindow("Unsupported Architecture", appTitle + " currently only runs on Windows and Linux.");
    			System.exit(1);
    		}
    		
        	games = new GameMap();
        	games.load(4, supportedGamesFile, XMLversion);
        	games.loadDefaults(4, gameDefaultsFile, XMLversion, osArch);
        	games.setInstalledGames();
        	
        	updateUserGames();
    	}

    	games = new GameMap();
    	games.load(4, supportedGamesFile, XMLversion);
    	games.loadInstalledGames(4, userGamesFile, folderImageFile, XMLversion);
    	
    	splashBar.setSelection(30);
    	buildGUI();
    	splashBar.setSelection(40);

    	for (Game game = games.getFirst(); game != null; game = games.getNext(game.nodeID)) {
    		buildGameTree(gameTree, game);
    		game.loadMapImages(shell, mapImages, mapsDir);
    	}
    	currentGamePopupType = "";
    	gamePopup	= new Menu(gameTree);
		serverPopup = new Menu(serverTable);
    	buildGameTreePopup();
    	
    	splashBar.setSelection(70);
    	buildMenu();

    	splashBar.setSelection(90);

    	buildServerTableHeading();
    	buildVarTableHeading();
    	
		shell.setText(appTitle);
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
			}
		});

    	splashBar.setSelection(100);

		shell.pack();
		splash.close();
		splashImage.dispose();
		
		
		shell.open();
		shell.setMaximized(true);

		if (areUpdatesChecked) {
			if (checkForUpdates() == OLD_VERSION) {
				MessageBox	mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				mb.setMessage("Version " + latestVersion + " of " + appTitle + " is available.\n\n" +
						"Would like you to go to the project website to download it now?");
				mb.setText("Update Available");
				if (mb.open() == SWT.YES) {
					BareBonesBrowserLaunch.openURL(projectDownloadUrl);
				}
			}
		}

		if (gameTree.getItemCount() == 0) {
			doGameSetup();
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
		disposeImages();
		
    	SysLogger.closeLogFile();
    }
	
	public void loadLanguages(int logLevel, ArrayList<LanguageEntry> langList, String languagesFile) {
		LanguageLoader	ll = new LanguageLoader(langList, languagesFile);
		ll.parseLanguages();
		ll.printLanguages(logLevel);
	}

	private void buildMenu() {
		
		mainMenu		= new Menu(shell, SWT.BAR);
		
		fileMenu		= new Menu(mainMenu);
		fileItem		= new MenuItem(mainMenu, SWT.CASCADE);
		
		fileItem.setText(resources.getString("menu.file"));
		fileItem.setMenu(fileMenu);
		
		editMenu		= new Menu(mainMenu);
		editItem		= new MenuItem(mainMenu, SWT.CASCADE);
		editItem.setText(resources.getString("menu.edit"));
		editItem.setMenu(editMenu);

//		viewMenu		= new Menu(mainMenu);
//		viewItem		= new MenuItem(mainMenu, SWT.CASCADE);
//		viewItem.setText(resources.getString("menu.view"));
//		viewItem.setMenu(viewMenu);
//
//		gameMenu		= new Menu(mainMenu);
//		gameItem		= new MenuItem(mainMenu, SWT.CASCADE);
//		gameItem.setText(resources.getString("menu.game"));
//		gameItem.setMenu(gameMenu);
//
		serverMenu		= new Menu(mainMenu);
		serverItem		= new MenuItem(mainMenu, SWT.CASCADE);
		serverItem.setText(resources.getString("menu.server"));
		serverItem.setMenu(serverMenu);

		helpMenu		= new Menu(mainMenu);
		helpItem		= new MenuItem(mainMenu, SWT.CASCADE);
		helpItem.setText(resources.getString("menu.help"));
		helpItem.setMenu(helpMenu);

//		fileStatsItem		= new MenuItem(fileMenu, SWT.NONE);
//		fileStatsItem.setText(resources.getString("file.stats"));
//		
//		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		fileExitItem		= new MenuItem(fileMenu, SWT.NONE);
		fileExitItem.setText(resources.getString("file.exit"));
		fileExitItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		
//		editAddServerItem	= new MenuItem(editMenu, SWT.NONE);
//		editAddServerItem.setText(resources.getString("edit.add_server"));
//		
//		editRemoveServerItem	= new MenuItem(editMenu, SWT.NONE);
//		editRemoveServerItem.setText(resources.getString("edit.remove_server"));
//		
//		new MenuItem(editMenu, SWT.SEPARATOR);
//		
//		editAddMasterItem	= new MenuItem(editMenu, SWT.NONE);
//		editAddMasterItem.setText(resources.getString("edit.add_master"));
//		
//		editDeleteMasterItem	= new MenuItem(editMenu, SWT.NONE);
//		editDeleteMasterItem.setText(resources.getString("edit.delete_master"));
//		
//		new MenuItem(editMenu, SWT.SEPARATOR);
		
		editPreferencesItem	= new MenuItem(editMenu, SWT.NONE);
		editPreferencesItem.setText(resources.getString("edit.prefs"));
		editPreferencesItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				configurePreferences();
			}
		});
		
		new MenuItem(editMenu, SWT.SEPARATOR);

		gameConfigureItem	= new MenuItem(editMenu, SWT.NONE);
		gameConfigureItem.setText(resources.getString("edit.games"));
		gameConfigureItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				doGameSetup();
			}
		});
		

//		viewToolbarItem	= new MenuItem(viewMenu, SWT.NONE);
//		viewToolbarItem.setText(resources.getString("view.toolbar"));
//
//		viewColumnsItem	= new MenuItem(viewMenu, SWT.NONE);
//		viewColumnsItem.setText(resources.getString("view.columns"));
		
		serverUpdateSelectedItem	= new MenuItem(serverMenu, SWT.NONE);
		serverUpdateSelectedItem.setText(resources.getString("server.update_selected"));
		serverUpdateSelectedItem.setEnabled(false);
		serverUpdateSelectedItem.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		updateSelected();
	    	}
	    });
		
		serverPingSelectedItem	= new MenuItem(serverMenu, SWT.NONE);
		serverPingSelectedItem.setText(resources.getString("server.ping_selected"));
		serverPingSelectedItem.setEnabled(false);
		serverPingSelectedItem.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		pingSelected();
	    	}
	    });
		
		serverWatchSelectedItem	= new MenuItem(serverMenu, SWT.NONE);
		serverWatchSelectedItem.setText(resources.getString("server.watch"));
		serverWatchSelectedItem.setEnabled(false);
		serverWatchSelectedItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedServerList = buildSelectedServerList(serverTable);
				if (selectedServerList != null) {
					for (int i = 0; i < selectedServerList.getCount(); i++) {
						(new WatchServer(display, selectedServerList.get(i))).run();
					}
				}
	    	}
	    });
		
		serverConnectItem	= new MenuItem(serverMenu, SWT.NONE);
		serverConnectItem.setText(resources.getString("server.connect"));
		serverConnectItem.setEnabled(false);
		serverConnectItem.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
				joinServer(selectedServer);
	    	}
	    });
		
//		serverRconItem	= new MenuItem(serverMenu, SWT.NONE);
//		serverRconItem.setText(resources.getString("server.remote_admin"));
//		serverRconItem.setEnabled(false);
//		
//		new MenuItem(serverMenu, SWT.SEPARATOR);
//		
//		serverFindLanItem	= new MenuItem(serverMenu, SWT.NONE);
//		serverFindLanItem.setText(resources.getString("server.find_lan"));			// Put in folder named "LAN Servers (if any are found)
//		serverFindLanItem.setEnabled(false);
//		
//		serverAddTriggerItem	= new MenuItem(serverMenu, SWT.NONE);
//		serverAddTriggerItem.setText(resources.getString("server.add_trigger"));
//		serverAddTriggerItem.setEnabled(false);
//		
//		serverEditTriggerItem	= new MenuItem(serverMenu, SWT.NONE);
//		serverEditTriggerItem.setText(resources.getString("server.edit_trigger"));
//		serverEditTriggerItem.setEnabled(false);
		
		helpAboutItem	= new MenuItem(helpMenu, SWT.NONE);
		helpAboutItem.setText(resources.getString("help.about"));
		helpAboutItem.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
				AboutDialog ha = new AboutDialog(shell, oracleImageFile);
				ha.open();
	    	}
	    });

		helpCheckUpdates	= new MenuItem(helpMenu, SWT.NONE);
		helpCheckUpdates.setText(resources.getString("help.check_for_updates"));
		helpCheckUpdates.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
				if (checkForUpdates() == OLD_VERSION) {
					MessageBox	mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					mb.setMessage("Version " + latestVersion + " of " + appTitle + " is available.\n\n" +
							"Would like you to go to the project website to download it now?");
					mb.setText("Update Available");
					if (mb.open() == SWT.YES) {
						BareBonesBrowserLaunch.openURL(projectDownloadUrl);
					}
				} else {
					MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
					mb.setText("No Updates Available");
					mb.setMessage("You are running the most current release of The All-Knowing Mind.");
					mb.open();
				}
	    	}
	    });
		
		shell.setMenuBar(mainMenu);
	}


	private void doGameSetup() {
		GameMap newMap;
		GameSetupDialog gsd = new GameSetupDialog(shell, hostOS, resources);
		if ((newMap = gsd.open(games)) != null) {
			SysLogger.logMsg(8, "Game setup canceled: " + (newMap == null));
			games = newMap;
			gameTree.removeAll();
			for (Game game = games.getFirst(); game != null; game = games.getNext(game.nodeID)) {
				buildGameTree(gameTree, game);
			}
			if (gameTree.getItemCount() > 0) {
				gameTree.select(gameTree.getItem(0));
				treeItemSelected();
			}
			updateUserGames();
		}
	}

	private void configurePreferences() {
		PreferencesDialog	pd = new PreferencesDialog(shell);
		
		if (pd.open()) {
			locale = new Locale(language);
			saveProperties(options, mainConfigFile);
		}
	}

	private void buildToolbar() {
		createToolbarImages();
		rebuildMaster	= createToolItem(toolbar, rebuildImage, resources.getString("tooltip.rebuild"));
		rebuildMaster.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		refreshGame();
	    	}
	    });
		rebuildMaster.setEnabled(false);
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		updateCurrent	= createToolItem(toolbar, updateCurrentImage, resources.getString("tooltip.update_current"));
		updateCurrent.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		updateGame();
	    	}
	    });
		updateCurrent.setEnabled(false);
		
		pingCurrent	= createToolItem(toolbar, pingCurrentImage, resources.getString("tooltip.ping_current"));
		pingCurrent.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		updateAllPings();
	    	}
	    });
		pingCurrent.setEnabled(false);

		new ToolItem(toolbar, SWT.SEPARATOR);
		
		updateSelected	= createToolItem(toolbar, updateSelectedImage, resources.getString("tooltip.update_selected"));
		updateSelected.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		updateSelected();
	    	}
	    });
		updateSelected.setEnabled(false);
		
		pingSelected	= createToolItem(toolbar, pingImage, resources.getString("tooltip.ping_selected"));
		pingSelected.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		pingSelected();
	    	}
	    });
		pingSelected.setEnabled(false);
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		cancelAction	= createToolItem(toolbar, cancelImage, resources.getString("tooltip.cancel"));
		cancelAction.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		selectedGame.cancelAction = true;
	    		cancelAction.setEnabled(false);
	    	}
	    });
		cancelAction.setEnabled(false);

		new ToolItem(toolbar, SWT.SEPARATOR);
		
		watchServer	= createToolItem(toolbar, watchImage, resources.getString("tooltip.watch"));
		watchServer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedServerList = buildSelectedServerList(serverTable);
				if (selectedServerList != null) {
					for (int i = 0; i < selectedServerList.getCount(); i++) {
						(new WatchServer(display, selectedServerList.get(i))).run();
					}
				}
	    	}
	    });
		watchServer.setEnabled(false);
		
		connectSelected	= createToolItem(toolbar, connectImage, resources.getString("tooltip.connect"));
		connectSelected.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
				joinServer(selectedServer);
	    	}
	    });
		connectSelected.setEnabled(false);
		
//		findPlayer		= createToolItem(toolbar, findImage, resources.getString("tooltip.search"));
//		findPlayer.addSelectionListener(new SelectionAdapter() {
//	    	public void widgetSelected(SelectionEvent event) {
//	    	}
//	    });
//		findPlayer.setEnabled(false);
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		configurePrefs	= createToolItem(toolbar, optionsImage, resources.getString("tooltip.configure"));
		configurePrefs.setEnabled(true);
		configurePrefs.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		configurePreferences();
	    	}
	    });
		
		configureGames	= createToolItem(toolbar, gamesImage, resources.getString("tooltip.games"));
		configureGames.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		doGameSetup();
	    	}
	    });
	}
	
	private void createToolbarImages() {
		String	ext;
		if (hostOS.startsWith("win")) {
			ext = ".gif";
		} else {
			ext = ".png";
		}
		rebuildImage		= new Image(shell.getDisplay(), toolbarDir + fileSep + "rebuild_master" + ext);
		updateCurrentImage	= new Image(shell.getDisplay(), toolbarDir + fileSep + "update_current" + ext);
		pingCurrentImage	= new Image(shell.getDisplay(), toolbarDir + fileSep + "history" + ext);
		updateSelectedImage	= new Image(shell.getDisplay(), toolbarDir + fileSep + "update_selected" + ext);
		pingImage			= new Image(shell.getDisplay(), toolbarDir + fileSep + "ping_selected" + ext);
		cancelImage			= new Image(shell.getDisplay(), toolbarDir + fileSep + "cancel" + ext);
		connectImage		= new Image(shell.getDisplay(), toolbarDir + fileSep + "connect" + ext);
		findImage			= new Image(shell.getDisplay(), toolbarDir + fileSep + "find" + ext);
		optionsImage		= new Image(shell.getDisplay(), toolbarDir + fileSep + "preferences" + ext);
		gamesImage			= new Image(shell.getDisplay(), toolbarDir + fileSep + "configure" + ext);
		watchImage			= new Image(shell.getDisplay(), toolbarDir + fileSep + "watch2" + ext);
	}

	/**
	 * Create a new ToolItem for the passed ToolBar with a given image and tool tip text
	 * 
	 * @param ToolBar parent - parent ToolBar
	 * @param Image image - image to display for this item
	 * @param String toolTipText - tool tip text for this item
	 * @return new ToolItem
	 */
	
	private ToolItem createToolItem(ToolBar parent, Image image, String toolTipText) {
		ToolItem item = new ToolItem(parent, SWT.PUSH);
		item.setImage(image);
		item.setToolTipText(toolTipText);
		return item;
	}

	/*
	private void minimizeToTray(ShellEvent e) {
		if (tray == null) {
			System.out.println("The system tray is not available");
		} else {
			if (System.currentTimeMillis() - lastMinimized > 1000) {
				SysLogger.logMsg(9, "minimizeToTray() called");
				trayApp = new TrayItem(tray, SWT.NONE);
				trayApp.setToolTipText("The All-Knowing Mind");
				trayApp.addListener(SWT.Show, new Listener() {
					public void handleEvent(Event event) {
						SysLogger.logMsg(9, "show");
					}
				});
				trayApp.addListener(SWT.Hide, new Listener() {
					public void handleEvent(Event event) {
						SysLogger.logMsg(9, "hide");
					}
				});
				trayApp.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						SysLogger.logMsg(9, "selection");
					}
				});
				trayApp.addListener(SWT.DefaultSelection, new Listener() {
					public void handleEvent(Event event) {
						SysLogger.logMsg(9, "default selection");
						System.out.println("Tray Icon double clicked");
						restoreWindow();
						trayApp.dispose();
					}
				});
				shell.setVisible(false);
				trayApp.setImage(trayImage);
				shell.setMinimized(true);
			}
		}
	}


	private void restoreWindow() {
		shell.setMinimized(false);
		shell.setVisible(true);
	}
	*/

	private void treeItemSelected() {
		if (gameTree.getSelectionCount() > 0) {
			selectedGameItem = gameTree.getSelection()[0];
			selectedNodeList = getSelectedGameServerList(selectedGameItem);
			if (previousGameItem != selectedGameItem) {
				SysLogger.logMsg(4, "Game selected: " +  selectedGameItem.getText());
				selectedServer = null;
				displayServerList();
				displayServer(selectedServer);
				updateMenuItems(selectedNodeList);
				previousGameItem = selectedGameItem;
				serverTable.deselectAll();
			}
			buildGameTreePopup();
			previousServerItem = null;
			selectedServer = null;
		}
	}
	
	private void buildGUI() {
		GridLayout		gl;
		FormData		data;
		GridData		gdata;

		String iconFile = imageDir + fileSep + appIconFile;

		if (new File(iconFile).exists()) {
			shell.setImage(new Image(shell.getDisplay(), iconFile));
		}

		/*
		shell.addShellListener(new ShellAdapter() {
			public void shellIconified(ShellEvent e) {
				minimizeToTray(e);
			}
		});
		*/

		shell.setLayout(new FormLayout());

		toolbarComp = new Composite(shell, SWT.NONE);
		gl = new GridLayout();
		gl.numColumns = 1;
		gl.marginHeight = gl.marginWidth = 3;
		toolbarComp.setLayout(gl);
		data = new FormData();
		data.top	= new FormAttachment(0,0);
		data.bottom	= new FormAttachment(100,0);
		data.left	= new FormAttachment(0,0);
		data.right	= new FormAttachment(100,0);
		toolbarComp.setLayoutData(data);
		
		toolbar = new ToolBar(toolbarComp, SWT.HORIZONTAL);
		mainComp = new Composite(toolbarComp, SWT.NONE);
		statusComp	= new Composite(toolbarComp, SWT.BORDER);

		buildToolbar();
		gdata = new GridData();
		toolbar.setLayoutData(gdata);

		gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginHeight = gl.marginWidth = 0;
		gl.horizontalSpacing = 3;
		gl.verticalSpacing = 0;
		mainComp.setLayout(gl);
		gdata = new GridData(GridData.FILL_BOTH);
		mainComp.setLayoutData(gdata);

		leftComp	= new Composite(mainComp, SWT.NONE);
		gl = new GridLayout();
		gl.numColumns = 1;
		gl.marginHeight = gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 3;
		leftComp.setLayout(gl);
		gdata = new GridData(GridData.FILL_VERTICAL);
		leftComp.setLayoutData(gdata);
		
		gameTree = new Tree(leftComp, SWT.SINGLE | SWT.BORDER);
		gdata = new GridData(GridData.FILL_VERTICAL);
		
		gdata.widthHint = 182;
		gameTree.setLayoutData(gdata);
		
		gameTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SysLogger.logMsg(9, "Game tree item explicitly selected");
				treeItemSelected();
			}
		});
		gameTree.addTreeListener(new TreeListener() {
			public void treeExpanded(TreeEvent e) {
				SysLogger.logMsg(9, "Game tree item expanded");
				treeItemSelected();
			}
			
			public void treeCollapsed(TreeEvent e) {
				SysLogger.logMsg(9, "Game tree item collapsed");
				treeItemSelected();
			}
		});
		
		
		DropTarget dt = new DropTarget(gameTree, DND.DROP_COPY | DND.DROP_DEFAULT);
		dt.setTransfer(new Transfer[] { ServerTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = DND.DROP_COPY;
			}

			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = DND.DROP_COPY;
			}

			public void dragOver(DropTargetEvent event) {
				TreeItem item = (TreeItem) event.item;
				if (item != null) {
					GameNode	node = (GameNode) item.getData(NODE);
					if (node != null) {
						if (node.nodeType.equals(GameNode.FOLDER_NODE)) {
							event.feedback = DND.FEEDBACK_SELECT;
						} else {
							event.feedback = DND.FEEDBACK_NONE;
						}
					}
				}
			}

			public void drop(DropTargetEvent event) {
		        TreeItem item = (TreeItem) event.item;
				GameNode	node = (GameNode) item.getData(NODE);
				System.out.println("Game Node: " + node.nodeName + ", Node Type: " + node.nodeType + "  Node ID; " + node.nodeID);
				if (node.nodeType.equals(GameNode.FOLDER_NODE)) {
					if (event.data != null) {
						System.out.println("event data is not null");
						Server[] sList = (Server[]) event.data;
						System.out.println("Server count: " + sList.length);
						for (int i = 0; i < sList.length; i++) {
							Server s = sList[i];
							System.out.println("Game ID: " + s.game.nodeID + ", Node ID: " + node.parent.nodeID);
							if (s.game.nodeID.equals(node.parent.nodeID)) {	// The server is of the same game type as the folder
								s.mod = (Mod) s.game.childList.findGameNode(s.gameMod);
								if ((s.mod == null) || ((s.mod != null) && !s.mod.isDisplayed)) {
									s.mod = (Mod) s.game.childList.findGameNode(Mod.OTHER);
								}
								if (s.mod == null) {
									s.mod = (Mod) s.game.childList.findGameNode(Mod.UNKNOWN);
									s.gameIconFile = s.game.iconFileName;
								} else {
									s.gameIconFile = s.mod.iconFileName;
								}
								Folder f = (Folder) node;
								if (f.serverList.findServerByAddress(s.address.getHostAddress(), s.port) == null) { 
									f.serverList.add(s);
								}
							}
						}
						updateUserGames();
					}
				}
			}
		});

		mapCanvas = new Canvas(leftComp, SWT.BORDER);
		gdata = new GridData();
		gdata.heightHint = MAP_IMAGE_WIDTH;
		gdata.widthHint = MAP_IMAGE_WIDTH;
		mapCanvas.setLayoutData(gdata);
		mapCanvas.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent event) {
				if (mapImage != null) {
					event.gc.drawImage(mapImage, 0, 0);
				}
			}
		});

		rightComp	= new Composite(mainComp, SWT.NONE);
		rightComp.setLayout(new FormLayout());
		gdata  = new GridData(GridData.FILL_BOTH);
		rightComp.setLayoutData(gdata);

		mainSash	= new Sash(rightComp, SWT.HORIZONTAL);
		data = new FormData();
		data.top	= new FormAttachment(60, 0); 
		data.left	= new FormAttachment(0, 0);
		data.right	= new FormAttachment(100, 0);
		mainSash.setLayoutData(data);
		mainSash.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				((FormData) mainSash.getLayoutData()).top = new FormAttachment(0, event.y);
				mainSash.getParent().layout();
			}
		});

		serverTable = new Table(rightComp, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		data = new FormData();
		data.top	= new FormAttachment(0,0);
		data.bottom	= new FormAttachment(mainSash,0);
		data.left	= new FormAttachment(0,0);
		data.right	= new FormAttachment(100,0);
		serverTable.setLayoutData(data);
		serverTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int	selectedCount = serverTable.getSelectionCount();
				if (selectedCount > 0) {
					if (selectedCount == 1) {
						TableItem currentServerItem = serverTable.getSelection()[0];
						selectedServer = getSelectedItemServer(currentServerItem);
						if (previousServerItem != currentServerItem) {
							displayServer(selectedServer);
							previousServerItem = currentServerItem;
						}
					} else {
						previousServerItem = null;
						selectedServer = null;
						displayServer(selectedServer);
					}
				}

				updateMenuItems(selectedNodeList);
			}
		});
		serverTable.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event event) {
				TableItem currentServerItem = serverTable.getSelection()[0];
				selectedServer = getSelectedItemServer(currentServerItem);
				if (previousServerItem != currentServerItem) {
					displayServer(selectedServer);
					previousServerItem = currentServerItem;
				}
				updateMenuItems(selectedNodeList);
				joinServer(selectedServer);
			}
		});

		DragSource source = new DragSource(serverTable, DND.DROP_COPY);
		source.setTransfer(new Transfer[] { ServerTransfer.getInstance() });
		source.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				if (ServerTransfer.getInstance().isSupportedType(event.dataType)) {
					// Get the selected items in the drag source
					DragSource ds = (DragSource) event.widget;
					Table table = (Table) ds.getControl();
					TableItem[] selection = table.getSelection();

					int			count = 0;
					for (int i = 0; i < selection.length; i++) {
						Server s = (Server) selection[i].getData(SERVER_NODE);
						if (!s.gameMod.equals(Mod.UNKNOWN)) {
							count++;
						}
					}

					Server[]	servers = new Server[count];
					int j = 0;
					for (int i = 0; i < selection.length; i++) {
						Server s = (Server) selection[i].getData(SERVER_NODE);
						if (!s.gameMod.equals(Mod.UNKNOWN)) {
							servers[j++] = s;
						}
						//System.out.println("Server name: " + servers[i].readableHostName);
					}
					if (count == 0) {
						event.doit = false;
					}
					// Put the data into the event
					event.data = servers;
				}
			}
		});

		
		bottomComp	= new Composite(rightComp, SWT.NONE);
		bottomComp.setLayout(new FormLayout());
		data = new FormData();
		data.top	= new FormAttachment(mainSash,0);
		data.bottom	= new FormAttachment(100, 0);
		data.left	= new FormAttachment(0,0);
		data.right	= new FormAttachment(100,0);
		bottomComp.setLayoutData(data);

		gameSash	= new Sash(bottomComp, SWT.VERTICAL);
		data = new FormData();
		data.top	= new FormAttachment(0, 0);
		data.bottom	= new FormAttachment(100, 0);
		data.left	= new FormAttachment(75, 0);
		gameSash.setLayoutData(data);
		gameSash.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				((FormData) gameSash.getLayoutData()).left = new FormAttachment(0, event.x);
				gameSash.getParent().layout();
			}
		});

		gameComp	= new Composite(bottomComp, SWT.BORDER);
		//gameComp.setLayout(new FillLayout());
		gameComp.setLayout(new FormLayout());
		data = new FormData();
		data.top	= new FormAttachment(0,0);
		data.bottom	= new FormAttachment(100, 0);
		data.left	= new FormAttachment(0,0);
		data.right	= new FormAttachment(gameSash,0);
		gameComp.setLayoutData(data);
		gameWindow = new GameWindow(gameComp, resources);

		varTable = new Table(bottomComp, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top	= new FormAttachment(0,0);
		data.bottom	= new FormAttachment(100,0);
		data.left	= new FormAttachment(gameSash,0);
		data.right	= new FormAttachment(100,0);
		varTable.setLayoutData(data);

		gl = new GridLayout();
		gl.numColumns = 3;
		gl.makeColumnsEqualWidth = true;
		gl.marginHeight = 1;
		gl.marginWidth = 2;
		gl.horizontalSpacing = 4;
		gl.verticalSpacing = 0;
		
		statusComp.setLayout(gl);
		gdata = new GridData(GridData.FILL_HORIZONTAL);
		//gdata.heightHint = 26;
		statusComp.setLayoutData(gdata);

		statusLabel = new Label(statusComp, SWT.LEFT | SWT.BORDER);
		statusLabel.setText(resources.getString("status.startup"));
		gdata = new GridData(GridData.FILL_HORIZONTAL);
		statusLabel.setLayoutData(gdata);

		gameInfo = new Label(statusComp, SWT.LEFT | SWT.BORDER);
		gdata = new GridData(GridData.FILL_HORIZONTAL);
		gameInfo.setLayoutData(gdata);

		pBar = new ProgressBar(statusComp, SWT.HORIZONTAL | SWT.SMOOTH | SWT.BORDER);
		gdata = new GridData(GridData.FILL_HORIZONTAL);
		pBar.setLayoutData(gdata);
	}

	private void buildGameTreePopup() {

		if (selectedGameNode != null) {
			if (!currentGamePopupType.equals(selectedGameNode.nodeType)) {
				MenuItem[] menuItems = gamePopup.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}

				if (selectedGameNode.nodeType.equals(GameNode.GAME_NODE)) {
					createGamePopup();
				} else if (selectedGameNode.nodeType.equals(GameNode.FOLDER_NODE)) {
					createFolderPopup();
				} else if (selectedGameNode.nodeType.equals(GameNode.MOD_NODE)) {
					createModPopup();
				} else if (selectedGameNode.nodeType.equals(GameNode.TYPE_NODE)) {
					createGameTypePopup();
				} else {
					throw new IllegalArgumentException("Unknown GameNode type: " + selectedGameNode.nodeType);
				}
				gameTree.setMenu(gamePopup);
				
				createServerPopup();
				updateGameTreePopupItems(selectedNodeList);
				
				currentGamePopupType = selectedGameNode.nodeType;
			}
		}
	}

	private void createGamePopup() {
		
		// Create all the items in the popup menu
		gtRefreshGame = new MenuItem(gamePopup, SWT.NONE);
		gtRefreshGame.setText(resources.getString("gamepop.rebuild"));
		gtRefreshGame.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Disable all pop-up menu selections
				refreshGame();
			}
		});
		
		gtUpdateGame = new MenuItem(gamePopup, SWT.NONE);
		gtUpdateGame.setText(resources.getString("gamepop.update_all"));
		gtUpdateGame.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateGame();
			}
		});

		gtPingAllItem = new MenuItem(gamePopup, SWT.NONE);
		gtPingAllItem.setText(resources.getString("gamepop.ping_all"));
		gtPingAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateAllPings();
			}
		});

		new MenuItem(gamePopup, SWT.SEPARATOR);
		
		gtCreateFolderItem	= new MenuItem(gamePopup, SWT.NONE);
		gtCreateFolderItem.setText(resources.getString("gamepop.add_folder"));
		gtCreateFolderItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addFolder();
			}
		});
	}

	private void createFolderPopup() {
		gtUpdateGame = new MenuItem(gamePopup, SWT.NONE);
		gtUpdateGame.setText(resources.getString("gamepop.update_all"));
		gtUpdateGame.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateGame();
			}
		});

		gtPingAllItem = new MenuItem(gamePopup, SWT.NONE);
		gtPingAllItem.setText(resources.getString("gamepop.ping_all"));
		gtPingAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateAllPings();
			}
		});

		new MenuItem(gamePopup, SWT.SEPARATOR);
		
		gtAddServer	= new MenuItem(gamePopup, SWT.NONE);
		gtAddServer.setText(resources.getString("gamepop.add_server"));
		gtAddServer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addServer();
			}
		});
		
		new MenuItem(gamePopup, SWT.SEPARATOR);
		
		gtDeleteFolderItem	= new MenuItem(gamePopup, SWT.NONE);
		gtDeleteFolderItem.setText(resources.getString("gamepop.delete_folder"));
		gtDeleteFolderItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				deleteThisFolder();
			}
		});
	}
	
	private void createModPopup() {
		gtUpdateGame = new MenuItem(gamePopup, SWT.NONE);
		gtUpdateGame.setText(resources.getString("gamepop.update_all"));
		gtUpdateGame.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateGame();
			}
		});

		gtPingAllItem = new MenuItem(gamePopup, SWT.NONE);
		gtPingAllItem.setText(resources.getString("gamepop.ping_all"));
		gtPingAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateAllPings();
			}
		});
	}
	
	private void createGameTypePopup() {
		gtUpdateGame = new MenuItem(gamePopup, SWT.NONE);
		gtUpdateGame.setText(resources.getString("gamepop.update_all"));
		gtUpdateGame.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateGame();
			}
		});

		gtPingAllItem = new MenuItem(gamePopup, SWT.NONE);
		gtPingAllItem.setText(resources.getString("gamepop.ping_all"));
		gtPingAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateAllPings();
			}
		});
	}
	
	
	private void createServerPopup() {
		// Create the server popup menu
		MenuItem[] menuItems = serverPopup.getItems();
		for (int i = 0; i < menuItems.length; i++) {
			menuItems[i].dispose();
		}
		stUpdateSelectedItem = new MenuItem(serverPopup, SWT.NONE);
		stUpdateSelectedItem.setText(resources.getString("serverpop.update_selected"));
		stUpdateSelectedItem.setEnabled(false);
		stUpdateSelectedItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Disable all pop-up menu selections
				updateSelected();
			}
		});

		stPingSelectedItem = new MenuItem(serverPopup, SWT.NONE);
		stPingSelectedItem.setText(resources.getString("serverpop.ping_selected"));
		stPingSelectedItem.setEnabled(false);
		stPingSelectedItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Disable all pop-up menu selections
				pingSelected();
			}
		});
		
		stJoinSelectedItem = new MenuItem(serverPopup, SWT.NONE);
		stJoinSelectedItem.setText(resources.getString("serverpop.join"));
		stJoinSelectedItem.setEnabled(false);
		stJoinSelectedItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Disable all pop-up menu selections
				joinServer(selectedServer);
			}
		});
		
		stWatchSelected = new MenuItem(serverPopup, SWT.NONE);
		stWatchSelected.setText(resources.getString("serverpop.watch"));
		stWatchSelected.setEnabled(false);
		stWatchSelected.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedServerList = buildSelectedServerList(serverTable);
				if (selectedServerList != null) {
					for (int i = 0; i < selectedServerList.getCount(); i++) {
						(new WatchServer(display, selectedServerList.get(i))).run();
					}
				}
			}
		});

		new MenuItem(serverPopup, SWT.SEPARATOR);

		if (selectedGameNode.nodeType.equals(GameNode.FOLDER_NODE)) {
			stRemoveFolderServers = new MenuItem(serverPopup, SWT.NONE);
			stRemoveFolderServers.setText(resources.getString("serverpop.remove_from_folder"));
			stRemoveFolderServers.setEnabled(false);
			stRemoveFolderServers.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (selectedGameNode.serverList != null) {
						removeServersFromFolderDialog(selectedGameNode.serverList);
					}
				}
			});
		} else {
			stAddServerToFolder = new MenuItem(serverPopup, SWT.NONE);
			stAddServerToFolder.setText(resources.getString("serverpop.add_to_folder"));
			stAddServerToFolder.setEnabled(false);
			stAddServerToFolder.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					selectedServerList = buildSelectedServerList(serverTable);
					if (selectedServerList != null) {
						addServersToFolderDialog(selectedServerList);
					}
				}
			});
		}

		new MenuItem(serverPopup, SWT.SEPARATOR);

		stCopyAddress = new MenuItem(serverPopup, SWT.NONE);
		stCopyAddress.setText(resources.getString("serverpop.copy_address"));
		stCopyAddress.setEnabled(false);
		stCopyAddress.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (selectedServer == null) {
					SysLogger.logMsg(0, "selectedServer set to null while copy address to clipboard is enabled");
				} else {
					String serverAddress = selectedServer.address.getHostAddress() + ":" + selectedServer.port;
					SysLogger.logMsg(4, "Copying " + serverAddress + " to clipboard");
					TextTransfer textTransfer = TextTransfer.getInstance();
					clip.setContents(new Object[]{serverAddress}, new Transfer[]{textTransfer});
				}
			}
		});
		
		stCopyServer = new MenuItem(serverPopup, SWT.NONE);
		stCopyServer.setText(resources.getString("serverpop.copy_all"));
		stCopyServer.setEnabled(false);
		stCopyServer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (selectedServer == null) {
					SysLogger.logMsg(0, "selectedServer set to null while copy address to clipboard is enabled");
				} else {
					Server s = selectedServer;
					String serverString = s.readableHostName + " | " + s.pingTime + " ms | " + s.players.getCount() + "/" + 
						s.maxClients + " | " + s.mapName + " | " + s.address.getHostAddress() + ":" + s.port;
					SysLogger.logMsg(4, "Copying " + serverString + " to clipboard");

					TextTransfer textTransfer = TextTransfer.getInstance();
					clip.setContents(new Object[]{serverString}, new Transfer[]{textTransfer});
				}
			}
		});

		serverTable.setMenu(serverPopup);
	}
	
	private void addFolder() {
		InputDialog fd = new InputDialog(shell);
		String		newFolder;
		fd.setText("Add Folder");
		fd.setMessage("New Folder");
		fd.setWidth(150);
		if ((newFolder = fd.open()) != null) {
			if (selectedGame.folders.findGameNode(newFolder) == null) {
				TreeItem t;
				Folder f = new Folder(selectedGame, newFolder, folderImageFile);
				selectedGame.folders.add(f);
				if ((t = findGameItem(selectedGame)) != null) {
					TreeItem m;
					gameTree.setRedraw(false);
					m = new TreeItem(t, SWT.NONE, 0);
					m.setText(f.nodeName);
					m.setImage(new Image(display, f.iconFileName));
					m.setData(NODE, f);
					gameTree.setRedraw(true);
					stAddServerToFolder.setEnabled(true);
					updateUserGames();
				}
			}
		}
	}

	private void deleteThisFolder() {
		if ((selectedGameNode != null) && selectedGameNode.nodeType.equals(GameNode.FOLDER_NODE)) {
			Folder f;

			gameTree.setRedraw(false);
			selectedGameItem.dispose();
			gameTree.setRedraw(true);
			if ((f = (Folder) selectedGame.folders.findGameNode(selectedGameNode.nodeID)) != null) {
				selectedGame.folders.remove(f);
			}
			updateUserGames();
		}
	}
	
	private void addServer() {
		AddServerDialog asd = new AddServerDialog(shell);
		Folder			f = (Folder) selectedGameNode;
		asd.setGame(selectedGame);
		Server server = asd.open();
		if (server != null) {
			f.serverList.add(server);
		}
		updateUserGames();
		previousGameItem = null;
		treeItemSelected();
	}

	private void addServersToFolderDialog(ServerList selectedServerList) {
		if (selectedGame.folders.getCount() > 0) {
			SelectionDialog sd = new SelectionDialog(shell);
			String			selFolder;

			sd.setText("Add servers to folder");
			sd.setMessage("Select Folder");
			for (int i = 0; i < selectedGame.folders.getCount(); i++) {
				sd.addSelection(selectedGame.folders.get(i).nodeID);
			}
			if ((selFolder = sd.open()) != null) {
				Folder f;
				if ((f = (Folder) selectedGame.folders.findGameNode(selFolder)) != null) {
					for (int i = 0; i < selectedServerList.getCount(); i++) {
						Server s = selectedServerList.get(i);
						if (f.serverList.findServerByAddress(s.address.getHostAddress(), s.port) == null) { 
							f.serverList.add(s);
						}
					}
				}
			}
			updateUserGames();
		}
	}

	private void removeServersFromFolderDialog(ServerList selectedServerList) {
		int[] selectedIndices = serverTable.getSelectionIndices(); 
		for (int i = 0; i < selectedIndices.length; i++) {
			Server s = (Server) serverTable.getItem(selectedIndices[i]).getData(SERVER_NODE);
			for (int j = 0; j < selectedServerList.getCount(); j++) {
				if (s == selectedServerList.get(j)) {
					selectedServerList.remove(j);
					break;
				}
			}
		}
		serverTable.remove(selectedIndices);
		updateUserGames();
	}

//	/**
//	 * Open a <code>SelectionDialog</code> to allow the user to delete a folder.
//	 */
//	
//	private void deleteFolder() {
//		if (selectedGame.folders.getCount() > 0) {
//			SelectionDialog sd = new SelectionDialog(shell);
//			String			selFolder;
//			sd.setText("Delete Folder");
//			sd.setMessage("Select Folder");
//			for (int i = 0; i < selectedGame.folders.getCount(); i++) {
//				sd.addSelection(selectedGame.folders.get(i).nodeID);
//			}
//			if ((selFolder = sd.open()) != null) {
//				TreeItem t;
//				if ((t = findGameItem(selectedGame)) != null) {
//					gameTree.setRedraw(false);
//					for (int i = 0; i < t.getItemCount(); i++) {
//						TreeItem m = t.getItem(i);
//						if (m.getText().equals(selFolder)) {
//							m.dispose();
//							break;
//						}
//					}
//					gameTree.setRedraw(true);
//				}
//				Folder f;
//				if ((f = (Folder) selectedGame.folders.findGameNode(selFolder)) != null) {
//					selectedGame.folders.remove(f);
//				}
//	        	try {
//	            	games.writeUserGames(userGamesFile, XMLversion, hostOS);
//				} catch (IOException e) {
//					e.printStackTrace();
//					errorWindow("I/O Error", "Could not write " + userGamesFile + ".  Exiting.");
//					System.exit(1);
//				}
//			}
//		}
//	}

	/**
	 * Build the sub-tree for the specified game and display it.  This will include all mods, folders, and 
	 * game types specified for in this game's configuration.
	 * 
	 * @param Game game - and initialized Game object 
	 */
	private void buildGameTree(Tree gameTree, Game game) {
		TreeItem	g;
		TreeItem	m;
		TreeItem	t;
		Folder		folder;
		Mod			mod;
		GameType	type;
		int			i;
		int			j;
		String		ext = (hostOS.startsWith("win") ? ".gif" : ".png");

		if (!game.isDisplayed) return;
		gameTree.setRedraw(false);

		g = new TreeItem(gameTree, SWT.NONE);
		g.setText(game.nodeName);
		try {
			g.setImage(new Image(shell.getDisplay(), gameIconDir + fileSep + game.iconFileName + ext));
		} catch (Exception e) {
			g.setImage(new Image(shell.getDisplay(), gameIconDir + fileSep + "jollyroger" + ext));
		}
		g.setData(NODE, game);
		
		for (i = 0; i < game.folders.getCount(); i++) {
			folder = (Folder) game.folders.get(i);
			m = new TreeItem(g, SWT.NONE);
			m.setText(folder.nodeName);
			m.setImage(new Image(display, folder.iconFileName));
			m.setData(NODE, folder);
		}

		for (i = 0; i < game.childList.getCount(); i++) {
			mod = (Mod) game.childList.get(i);
			if (mod.isDisplayed) {
				m = new TreeItem(g, SWT.NONE);
				m.setText(mod.nodeName);
				m.setImage(new Image(shell.getDisplay(), gameIconDir + fileSep + mod.iconFileName + ext));
				m.setData(NODE, mod);
				for (j = 0; j < mod.childList.getCount(); j++) {
					type = (GameType) mod.childList.get(j);
					t = new TreeItem(m, SWT.NONE);
					t.setText(type.nodeName);
					t.setImage(new Image(shell.getDisplay(), gameIconDir + fileSep + serverIcon));
					t.setData(NODE, type);
				}
			}
		}
		gameTree.setRedraw(true);
	}
	
	private class WatchServer {
		
		private Server		server;
		private Display	parent;
		private Shell		myShell;
		private GameWindow	ww;

		/**
		 * Constructor taking parent display and desired server
		 * 
		 * @param parent
		 * @param svr
		 */
		public WatchServer (Display parent, Server svr) {
			this.parent		= parent;
			server			= svr;
		}
		
		public void run() {

			myShell = new Shell(parent);
			myShell.setText(server.readableHostName + " (" + server.address.getHostAddress() + ":" + server.port + ")");
			myShell.setImage(new Image(myShell.getDisplay(), gameIconDir + File.separator + server.gameIconFile + imageExt));
			
			myShell.setLayout(new FormLayout());
			ww = new GameWindow(myShell, resources);
			ww.displayServer(server);
			myShell.pack();
			myShell.open();
			updateServer();


    		//server.getServerStatus();
		}
		
		private void updateServer() {
			(new Thread () {
				public void run() {
					while (!myShell.isDisposed()) {
						server.getQuickStatus();
						display.asyncExec(new Runnable() {
							public void run() {
								ww.displayServer(server);
							}
						});

						try {
							Thread.sleep(1000);
						} catch (Throwable th) {
							return;
						}
					}
				}
			}).start();
		}
	}

	private void buildServerTableHeading() {
		String[]		ColumnName = {	resources.getString("serverhdr.mod"), 
										resources.getString("serverhdr.pw"), 
										resources.getString("serverhdr.pb"), 
										resources.getString("serverhdr.country"), 
										resources.getString("serverhdr.name"), 
										resources.getString("serverhdr.ping"), 
										resources.getString("serverhdr.players"), 
										resources.getString("serverhdr.version"), 
										resources.getString("serverhdr.game"), 
										resources.getString("serverhdr.map"), 
										resources.getString("serverhdr.ip") };
		int[]			ColumnAlign = {SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
		int[]			ColumnWidth = { 75, 32, 28, 140, 300, 40, 50, 80, 80, 100, 100 };
		boolean[]		ColumnResize = { true, false, false, true, true, true, true, true, true, true, true };
		TableColumn[]	serverTableColumns;

		serverTable.setHeaderVisible(true);
		serverTable.setLinesVisible(true);
		serverTable.setRedraw(false);

		serverTableColumns = new TableColumn[ColumnName.length];
		for (int i = 0; i < serverTableColumns.length; i++) {
			serverTableColumns[i] = new TableColumn(serverTable, ColumnAlign[i]);
			serverTableColumns[i].setText(ColumnName[i]);
			serverTableColumns[i].setWidth(ColumnWidth[i]);
			serverTableColumns[i].setResizable(ColumnResize[i]);
			serverTableColumns[i].addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					TableColumn it = (TableColumn) e.widget;
					sortColumn = serverTable.indexOf(it);
					SysLogger.logMsg(4, "Sort on column " + sortColumn);
					if (sortColumn != lastSortColumn) {
						sortOrder = SORT_ASCENDING;
						lastSortColumn = sortColumn;
					} else {
						sortOrder = !sortOrder;
					}
					
					if (gameTree.getSelectionCount() > 0) {
						selectedServerList = buildSelectedServerList(serverTable);
						displayServerList();
						updateMenuItems(selectedNodeList);
						selectedServerList = null;
					}
				}
			});
		}
		serverTable.setRedraw(true);		
	}

	private void buildVarTableHeading() {
		String[]		ColumnName = {	resources.getString("cvar.attribute"), 
										resources.getString("cvar.value") };
		int[]			ColumnAlign = { SWT.LEFT, SWT.LEFT };
		int[]			ColumnWidth = { 100, 80 };
		boolean[]		ColumnResize = { true, true };
		TableColumn[]	varTableColumns;

		varTable.setHeaderVisible(true);
		varTable.setLinesVisible(false);
		varTable.setRedraw(false);

		varTableColumns = new TableColumn[ColumnName.length];
		for (int i = 0; i < varTableColumns.length; i++) {
			varTableColumns[i] = new TableColumn(varTable, ColumnAlign[i]);
			varTableColumns[i].setText(ColumnName[i]);
			varTableColumns[i].setWidth(ColumnWidth[i]);
			varTableColumns[i].setResizable(ColumnResize[i]);
		}
		varTable.setRedraw(true);		
	}

	/**
	 * Load the flag icon files found on the given path into a new ImageList.
	 * 
	 * @param String path - OS-specific directory containing flag icon gif files.
	 * @return new ImageList
	 */
	private ImageList loadFlagIcons(String path) {
		ImageList	list = new ImageList();
		File[] files;

	    FileFilter filter = new FileFilter() {
	        public boolean accept(File file) {
        		return file.getName().endsWith(".gif");
	        }
	    };
	    
	    files = (new File(path)).listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			String imageName	= files[i].getName();
			String fullPath;
			SysLogger.logMsg(7, "File: " + imageName);
			String country		= imageName.substring(0, imageName.indexOf('.'));
			fullPath = path + fileSep + imageName;
			SysLogger.logMsg(7, "Country: " + country + " File: " + fullPath);
			Image im = new Image(shell.getDisplay(), fullPath);
			list.put(country, im);
		}
		return list;
	}
	
	/**
	 * Load the game icons found on the given path into a new ImageList
	 * 
	 * @param String path - OS-specific directory containing PNG or TGA icon files for all games and mods.  This is used
	 * by the methods that display the server lists.  The file names (without extensions) must match the game/mod id specified
	 * for each mod.
	 * 
	 * @return new ImageList
	 */
	private ImageList loadGameIcons(String path) {
		ImageList	list = new ImageList();
		File[] files;

	    FileFilter filter = new FileFilter() {
	        public boolean accept(File file) {
	        	if (hostOS.startsWith("win")) {
		            return file.getName().endsWith(".gif") || file.getName().endsWith(".jpg");
	        	} else {
		            return file.getName().endsWith(".png") || file.getName().endsWith(".tga");
	        	}
	        }
	    };
	    
	    files = (new File(path)).listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			String imageName	= files[i].getName();
			String fullPath;
			String gameName		= imageName.substring(0, imageName.indexOf('.'));
			SysLogger.logMsg(6, "Game: " + gameName);
			fullPath = path + fileSep + imageName;
			SysLogger.logMsg(6, "Key: " + gameName + " File: " + fullPath);
			Image im = new Image(shell.getDisplay(), fullPath);
			list.put(gameName, im);
		}
		return list;
	}
	
	/**
	 * Locate a flag icon within the ImageList
	 * 
	 * @param country
	 * @return Image
	 */
	private Image findCountryIcon(String country) {
		if (flagIcons.containsKey(country)) {
			return flagIcons.get(country);
		} else {
			return jollyRoger;
		}
	}
	
	/**
	 * Set the status of all menu items based on items selected in the server table
	 * and the size of the passed ServerList. 
	 * 
	 * @param list
	 */
	
	private void updateGameTreePopupItems(ServerList list) {
		boolean		itemsSelected = (serverTable.getSelectionCount() > 0);
		boolean		singleton = (serverTable.getSelectionCount() == 1);

		if ((gtRefreshGame != null) && !gtRefreshGame.isDisposed()) {
			gtRefreshGame.setEnabled(true);
		}
		if ((gtUpdateGame != null) && !gtUpdateGame.isDisposed()) {
			if ((list != null) && (list.getCount() > 0)) {
				gtUpdateGame.setEnabled(true);
			} else {
				gtUpdateGame.setEnabled(false);
			}
		}
		if ((gtPingAllItem != null) && !gtPingAllItem.isDisposed()) {
			if ((list != null) && (list.getCount() > 0)) {
				gtPingAllItem.setEnabled(true);
			} else {
				gtPingAllItem.setEnabled(false);
			}
		}
		if ((stAddServerToFolder!= null) && !stAddServerToFolder.isDisposed()) {
			stAddServerToFolder.setEnabled(itemsSelected && (selectedGame.folders.getCount() > 0));
		}
		
		if ((stRemoveFolderServers!= null) && !stRemoveFolderServers.isDisposed()) {
			stRemoveFolderServers.setEnabled(itemsSelected);
		}
		
		if ((stPingSelectedItem != null) && (!stPingSelectedItem.isDisposed())) {
			stPingSelectedItem.setEnabled(itemsSelected);
			stUpdateSelectedItem.setEnabled(itemsSelected);
			stJoinSelectedItem.setEnabled((selectedServer != null) && selectedServer.game.isInstalled);
			stWatchSelected.setEnabled(itemsSelected);
			stCopyAddress.setEnabled(singleton);
			stCopyServer.setEnabled(singleton);
		}
	}

	private void updateMenuItems(ServerList list) {
		boolean		itemsSelected = (serverTable.getSelectionCount() > 0);
//		boolean		singleton = (serverTable.getSelectionCount() == 1);

		updateGameTreePopupItems(list);
		
		rebuildMaster.setEnabled(true);
		cancelAction.setEnabled(false);
		gameTree.setEnabled(true);
		serverTable.setEnabled(true);
		configureGames.setEnabled(true);
		configurePrefs.setEnabled(true);

		if ((list != null) && (list.getCount() > 0)) {
//			findPlayer.setEnabled(true);
			updateCurrent.setEnabled(true);
			pingCurrent.setEnabled(true);
//			serverFindLanItem.setEnabled(true);
//			serverAddTriggerItem.setEnabled(true);
//			serverEditTriggerItem.setEnabled(true);
		} else {
//			findPlayer.setEnabled(false);
			updateCurrent.setEnabled(false);
			pingCurrent.setEnabled(false);
//			serverFindLanItem.setEnabled(false);
//			serverAddTriggerItem.setEnabled(false);
//			serverEditTriggerItem.setEnabled(false);
		}
		
		pingSelected.setEnabled(itemsSelected);
		serverUpdateSelectedItem.setEnabled(itemsSelected);
		updateSelected.setEnabled(itemsSelected);
		serverPingSelectedItem.setEnabled(itemsSelected);
		connectSelected.setEnabled((selectedServer != null) && selectedServer.game.isInstalled);
		serverConnectItem.setEnabled((selectedServer != null) && selectedServer.game.isInstalled);
		watchServer.setEnabled(itemsSelected);
		serverWatchSelectedItem.setEnabled(itemsSelected);
//		serverRconItem.setEnabled(singleton);
	}

	/**
	 * Disable all menu items except for the cancelAction item in preparation for a long-running action.
	 * 
	 */
	private void disableMenuItems() {
		cancelAction.setEnabled(true);
		if ((gtRefreshGame != null) && (!gtRefreshGame.isDisposed())) {
			gtRefreshGame.setEnabled(false);
		}
		if ((gtUpdateGame != null) && (!gtUpdateGame.isDisposed())) {
			gtUpdateGame.setEnabled(false);
		}
		if ((gtPingAllItem != null) && (!gtPingAllItem.isDisposed())) {
			gtPingAllItem.setEnabled(false);			
		}

		gameTree.setEnabled(false);
		serverTable.setEnabled(false);

		stUpdateSelectedItem.setEnabled(false);
		stPingSelectedItem.setEnabled(false);
		stJoinSelectedItem.setEnabled(false);
		
		if ((stAddServerToFolder!= null) && !stAddServerToFolder.isDisposed()) {
			stAddServerToFolder.setEnabled(false);
		}
		if ((stRemoveFolderServers!= null) && !stRemoveFolderServers.isDisposed()) {
			stRemoveFolderServers.setEnabled(false);
		}
		stWatchSelected.setEnabled(false);
		stCopyAddress.setEnabled(false);
		stCopyServer.setEnabled(false);

		rebuildMaster.setEnabled(false);
		updateCurrent.setEnabled(false);
		pingCurrent.setEnabled(false);
		updateSelected.setEnabled(false);
		pingSelected.setEnabled(false);
		connectSelected.setEnabled(false);
		watchServer.setEnabled(false);
//		findPlayer.setEnabled(false);
		configureGames.setEnabled(false);
		configurePrefs.setEnabled(false);
				
		serverUpdateSelectedItem.setEnabled(false);
		serverPingSelectedItem.setEnabled(false);
		serverWatchSelectedItem.setEnabled(false);
		serverConnectItem.setEnabled(false);
//		serverRconItem.setEnabled(false);
//		serverFindLanItem.setEnabled(false);
//		serverAddTriggerItem.setEnabled(false);
//		serverEditTriggerItem.setEnabled(false);
	}
	
	/**
	 * Retrieve the ServerList from the selected game tree item.  Also sets 'selectedGameItem', 'selectedGameNode', and
	 * 'selectedGame'.
	 * 
	 * @param TreeItem ti
	 * @return reference to an existing ServerList
	 */
	private ServerList getSelectedGameServerList(TreeItem ti) {
		selectedGameItem = gameTree.getSelection()[0];
		selectedGameNode = (GameNode) selectedGameItem.getData(NODE);
		if (selectedGameNode.nodeType.equals(GameNode.GAME_NODE)) {
			selectedGame = (Game) selectedGameNode;
			gameInfo.setText(selectedGame.nodeName);
		} else if (selectedGameNode.nodeType.equals(GameNode.MOD_NODE)) {
			selectedGame = (Game) selectedGameNode.parent;
			gameInfo.setText(selectedGame.nodeName + " | " + selectedGameNode.nodeName);
		} else if (selectedGameNode.nodeType.equals(GameNode.TYPE_NODE)) {
			String modName = ((Mod) selectedGameNode.parent).nodeName;
			selectedGame = (Game) selectedGameNode.parent.parent;
			gameInfo.setText(selectedGame.nodeName + " | " + modName + " | "+ selectedGameNode.nodeName);
		} else if (selectedGameNode.nodeType.equals(GameNode.FOLDER_NODE)) {
			selectedGame = (Game) selectedGameNode.parent;
			gameInfo.setText(selectedGame.nodeName + " | " + selectedGameNode.nodeName);
		}
		return selectedGameNode.getServerList();
	}

	private TreeItem findGameItem(Game game) {
		for (int i = 0; i < gameTree.getItemCount(); i ++) {
			if (gameTree.getItem(i).getData(NODE) == game) {
				return gameTree.getItem(i);
			}
		}
		return null;
	}
	

	private int getSelectedItemPlayerCount(TreeItem ti) {
		return ((GameNode) gameTree.getSelection()[0].getData(NODE)).playerCount;
	}

	private void displayServerList() {
		selectedNodeList = getSelectedGameServerList(selectedGameItem);
		if (selectedNodeList == null) return;

		Server		s;				// server reference
		TableItem	item;			// TableItem to be added to the serverTable
		int			c;				// Column index
		int			insertIndex;	// Index for insertion for sortation
		pBar.setMaximum(selectedNodeList.getCount());
		pBar.setSelection(0);

		serverTable.setRedraw(false);
		serverTable.removeAll();
		
		for (loop = 0; loop < selectedNodeList.getCount(); loop++) {
			pBar.setSelection(loop);

			s = selectedNodeList.get(loop);
			c = 0;
			
			if (sortColumn == -1) {
				item = new TableItem(serverTable, SWT.NONE);
				if (isSelected(s)) serverTable.select(selectedNodeList.getCount() - 1);

			} else {
				insertIndex = findServerInsertIndex(s);
				item = new TableItem(serverTable, SWT.NONE, insertIndex);
				if (isSelected(s)) serverTable.select(insertIndex);
			}

			// "Mod", "Flag", "PW", "PB", "Country", "Name", "Ping", "Players", "Version", "Game", "Map", "IP Address"
			item.setText(c, s.gameMod);
			if (gameIcons.containsKey(s.gameIconFile)) {
				item.setImage(c++, gameIcons.get(s.gameIconFile));
			} else {
				item.setImage(c++, jollyRoger);
			}

			if (s.needPassword) {
				item.setImage(c++, lockedImage);
			} else {
				item.setImage(c++, blankImage);
			}
			
			if (s.pbEnabled) {
				item.setImage(c++, pbImage);
			} else {
				item.setImage(c++, blankImage);
			}

			if (s.country == null) {
				s.country = ipLookup.findServerCountry(s.address.getHostAddress());
			}
			item.setText(c, s.country);
			item.setImage(c++, findCountryIcon(s.country.toLowerCase()));
			item.setText(c++, s.readableHostName == null ? Server.UNKNOWN_HOST : s.readableHostName);
			item.setText(c++, Integer.toString(s.pingTime));
			item.setText(c++, Integer.toString(s.players.getCount()) + "/" + s.maxClients);
			if (s.gameEngine == null) {
				item.setText(c++, "???");
			} else {
				item.setText(c++, s.gameEngine + " " + s.engineVersion);
			}
			
			item.setText(c++, s.gameType == null ? " " : s.gameType);
			item.setText(c++, s.mapName == null ? Server.UNKNOWN_MAP : s.mapName);
			item.setText(c++, s.address.getHostAddress() + ":" + s.port);
			item.setData(SERVER_NODE, s);
		}
		serverTable.setRedraw(true);
		statusLabel.setText(Integer.toString(getSelectedItemPlayerCount(selectedGameItem)) + " " + 
				resources.getString("status.players_on") + " " + selectedNodeList.getCount() + " " + 
				resources.getString("status.servers"));
		statusLabel.redraw();
		pBar.setSelection(0);
	}	
	
	private boolean isSelected(Server s) {
		int		i;
		if ((selectedServerList == null) || (selectedServerList.getCount() == 0)) return false;
		if ((i = selectedServerList.findServerIndex(s)) != -1) {
			selectedServerList.remove(i);
			return true;
		} else {
			return false;
		}
	}
	
	private int findServerInsertIndex(Server s) {
		int			i;
		TableItem	it;
		String		tmp;
		
		for (i = 0; i < serverTable.getItemCount(); i++) {
			it = serverTable.getItem(i);
			
			if (!s.isValid) return serverTable.getItemCount();
			if (!((Server) it.getData(SERVER_NODE)).isValid) return i;

			switch (sortColumn) {
			case 0:		// Mod
				if (s.gameMod == null)
					tmp = Mod.UNKNOWN;
				else
					tmp = s.gameMod;
				if (insertBefore(tmp, it.getText(sortColumn), sortOrder)) return i;
				break;
				
			case 1:		// Password needed
				if (insertBefore(s.isPasswordNeeded(), ((Server) it.getData(SERVER_NODE)).isPasswordNeeded(), sortOrder)) return i;
				break;
				
			case 2:		// Punkbuster enabled
				if (insertBefore(s.pbEnabled, ((Server) it.getData(SERVER_NODE)).pbEnabled, sortOrder)) return i;
				break;
				
			case 3:		// Country
				if (insertBefore(s.country, ((Server) it.getData(SERVER_NODE)).country, sortOrder)) return i;
				break;
				
			case 4:		// Host name
				if (s.readableHostName == null) 
					tmp = Server.UNKNOWN_HOST; 
				else 
					tmp = s.readableHostName;
				if (insertBefore(tmp, it.getText(sortColumn), sortOrder)) return i;
				break;
				
			case 5:		// Ping
				if (insertBefore(s.pingTime, ((Server) it.getData(SERVER_NODE)).pingTime, sortOrder)) return i;
				break;
				
			case 6:		// Players
				if (insertBefore(s.players.getCount(), ((Server) it.getData(SERVER_NODE)).players.getCount(), sortOrder)) return i;
				break;
				
			case 7:		// Game engine/version
				if (s.gameEngine == null)
					tmp = Server.UNKNOWN_ENGINE;
				else
					tmp = s.gameEngine + " " + s.engineVersion;
				if (insertBefore(tmp, it.getText(sortColumn), sortOrder)) return i;
				break;
				
			case 8:		// Game type
				if (s.gameType == null)
					tmp = Server.UNKNOWN_GAMETYPE;
				else 
					tmp = s.gameType;
				if (insertBefore(tmp, it.getText(sortColumn), sortOrder)) return i;
				break;
				
			case 9:	// Map
				if (s.mapName == null)
					tmp = Server.UNKNOWN_MAP;
				else
					tmp = s.mapName;
				if (insertBefore(tmp, it.getText(sortColumn), sortOrder)) return i;
				break;
				
			case 10:	// IP Address
				if (insertBefore(s.address, s.port, 
								((Server) it.getData(SERVER_NODE)).address, ((Server) it.getData(SERVER_NODE)).port, 
								sortOrder)) {
					return i; 
				}
				break;
				
			default:
				SysLogger.logMsg(0, "Error in findInsertIndex: trying to sort by column " + sortColumn);
				sortColumn = -1;
				return 0;
			}
		}
		return i;
	}
	
	private boolean insertBefore(String newItem, String existingItem, boolean sortOrder) {
		if (sortOrder == SORT_ASCENDING) {
			return collator.compare(newItem, existingItem) < 0;
		} else {
			return collator.compare(newItem, existingItem) > 0;
		}
	}
	
	private boolean insertBefore(int newItem, int existingItem, boolean sortOrder) {
		if (sortOrder == SORT_ASCENDING) {
			return newItem < existingItem;
		} else {
			return newItem > existingItem;
		}
	}
	
	private boolean insertBefore(InetAddress newItem, int newPort, InetAddress existingItem, int existingPort, boolean sortOrder) {
		byte[]	addr			= newItem.getAddress();
		long	newAddr			= ((((long) addr[0]) & 0xFF) << 24) + ((((long) addr[1]) & 0xFF) << 16) + 
									((((long) addr[2]) & 0xFF) << 8) + (((long) addr[3]) & 0xFF);
		addr					= existingItem.getAddress();
		long	existingAddr	= ((((long) addr[0]) & 0xFF) << 24) + ((((long) addr[1]) & 0xFF) << 16) + 
									((((long) addr[2]) & 0xFF) << 8) + (((long) addr[3]) & 0xFF);
		if (newAddr == existingAddr) {
			if (sortOrder == SORT_ASCENDING) {
				return newPort < existingPort;
			} else {
				return newPort > existingPort;
			}
		} else {
			if (sortOrder == SORT_ASCENDING) {
				return newAddr < existingAddr;
			} else {
				return newAddr > existingAddr;
			}
		}
	}

	private boolean insertBefore(boolean newItem, boolean existingItem, boolean sortOrder) {
		if (sortOrder == SORT_ASCENDING) {
			return !newItem && existingItem;
		} else {
			return newItem && !existingItem;
		}
	}

	private Server getSelectedItemServer(TableItem ti) {
		TableItem	selectedServerItem = serverTable.getSelection()[0];
		
		selectedServer = (Server) selectedServerItem.getData(SERVER_NODE); // selectedServer is a class global - assuming it is only set here
		SysLogger.logMsg(7, "Server selection made (" + selectedServer.hostName + ") for " + selectedServerItem.getText());
		return selectedServer;
	}

	private void displayServer(Server s) {
		if (s == null) {
			gameWindow.clearWindow();

			varTable.setRedraw(false);
			varTable.removeAll();
			varTable.setRedraw(true);

			mapCanvas.setRedraw(false);
			if (!mapImage.isDisposed()) mapImage.dispose();
			mapImage = new Image(shell.getDisplay(), 200, 200); 
			mapCanvas.setRedraw(true);
			return;
		}

		gameWindow.displayServer(s);
		
		mapCanvas.setRedraw(false);
		if (mapImages.containsKey(s.mapName.toLowerCase())) {
			if (!mapImage.isDisposed()) mapImage.dispose();
			Image im = new Image(shell.getDisplay(), new ImageData(mapImages.get(s.mapName.toLowerCase())));
			int y = im.getBounds().height;
			int x = im.getBounds().width;
			int xdim;
			int ydim;
			float ar = (float) y / (float) x;
			if (ar > 1) {
				ydim = MAP_IMAGE_WIDTH;
				xdim = (int) (MAP_IMAGE_WIDTH / ar);
			} else {
				xdim = MAP_IMAGE_WIDTH;
				ydim = (int) (MAP_IMAGE_WIDTH * ar);
			}
			im.dispose();
			GridData gdata = new GridData();
			gdata.heightHint = ydim;
			gdata.widthHint = xdim;
			mapCanvas.setLayoutData(gdata);
			mapCanvas.getParent().layout(true);
			mapImage = new Image(shell.getDisplay(), 
					new ImageData(mapImages.get(s.mapName.toLowerCase())).scaledTo(xdim, ydim));
		} else {
			if (!mapImage.isDisposed()) mapImage.dispose();
			mapImage = new Image(shell.getDisplay(), MAP_IMAGE_WIDTH, MAP_IMAGE_WIDTH); 
		}
		mapCanvas.setRedraw(true);

		varTable.setRedraw(false);
		varTable.removeAll();
		if (s.attributes != null) {
			for (VarEntry cv = s.attributes.getFirst(); cv != null; cv = s.attributes.getNext(cv.getKey())) {
				SysLogger.logMsg(6, cv.getKey() + " = " + cv.getValue());
				int c = 0;
				TableItem item = new TableItem(varTable, SWT.NONE);

				item.setText(c++, cv.getKey());
				item.setText(c++, cv.getValue());
				item.setData(cv);
			}
		}
		varTable.setRedraw(true);
	}

	private void refreshGame() {
		disableMenuItems();
		(new Thread () {
			public void run() {
				rebuildGameServerList();
			}
		}).start();
	}

	private void rebuildGameServerList() {
		selectedGame.clearServerLists();
		selectedNodeList = selectedGame.getServerList();
		Thread masterServerQuery = new Thread() {
			public void run() {
				selectedGame.queryMasterServer(selectedNodeList);
				findServerLocations(selectedNodeList);
			}
		};
		
		masterServerQuery.start();
		Thread.yield();
		while (masterServerQuery.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (Throwable th) {}
			if (display.isDisposed()) return;
			display.asyncExec(new Runnable() {
				public void run() {
					if (statusLabel.isDisposed()) return;
					statusLabel.setText(resources.getString("status.query_master") + " " + selectedNodeList.getCount());
				}
			});
		}

		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setMinimum(0);		// Set up progress bar
				pBar.setSelection(0);
				pBar.setMaximum(selectedNodeList.getCount());
			}
		});
	
		Thread queryServers = new Thread() {
			public void run() {
				SysLogger.logMsg(7, "Start server query thread for " + selectedNodeList.getCount() + " servers.");
				selectedGame.queryServerList(selectedNodeList);
			}
		};
		queryServers.start();
		Thread.yield();
		while (queryServers.isAlive()) {
			SysLogger.logMsg(7, "queryServers is still alive.  Counter is " + selectedNodeList.counter);
			try {
				Thread.sleep(100);
			} catch (Throwable th) {}
			if (display.isDisposed()) return;
			display.asyncExec(new Runnable() {
				public void run() {
					if (pBar.isDisposed()) return;
					pBar.setSelection(selectedNodeList.counter);
					statusLabel.setText(resources.getString("status.query_server") + " " + 
							selectedNodeList.counter + " / " + selectedNodeList.getCount());
				}
			});
		}

		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setSelection(0);
				statusLabel.setText(resources.getString("status.building"));
				displayServerList();
				updateMenuItems(selectedNodeList);

				//statusLabel.setText(Integer.toString(getSelectedItemPlayerCount(selectedGameItem)) + " players on " + selectedNodeList.getCount() + " servers");
			}
		});
		if (selectedGame.cancelAction) selectedGame.cancelAction = false;
	}
	
	private void updateGame() {
		disableMenuItems();
		(new Thread () {
			public void run() {
				updateServerNode();
			}
		}).start();
	}
	
	private void updateServerNode() {
		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setMinimum(0);		// Set up progress bar
				pBar.setSelection(0);
				pBar.setMaximum(selectedNodeList.getCount());
			}
		});

		//
		Thread updateNode = new Thread() {
			public void run() {
				SysLogger.logMsg(7, "Start server query thread for " + selectedNodeList.getCount() + " servers.");
				selectedGame.updateServerNode(selectedGameNode);
			}
		};
		
		updateNode.start();
		Thread.yield();
		while (updateNode.isAlive()) {
			SysLogger.logMsg(7, "updateServers is still alive.  Counter is " + selectedNodeList.counter);
			try {
				Thread.sleep(100);
			} catch (Throwable th) {}
			if (display.isDisposed()) return;
			display.asyncExec(new Runnable() {
				public void run() {
					if (pBar.isDisposed()) return;
					pBar.setSelection(selectedNodeList.counter);
					statusLabel.setText(resources.getString("status.query_server") + " " + selectedNodeList.counter + 
							" / " + selectedNodeList.getCount());
				}
			});
		}

		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setSelection(0);
				statusLabel.setText(resources.getString("status.building"));
				displayServerList();
				updateMenuItems(selectedNodeList);
				//statusLabel.setText(Integer.toString(getSelectedItemPlayerCount(selectedGameItem)) + " players on " + selectedNodeList.getCount() + " servers");
			}
		});
		if (selectedGame.cancelAction) selectedGame.cancelAction = false;
	}

	private void updateAllPings() {
		disableMenuItems();
		(new Thread () {
			public void run() {
				pingServerNode();
			}
		}).start();
	}

	private void pingServerNode() {
		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setMinimum(0);		// Set up progress bar
				pBar.setSelection(0);
				pBar.setMaximum(selectedNodeList.getCount());
			}
		});

		//
		Thread repingServers = new Thread() {
			public void run() {
				SysLogger.logMsg(7, "Start server query thread for " + selectedNodeList.getCount() + " servers.");
				selectedGame.pingServerNode(selectedGameNode);
			}
		};
		
		repingServers.start();
		Thread.yield();
		while (repingServers.isAlive()) {
			SysLogger.logMsg(7, "pingServerList is still alive.  Counter is " + selectedNodeList.counter);
			try {
				Thread.sleep(100);
			} catch (Throwable th) {}
			if (display.isDisposed()) return;
			display.asyncExec(new Runnable() {
				public void run() {
					if (pBar.isDisposed()) return;
					pBar.setSelection(selectedNodeList.counter);
					statusLabel.setText(resources.getString("status.query_server") + " " + selectedNodeList.counter + 
							" / " + selectedNodeList.getCount());
				}
			});
		}

		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setSelection(0);
				statusLabel.setText(resources.getString("status.building"));
				updateNodePings();
				updateMenuItems(selectedNodeList);
			}
		});
		if (selectedGame.cancelAction) selectedGame.cancelAction = false;
	}

	private void updateNodePings() {
		selectedNodeList = getSelectedGameServerList(selectedGameItem);
		if (selectedNodeList == null) return;

		Server s;
		serverTable.setRedraw(false);
		for (int i = 0; i < selectedNodeList.getCount(); i++) {
			s = selectedNodeList.get(i);
			TableItem	item	= serverTable.getItem(i);
			item.setText(6, Integer.toString(s.pingTime));
			item.setData(SERVER_NODE, s);
		}
		serverTable.setRedraw(true);
	}
	
	private void updateSelected() {
		disableMenuItems();
		selectedServerList = buildSelectedServerList(serverTable);
		(new Thread () {
			public void run() {
				updateSelectedServers();
			}
		}).start();
	}
	
	private void updateSelectedServers() {
		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setMinimum(0);		// Set up progress bar
				pBar.setSelection(0);
				pBar.setMaximum(selectedServerList.getCount());
			}
		});

		//
		Thread updateNode = new Thread() {
			public void run() {
				SysLogger.logMsg(7, "Start server query thread for " + selectedServerList.getCount() + " servers.");
				selectedGame.updateServerList(selectedServerList);
			}
		};
		
		updateNode.start();
		Thread.yield();
		while (updateNode.isAlive()) {
			SysLogger.logMsg(7, "updateServers is still alive.  Counter is " + selectedServerList.counter);
			try {
				Thread.sleep(100);
			} catch (Throwable th) {}
			if (display.isDisposed()) return;
			display.asyncExec(new Runnable() {
				public void run() {
					if (pBar.isDisposed()) return;
					pBar.setSelection(selectedServerList.counter);
					statusLabel.setText(resources.getString("status.query_server") + " " + selectedServerList.counter + 
							" / " + selectedServerList.getCount());
				}
			});
		}

		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setSelection(0);
				displayUpdatedServerList(selectedServerList);
				displayServer(selectedServer);
				updateMenuItems(selectedServerList);
				selectedServerList = null;
			}
		});
		if (selectedGame.cancelAction) selectedGame.cancelAction = false;
	}

	private void pingSelected() {
		disableMenuItems();
		selectedServerList = buildSelectedServerList(serverTable);
		(new Thread () {
			public void run() {
				pingSelectedServers();
			}
		}).start();
	}
	
	private void pingSelectedServers() {
		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setMinimum(0);		// Set up progress bar
				pBar.setSelection(0);
				pBar.setMaximum(selectedServerList.getCount());
			}
		});

		//
		Thread updateNode = new Thread() {
			public void run() {
				SysLogger.logMsg(7, "Start server ping thread for " + selectedServerList.getCount() + " servers.");
				selectedGame.pingServerList(selectedServerList);
			}
		};
		
		updateNode.start();
		Thread.yield();
		while (updateNode.isAlive()) {
			SysLogger.logMsg(7, "pingServers is still alive.  Counter is " + selectedServerList.counter);
			try {
				Thread.sleep(100);
			} catch (Throwable th) {}
			if (display.isDisposed()) return;
			display.asyncExec(new Runnable() {
				public void run() {
					if (pBar.isDisposed()) return;
					pBar.setSelection(selectedServerList.counter);
					statusLabel.setText(resources.getString("status.ping_server") + " " + selectedServerList.counter + 
							" / " + selectedServerList.getCount());
				}
			});
		}

		display.asyncExec(new Runnable() {
			public void run() {
				pBar.setSelection(0);
				updateListPings(selectedServerList);
				displayServer(selectedServer);
				updateMenuItems(selectedServerList);
				selectedServerList = null;
			}
		});
		if (selectedGame.cancelAction) selectedGame.cancelAction = false;
	}
	
	private void updateListPings(ServerList list) {
		serverTable.setRedraw(false);
		for (int i = 0; i < list.getCount(); i++) {
			TableItem	item	= serverTable.getSelection()[i];
			Server		s		= list.get(i);

			item.setText(5, Integer.toString(s.pingTime));
			//item.setData(SERVER_NODE, s);
		}
		serverTable.setRedraw(true);
	}
	
	private void displayUpdatedServerList(ServerList list) {
		
		serverTable.setRedraw(false);
		for (int i = 0; i < list.getCount(); i++) {
			TableItem	item	= serverTable.getSelection()[i];
			Server		s		= list.get(i);
			int			c		= 0;

			item.setText(c, s.gameMod);
			if (gameIcons.containsKey(s.gameIconFile)) {
				item.setImage(c++, gameIcons.get(s.gameIconFile));
			} else {
				item.setImage(c++, jollyRoger);
			}

			if (s.needPassword) {
				item.setImage(c++, lockedImage);
			} else {
				item.setImage(c++, blankImage);
			}
			
			if (s.pbEnabled) {
				item.setImage(c++, pbImage);
			} else {
				item.setImage(c++, blankImage);
			}

			item.setText(c, s.country);
			item.setImage(c++, findCountryIcon(s.country.toLowerCase()));
			item.setText(c++, s.readableHostName == null ? Server.UNKNOWN_HOST : s.readableHostName);
			item.setText(c++, Integer.toString(s.pingTime));
			item.setText(c++, Integer.toString(s.players.getCount()) + "/" + s.maxClients);
			if (s.gameEngine == null) {
				item.setText(c++, Server.UNKNOWN_ENGINE);
			} else {
				item.setText(c++, s.gameEngine + " " + s.engineVersion);
			}
			item.setText(c++, s.gameType == null ? " " : s.gameType);
			item.setText(c++, s.mapName == null ? Server.UNKNOWN_MAP : s.mapName);
			item.setText(c++, s.address.getHostAddress() + ":" + s.port);
			//item.setData(SERVER_NODE, s);

		}
		serverTable.setRedraw(true);
	}

	/*
	private class JoinServerThread implements Runnable {
		private String		connectString;
		private String		workingDir;
		
		public JoinServerThread(String connStr, String wDir) {
			connectString	= connStr;
			workingDir		= wDir;
		}

		public void run() {
			try {
				SysLogger.logMsg(4, "Exec String: \"" + connectString + "\" Working dir: " + workingDir);
				Process proc = Runtime.getRuntime().exec(connectString, null, new File(workingDir));
				
				if (joinAction == JoinAction.Exit) {
					shell.close();
				}

				InputStream stderr = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				//String line = null;
				while ((br.readLine()) != null);
					//System.out.println(line);
				//int exitVal = proc.waitFor();
				proc.waitFor();
				//System.out.println("Process exitValue: " + exitVal);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	*/

	public void runCommandLine(String connectString, String workingDirectory) throws IOException {
		String[] cmd = connectString.split("\\s+");
	    ProcessBuilder processBuilder = new ProcessBuilder(cmd);
	    processBuilder.directory(new File(workingDirectory));
	    Process process = processBuilder.start();
	    Thread commandLineThread = new Thread(() -> {
	    	try {
				InputStream stderr = process.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				//String line = null;
				while ((br.readLine()) != null);
					//System.out.println(line);
				//int exitVal = proc.waitFor();
				process.waitFor();
            } catch (IOException ex) {
                ex.printStackTrace();
	        } catch (InterruptedException ex) {
	            ex.printStackTrace();
	        }
			/*
	    	try {
                BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while((line=br.readLine())!=null){
                    System.out.println(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            */
        });
        commandLineThread.setDaemon(true);
        commandLineThread.start();
	}

	
	public void joinServer(Server s) {
		String				engineStart;
		String				startParms;
		String				workingDir;
		String				connectString;
		VarEntry			entry;

		if (!s.game.isInstalled) {
			errorWindow("Launch Error", s.game.nodeName + " is not installed.");
			return;
		}

		if (s.mod == null) {
			selectedServerList = buildSelectedServerList(serverTable);
			selectedGame.updateServerList(selectedServerList);
		}

		if (s.game.multiEngine) {
			entry = s.game.startCommand.get(s.gameEngine.toLowerCase());
			if (entry != null) {
				engineStart = entry.getValue();
			} else {
				errorWindow("Configuration Error", "Multi-engine start command is not defined for " + s.gameEngine.toLowerCase());
				SysLogger.logMsg(0, "Can't start client. No multi-engine start command defined for " + s.gameEngine.toLowerCase());
				return;
			}

			entry = s.game.startParms.get(s.gameEngine.toLowerCase());
			if (entry != null) {
				startParms = entry.getValue();
			} else {
				startParms = "";
			}

			entry = s.game.workingDir.get(s.gameEngine.toLowerCase());
			if (entry != null) {
				workingDir = entry.getValue();
			} else {
				errorWindow("Configuration Error", "Multi-engine start command is not defined for " + s.gameEngine.toLowerCase());
				SysLogger.logMsg(0, "Can't start client. No multi-engine working directory defined for " + s.gameEngine.toLowerCase());
				return;
			}
		} else {
			entry = s.game.startCommand.get("default");
			if (entry != null) {
				engineStart = entry.getValue();
			} else {
				errorWindow("Configuration Error", "Multi-engine start command is not defined for " + s.gameEngine.toLowerCase());
				SysLogger.logMsg(0, "Can't start client. No default start command defined for " + s.game.nodeName);
				return;
			}
			
			entry = s.game.startParms.get("default");
			if (entry != null) {
				startParms = entry.getValue();
			} else {
				startParms = "";
			}
			
			entry = s.game.workingDir.get("default");
			if (entry != null) {
				workingDir = entry.getValue();
			} else {
				errorWindow("Configuration Error", "Default working directory is not defined for " + s.game.nodeName);
				SysLogger.logMsg(0, "Can't start client. No default working directory defined for " + s.game.nodeName);
				return;
			}
		}
		
		connectString = engineStart + " " + startParms;
		if (s.mod.startParms != null) {
			connectString += " " + s.mod.startParms;
		}

		if (s.mod != null) {
			if (s.mod.workingDir != null) {
				workingDir = s.mod.workingDir;
			}

			if ((s.mod.startCommand != null) && (s.mod.startParms != null)) {
				connectString = s.mod.startCommand + " " + s. mod.startParms+ " " + startParms; 
			}
//		} else {
//			connectString = engineStart + " " + startParms.replace(tokIpAddr, s.address.getHostAddress()).replace(tokPort, Integer.toString(s.port));
		}
//		connectString.replace(tokModId, s.mod.nodeID);

		connectString = connectString.replace(tokIpAddr, s.address.getHostAddress()).replace(tokPort, Integer.toString(s.port)).replace(tokModId, s.mod.nodeID);
		
		if (joinAction == JoinAction.Minimize) {
			shell.setMinimized(true);
		}
		SysLogger.logMsg(4, "Starting " + s.game.nodeName + " and connecting to " +	s.address.getHostAddress() + ":" + s.port);
		try {
			runCommandLine(connectString, workingDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//gameThread = new JoinServerThread(connectString, workingDir);
		//gameThread.run();
	}

	private ServerList buildSelectedServerList(Table table) {
		ServerList list = new ServerList();
		for (int i = 0; i < table.getSelectionCount(); i++) {
			list.add((Server) table.getSelection()[i].getData(SERVER_NODE));
		}
		return list;
	}

	private void disposeImages() {
		if (rebuildImage != null) rebuildImage.dispose();
		if (updateCurrentImage != null) updateCurrentImage.dispose();
		if (pingCurrentImage != null) pingCurrentImage.dispose();
		if (updateSelectedImage != null) updateSelectedImage.dispose();
		if (pingImage != null) pingImage.dispose();
		if (cancelImage != null) cancelImage.dispose();
		if (connectImage != null) connectImage.dispose();
		if (findImage != null) findImage.dispose();
		if (optionsImage != null) optionsImage.dispose();
		if (gamesImage != null) gamesImage.dispose();
	}
	
	private void deleteLogFiles() {
		
	    File dir = new File(logDir);
	    if (!dir.exists()) dir.mkdirs();
		
		File	file = new File(logFileName);

		if (file.exists()) {
			file.delete();
		}
		
		dir = new File(logDir + fileSep);
		File[]	files;
		
	    FileFilter filter = new FileFilter() {
	        public boolean accept(File file) {
	            return file.getName().startsWith("master_server_");
	        }
	    };

	    files = dir.listFiles(filter);
	    for (int i = 0; i < files.length; i++) {
	    	// System.out.println(files[i].getName());
	    	files[i].delete();
	    }
	}
	
	private void findServerLocations(ServerList serverList) {
		Server server;
		for (int i = 0; i < serverList.getCount(); i++) {
			server = serverList.get(i);
			server.country = ipLookup.findServerCountry(server.address.getHostAddress());
			SysLogger.logMsg(7, "Server: " + server.address.getHostAddress() + ":" + server.port + "\t" + server.country);
		}
	}
	
	public static void redirect(String log) {
		try {
			File logFile = new File(log);
			System.setErr(new PrintStream(new FileOutputStream(logFile)));
			System.setOut(new PrintStream(new FileOutputStream(logFile)));
		}
		catch (Throwable t) {
			SysLogger.logMsg(4, "Error overriding standard output to file.");
			t.printStackTrace(System.err);
		}
	}
	
	public void updateUserGames() {
    	try {
        	games.writeUserGames(userGamesFile, XMLversion, hostOS);
		} catch (IOException e) {
			e.printStackTrace();
			errorWindow("I/O Error", "Could not write " + userGamesFile + ".  Exiting.");
			System.exit(1);
		}
	}
}
