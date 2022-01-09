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

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Dialog class to input a single string.
 */
public class NewEngineDialog extends Dialog {
	private Label		engineLabel;
	private Text		engineId;
	private Label		workingLabel;
	private Text	 	workingDir;
	private Label		commandLabel;
	private Text 		startCommand;
	private Label		parmsLabel;
	private Text 		startParms;
	private String[]	engines;
	private String[]	engineParms;
	private String		hostOs;

	/**
	 * InputDialog constructor
	 *
	 * @param parent the parent
	 */
	public NewEngineDialog(Shell parent, String[] engines, String hostOs) {
		// Let users override the default styles
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("New Game Engine");
		this.engines = engines;
		this.hostOs = hostOs;
	}

	/**
	 * Opens the dialog and returns the input
	 *
	 * @return String
	 */
	public String[] open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return engineParms;
	}

	/**
	 * Creates the dialog's contents
	 *
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		GridData	data;
		GridLayout gl = new GridLayout(3, false);
		gl.horizontalSpacing = 10;
		gl.verticalSpacing = 15;
		gl.marginHeight = 20;
		gl.marginWidth = 20;
		shell.setLayout(gl);
		
		engineLabel = new Label(shell, SWT.NONE);
		engineLabel.setText("Engine ID");
		engineLabel.setLayoutData(new GridData());

		engineId = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 80;
		data.horizontalSpan = 2;
		engineId.setLayoutData(data);

		workingLabel = new Label(shell, SWT.NONE);
		workingLabel.setText("Working Directory");
		workingLabel.setLayoutData(new GridData());
		
		workingDir = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 200;
		workingDir.setLayoutData(data);

		Button	wDirButton = new Button(shell, SWT.PUSH);
		wDirButton.setText("Browse...");
		data = new GridData();
		wDirButton.setLayoutData(data);
		wDirButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(shell);

				dlg.setFilterPath(workingDir.getText());
				dlg.setText("Working Directory");

				dlg.setMessage("Select a directory");
				String dir = dlg.open();
				if (dir != null) {
					workingDir.setText(dir);
				}
			}
		});
		
		commandLabel = new Label(shell, SWT.NONE);
		commandLabel.setText("Start Command");
		commandLabel.setLayoutData(new GridData());
		
		startCommand = new Text(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 200;
		startCommand.setLayoutData(data);

		Button sCmdButton = new Button(shell, SWT.PUSH);
		sCmdButton.setText("Browse...");
		data = new GridData();
		sCmdButton.setLayoutData(data);
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
				dlg.setFilterPath(workingDir.getText());
				String fn = dlg.open();
				if (fn != null) {
					startCommand.setText(fn);
				}
			}
		});

		parmsLabel = new Label(shell, SWT.NONE);
		parmsLabel.setText("Start Parameters");
		parmsLabel.setLayoutData(new GridData());

		startParms = new Text(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		startParms.setLayoutData(data);
		
		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("Ok");
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String defaultEngineName = "";
				engineId.setText(engineId.getText().toLowerCase());
				
				// Make sure this engine name doesn't already exist
				for (int i = 0; i < engines.length; i++) {
					if (engineId.getText().equalsIgnoreCase(engines[i])) {
						MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
						mb.setText("Error");
						mb.setMessage("Specified engine ID already exists.");
						mb.open();
						return;
					}
				}
				
				if (engines.length == 1) {
					// Prompt for new default engine name with InputDialog
					InputDialog fd = new InputDialog(shell);
					fd.setText("Set Default Engine Name");
					fd.setMessage("Default Engine Name");
					fd.setWidth(150);
					if ((defaultEngineName = fd.open()) != null) {
						defaultEngineName = defaultEngineName.toLowerCase();
						if (defaultEngineName.equalsIgnoreCase(engineId.getText())) {
							MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
							mb.setText("Error");
							mb.setMessage("Default engine ID cannot be the same as the new engine ID.");
							mb.open();
							return;
						}
					} else {
						return;
					}
					// if return value is null, then simply return from this method.
				}
				if (engines.length == 1) {
					engineParms = new String[5];
					engineParms[4] = defaultEngineName;
				} else {
					engineParms = new String[4];
				}
				engineParms[0] = engineId.getText();
				engineParms[1] = workingDir.getText(); 
				engineParms[2] = startCommand.getText(); 
				engineParms[3] = startParms.getText(); 
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				engineParms = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}
}
