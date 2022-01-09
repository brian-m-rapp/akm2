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
public class AboutDialog extends Dialog {
	String imageFileName;
	/**
	 * InputDialog constructor
	 *
	 * @param parent the parent
	 */
	public AboutDialog(Shell parent, String imageFile) {
		// Pass the default styles here
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		imageFileName = imageFile;
	}

	/**
	 * Opens the dialog and returns the input
	 *
	 * @return String
	 */
	public void open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText("About AKM");
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
		return;
	}

	/**
	 * Creates the dialog's contents
	 *
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		GridLayout gl = new GridLayout(1, true);
		GridData data;

		gl.horizontalSpacing = 10;
		gl.verticalSpacing = 15;
		gl.marginHeight = 20;
		gl.marginWidth = 20;
		shell.setLayout(gl);

//		Image oracleImage = new Image(shell.getDisplay(), imageFileName);
//		Label label = new Label(shell, SWT.NONE);
//		label.setImage(oracleImage);
//		
//		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
//		label.setLayoutData(data);
		
		// Display the input box
		final Text text = new Text(shell, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 400;
		//data.heightHint = 400;
		text.setLayoutData(data);

		text.setText("The All-Knowing Mind\n");
		text.append("version 0.2\n");
		text.append("(c) 2008-2009 Brian Rapp (dilireus@speakeasy.net)\n");
		text.append("All Rights Reserved\n");
		text.append("\nProject Page\n");
		text.append("http://sourceforge.net/projects/allknowingmind\n\n");
		text.append("Special thanks to:\n\n");
		text.append("Jacorre Design Studio (www.jacorre.com) for the flag icons\n\n");
		text.append("MaxMind (www.maxmind.com) for GeoIP software\n\n");
		text.append("The Apache Foundation (www.apache.org) for the Commons CLI\n\n");
		text.append("The Eclipse Foundation (www.eclipse.org) for SWT\n\n");
		text.append("znerd for xmlenc (sourceforge.net/projects/xmlenc)\n\n");
		text.append("Sourceforge.net for hosting this project");
		text.setTopIndex(0);

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("Ok");
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		shell.setDefaultButton(ok);
	}
}
