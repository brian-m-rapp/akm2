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
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Dialog class to input a single string.
 */
public class AddServerDialog extends Dialog {
	private Server 	server;
	private Game		game;

	/**
	 * InputDialog constructor
	 *
	 * @param parent the parent
	 */
	public AddServerDialog(Shell parent) {
		// Pass the default styles here
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Opens the dialog and returns the input
	 *
	 * @return String
	 */
	public Server open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText("Add New Server");
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
		return server;
	}

	/**
	 * Creates the dialog's contents
	 *
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) throws IllegalArgumentException {
		GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = 10;
		gl.verticalSpacing = 15;
		gl.marginHeight = 20;
		gl.marginWidth = 20;
		shell.setLayout(gl);

		Label ipLabel = new Label(shell, SWT.NONE);
		ipLabel.setText("IP Address");
		GridData data = new GridData();
		ipLabel.setLayoutData(data);

		final Text ipText = new Text(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 98;
		ipText.setLayoutData(data);

		Label portLabel = new Label(shell, SWT.NONE);
		portLabel.setText("Port");
		data = new GridData();
		ipLabel.setLayoutData(data);

		final Text portText = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 40;
		portText.setLayoutData(data);
		portText.setText(Integer.toString(getGame().defaultPort));

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("Ok");
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					if (game.gameNetProtocol.equals("q3s") ||
							game.gameNetProtocol.equals("q3i")) {
						server = new Q3Server(game, ipText.getText(), Integer.parseInt(portText.getText()));
					} else if (game.gameNetProtocol.equals("etqw")) {
						server = new ETQWServer(game, ipText.getText(), Integer.parseInt(portText.getText()));
					} else {
						MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
						mb.setText("Unknown protocol");
						mb.setMessage("Unknown game server protocol " + game.gameNetProtocol);
						mb.open();
						SysLogger.logMsg(9, "Unknown game server protocol " + game.gameNetProtocol);
						return;
					}
				} catch (NumberFormatException e) {
					MessageBox	mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					mb.setText("Number Format Error");
					mb.setMessage("Invalid port number " + portText.getText());
					mb.open();
					SysLogger.logMsg(9, "Invalid port number " + portText.getText());
					return;
				}
				ServerList list = new ServerList();
				list.add(server);
				game.updateServerList(list);
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
				server = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}
}
