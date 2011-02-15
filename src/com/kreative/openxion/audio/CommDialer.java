/*
 * Copyright &copy; 2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.audio;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import com.kreative.openxion.XNScriptError;
import javax.comm.*;

public class CommDialer implements ModemDialer {
	@Override
	public String[] getSerialPorts() {
		CommPortIdentifier portId;
		Enumeration<?> en = CommPortIdentifier.getPortIdentifiers();
		Vector<String> listData = new Vector<String>();
		while (en.hasMoreElements()) {
			portId = (CommPortIdentifier)en.nextElement();
			listData.addElement(portId.getName());
		}
		return listData.toArray(new String[0]);
	}
	
	private CommPortIdentifier getSerialPort(String serialPort) {
		CommPortIdentifier portId;
		Enumeration<?> en = CommPortIdentifier.getPortIdentifiers();
		while (en.hasMoreElements()) {
			portId = (CommPortIdentifier)en.nextElement();
			if (portId.getName().equalsIgnoreCase(serialPort)) {
				return portId;
			}
		}
		en = CommPortIdentifier.getPortIdentifiers();
		if (en.hasMoreElements()) {
			return (CommPortIdentifier)en.nextElement();
		}
		return null;
	}

	@Override
	public void dial(String serialPort, String command, String number, long predialPause, long postdialPause) {
		CommPortIdentifier portId = getSerialPort(serialPort);
		if (portId != null) {
			try {
				CommPort port = portId.open("OpenXION", (int)predialPause);
				PrintStream out = new PrintStream(port.getOutputStream(), true);
				try { Thread.sleep(predialPause); } catch (InterruptedException e) {}
				out.println(command + number);
				try { Thread.sleep(postdialPause); } catch (InterruptedException e) {}
				out.close();
				port.close();
			} catch (PortInUseException e) {
				throw new XNScriptError("Can't open port");
			} catch (IOException e) {
				throw new XNScriptError("Can't write to port");
			}
		} else {
			throw new XNScriptError("No ports open");
		}
	}
}
