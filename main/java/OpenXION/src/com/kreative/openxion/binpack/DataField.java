/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.openxion.binpack;

public class DataField {
	private DataType type;
	private int size;
	private boolean littleEndian;
	private Object elaboration;
	private DFExpression count;
	private String name;
	private String description;
	
	public DataField(DataType type) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = null;
		this.count = null;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, int size) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = null;
		this.count = null;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, boolean littleEndian) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = null;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, int size, boolean littleEndian) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = null;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, Object elaboration) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = null;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, int size, Object elaboration) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = null;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, boolean littleEndian, Object elaboration) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = null;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, Object elaboration) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = null;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, int size, DFExpression count) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = null;
		this.count = count;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, boolean littleEndian, DFExpression count) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = count;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, DFExpression count) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = count;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, Object elaboration, DFExpression count) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = count;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, int size, Object elaboration, DFExpression count) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = count;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, boolean littleEndian, Object elaboration, DFExpression count) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = count;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, Object elaboration, DFExpression count) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = count;
		this.name = null;
		this.description = null;
	}
	
	public DataField(DataType type, String name) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = null;
		this.count = null;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, int size, String name) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = null;
		this.count = null;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, boolean littleEndian, String name) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = null;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, String name) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = null;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, Object elaboration, String name) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = null;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, int size, Object elaboration, String name) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = null;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, boolean littleEndian, Object elaboration, String name) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = null;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, Object elaboration, String name) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = null;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, int size, DFExpression count, String name) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = null;
		this.count = count;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, boolean littleEndian, DFExpression count, String name) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = count;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, DFExpression count, String name) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = count;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, Object elaboration, DFExpression count, String name) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = count;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, int size, Object elaboration, DFExpression count, String name) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = count;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, boolean littleEndian, Object elaboration, DFExpression count, String name) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = count;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, Object elaboration, DFExpression count, String name) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = count;
		this.name = name;
		this.description = null;
	}
	
	public DataField(DataType type, String name, String description) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = null;
		this.count = null;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, int size, String name, String description) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = null;
		this.count = null;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, boolean littleEndian, String name, String description) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = null;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, String name, String description) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = null;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, Object elaboration, String name, String description) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = null;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, int size, Object elaboration, String name, String description) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = null;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, boolean littleEndian, Object elaboration, String name, String description) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = null;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, Object elaboration, String name, String description) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = null;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, int size, DFExpression count, String name, String description) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = null;
		this.count = count;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, boolean littleEndian, DFExpression count, String name, String description) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = count;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, DFExpression count, String name, String description) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = null;
		this.count = count;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, Object elaboration, DFExpression count, String name, String description) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = count;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, int size, Object elaboration, DFExpression count, String name, String description) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
		this.elaboration = elaboration;
		this.count = count;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, boolean littleEndian, Object elaboration, DFExpression count, String name, String description) {
		this.type = type;
		this.size = type.defaultSize();
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = count;
		this.name = name;
		this.description = description;
	}
	
	public DataField(DataType type, int size, boolean littleEndian, Object elaboration, DFExpression count, String name, String description) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
		this.elaboration = elaboration;
		this.count = count;
		this.name = name;
		this.description = description;
	}
	
	public DataType type() {
		return type;
	}
	
	public int size() {
		return size;
	}
	
	public boolean littleEndian() {
		return littleEndian;
	}
	
	public boolean bigEndian() {
		return !littleEndian;
	}
	
	public Object elaboration() {
		return elaboration;
	}
	
	public DFExpression count() {
		return count;
	}
	
	public String name() {
		return name;
	}
	
	public String description() {
		return description;
	}
}
