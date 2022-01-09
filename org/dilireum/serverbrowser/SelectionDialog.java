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
import java.util.*;

/**
 * Dialog class to input a single string.
 */
public class SelectionDialog extends Dialog {
	private ArrayList<String>	entries;
	private String message;
	private String input;

	/**
	 * InputDialog constructor
	 *
	 * @param parent the parent
	 */
	public SelectionDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * InputDialog constructor
	 *
	 * @param parent the parent
	 * @param style the style
	 */
	public SelectionDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Input Dialog");
		setMessage("Please enter a value:");
		entries = new ArrayList<String>();
	}

	/**
	 * Adds a selection entry into the combo box.
	 * @param entry String to add.
	 */
	public void addSelection(String entry) {
		entries.add(entry);
	}

	/**
	 * Gets the message
	 *
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the input
	 *
	 * @return String
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Sets the input
	 *
	 * @param input the new input
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * Opens the dialog and returns the input
	 *
	 * @return String
	 */
	public String open() {
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
		return input;
	}

	/**
	 * Creates the dialog's contents
	 *
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, false));

		// Show the message
		Label label = new Label(shell, SWT.NONE);
		label.setText(message);
		GridData data = new GridData();
		//data.horizontalSpan = 2;
		label.setLayoutData(data);

		// Display the input box
		final Combo combo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 0; i < entries.size(); i++) {
			combo.add(entries.get(i));
		}
		combo.select(0);
		data = new GridData(GridData.FILL_HORIZONTAL);
		//data.horizontalSpan = 2;
		combo.setLayoutData(data);

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("Ok");
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = combo.getText();
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
				input = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}
}
