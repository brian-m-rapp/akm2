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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class SplashScreen {

	public static void main(String[] args) {
		final Display display = new Display();
		final int[] count = new int[] { 4 };
		final Image image = new Image(display, "images/oracle480x314.png");
		//GC gc = new GC(image);
		//gc.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		//gc.fillRectangle(image.getBounds());
		//gc.drawText("Splash Screen", 10, 10);
		//gc.dispose();
		final Shell splash = new Shell(SWT.ON_TOP);
		final ProgressBar bar = new ProgressBar(splash, SWT.NONE);
		bar.setMaximum(count[0]);
		Label label = new Label(splash, SWT.NONE);
		label.setImage(image);
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
		bar.setLayoutData(progressData);
		splash.pack();
		Rectangle splashRect = splash.getBounds();
		Rectangle displayRect = display.getPrimaryMonitor().getBounds();
		int x = (displayRect.width - splashRect.width) / 2;
		int y = (displayRect.height - splashRect.height) / 2;
		splash.setLocation(x, y);
		splash.open();
		display.asyncExec(new Runnable() {
			public void run() {
				Shell[] shells = new Shell[count[0]];
				for (int i = 0; i < count[0]; i++) {
					shells[i] = new Shell(display);
					shells[i].setSize(300, 300);
					shells[i].addListener(SWT.Close, new Listener() {
						public void handleEvent(Event e) {
							--count[0];
						}
					});
					bar.setSelection(i + 1);
					try {
						Thread.sleep(1000);
					} catch (Throwable e) {
					}
				}
				splash.close();
				image.dispose();
				for (int i = 0; i < count[0]; i++) {
					shells[i].open();
				}
			}
		});
		while (count[0] != 0) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}