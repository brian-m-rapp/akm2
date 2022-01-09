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

import org.dilireum.logging.SysLogger;
import org.dilireum.serverbrowser.AllKnowingMind.JoinAction;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Dialog class to input a single string.
 */
public class PreferencesDialog extends Dialog {
	private boolean	valueChanged = false;
	/**
	 * InputDialog constructor
	 *
	 * @param parent the parent
	 */
	public PreferencesDialog(Shell parent) {
		// Pass the default styles here
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * Opens the dialog and returns the input
	 *
	 * @return String
	 */
	public boolean open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText("User Preferences");
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
		return valueChanged;
	}

	/**
	 * Creates the dialog's contents
	 *
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = 20;
		//gl.verticalSpacing = 15;
		gl.marginHeight = 10;
		gl.marginWidth = 10;
		shell.setLayout(gl);

		// Show the message
		Label langLabel = new Label(shell, SWT.NONE);
		langLabel.setText("Language");
		GridData data = new GridData();
		langLabel.setLayoutData(data);

		// Display the input box
		final Combo		langs = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 120;
		langs.setLayoutData(data);

		int		currentIndex = 0;
		for (int i = 0; i < AllKnowingMind.languageList.size(); i++) {
			LanguageEntry le = AllKnowingMind.languageList.get(i);
			langs.add(le.getName());
			if (AllKnowingMind.language.equals(le.getIsoKey())) {
				currentIndex = i;
			}
		}
		langs.select(currentIndex);
		
		final Button	splash = new Button(shell, SWT.CHECK);
		splash.setText("Show splash screen");
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		splash.setLayoutData(data);
		splash.setSelection(AllKnowingMind.enableSplash);

		final Button	getUpdates = new Button(shell, SWT.CHECK);
		getUpdates.setText("Check for new version on start up");
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		getUpdates.setLayoutData(data);
		getUpdates.setSelection(AllKnowingMind.areUpdatesChecked);
		
		Label joinLabel = new Label(shell, SWT.NONE);
		joinLabel.setText("Game join action:");
		data = new GridData();
		data.horizontalSpan = 2;
		joinLabel.setLayoutData(data);
		
		final Composite	radioGroup = new Composite(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		radioGroup.setLayoutData(data);
		radioGroup.setLayout(new GridLayout(1, true));

		final Button	minButton	= new Button(radioGroup, SWT.RADIO);
		final Button	exitButton	= new Button(radioGroup, SWT.RADIO);
		final Button	noneButton	= new Button(radioGroup, SWT.RADIO);
		
		minButton.setLayoutData(new GridData());
		minButton.setText("Minimize");
		exitButton.setLayoutData(new GridData());
		exitButton.setText("Exit");
		noneButton.setLayoutData(new GridData());
		noneButton.setText("Do nothing");

		if (AllKnowingMind.joinAction == JoinAction.Minimize) {
			minButton.setSelection(true);
		} else if (AllKnowingMind.joinAction == JoinAction.Exit) {
			exitButton.setSelection(true);
		} else {
			noneButton.setSelection(true);
		}
		
		Label concLabel = new Label(shell, SWT.NONE);
		concLabel.setText("Maximum Concurrent Queries");
		data = new GridData();
		concLabel.setLayoutData(data);

		final Text		concLevel = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 30;
		concLevel.setLayoutData(data);
		concLevel.setText(Integer.toString(AllKnowingMind.maxConcurrency));
		
		Label retryLabel = new Label(shell, SWT.NONE);
		retryLabel.setText("Maximum Query Retries");
		data = new GridData();
		retryLabel.setLayoutData(data);

		final Text		retryLevel = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 20;
		retryLevel.setLayoutData(data);
		retryLevel.setText(Integer.toString(AllKnowingMind.maxRetries));
		
		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("Ok");
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int		index = langs.getSelectionIndex();
				
				AllKnowingMind.language = AllKnowingMind.languageList.get(index).getIsoKey();
				try {
					AllKnowingMind.maxConcurrency = Integer.parseInt(concLevel.getText().trim());
					if (AllKnowingMind.maxConcurrency > AllKnowingMind.MAX_CONCURRENCY) {
						AllKnowingMind.maxConcurrency = AllKnowingMind.MAX_CONCURRENCY;
					}
					
					if (AllKnowingMind.maxConcurrency < 1) {
						AllKnowingMind.maxConcurrency = AllKnowingMind.DEFAULT_MAX_CONCURRENCY;
					}
				} catch (NumberFormatException e) {
					SysLogger.logMsg(0, "Invalid concurrency level " + concLevel.getText());
					AllKnowingMind.maxConcurrency = AllKnowingMind.DEFAULT_MAX_CONCURRENCY;
				}

				try {
					AllKnowingMind.maxRetries = Integer.parseInt(retryLevel.getText().trim());
					if (AllKnowingMind.maxRetries > AllKnowingMind.MAX_RETRIES) {
						AllKnowingMind.maxRetries = AllKnowingMind.MAX_RETRIES;
					}
					
					if (AllKnowingMind.maxRetries < 1) {
						AllKnowingMind.maxRetries = AllKnowingMind.DEFAULT_MAX_RETRIES;
					}
				} catch (NumberFormatException e) {
					SysLogger.logMsg(0, "Invalid retry count " + retryLevel.getText());
					AllKnowingMind.maxRetries = AllKnowingMind.DEFAULT_MAX_RETRIES;
				}
				
				AllKnowingMind.enableSplash = splash.getSelection();
				AllKnowingMind.areUpdatesChecked = getUpdates.getSelection();
				
				if (minButton.getSelection()) {
					AllKnowingMind.joinAction = JoinAction.Minimize;
				} else if (exitButton.getSelection()) {
					AllKnowingMind.joinAction = JoinAction.Exit;
				} else {
					AllKnowingMind.joinAction = JoinAction.None;
				}
				
				valueChanged = true;
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
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}
}
