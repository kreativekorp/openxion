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

public interface AudioDialer {
	public static final float DTMF_LOW_123A = 697;
	public static final float DTMF_LOW_456B = 770;
	public static final float DTMF_LOW_789C = 852;
	public static final float DTMF_LOW_S0PD = 941;
	public static final float DTMF_HIGH_147S = 1209;
	public static final float DTMF_HIGH_2580 = 1336;
	public static final float DTMF_HIGH_369P = 1477;
	public static final float DTMF_HIGH_ABCD = 1633;
	public static final float DTMF_LOW_DT = 350;
	public static final float DTMF_HIGH_DT = 440;
	public static final float DTMF_LOW_RB = 440;
	public static final float DTMF_HIGH_RB = 480;
	public static final float DTMF_LOW_BUSY = 480;
	public static final float DTMF_HIGH_BUSY = 620;
	
	public static final float[] DTMF_NULL = { 0, 0 };
	public static final float[] DTMF_0 = { DTMF_LOW_S0PD, DTMF_HIGH_2580 };
	public static final float[] DTMF_1 = { DTMF_LOW_123A, DTMF_HIGH_147S };
	public static final float[] DTMF_2 = { DTMF_LOW_123A, DTMF_HIGH_2580 };
	public static final float[] DTMF_3 = { DTMF_LOW_123A, DTMF_HIGH_369P };
	public static final float[] DTMF_4 = { DTMF_LOW_456B, DTMF_HIGH_147S };
	public static final float[] DTMF_5 = { DTMF_LOW_456B, DTMF_HIGH_2580 };
	public static final float[] DTMF_6 = { DTMF_LOW_456B, DTMF_HIGH_369P };
	public static final float[] DTMF_7 = { DTMF_LOW_789C, DTMF_HIGH_147S };
	public static final float[] DTMF_8 = { DTMF_LOW_789C, DTMF_HIGH_2580 };
	public static final float[] DTMF_9 = { DTMF_LOW_789C, DTMF_HIGH_369P };
	public static final float[] DTMF_A = { DTMF_LOW_123A, DTMF_HIGH_ABCD };
	public static final float[] DTMF_B = { DTMF_LOW_456B, DTMF_HIGH_ABCD };
	public static final float[] DTMF_C = { DTMF_LOW_789C, DTMF_HIGH_ABCD };
	public static final float[] DTMF_D = { DTMF_LOW_S0PD, DTMF_HIGH_ABCD };
	public static final float[] DTMF_STAR = { DTMF_LOW_S0PD, DTMF_HIGH_147S };
	public static final float[] DTMF_POUND = { DTMF_LOW_S0PD, DTMF_HIGH_369P };
	public static final float[] DTMF_DIALTONE = { DTMF_LOW_DT, DTMF_HIGH_DT };
	public static final float[] DTMF_RINGBACK = { DTMF_LOW_RB, DTMF_HIGH_RB };
	public static final float[] DTMF_BUSY = { DTMF_LOW_BUSY, DTMF_HIGH_BUSY };
	
	public float getMinimumAmplitude();
	public float getMaximumAmplitude();
	public void dial(char button, long duration, float amplitude);
	public void dial(char[] buttons, long duration, float amplitude);
	public void dial(String number, long duration, float amplitude);
	public boolean isDialing();
	public char getButtonDialed();
	public char[] getButtonsDialed();
	public String getNumberDialed();
	public void finishDialing();
	public void stopDialing();
}
