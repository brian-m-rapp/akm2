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

import java.util.ArrayList;

import org.dilireum.logging.SysLogger;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Dialog class to input a single string.
 */
@SuppressWarnings("unused")
public class NewMasterServerDialog extends Dialog {
	private MasterServer	newMaster;
	private Combo			masterType;
	private Button			enabled;
	private Text			masterUrl;
	private Label			portLabel;
	private Text			port;
	private Label			queryLabel;
	private Text			queryName;
	private Label			protLabel;
	private Text			protocols;
	private Button			okButton;
	private Button			cancelButton;
	private Game			game;

	/**
	 * InputDialog constructor
	 *
	 * @param parent the parent
	 */
	public NewMasterServerDialog(Shell parent, Game game) {
		// Let users override the default styles
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("New Master Server");
		this.game = game;
	}

	/**
	 * Opens the dialog and returns the input
	 *
	 * @return String
	 */
	public MasterServer open() {
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
		return newMaster;
	}

	/**
	 * Creates the dialog's contents
	 *
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		GridData	data;
		GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = 10;
		gl.verticalSpacing = 15;
		gl.marginHeight = 20;
		gl.marginWidth = 20;
		shell.setLayout(gl);
		
		Label	tl = new Label(shell, SWT.NONE);
		tl.setText("Type");
		tl.setLayoutData(new GridData());
		
		masterType = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData();
		data.widthHint = 80;
		masterType.setLayoutData(data);
		masterType.add("Quake 3");
		masterType.add("HTTP");
		masterType.select(0);
		masterType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String mType = masterType.getText();
				if (mType.equals("HTTP")) {
					portLabel.setVisible(false);
					port.setVisible(false);
					queryLabel.setVisible(false);
					queryName.setVisible(false);
					protLabel.setVisible(false);
					protocols.setVisible(false);
				} else if (mType.equals("Quake 3")){
					portLabel.setVisible(true);
					port.setVisible(true);
					queryLabel.setVisible(true);
					queryName.setVisible(true);
					protLabel.setVisible(true);
					protocols.setVisible(true);
				} else {
					throw new IllegalArgumentException("Unknown master server type " + mType);
				}
			}
		});
		
		enabled = new Button(shell, SWT.CHECK);
		enabled.setText("Master Server Enabled");
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		enabled.setLayoutData(data);
		enabled.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			}
		});

		Label	ul = new Label(shell, SWT.NONE);
		ul.setText("Address");
		ul.setLayoutData(new GridData());
		
		masterUrl = new Text(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 300;
		masterUrl.setLayoutData(data);

		portLabel = new Label(shell, SWT.NONE);
		portLabel.setText("Port");
		portLabel.setLayoutData(new GridData());
		portLabel.setVisible(true);
		
		port = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 40;
		port.setLayoutData(data);
		port.setVisible(true);

		queryLabel = new Label(shell, SWT.NONE);
		queryLabel.setText("Query Text");
		queryLabel.setLayoutData(new GridData());
		queryLabel.setVisible(true);
		
		queryName = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 150;
		queryName.setLayoutData(data);
		queryName.setVisible(true);

		protLabel = new Label(shell, SWT.NONE);
		protLabel.setText("Protocols");
		protLabel.setLayoutData(new GridData());
		protLabel.setVisible(true);
		
		protocols = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 150;
		protocols.setLayoutData(data);
		protocols.setVisible(true);
		
		okButton = new Button(shell, SWT.PUSH);
		okButton.setText("&Ok");
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		okButton.setLayoutData(data);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (masterType.getText().equals("HTTP")) {
					newMaster = new ETQWMasterServer(game, masterUrl.getText(), enabled.getSelection());
				} else if (masterType.getText().equals("Quake 3")) {
					ArrayList<Integer>	pList = makeProtocolArray(protocols.getText());
					if (queryName.getText().length() == 0) {
						newMaster = new Q3MasterServer(game, masterUrl.getText(), Integer.parseInt(port.getText()), 
								pList, enabled.getSelection());
					} else {
						newMaster = new Q3MasterServer(game, masterUrl.getText(), Integer.parseInt(port.getText()), 
								pList, queryName.getText(), enabled.getSelection());
					}
				} else {
					throw new IllegalArgumentException("Unknown Master Type " + masterType.getText());
				}
				shell.close();
			}
		});
		shell.setDefaultButton(okButton);

		cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("&Cancel");
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
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
}
