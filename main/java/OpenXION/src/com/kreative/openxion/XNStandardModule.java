/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;

import com.kreative.openxion.ast.*;
import com.kreative.openxion.io.*;
import com.kreative.openxion.math.*;
import com.kreative.openxion.util.*;
import com.kreative.openxion.xom.*;
import com.kreative.openxion.xom.inst.*;
import com.kreative.openxion.xom.type.*;

/**
 * XNStandardModule is the XNModule responsible for the
 * constants, ordinals, data types, commands, and functions
 * in the XION Scripting Language Standard, Version 1.0.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNStandardModule extends XNModule {
	private static final long serialVersionUID = 2L;
	
	public static final String MODULE_NAME = "OpenXION Standard Module";
	public static final String MODULE_VERSION = "1.3";
	
	private static XNStandardModule instance = null;
	public static final synchronized XNStandardModule instance() {
		if (instance == null) {
			instance = new XNStandardModule();
		}
		return instance;
	}
	
	private XNStandardModule() {
		super();
		
		constants.put("zero", XOMInteger.ZERO);
		constants.put("never", XOMInteger.ZERO);
		constants.put("one", XOMInteger.ONE);
		constants.put("once", XOMInteger.ONE);
		constants.put("two", new XOMInteger(2));
		constants.put("twice", new XOMInteger(2));
		constants.put("three", new XOMInteger(3));
		constants.put("thrice", new XOMInteger(3));
		constants.put("four", new XOMInteger(4));
		constants.put("five", new XOMInteger(5));
		constants.put("six", new XOMInteger(6));
		constants.put("seven", new XOMInteger(7));
		constants.put("eight", new XOMInteger(8));
		constants.put("nine", new XOMInteger(9));
		constants.put("ten", new XOMInteger(10));
		constants.put("eleven", new XOMInteger(11));
		constants.put("twelve", new XOMInteger(12));
		constants.put("thirteen", new XOMInteger(13));
		constants.put("fourteen", new XOMInteger(14));
		constants.put("fifteen", new XOMInteger(15));
		constants.put("sixteen", new XOMInteger(16));
		constants.put("seventeen", new XOMInteger(17));
		constants.put("eighteen", new XOMInteger(18));
		constants.put("nineteen", new XOMInteger(19));
		constants.put("twenty", new XOMInteger(20));
		
		constants.put("phi", XOMNumber.PHI);
		constants.put("\u03C6", XOMNumber.PHI);
		constants.put("euler", XOMNumber.E);
		constants.put("pi", XOMNumber.PI);
		constants.put("\u03C0", XOMNumber.PI);
		constants.put("\u221E", XOMNumber.POSITIVE_INFINITY);
		constants.put("inf", XOMNumber.POSITIVE_INFINITY);
		constants.put("infinity", XOMNumber.POSITIVE_INFINITY);
		constants.put("forever", XOMNumber.POSITIVE_INFINITY);
		constants.put("nan", XOMNumber.NaN);
		
		constants.put("empty", XOMEmpty.EMPTY);
		constants.put("null", new XOMString("\u0000"));
		constants.put("backspace", new XOMString("\b"));
		constants.put("tab", new XOMString("\t"));
		constants.put("linefeed", new XOMString("\n"));
		constants.put("newline", new XOMString("\n"));
		constants.put("formfeed", new XOMString("\f"));
		constants.put("return", new XOMString("\r"));
		constants.put("escape", new XOMString("\u001B"));
		constants.put("space", new XOMString(" "));
		constants.put("quote", new XOMString("\""));
		constants.put("apostrophe", new XOMString("'"));
		constants.put("comma", new XOMString(","));
		constants.put("period", new XOMString("."));
		constants.put("fullstop", new XOMString("."));
		constants.put("slash", new XOMString("/"));
		constants.put("colon", new XOMString(":"));
		constants.put("semicolon", new XOMString(";"));
		constants.put("backslash", new XOMString("\\"));
		constants.put("del", new XOMString("\u007F"));
		constants.put("delete", new XOMString("\u007F"));
		constants.put("linesep", new XOMString("\u2028"));
		constants.put("parasep", new XOMString("\u2029"));
		constants.put("eof", new XOMString("\uFFFF"));
		constants.put("end", new XOMString("\uFFFF"));
		
		constants.put("true", XOMBoolean.TRUE);
		constants.put("false", XOMBoolean.FALSE);
		
		constants.put("white", new XOMColor(65535, 65535, 65535));
		constants.put("lightgray", new XOMColor(49152, 49152, 49152));
		constants.put("lightgrey", new XOMColor(49152, 49152, 49152));
		constants.put("silver", new XOMColor(49152, 49152, 49152));
		constants.put("gray", new XOMColor(32768, 32768, 32768));
		constants.put("grey", new XOMColor(32768, 32768, 32768));
		constants.put("darkgray", new XOMColor(16384, 16384, 16384));
		constants.put("darkgrey", new XOMColor(16384, 16384, 16384));
		constants.put("black", new XOMColor(0, 0, 0));
		constants.put("coral", new XOMColor(65535, 32768, 32768));
		constants.put("corange", new XOMColor(65535, 49152, 32768));
		constants.put("lemon", new XOMColor(65535, 65535, 32768));
		constants.put("lime", new XOMColor(32768, 65535, 32768));
		constants.put("sky", new XOMColor(32768, 65535, 65535));
		constants.put("frost", new XOMColor(32768, 32768, 65535));
		constants.put("lavender", new XOMColor(49152, 32768, 65535));
		constants.put("pink", new XOMColor(65535, 32768, 65535));
		constants.put("red", new XOMColor(65535, 0, 0));
		constants.put("scarlet", new XOMColor(65535, 16384, 0));
		constants.put("orange", new XOMColor(65535, 32768, 0));
		constants.put("blonde", new XOMColor(65535, 49152, 0));
		constants.put("gold", new XOMColor(65535, 49152, 0));
		constants.put("yellow", new XOMColor(65535, 65535, 0));
		constants.put("chartreuse", new XOMColor(32768, 65535, 0));
		constants.put("green", new XOMColor(0, 65535, 0));
		constants.put("aquamarine", new XOMColor(0, 65535, 32768));
		constants.put("aqua", new XOMColor(0, 65535, 65535));
		constants.put("cyan", new XOMColor(0, 65535, 65535));
		constants.put("azure", new XOMColor(0, 32768, 65535));
		constants.put("blue", new XOMColor(0, 0, 65535));
		constants.put("indigo", new XOMColor(16384, 0, 65535));
		constants.put("violet", new XOMColor(32768, 0, 65535));
		constants.put("purple", new XOMColor(49152, 0, 65535));
		constants.put("magenta", new XOMColor(65535, 0, 65535));
		constants.put("fuchsia", new XOMColor(65535, 0, 65535));
		constants.put("rose", new XOMColor(65535, 0, 32768));
		constants.put("maroon", new XOMColor(32768, 0, 0));
		constants.put("umber", new XOMColor(32768, 16384, 0));
		constants.put("olive", new XOMColor(32768, 32768, 0));
		constants.put("pine", new XOMColor(0, 32768, 0));
		constants.put("teal", new XOMColor(0, 32768, 32768));
		constants.put("navy", new XOMColor(0, 0, 32768));
		constants.put("eggplant", new XOMColor(16384, 0, 32768));
		constants.put("plum", new XOMColor(32768, 0, 32768));
		constants.put("brown", new XOMColor(39321, 26214, 13107));
		constants.put("creme", new XOMColor(65535, 61166, 52428));
		
		ordinals.put("zeroth", 0);
		ordinals.put("first", 1);
		ordinals.put("second", 2);
		ordinals.put("third", 3);
		ordinals.put("fourth", 4);
		ordinals.put("fifth", 5);
		ordinals.put("sixth", 6);
		ordinals.put("seventh", 7);
		ordinals.put("eighth", 8);
		ordinals.put("ninth", 9);
		ordinals.put("tenth", 10);
		ordinals.put("eleventh", 11);
		ordinals.put("twelfth", 12);
		ordinals.put("thirteenth", 13);
		ordinals.put("fourteenth", 14);
		ordinals.put("fifteenth", 15);
		ordinals.put("sixteenth", 16);
		ordinals.put("seventeenth", 17);
		ordinals.put("eighteenth", 18);
		ordinals.put("nineteenth", 19);
		ordinals.put("twentieth", 20);
		ordinals.put("mid", XIONUtil.INDEX_MIDDLE);
		ordinals.put("middle", XIONUtil.INDEX_MIDDLE);
		ordinals.put("last", -1);
		ordinals.put("any", XIONUtil.INDEX_ANY);
		ordinals.put("this", XIONUtil.INDEX_CURRENT);
		ordinals.put("current", XIONUtil.INDEX_CURRENT);
		ordinals.put("prev", XIONUtil.INDEX_PREVIOUS);
		ordinals.put("previous", XIONUtil.INDEX_PREVIOUS);
		ordinals.put("next", XIONUtil.INDEX_NEXT);
		ordinals.put("recent", XIONUtil.INDEX_RECENT);
		
		dataTypes.put("variant", XOMVariantType.instance);
		dataTypes.put("variants", XOMVariantType.listInstance);
		dataTypes.put("list", XOMListType.instance);
		dataTypes.put("lists", XOMListType.listInstance);
		dataTypes.put("string", XOMStringType.instance);
		dataTypes.put("strings", XOMStringType.listInstance);
		dataTypes.put("boolean", XOMBooleanType.instance);
		dataTypes.put("booleans", XOMBooleanType.listInstance);
		dataTypes.put("number", XOMNumberType.instance);
		dataTypes.put("numbers", XOMNumberType.listInstance);
		dataTypes.put("integer", XOMIntegerType.instance);
		dataTypes.put("integers", XOMIntegerType.listInstance);
		dataTypes.put("complex", XOMComplexType.instance);
		dataTypes.put("complexes", XOMComplexType.listInstance);
		dataTypes.put("point", XOMPointType.instance);
		dataTypes.put("points", XOMPointType.listInstance);
		dataTypes.put("rect", XOMRectangleType.instance);
		dataTypes.put("rects", XOMRectangleType.listInstance);
		dataTypes.put("rectangle", XOMRectangleType.instance);
		dataTypes.put("rectangles", XOMRectangleType.listInstance);
		dataTypes.put("color", XOMColorType.instance);
		dataTypes.put("colors", XOMColorType.listInstance);
		dataTypes.put("colour", XOMColorType.instance);
		dataTypes.put("colours", XOMColorType.listInstance);
		dataTypes.put("date", XOMDateType.instance);
		dataTypes.put("dates", XOMDateType.listInstance);
		dataTypes.put("binary", XOMBinaryType.instance);
		dataTypes.put("binaries", XOMBinaryType.listInstance);
		dataTypes.put("dictionary", XOMDictionaryType.instance);
		dataTypes.put("dictionaries", XOMDictionaryType.listInstance);
		dataTypes.put("reference", XOMReferenceType.instance);
		dataTypes.put("references", XOMReferenceType.listInstance);
		dataTypes.put("object", XOMUserObjectType.rootInstance);
		dataTypes.put("objects", XOMUserObjectType.rootListInstance);
		
		dataTypes.put("interpreter", XOMInterpreterType.instance);
		dataTypes.put("interpreters", XOMInterpreterType.listInstance);
		dataTypes.put("environment", XOMInterpreterType.instance);
		dataTypes.put("environments", XOMInterpreterType.listInstance);
		
		dataTypes.put("element", XOMListChunkType.instance);
		dataTypes.put("elements", XOMListChunkType.listInstance);
		dataTypes.put("entry", XOMDictionaryChunkType.instance);
		dataTypes.put("entries", XOMDictionaryChunkType.listInstance);
		
		dataTypes.put("char", XOMStringChunkType.characterInstance);
		dataTypes.put("chars", XOMStringChunkType.characterListInstance);
		dataTypes.put("character", XOMStringChunkType.characterInstance);
		dataTypes.put("characters", XOMStringChunkType.characterListInstance);
		dataTypes.put("line", XOMStringChunkType.lineInstance);
		dataTypes.put("lines", XOMStringChunkType.lineListInstance);
		dataTypes.put("item", XOMStringChunkType.itemInstance);
		dataTypes.put("items", XOMStringChunkType.itemListInstance);
		dataTypes.put("col", XOMStringChunkType.columnInstance);
		dataTypes.put("cols", XOMStringChunkType.columnListInstance);
		dataTypes.put("column", XOMStringChunkType.columnInstance);
		dataTypes.put("columns", XOMStringChunkType.columnListInstance);
		dataTypes.put("row", XOMStringChunkType.rowInstance);
		dataTypes.put("rows", XOMStringChunkType.rowListInstance);
		dataTypes.put("word", XOMStringChunkType.wordInstance);
		dataTypes.put("words", XOMStringChunkType.wordListInstance);
		dataTypes.put("sent", XOMStringChunkType.sentenceInstance);
		dataTypes.put("sents", XOMStringChunkType.sentenceListInstance);
		dataTypes.put("sentence", XOMStringChunkType.sentenceInstance);
		dataTypes.put("sentences", XOMStringChunkType.sentenceListInstance);
		dataTypes.put("para", XOMStringChunkType.paragraphInstance);
		dataTypes.put("paras", XOMStringChunkType.paragraphListInstance);
		dataTypes.put("paragraph", XOMStringChunkType.paragraphInstance);
		dataTypes.put("paragraphs", XOMStringChunkType.paragraphListInstance);
		
		dataTypes.put("byte", XOMBinaryByteChunkType.instance);
		dataTypes.put("bytes", XOMBinaryByteChunkType.listInstance);
		dataTypes.put("tinyint", XOMBinaryNumericChunkType.tinyIntInstance);
		dataTypes.put("tinyints", XOMBinaryNumericChunkType.tinyIntListInstance);
		dataTypes.put("shortint", XOMBinaryNumericChunkType.shortIntInstance);
		dataTypes.put("shortints", XOMBinaryNumericChunkType.shortIntListInstance);
		dataTypes.put("medint", XOMBinaryNumericChunkType.mediumIntInstance);
		dataTypes.put("medints", XOMBinaryNumericChunkType.mediumIntListInstance);
		dataTypes.put("mediumint", XOMBinaryNumericChunkType.mediumIntInstance);
		dataTypes.put("mediumints", XOMBinaryNumericChunkType.mediumIntListInstance);
		dataTypes.put("longint", XOMBinaryNumericChunkType.longIntInstance);
		dataTypes.put("longints", XOMBinaryNumericChunkType.longIntListInstance);
		dataTypes.put("halffloat", XOMBinaryNumericChunkType.halfFloatInstance);
		dataTypes.put("halffloats", XOMBinaryNumericChunkType.halfFloatListInstance);
		dataTypes.put("singlefloat", XOMBinaryNumericChunkType.singleFloatInstance);
		dataTypes.put("singlefloats", XOMBinaryNumericChunkType.singleFloatListInstance);
		dataTypes.put("doublefloat", XOMBinaryNumericChunkType.doubleFloatInstance);
		dataTypes.put("doublefloats", XOMBinaryNumericChunkType.doubleFloatListInstance);
		
		dataTypes.put("disk", XOMDiskType.instance);
		dataTypes.put("disks", XOMDiskType.listInstance);
		dataTypes.put("volume", XOMDiskType.instance);
		dataTypes.put("volumes", XOMDiskType.listInstance);
		dataTypes.put("folder", XOMFolderType.instance);
		dataTypes.put("folders", XOMFolderType.listInstance);
		dataTypes.put("directory", XOMFolderType.instance);
		dataTypes.put("directories", XOMFolderType.listInstance);
		dataTypes.put("file", XOMFileType.instance);
		dataTypes.put("files", XOMFileType.listInstance);
		dataTypes.put("fork", XOMForkType.instance);
		dataTypes.put("forks", XOMForkType.listInstance);
		
		commandParsers.put("add",p_add);
		commandParsers.put("answer",p_answer);
		commandParsers.put("ask",p_ask);
		commandParsers.put("create",p_create);
		commandParsers.put("delete",p_delete);
		commandParsers.put("divide",p_divide);
		commandParsers.put("get",p_get);
		commandParsers.put("let",p_let);
		commandParsers.put("modulo",p_modulo);
		commandParsers.put("multiply",p_multiply);
		commandParsers.put("put",p_put);
		commandParsers.put("set",p_set);
		commandParsers.put("subtract",p_subtract);
		commandParsers.put("beep",p_beep);
		commandParsers.put("close",p_close);
		commandParsers.put("convert",p_convert);
		commandParsers.put("open",p_open);
		commandParsers.put("read",p_read);
		commandParsers.put("sort",p_sort);
		commandParsers.put("truncate",p_truncate);
		commandParsers.put("wait",p_wait);
		commandParsers.put("write",p_write);
		
		commands.put("add",c_add);
		commands.put("answer",c_answer);
		commands.put("ask",c_ask);
		commands.put("create",c_create);
		commands.put("delete",c_delete);
		commands.put("divide",c_divide);
		commands.put("get",c_get);
		commands.put("let",c_let);
		commands.put("modulo",c_modulo);
		commands.put("multiply",c_multiply);
		commands.put("put",c_put);
		commands.put("set",c_set);
		commands.put("subtract",c_subtract);
		commands.put("beep",c_beep);
		commands.put("close",c_close);
		commands.put("convert",c_convert);
		commands.put("open",c_open);
		commands.put("read",c_read);
		commands.put("sort",c_sort);
		commands.put("truncate",c_truncate);
		commands.put("wait",c_wait);
		commands.put("write",c_write);
		
		functions.put("\u03B3",f_gamma);
		functions.put("\u03B2",f_beta);
		functions.put("abs",f_abs);
		functions.put("acos",f_acos);
		functions.put("acosh",f_acosh);
		functions.put("acot",f_acot);
		functions.put("acoth",f_acoth);
		functions.put("acsc",f_acsc);
		functions.put("acsch",f_acsch);
		functions.put("agm",f_agm);
		functions.put("and",f_and);
		functions.put("annuity",f_annuity);
		functions.put("appfile", f_applicationfile);
		functions.put("apppath", f_applicationpath);
		functions.put("appordocfile", f_applicationordocumentfile);
		functions.put("appordocpath", f_applicationordocumentpath);
		functions.put("applicationfile", f_applicationfile);
		functions.put("applicationpath", f_applicationpath);
		functions.put("applicationordocumentfile", f_applicationordocumentfile);
		functions.put("applicationordocumentpath", f_applicationordocumentpath);
		functions.put("arg",f_arg);
		functions.put("asc",f_asc);
		functions.put("ascending",f_asc);
		functions.put("asec",f_asec);
		functions.put("asech",f_asech);
		functions.put("asin",f_asin);
		functions.put("asinh",f_asinh);
		functions.put("atan",f_atan);
		functions.put("atan2",f_atan2);
		functions.put("atanh",f_atanh);
		functions.put("aug",f_aug);
		functions.put("average",f_average);
		functions.put("avg",f_average);
		functions.put("bc",f_bc);
		functions.put("beta",f_beta);
		functions.put("bin",f_bin);
		functions.put("bintochar",f_bintochar);
		functions.put("bintouni",f_bintouni);
		functions.put("cbrt",f_cbrt);
		functions.put("ceil",f_ceil);
		functions.put("center",f_center);
		functions.put("chartobin",f_chartobin);
		functions.put("chartonum",f_chartonum);
		functions.put("choose",f_ncr);
		functions.put("compound",f_compound);
		functions.put("concat",f_concat);
		functions.put("concatsp",f_concatsp);
		functions.put("conj",f_conj);
		functions.put("cos",f_cos);
		functions.put("cosh",f_cosh);
		functions.put("cot",f_cot);
		functions.put("coth",f_coth);
		functions.put("countfields",f_countfields);
		functions.put("cpad", f_cpad);
		functions.put("csc",f_csc);
		functions.put("csch",f_csch);
		functions.put("cscountfields",f_cscountfields);
		functions.put("csexplode",f_csexplode);
		functions.put("csinstr",f_csinstr);
		functions.put("csnthfield",f_csnthfield);
		functions.put("csoffset",f_csoffset);
		functions.put("csreplace",f_csreplace);
		functions.put("csreplaceall",f_csreplaceall);
		functions.put("csrinstr",f_csrinstr);
		functions.put("csstrcmp", f_csstrcmp);
		functions.put("date",f_date);
		functions.put("dateitems",f_dateitems);
		functions.put("dec",f_dec);
		functions.put("decreasing",f_dec);
		functions.put("desc",f_dec);
		functions.put("descending",f_dec);
		functions.put("docfile", f_documentfile);
		functions.put("docpath", f_documentpath);
		functions.put("documentfile", f_documentfile);
		functions.put("documentpath", f_documentpath);
		functions.put("equal",f_equal);
		functions.put("exp",f_exp);
		functions.put("exp1",f_exp1);
		functions.put("exp10",f_exp10);
		functions.put("exp2",f_exp2);
		functions.put("explode",f_explode);
		functions.put("fact",f_fact);
		functions.put("factorial",f_fact);
		functions.put("floor",f_floor);
		functions.put("frac",f_frac);
		functions.put("gamma",f_gamma);
		functions.put("gcd",f_gcd);
		functions.put("geom",f_geom);
		functions.put("geomean",f_geom);
		functions.put("hash",f_hash);
		functions.put("head",f_head);
		functions.put("hex",f_hex);
		functions.put("hypot",f_hypot);
		functions.put("im",f_im);
		functions.put("implode",f_implode);
		functions.put("inc",f_asc);
		functions.put("includefile", f_includefile);
		functions.put("includepath", f_includepath);
		functions.put("increasing",f_asc);
		functions.put("instr",f_instr);
		functions.put("int",f_trunc);
		functions.put("isfinite", f_isfinite);
		functions.put("isinfinite", f_isinfinite);
		functions.put("isnan", f_isnan);
		functions.put("lcase",f_lcase);
		functions.put("lcm",f_lcm);
		functions.put("lconcat",f_lconcat);
		functions.put("left",f_left);
		functions.put("len",f_len);
		functions.put("length",f_len);
		functions.put("llength",f_number);
		functions.put("ln",f_ln);
		functions.put("ln\u03B3",f_lngamma);
		functions.put("ln\u03B2",f_lnbeta);
		functions.put("ln1",f_ln1);
		functions.put("lnbeta",f_lnbeta);
		functions.put("lnfact",f_lnfact);
		functions.put("lnfactorial",f_lnfact);
		functions.put("lngamma",f_lngamma);
		functions.put("log",f_log);
		functions.put("log10",f_log10);
		functions.put("log2",f_log2);
		functions.put("lpad",f_lpad);
		functions.put("lreverse",f_lreverse);
		functions.put("ltrim",f_ltrim);
		functions.put("max",f_max);
		functions.put("maximum",f_max);
		functions.put("mid",f_mid);
		functions.put("min",f_min);
		functions.put("minimum",f_min);
		functions.put("ncr",f_ncr);
		functions.put("npr",f_npr);
		functions.put("nthfield",f_nthfield);
		functions.put("number",f_number);
		functions.put("numtochar",f_numtochar);
		functions.put("numtouni",f_numtouni);
		functions.put("oct",f_oct);
		functions.put("offset",f_offset);
		functions.put("or",f_or);
		functions.put("param",f_param);
		functions.put("paramcount",f_paramcount);
		functions.put("params",f_params);
		functions.put("parent",f_parent);
		functions.put("path", f_applicationordocumentpath);
		functions.put("pick",f_npr);
		functions.put("pow",f_pow);
		functions.put("prod",f_prod);
		functions.put("product",f_prod);
		functions.put("progfile", f_applicationfile);
		functions.put("progpath", f_applicationpath);
		functions.put("progordocfile", f_applicationordocumentfile);
		functions.put("progordocpath", f_applicationordocumentpath);
		functions.put("programfile", f_applicationfile);
		functions.put("programpath", f_applicationpath);
		functions.put("programordocumentfile", f_applicationordocumentfile);
		functions.put("programordocumentpath", f_applicationordocumentpath);
		functions.put("pstddev",f_pstddev);
		functions.put("pvariance",f_pvariance);
		functions.put("radius",f_hypot);
		functions.put("random",f_random);
		functions.put("randomdecimal",f_randomdecimal);
		functions.put("randomrange",f_randomrange);
		functions.put("re",f_re);
		functions.put("replace",f_replace);
		functions.put("replaceall",f_replaceall);
		functions.put("result",f_result);
		functions.put("reverse",f_reverse);
		functions.put("reversebits",f_reversebits);
		functions.put("reversebytes",f_reversebytes);
		functions.put("right",f_right);
		functions.put("rinstr",f_rinstr);
		functions.put("rint",f_rint);
		functions.put("rms",f_rms);
		functions.put("root",f_root);
		functions.put("rot13",f_rot13);
		functions.put("round",f_round);
		functions.put("rpad",f_rpad);
		functions.put("rsr",f_rsr);
		functions.put("rtrim",f_rtrim);
		functions.put("sec",f_sec);
		functions.put("sech",f_sech);
		functions.put("secs",f_secs);
		functions.put("seconds",f_secs);
		functions.put("sgn",f_sgn);
		functions.put("signum",f_sgn);
		functions.put("sin",f_sin);
		functions.put("sinh",f_sinh);
		functions.put("sqrt",f_sqrt);
		functions.put("sstddev",f_sstddev);
		functions.put("stddev",f_pstddev);
		functions.put("strcmp", f_strcmp);
		functions.put("substr",f_substr);
		functions.put("substring",f_substring);
		functions.put("sum",f_sum);
		functions.put("svariance",f_svariance);
		functions.put("systemname",f_systemname);
		functions.put("systemversion",f_systemversion);
		functions.put("tail",f_tail);
		functions.put("tan",f_tan);
		functions.put("tanh",f_tanh);
		functions.put("tcase",f_tcase);
		functions.put("theta",f_theta);
		functions.put("ticks",f_ticks);
		functions.put("time",f_time);
		functions.put("todeg",f_todeg);
		functions.put("todegrees",f_todeg);
		functions.put("torad",f_torad);
		functions.put("toradians",f_torad);
		functions.put("trim",f_trim);
		functions.put("trunc",f_trunc);
		functions.put("ucase",f_ucase);
		functions.put("unitobin",f_unitobin);
		functions.put("unitonum",f_unitonum);
		functions.put("value",f_value);
		functions.put("variance",f_pvariance);
		functions.put("version",f_version);
		functions.put("xcoord",f_xcoord);
		functions.put("xionname",f_xionname);
		functions.put("xionversion",f_xionversion);
		functions.put("xor",f_xor);
		functions.put("ycoord",f_ycoord);
		
		properties.put("username", p_username);
		properties.put("applicationpaths", p_applicationPaths);
		properties.put("programpaths", p_applicationPaths);
		properties.put("documentpaths", p_documentPaths);
		properties.put("includepaths", p_includePaths);
		properties.put("itemdelimiter", p_itemdelimiter);
		properties.put("columndelimiter", p_columndelimiter);
		properties.put("rowdelimiter", p_rowdelimiter);
		properties.put("littleendian", p_littleendian);
		properties.put("unsigned", p_unsigned);
		properties.put("numberformat", p_numberformat);
		properties.put("textencoding", p_textencoding);
		properties.put("lineending", p_lineending);
		properties.put("mathprocessor", p_mathprocessor);
		properties.put("precision", p_precision);
		properties.put("roundingmode", p_roundingmode);
		
		versions.put("standardmodule", new Version(XNStandardModule.MODULE_NAME, XNStandardModule.MODULE_VERSION));
		versions.put("interpreter", new Version(XNMain.XION_NAME, XNMain.XION_VERSION));
		versions.put("xion", new Version(XNMain.XION_NAME, XNMain.XION_VERSION));
		versions.put("openxion", new Version(XNMain.XION_NAME, XNMain.XION_VERSION));
		versions.put("system", new Version(System.getProperty("os.name"), System.getProperty("os.version")));
		versions.put("operatingsystem", new Version(System.getProperty("os.name"), System.getProperty("os.version")));
		versions.put("opsys", new Version(System.getProperty("os.name"), System.getProperty("os.version")));
		versions.put("os", new Version(System.getProperty("os.name"), System.getProperty("os.version")));
		
		ioManagers.add(XOMFileIOManager.instance);
		ioMethods.add(BinaryIOMethod.instance);
		ioMethods.add(TextIOMethod.instance);
	}
	
	public String toString() {
		return "XNStandardModule";
	}
	
	private static XNExpression getTokenExpression(XNParser p, String s) {
		if (p.lookToken(1).image.equalsIgnoreCase(s)) {
			return new XNStringExpression(p.getToken());
		} else {
			throw new XNParseError(s, p.lookToken(1));
		}
	}
	
	private static XNExpression getTokenExpression(XNParser p, int n) {
		if (n < 1) {
			return new XNEmptyExpression(p.getSource(), 0, 0);
		} else {
			XNToken t = p.getToken(); n--;
			while (n > 0) {
				XNToken u = p.getToken(); n--;
				XNToken v = new XNToken(t.kind, t.image+" "+u.image, t.source, t.beginLine, t.beginColumn, u.endLine, u.endColumn);
				v.specialToken = t.specialToken;
				v.next = u.next;
				t = v;
			}
			return new XNStringExpression(t);
		}
	}
	
	private static XNExpression createTokenExpression(XNParser p, String s) {
		if (s == null || s.trim().length() == 0) {
			return new XNEmptyExpression(p.getSource(), 0, 0);
		} else {
			return new XNStringExpression(new XNToken(XNToken.ID, s, p.getSource(), 0, 0, 0, 0));
		}
	}
	
	private static XNToken getTokenFromTokenExpression(XNParser p, XNExpression e) {
		if (e instanceof XNStringExpression) {
			return ((XNStringExpression)e).literal;
		} else if (e instanceof XNNumberExpression) {
			return ((XNNumberExpression)e).literal;
		} else {
			return new XNToken(XNToken.ID, "", p.getSource(), 0,0,0,0);
		}
	}
	
	private static String myDescribeCommand(String commandName, List<XNExpression> parameters) {
		String s = "";
		for (XNExpression p : parameters) {
			if (p instanceof XNEmptyExpression) {
				XNEmptyExpression ee = (XNEmptyExpression)p;
				if (ee.getBeginCol() != 0 || ee.getBeginLine() != 0) {
					s += " "+p.toString();
				}
			} else {
				s += " "+p.toString();
			}
		}
		return s.trim();
	}
	
	private static final CommandParser p_add = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("to");
			XNExpression rvalue = p.getListExpression(myKeywords);
			XNExpression to = getTokenExpression(p,"to");
			XNExpression lvalue = p.getListExpression(keywords);
			return Arrays.asList(new XNExpression[]{
					rvalue,
					to,
					lvalue
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_answer = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			String kind;
			if (
					p.lookToken(1).toString().equalsIgnoreCase("list") ||
					p.lookToken(1).toString().equalsIgnoreCase("file") ||
					p.lookToken(1).toString().equalsIgnoreCase("folder") ||
					p.lookToken(1).toString().equalsIgnoreCase("directory") ||
					p.lookToken(1).toString().equalsIgnoreCase("disk") ||
					p.lookToken(1).toString().equalsIgnoreCase("volume")
			) {
				kind = getTokenExpression(p,1).toString().toLowerCase();
			} else {
				kind = "normal";
			}
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(kind.equals("file") ? "of" : "with");
			myKeywords.add("at");
			List<XNExpression> following = new Vector<XNExpression>();
			following.add(createTokenExpression(p, kind));
			following.add(p.getListExpression(myKeywords));
			if (
					kind.equals("file") ?
							(p.lookToken(1).toString().equalsIgnoreCase("of") && p.lookToken(2).toString().equalsIgnoreCase("type")) :
								(p.lookToken(1).toString().equalsIgnoreCase("with"))
			) {
				myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("or");
				myKeywords.add("at");
				following.add(getTokenExpression(p, kind.equals("file")?2:1));
				following.add(p.getListExpression(myKeywords));
				if (p.lookToken(1).toString().equalsIgnoreCase("or")) {
					getTokenExpression(p,1);
					following.add(p.getListExpression(myKeywords));
				}
				if (p.lookToken(1).toString().equalsIgnoreCase("or")) {
					getTokenExpression(p,1);
					following.add(p.getListExpression(myKeywords));
				}
			}
			if (p.lookToken(1).toString().equalsIgnoreCase("at")) {
				following.add(getTokenExpression(p,1));
				following.add(p.getListExpression(keywords));
			}
			return following;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_ask = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			String kind;
			if (p.lookToken(1).toString().equalsIgnoreCase("password") && p.lookToken(2).toString().equalsIgnoreCase("clear")) {
				kind = "password clear"; p.consumeTokens(2);
			} else if (
					p.lookToken(1).toString().equalsIgnoreCase("password") ||
					p.lookToken(1).toString().equalsIgnoreCase("file") ||
					p.lookToken(1).toString().equalsIgnoreCase("folder") ||
					p.lookToken(1).toString().equalsIgnoreCase("directory")
			) {
				kind = getTokenExpression(p,1).toString().toLowerCase();
			} else {
				kind = "normal";
			}
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("with");
			myKeywords.add("at");
			List<XNExpression> following = new Vector<XNExpression>();
			following.add(createTokenExpression(p, kind));
			following.add(p.getListExpression(myKeywords));
			if (p.lookToken(1).toString().equalsIgnoreCase("with")) {
				myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("at");
				following.add(getTokenExpression(p,1));
				following.add(p.getListExpression(myKeywords));
			}
			if (p.lookToken(1).toString().equalsIgnoreCase("at")) {
				following.add(getTokenExpression(p,1));
				following.add(p.getListExpression(keywords));
			}
			return following;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_create = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
			myKeywords.add("in");
			myKeywords.add("with");
			XNExpression what = p.getListExpression(myKeywords);
			XNExpression with;
			XNExpression withWhat;
			if (p.lookToken(1).toString().equalsIgnoreCase("with")) {
				myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("in");
				with = getTokenExpression(p,1);
				withWhat = p.getListExpression(myKeywords);
				return Arrays.asList(new XNExpression[]{
						what,
						with,
						withWhat
				});
			} else {
				return Arrays.asList(new XNExpression[]{
						what
				});
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_delete = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
			return Arrays.asList(new XNExpression[]{
					p.getListExpression(myKeywords)
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_divide = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("by");
			myKeywords.add("rounding");
			XNExpression lvalue = p.getListExpression(myKeywords);
			XNExpression by = getTokenExpression(p,"by");
			XNExpression rvalue = p.getListExpression(keywords);
			Vector<XNExpression> following = new Vector<XNExpression>();
			if (
					p.lookToken(1).toString().equalsIgnoreCase("rounding") &&
					(
							p.lookToken(2).toString().equalsIgnoreCase("up") ||
							p.lookToken(2).toString().equalsIgnoreCase("down")
					)
			) {
				following.add(getTokenExpression(p,1));
				following.add(getTokenExpression(p,1));
			} else if (
					p.lookToken(1).toString().equalsIgnoreCase("rounding") &&
					(
							p.lookToken(2).toString().equalsIgnoreCase("to") ||
							p.lookToken(2).toString().equalsIgnoreCase("toward") ||
							p.lookToken(2).toString().equalsIgnoreCase("towards")
					) && (
							p.lookToken(3).toString().equalsIgnoreCase("zero") ||
							p.lookToken(3).toString().equalsIgnoreCase("infinity") ||
							p.lookToken(3).toString().equalsIgnoreCase("nearest") ||
							p.lookToken(3).toString().equalsIgnoreCase("even") ||
							p.lookToken(3).toString().equalsIgnoreCase("ceiling") ||
							p.lookToken(3).toString().equalsIgnoreCase("floor")
					)
			) {
				following.add(getTokenExpression(p,1));
				following.add(getTokenExpression(p,1));
				following.add(getTokenExpression(p,1));
			}
			List<XNExpression> ret = new Vector<XNExpression>();
			ret.add(lvalue);
			ret.add(by);
			ret.add(rvalue);
			for (XNExpression f : following) {
				ret.add(f);
			}
			return ret;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_get = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			return Arrays.asList(new XNExpression[]{
					p.getListExpression(keywords)
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_let = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("=");
			XNExpression lvalue = p.getListExpression(myKeywords);
			XNExpression eq = getTokenExpression(p,"=");
			XNExpression rvalue = p.getListExpression(keywords);
			return Arrays.asList(new XNExpression[]{
					lvalue,
					eq,
					rvalue
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_modulo = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("by");
			myKeywords.add("rounding");
			XNExpression lvalue = p.getListExpression(myKeywords);
			XNExpression by = getTokenExpression(p,"by");
			XNExpression rvalue = p.getListExpression(keywords);
			Vector<XNExpression> following = new Vector<XNExpression>();
			if (
					p.lookToken(1).toString().equalsIgnoreCase("rounding") &&
					(
							p.lookToken(2).toString().equalsIgnoreCase("up") ||
							p.lookToken(2).toString().equalsIgnoreCase("down")
					)
			) {
				following.add(getTokenExpression(p,1));
				following.add(getTokenExpression(p,1));
			} else if (
					p.lookToken(1).toString().equalsIgnoreCase("rounding") &&
					(
							p.lookToken(2).toString().equalsIgnoreCase("to") ||
							p.lookToken(2).toString().equalsIgnoreCase("toward") ||
							p.lookToken(2).toString().equalsIgnoreCase("towards")
					) && (
							p.lookToken(3).toString().equalsIgnoreCase("zero") ||
							p.lookToken(3).toString().equalsIgnoreCase("infinity") ||
							p.lookToken(3).toString().equalsIgnoreCase("nearest") ||
							p.lookToken(3).toString().equalsIgnoreCase("even") ||
							p.lookToken(3).toString().equalsIgnoreCase("ceiling") ||
							p.lookToken(3).toString().equalsIgnoreCase("floor")
					)
			) {
				following.add(getTokenExpression(p,1));
				following.add(getTokenExpression(p,1));
				following.add(getTokenExpression(p,1));
			}
			List<XNExpression> ret = new Vector<XNExpression>();
			ret.add(lvalue);
			ret.add(by);
			ret.add(rvalue);
			for (XNExpression f : following) {
				ret.add(f);
			}
			return ret;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_multiply = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("by");
			XNExpression lvalue = p.getListExpression(myKeywords);
			XNExpression by = getTokenExpression(p,"by");
			XNExpression rvalue = p.getListExpression(keywords);
			return Arrays.asList(new XNExpression[]{
					lvalue,
					by,
					rvalue
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_put = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("into");
			myKeywords.add("before");
			myKeywords.add("after");
			myKeywords.add("with");
			XNExpression what = p.getListExpression(myKeywords);
			if (p.lookPreposition(1) != null) {
				myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("with");
				XNExpression prep = getTokenExpression(p,1);
				XNExpression dest = p.getListExpression(myKeywords);
				if (p.lookToken(1).toString().equalsIgnoreCase("with")) {
					XNExpression with = getTokenExpression(p,1);
					XNExpression propertyName = getTokenExpression(p,1);
					if (getTokenFromTokenExpression(p, propertyName).kind != XNToken.ID) {
						throw new XNParseError("property name", getTokenFromTokenExpression(p, propertyName));
					}
					XNExpression propertyValue = p.getListExpression(keywords);
					return Arrays.asList(new XNExpression[]{
							what,
							prep,
							dest,
							with,
							propertyName,
							propertyValue
					});
				} else {
					return Arrays.asList(new XNExpression[]{
							what,
							prep,
							dest
					});
				}
			} else {
				return Arrays.asList(new XNExpression[]{
						what
				});
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_set = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			XNExpression propertyName;
			XNExpression of;
			XNExpression obj;
			XNExpression to;
			XNExpression val;
			if (p.lookToken(1).toString().equalsIgnoreCase("the") && p.lookModifier(2) != null && p.lookToken(3).kind == XNToken.ID) {
				getTokenExpression(p,1);
				p.getModifier();
				propertyName = getTokenExpression(p,1);
			} else if (p.lookToken(1).toString().equalsIgnoreCase("the") && p.lookToken(2).kind == XNToken.ID) {
				getTokenExpression(p,1);
				propertyName = getTokenExpression(p,1);
			} else if (p.lookModifier(1) != null && p.lookToken(2).kind == XNToken.ID) {
				p.getModifier();
				propertyName = getTokenExpression(p,1);
			} else if (p.lookToken(1).kind == XNToken.ID) {
				propertyName = getTokenExpression(p,1);
			} else {
				throw new XNParseError("property name", p.lookToken(1));
			}
			if (p.lookOfIn(1)) {
				of = getTokenExpression(p,1);
				HashSet<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
				myKeywords.add("to");
				obj = p.getListExpression(myKeywords);
			} else {
				of = createTokenExpression(p, "of");
				obj = new XNVariantSingletonDescriptor();
				((XNVariantSingletonDescriptor)obj).theToken = new XNToken(XNToken.ID, "the", p.getSource(), 0,0,0,0);
				((XNVariantSingletonDescriptor)obj).dtTokens = new XNToken[]{new XNToken(XNToken.ID, "interpreter", p.getSource(), 0,0,0,0)};
				((XNVariantSingletonDescriptor)obj).datatype = new XNDataType(new String[]{"interpreter"}, 0);
				((XNVariantSingletonDescriptor)obj).ofInToken = null;
				((XNVariantSingletonDescriptor)obj).parentVariant = null;
			}
			to = getTokenExpression(p,"to");
			val = p.getListExpression(keywords);
			return Arrays.asList(new XNExpression[]{
					propertyName,
					of,
					obj,
					to,
					val
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_subtract = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("from");
			XNExpression rvalue = p.getListExpression(myKeywords);
			XNExpression from = getTokenExpression(p,"from");
			XNExpression lvalue = p.getListExpression(keywords);
			return Arrays.asList(new XNExpression[]{
					rvalue,
					from,
					lvalue
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_beep = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			if (p.lookListExpression(1, keywords)) {
				XNExpression count = p.getListExpression(keywords);
				if (p.lookToken(1).toString().equalsIgnoreCase("times")) {
					XNExpression times = getTokenExpression(p,1);
					return Arrays.asList(new XNExpression[]{
							count,
							times
					});
				} else {
					return Arrays.asList(new XNExpression[]{
							count
					});
				}
			} else {
				return Arrays.asList(new XNExpression[0]);
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_close = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
			myKeywords.add("in");
			myKeywords.add("with");
			XNExpression what = p.getListExpression(myKeywords);
			if (p.lookToken(1).toString().equalsIgnoreCase("in") || p.lookToken(1).toString().equalsIgnoreCase("with")) {
				XNExpression with = getTokenExpression(p,1);
				XNExpression withWhat = p.getListExpression(keywords);
				return Arrays.asList(new XNExpression[]{
						what,
						with,
						withWhat
				});
			} else {
				return Arrays.asList(new XNExpression[]{
						what
				});
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_convert = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("from");
			myKeywords.add("to");
			XNExpression what = p.getListExpression(myKeywords);
			XNExpression from;
			String ff1;
			XNExpression fand;
			String ff2;
			if (p.lookToken(1).toString().equalsIgnoreCase("from")) {
				from = getTokenExpression(p,1);
				if (p.lookModifier(1) != null && p.lookToken(2).toString().equalsIgnoreCase("date")) {
					ff1 = p.getModifier().toString()+" date"; getTokenExpression(p,1);
				} else if (p.lookModifier(1) != null && p.lookToken(2).toString().equalsIgnoreCase("time")) {
					ff1 = p.getModifier().toString()+" time"; getTokenExpression(p,1);
				} else if (p.lookToken(1).toString().equalsIgnoreCase("date") || p.lookToken(1).toString().equalsIgnoreCase("time")) {
					ff1 = getTokenExpression(p,1).toString().toLowerCase();
				} else if (p.lookToken(1).toString().equalsIgnoreCase("seconds") || p.lookToken(1).toString().equalsIgnoreCase("secs")) {
					ff1 = getTokenExpression(p,1).toString().toLowerCase();
				} else if (p.lookToken(1).toString().equalsIgnoreCase("dateitems")) {
					ff1 = getTokenExpression(p,1).toString().toLowerCase();
				} else {
					throw new XNParseError("date, time, seconds, or dateitems", p.lookToken(1));
				}
				if (p.lookToken(1).toString().equalsIgnoreCase("and")) {
					fand = getTokenExpression(p,1);
					if (p.lookModifier(1) != null && p.lookToken(2).toString().equalsIgnoreCase("date")) {
						ff2 = p.getModifier().toString()+" date"; getTokenExpression(p,1);
					} else if (p.lookModifier(1) != null && p.lookToken(2).toString().equalsIgnoreCase("time")) {
						ff2 = p.getModifier().toString()+" time"; getTokenExpression(p,1);
					} else if (p.lookToken(1).toString().equalsIgnoreCase("date") || p.lookToken(1).toString().equalsIgnoreCase("time")) {
						ff2 = getTokenExpression(p,1).toString().toLowerCase();
					} else if (p.lookToken(1).toString().equalsIgnoreCase("seconds") || p.lookToken(1).toString().equalsIgnoreCase("secs")) {
						ff2 = getTokenExpression(p,1).toString().toLowerCase();
					} else if (p.lookToken(1).toString().equalsIgnoreCase("dateitems")) {
						ff2 = getTokenExpression(p,1).toString().toLowerCase();
					} else {
						throw new XNParseError("date, time, seconds, or dateitems", p.lookToken(1));
					}
				} else {
					fand = new XNEmptyExpression(p.getSource(), 0, 0);
					ff2 = "";
				}
			} else {
				from = new XNEmptyExpression(p.getSource(), 0, 0);
				ff1 = "";
				fand = new XNEmptyExpression(p.getSource(), 0, 0);
				ff2 = "";
			}
			XNExpression to;
			String tf1;
			XNExpression tand;
			String tf2;
			if (p.lookToken(1).toString().equalsIgnoreCase("to")) {
				to = getTokenExpression(p,1);
				if (p.lookModifier(1) != null && p.lookToken(2).toString().equalsIgnoreCase("date")) {
					tf1 = p.getModifier().toString()+" date"; getTokenExpression(p,1);
				} else if (p.lookModifier(1) != null && p.lookToken(2).toString().equalsIgnoreCase("time")) {
					tf1 = p.getModifier().toString()+" time"; getTokenExpression(p,1);
				} else if (p.lookToken(1).toString().equalsIgnoreCase("date") || p.lookToken(1).toString().equalsIgnoreCase("time")) {
					tf1 = getTokenExpression(p,1).toString().toLowerCase();
				} else if (p.lookToken(1).toString().equalsIgnoreCase("seconds") || p.lookToken(1).toString().equalsIgnoreCase("secs")) {
					tf1 = getTokenExpression(p,1).toString().toLowerCase();
				} else if (p.lookToken(1).toString().equalsIgnoreCase("dateitems")) {
					tf1 = getTokenExpression(p,1).toString().toLowerCase();
				} else {
					throw new XNParseError("date, time, seconds, or dateitems", p.lookToken(1));
				}
				if (p.lookToken(1).toString().equalsIgnoreCase("and")) {
					tand = getTokenExpression(p,1);
					if (p.lookModifier(1) != null && p.lookToken(2).toString().equalsIgnoreCase("date")) {
						tf2 = p.getModifier().toString()+" date"; getTokenExpression(p,1);
					} else if (p.lookModifier(1) != null && p.lookToken(2).toString().equalsIgnoreCase("time")) {
						tf2 = p.getModifier().toString()+" time"; getTokenExpression(p,1);
					} else if (p.lookToken(1).toString().equalsIgnoreCase("date") || p.lookToken(1).toString().equalsIgnoreCase("time")) {
						tf2 = getTokenExpression(p,1).toString().toLowerCase();
					} else if (p.lookToken(1).toString().equalsIgnoreCase("seconds") || p.lookToken(1).toString().equalsIgnoreCase("secs")) {
						tf2 = getTokenExpression(p,1).toString().toLowerCase();
					} else if (p.lookToken(1).toString().equalsIgnoreCase("dateitems")) {
						tf2 = getTokenExpression(p,1).toString().toLowerCase();
					} else {
						throw new XNParseError("date, time, seconds, or dateitems", p.lookToken(1));
					}
				} else {
					tand = new XNEmptyExpression(p.getSource(), 0, 0);
					tf2 = "";
				}
			} else {
				throw new XNParseError("to", p.lookToken(1));
			}
			return Arrays.asList(new XNExpression[]{
					what,
					from,
					createTokenExpression(p, ff1),
					fand,
					createTokenExpression(p, ff2),
					to,
					createTokenExpression(p, tf1),
					tand,
					createTokenExpression(p, tf2)
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_open = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
			myKeywords.add("in");
			myKeywords.add("with");
			myKeywords.add("as");
			XNExpression what = p.getListExpression(myKeywords);
			if (p.lookToken(1).toString().equalsIgnoreCase("in") || p.lookToken(1).toString().equalsIgnoreCase("with") || p.lookToken(1).toString().equalsIgnoreCase("as")) {
				XNExpression with = getTokenExpression(p,1);
				XNExpression withWhat = p.getListExpression(keywords);
				return Arrays.asList(new XNExpression[]{
						what,
						with,
						withWhat
				});
			} else {
				return Arrays.asList(new XNExpression[]{
						what
				});
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_read = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
			myKeywords.add("at");
			myKeywords.add("for");
			myKeywords.add("until");
			XNExpression from = getTokenExpression(p,"from");
			XNExpression file = p.getListExpression(myKeywords);
			XNExpression at;
			XNExpression pos;
			XNExpression fr;
			XNExpression len;
			XNExpression ut;
			XNExpression stop;
			if (p.lookToken(1).toString().equalsIgnoreCase("at")) {
				myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("for");
				myKeywords.add("until");
				at = getTokenExpression(p,1);
				pos = p.getListExpression(myKeywords);
			} else {
				at = new XNEmptyExpression(p.getSource(), 0, 0);
				pos = new XNEmptyExpression(p.getSource(), 0, 0);
			}
			if (p.lookToken(1).toString().equalsIgnoreCase("for")) {
				myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("until");
				fr = getTokenExpression(p,1);
				len = p.getListExpression(myKeywords);
			} else {
				fr = new XNEmptyExpression(p.getSource(), 0, 0);
				len = new XNEmptyExpression(p.getSource(), 0, 0);
			}
			if (p.lookToken(1).toString().equalsIgnoreCase("until")) {
				ut = getTokenExpression(p,1);
				stop = p.getListExpression(keywords);
			} else {
				ut = new XNEmptyExpression(p.getSource(), 0, 0);
				stop = new XNEmptyExpression(p.getSource(), 0, 0);
			}
			return Arrays.asList(new XNExpression[]{
					from,
					file,
					at,
					pos,
					fr,
					len,
					ut,
					stop
			});
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_sort = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
			myKeywords.add("ascending");
			myKeywords.add("descending");
			myKeywords.add("text");
			myKeywords.add("numeric");
			myKeywords.add("datetime");
			myKeywords.add("international");
			myKeywords.add("by");
			XNExpression what = p.getListExpression(myKeywords);
			XNExpression dir;
			if (
				p.lookToken(1).toString().equalsIgnoreCase("ascending") |
				p.lookToken(1).toString().equalsIgnoreCase("descending")
			) {
				dir = getTokenExpression(p,1);
			} else {
				dir = createTokenExpression(p, "ascending");
			}
			XNExpression type;
			if (
				p.lookToken(1).toString().equalsIgnoreCase("text") |
				p.lookToken(1).toString().equalsIgnoreCase("numeric") |
				p.lookToken(1).toString().equalsIgnoreCase("datetime") |
				p.lookToken(1).toString().equalsIgnoreCase("international")
			) {
				type = getTokenExpression(p,1);
			} else {
				type = createTokenExpression(p, "text");
			}
			if (p.lookToken(1).toString().equalsIgnoreCase("by")) {
				XNExpression by = getTokenExpression(p,1);
				XNExpression crit = p.getListExpression(keywords);
				return Arrays.asList(new XNExpression[]{
						what,
						dir,
						type,
						by,
						crit
				});
			} else {
				return Arrays.asList(new XNExpression[]{
						what,
						dir,
						type
				});
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_truncate = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
			myKeywords.add("at");
			XNExpression file = p.getListExpression(myKeywords);
			if (p.lookToken(1).toString().equalsIgnoreCase("at")) {
				XNExpression at = getTokenExpression(p,1);
				XNExpression pos = p.getListExpression(keywords);
				return Arrays.asList(new XNExpression[]{
						file,
						at,
						pos
				});
			} else {
				return Arrays.asList(new XNExpression[]{
						file
				});
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_wait = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			if (p.lookToken(1).toString().equalsIgnoreCase("until")) {
				XNExpression ut = getTokenExpression(p,1);
				XNExpression cond = p.getListExpression(keywords);
				return Arrays.asList(new XNExpression[]{
						ut,
						cond
				});
			} else if (p.lookToken(1).toString().equalsIgnoreCase("while")) {
				XNExpression wt = getTokenExpression(p,1);
				XNExpression cond = p.getListExpression(keywords);
				return Arrays.asList(new XNExpression[]{
						wt,
						cond
				});
			} else {
				XNExpression ft;
				if (p.lookToken(1).toString().equalsIgnoreCase("for")) {
					ft = getTokenExpression(p, 1);
				} else {
					ft = createTokenExpression(p, "for");
				}
				XNExpression count = p.getListExpression(keywords);
				XNExpression unit;
				if (
					p.lookToken(1).toString().equalsIgnoreCase("nanoseconds") |
					p.lookToken(1).toString().equalsIgnoreCase("nanosecond") |
					p.lookToken(1).toString().equalsIgnoreCase("nanosecs") |
					p.lookToken(1).toString().equalsIgnoreCase("nanosec") |
					p.lookToken(1).toString().equalsIgnoreCase("nanos") |
					p.lookToken(1).toString().equalsIgnoreCase("nano") |
					p.lookToken(1).toString().equalsIgnoreCase("microseconds") |
					p.lookToken(1).toString().equalsIgnoreCase("microsecond") |
					p.lookToken(1).toString().equalsIgnoreCase("microsecs") |
					p.lookToken(1).toString().equalsIgnoreCase("microsec") |
					p.lookToken(1).toString().equalsIgnoreCase("micros") |
					p.lookToken(1).toString().equalsIgnoreCase("micro") |
					p.lookToken(1).toString().equalsIgnoreCase("milliseconds") |
					p.lookToken(1).toString().equalsIgnoreCase("millisecond") |
					p.lookToken(1).toString().equalsIgnoreCase("millisecs") |
					p.lookToken(1).toString().equalsIgnoreCase("millisec") |
					p.lookToken(1).toString().equalsIgnoreCase("millis") |
					p.lookToken(1).toString().equalsIgnoreCase("milli") |
					p.lookToken(1).toString().equalsIgnoreCase("seconds") |
					p.lookToken(1).toString().equalsIgnoreCase("second") |
					p.lookToken(1).toString().equalsIgnoreCase("secs") |
					p.lookToken(1).toString().equalsIgnoreCase("sec") |
					p.lookToken(1).toString().equalsIgnoreCase("ticks") |
					p.lookToken(1).toString().equalsIgnoreCase("tick") |
					p.lookToken(1).toString().equalsIgnoreCase("minutes") |
					p.lookToken(1).toString().equalsIgnoreCase("minute") |
					p.lookToken(1).toString().equalsIgnoreCase("mins") |
					p.lookToken(1).toString().equalsIgnoreCase("min") |
					p.lookToken(1).toString().equalsIgnoreCase("hours") |
					p.lookToken(1).toString().equalsIgnoreCase("hour")
				) {
					unit = getTokenExpression(p, 1);
				} else {
					unit = createTokenExpression(p, "ticks");
				}
				return Arrays.asList(new XNExpression[]{
						ft,
						count,
						unit
				});
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_write = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("to");
			XNExpression stuff = p.getListExpression(myKeywords);
			XNExpression to = getTokenExpression(p,"to");
			myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add(XNParser.ALLOW_BARE_SM_DESCRIPTORS_TAG);
			myKeywords.add("at");
			XNExpression file = p.getListExpression(myKeywords);
			if (p.lookToken(1).toString().equalsIgnoreCase("at")) {
				XNExpression at = getTokenExpression(p,1);
				XNExpression pos = p.getListExpression(keywords);
				return Arrays.asList(new XNExpression[]{
						stuff,
						to,
						file,
						at,
						pos
				});
			} else {
				return Arrays.asList(new XNExpression[]{
						stuff,
						to,
						file
				});
			}
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final long MS_PER_DAY = 1000L * 60L * 60L * 24L;
	private static long c_convert_0(long current, List<XNDateFormat> formats, String s, ParsePosition pos) {
		if (formats.isEmpty()) return current;
		for (XNDateFormat fmt : formats) {
			Date parsedDate = fmt.toJavaDateFormat().parse(s,pos);
			if (parsedDate != null) {
				if (fmt.name().toLowerCase().endsWith("time")) {
					return ((current / MS_PER_DAY) * MS_PER_DAY) + (parsedDate.getTime() % MS_PER_DAY);
				} else {
					return parsedDate.getTime();
				}
			}
		}
		throw new XNScriptError("Invalid date");
	}

	private static final Command c_add = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 3) {
				throw new XNScriptError("Can't understand arguments to add");
			}
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant bv = interp.evaluateExpression(parameters.get(0)).asPrimitive(ctx);
			XOMVariant av = interp.evaluateExpression(parameters.get(2)).asContainer(ctx, false);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.add(ac, bc, mc, mp);
				av.putIntoContents(ctx, rc);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.add(an, bn, mc, mp);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.add(an, bn, mc, mp);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.add(ac, bc, mc, mp);
				av.putIntoContents(ctx, rc);
			}
			else {
				throw new XOMMorphError("number");
			}
			return null;
		}
	};

	private static final Command c_answer = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			String kind;
			String prompt;
			List<XOMVariant> voptions;
			int x, y;
			if (parameters == null || parameters.isEmpty()) {
				kind = "normal";
				prompt = "";
				voptions = new Vector<XOMVariant>();
				x = 0; y = 0;
			}
			else if (parameters.size() == 1) {
				kind = "normal";
				prompt = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
				voptions = new Vector<XOMVariant>();
				x = 0; y = 0;
			}
			else if (parameters.size() == 2) {
				kind = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
				prompt = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
				voptions = new Vector<XOMVariant>();
				x = 0; y = 0;
			}
			else {
				kind = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
				prompt = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
				voptions = new Vector<XOMVariant>();
				for (int i = 2; i < parameters.size(); i++) {
					voptions.add(interp.evaluateExpression(parameters.get(i)).asPrimitive(ctx));
				}
				if (voptions.get(0).toTextString(ctx).equalsIgnoreCase("with")) {
					voptions.remove(0);
				}
				x = 0; y = 0;
				if (voptions.size() >= 2 && voptions.get(voptions.size()-2).toTextString(ctx).equalsIgnoreCase("at")) {
					voptions.remove(voptions.size()-2);
					XOMVariant loc = voptions.remove(voptions.size()-1);
					XOMPoint p = XOMPointType.instance.makeInstanceFrom(ctx, loc);
					x = p.x().intValue();
					y = p.y().intValue();
				}
			}
			if (kind.equalsIgnoreCase("list")) {
				List<String> options = new Vector<String>();
				for (XOMVariant w : voptions) {
					for (XOMVariant v : w.toPrimitiveList(ctx)) {
						options.add(v.toTextString(ctx));
					}
				}
				String s = ctx.getUI().answerList(prompt, options.toArray(new String[0]), x, y);
				ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(s));
			}
			else if (kind.equalsIgnoreCase("file")) {
				List<String> options = new Vector<String>();
				for (XOMVariant v : voptions) {
					options.add(v.toTextString(ctx));
				}
				File f = ctx.getUI().answerFile(prompt, options.toArray(new String[0]), x, y);
				if (f == null) {
					ctx.getVariableMap("it").setVariable(ctx, "it", XOMString.EMPTY_STRING);
					return new XOMString("Cancel");
				} else {
					ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(f.getAbsolutePath()));
					return new XOMString("OK");
				}
			}
			else if (kind.equalsIgnoreCase("folder") || kind.equalsIgnoreCase("directory")) {
				File f = ctx.getUI().answerFolder(prompt, x, y);
				if (f == null) {
					ctx.getVariableMap("it").setVariable(ctx, "it", XOMString.EMPTY_STRING);
					return new XOMString("Cancel");
				} else {
					ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(f.getAbsolutePath()));
					return new XOMString("OK");
				}
			}
			else if (kind.equalsIgnoreCase("disk") || kind.equalsIgnoreCase("volume")) {
				File f = ctx.getUI().answerDisk(prompt, x, y);
				if (f == null) {
					ctx.getVariableMap("it").setVariable(ctx, "it", XOMString.EMPTY_STRING);
					return new XOMString("Cancel");
				} else {
					ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(f.getAbsolutePath()));
					return new XOMString("OK");
				}
			}
			else {
				List<String> options = new Vector<String>();
				for (XOMVariant v : voptions) {
					options.add(v.toTextString(ctx));
				}
				String s = ctx.getUI().answer(prompt, options.toArray(new String[0]), x, y);
				ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(s));
			}
			return null;
		}
	};

	private static final Command c_ask = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			String kind;
			String prompt;
			String text;
			int x, y;
			if (parameters == null || parameters.isEmpty()) {
				kind = "normal";
				prompt = "";
				text = "";
				x = 0; y = 0;
			}
			else if (parameters.size() == 1) {
				kind = "normal";
				prompt = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
				text = "";
				x = 0; y = 0;
			}
			else if (parameters.size() == 2) {
				kind = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
				prompt = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
				text = "";
				x = 0; y = 0;
			}
			else {
				kind = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
				prompt = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
				List<XOMVariant> tmp = new Vector<XOMVariant>();
				for (int i = 2; i < parameters.size(); i++) {
					tmp.add(interp.evaluateExpression(parameters.get(i)).asPrimitive(ctx));
				}
				if (tmp.size() >= 2 && tmp.get(0).toTextString(ctx).equalsIgnoreCase("with")) {
					text = tmp.remove(1).toTextString(ctx);
					tmp.remove(0);
				} else {
					text = "";
				}
				if (tmp.size() >= 2 && tmp.get(0).toTextString(ctx).equalsIgnoreCase("at")) {
					XOMPoint p = XOMPointType.instance.makeInstanceFrom(ctx, tmp.remove(1));
					x = p.x().intValue();
					y = p.y().intValue();
					tmp.remove(0);
				} else {
					x = 0;
					y = 0;
				}
			}
			if (kind.equalsIgnoreCase("password")) {
				String s = ctx.getUI().askPassword(prompt, text, x, y);
				if (s == null) {
					ctx.getVariableMap("it").setVariable(ctx, "it", XOMString.EMPTY_STRING);
					return new XOMString("Cancel");
				} else {
					s = Long.toString(AtkinsonHash.hash(s) & 0xFFFFFFFFL);
					ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(s));
					return new XOMString("OK");
				}
			}
			else if (kind.equalsIgnoreCase("password clear")) {
				String s = ctx.getUI().askPassword(prompt, text, x, y);
				if (s == null) {
					ctx.getVariableMap("it").setVariable(ctx, "it", XOMString.EMPTY_STRING);
					return new XOMString("Cancel");
				} else {
					ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(s));
					return new XOMString("OK");
				}
			}
			else if (kind.equalsIgnoreCase("file")) {
				File f = ctx.getUI().askFile(prompt, text, x, y);
				if (f == null) {
					ctx.getVariableMap("it").setVariable(ctx, "it", XOMString.EMPTY_STRING);
					return new XOMString("Cancel");
				} else {
					ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(f.getAbsolutePath()));
					return new XOMString("OK");
				}
			}
			else if (kind.equalsIgnoreCase("folder") || kind.equalsIgnoreCase("directory")) {
				File f = ctx.getUI().askFolder(prompt, text, x, y);
				if (f == null) {
					ctx.getVariableMap("it").setVariable(ctx, "it", XOMString.EMPTY_STRING);
					return new XOMString("Cancel");
				} else {
					ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(f.getAbsolutePath()));
					return new XOMString("OK");
				}
			}
			else {
				String s = ctx.getUI().ask(prompt, text, x, y);
				if (s == null) {
					ctx.getVariableMap("it").setVariable(ctx, "it", XOMString.EMPTY_STRING);
					return new XOMString("Cancel");
				} else {
					ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(s));
					return new XOMString("OK");
				}
			}
		}
	};

	private static final Command c_create = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.isEmpty()) throw new XNScriptError("Can't understand arguments to create");
			
			XNExpression what = parameters.get(0);
			
			if (what instanceof XNNewExpression) {
				XNDataType dataTypeObj = ((XNNewExpression)what).datatype;
				String dataTypeStr = dataTypeObj.toNameString();
				XOMDataType<? extends XOMVariant> dataType = ctx.getDataType(dataTypeStr);
				XNExpression parentExpression = ((XNNewExpression)what).parentVariant;
				XOMVariant parent = (parentExpression == null) ? null : interp.evaluateExpression(parentExpression);
				XOMVariant thing;
				if (parent != null) {
					thing = dataType.createChildVariant(ctx, parent);
				} else {
					thing = dataType.createInstance(ctx);
				}
				if (ctx.getVariableMap("it").getVariable(ctx, "it") == null)
					ctx.getVariableMap("it").declareVariable(ctx, "it", dataType, thing);
				else
					ctx.getVariableMap("it").setVariable(ctx, "it", thing);
				return null;
			}
			
			if (!(what instanceof XNVariantDescriptor)) {
				String s = interp.evaluateExpression(what).toTextString(ctx);
				XNLexer lex = new XNLexer(s, new StringReader(s));
				XNParser par = new XNParser(ctx, lex);
				what = par.getListExpression(null);
				if ((!par.getToken().isEOF()) || (!(what instanceof XNVariantDescriptor))) {
					throw new XNScriptError("Can't understand arguments to create");
				}
			}
			
			XNVariantDescriptor expr = (XNVariantDescriptor)what;
			XNDataType dataTypeObj = ((XNVariantDescriptor)expr).datatype;
			String dataTypeStr = dataTypeObj.toNameString();
			XOMDataType<? extends XOMVariant> dataType = ctx.getDataType(dataTypeStr);
			XNExpression parentExpression = ((XNVariantDescriptor)expr).parentVariant;
			XOMVariant parent = (parentExpression == null) ? null : interp.evaluateExpression(parentExpression);
			
			if (parameters.size() < 3) {
				if (expr instanceof XNVariantIdDescriptor) {
					XNExpression idExpression = ((XNVariantIdDescriptor)expr).id;
					XOMVariant idVar = interp.evaluateExpression(idExpression).asPrimitive(ctx);
					XOMInteger idInt = XOMIntegerType.instance.makeInstanceFrom(ctx, idVar, true);
					if (parent != null) {
						dataType.createChildVariantByID(ctx, parent, idInt.toInt());
					} else {
						dataType.createInstanceByID(ctx, idInt.toInt());
					}
				} else if (expr instanceof XNVariantIndexNameDescriptor) {
					XNExpression startExpr = ((XNVariantIndexNameDescriptor)expr).start;
					XNExpression endExpr = ((XNVariantIndexNameDescriptor)expr).end;
					if (startExpr != null && endExpr != null) {
						int start = XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(startExpr).asPrimitive(ctx), true).toInt();
						int end = XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(endExpr).asPrimitive(ctx), true).toInt();
						if (parent != null) {
							dataType.createChildVariantByIndex(ctx, parent, start, end);
						} else {
							dataType.createInstanceByIndex(ctx, start, end);
						}
					} else if (startExpr != null) {
						XOMVariant idxNameVar = interp.evaluateExpression(startExpr).asPrimitive(ctx);
						if (!idxNameVar.toTextString(ctx).equals("") && XOMIntegerType.instance.canMakeInstanceFrom(ctx, idxNameVar, true)) {
							int index = XOMIntegerType.instance.makeInstanceFrom(ctx, idxNameVar, true).toInt();
							if (parent != null) {
								dataType.createChildVariantByIndex(ctx, parent, index);
							} else {
								dataType.createInstanceByIndex(ctx, index);
							}
						} else {
							String name = idxNameVar.toTextString(ctx);
							if (parent != null) {
								dataType.createChildVariantByName(ctx, parent, name);
							} else {
								dataType.createInstanceByName(ctx, name);
							}
						}
					} else if (endExpr != null) {
						XOMVariant idxNameVar = interp.evaluateExpression(endExpr).asPrimitive(ctx);
						if (!idxNameVar.toTextString(ctx).equals("") && XOMIntegerType.instance.canMakeInstanceFrom(ctx, idxNameVar, true)) {
							int index = XOMIntegerType.instance.makeInstanceFrom(ctx, idxNameVar, true).toInt();
							if (parent != null) {
								dataType.createChildVariantByIndex(ctx, parent, index);
							} else {
								dataType.createInstanceByIndex(ctx, index);
							}
						} else {
							String name = idxNameVar.toTextString(ctx);
							if (parent != null) {
								dataType.createChildVariantByName(ctx, parent, name);
							} else {
								dataType.createInstanceByName(ctx, name);
							}
						}
					} else {
						throw new XNScriptError("Can't understand arguments to create");
					}
				} else if (expr instanceof XNVariantOrdinalDescriptor) {
					XNToken startOrdinal = ((XNVariantOrdinalDescriptor)expr).startOrdinal;
					XNToken endOrdinal = ((XNVariantOrdinalDescriptor)expr).endOrdinal;
					if (startOrdinal != null && endOrdinal != null) {
						int start = ctx.getOrdinal(startOrdinal.image);
						int end = ctx.getOrdinal(endOrdinal.image);
						if (parent != null) {
							dataType.createChildVariantByIndex(ctx, parent, start, end);
						} else {
							dataType.createInstanceByIndex(ctx, start, end);
						}
					} else if (startOrdinal != null) {
						int start = ctx.getOrdinal(startOrdinal.image);
						if (parent != null) {
							dataType.createChildVariantByIndex(ctx, parent, start);
						} else {
							dataType.createInstanceByIndex(ctx, start);
						}
					} else if (endOrdinal != null) {
						int end = ctx.getOrdinal(endOrdinal.image);
						if (parent != null) {
							dataType.createChildVariantByIndex(ctx, parent, end);
						} else {
							dataType.createInstanceByIndex(ctx, end);
						}
					} else {
						throw new XNScriptError("Can't understand arguments to create");
					}
				} else {
					throw new XNScriptError("Can't understand arguments to create");
				}
			} else {
				XOMVariant content = interp.evaluateExpression(parameters.get(2));
				if (expr instanceof XNVariantIdDescriptor) {
					XNExpression idExpression = ((XNVariantIdDescriptor)expr).id;
					XOMVariant idVar = interp.evaluateExpression(idExpression).asPrimitive(ctx);
					XOMInteger idInt = XOMIntegerType.instance.makeInstanceFrom(ctx, idVar, true);
					if (parent != null) {
						dataType.createChildVariantByIDWith(ctx, parent, idInt.toInt(), content);
					} else {
						dataType.createInstanceByIDWith(ctx, idInt.toInt(), content);
					}
				} else if (expr instanceof XNVariantIndexNameDescriptor) {
					XNExpression startExpr = ((XNVariantIndexNameDescriptor)expr).start;
					XNExpression endExpr = ((XNVariantIndexNameDescriptor)expr).end;
					if (startExpr != null && endExpr != null) {
						int start = XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(startExpr).asPrimitive(ctx), true).toInt();
						int end = XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(endExpr).asPrimitive(ctx), true).toInt();
						if (parent != null) {
							dataType.createChildVariantByIndexWith(ctx, parent, start, end, content);
						} else {
							dataType.createInstanceByIndexWith(ctx, start, end, content);
						}
					} else if (startExpr != null) {
						XOMVariant idxNameVar = interp.evaluateExpression(startExpr).asPrimitive(ctx);
						if (!idxNameVar.toTextString(ctx).equals("") && XOMIntegerType.instance.canMakeInstanceFrom(ctx, idxNameVar, true)) {
							int index = XOMIntegerType.instance.makeInstanceFrom(ctx, idxNameVar, true).toInt();
							if (parent != null) {
								dataType.createChildVariantByIndexWith(ctx, parent, index, content);
							} else {
								dataType.createInstanceByIndexWith(ctx, index, content);
							}
						} else {
							String name = idxNameVar.toTextString(ctx);
							if (parent != null) {
								dataType.createChildVariantByNameWith(ctx, parent, name, content);
							} else {
								dataType.createInstanceByNameWith(ctx, name, content);
							}
						}
					} else if (endExpr != null) {
						XOMVariant idxNameVar = interp.evaluateExpression(endExpr).asPrimitive(ctx);
						if (!idxNameVar.toTextString(ctx).equals("") && XOMIntegerType.instance.canMakeInstanceFrom(ctx, idxNameVar, true)) {
							int index = XOMIntegerType.instance.makeInstanceFrom(ctx, idxNameVar, true).toInt();
							if (parent != null) {
								dataType.createChildVariantByIndexWith(ctx, parent, index, content);
							} else {
								dataType.createInstanceByIndexWith(ctx, index, content);
							}
						} else {
							String name = idxNameVar.toTextString(ctx);
							if (parent != null) {
								dataType.createChildVariantByNameWith(ctx, parent, name, content);
							} else {
								dataType.createInstanceByNameWith(ctx, name, content);
							}
						}
					} else {
						throw new XNScriptError("Can't understand arguments to create");
					}
				} else if (expr instanceof XNVariantOrdinalDescriptor) {
					XNToken startOrdinal = ((XNVariantOrdinalDescriptor)expr).startOrdinal;
					XNToken endOrdinal = ((XNVariantOrdinalDescriptor)expr).endOrdinal;
					if (startOrdinal != null && endOrdinal != null) {
						int start = ctx.getOrdinal(startOrdinal.image);
						int end = ctx.getOrdinal(endOrdinal.image);
						if (parent != null) {
							dataType.createChildVariantByIndexWith(ctx, parent, start, end, content);
						} else {
							dataType.createInstanceByIndexWith(ctx, start, end, content);
						}
					} else if (startOrdinal != null) {
						int start = ctx.getOrdinal(startOrdinal.image);
						if (parent != null) {
							dataType.createChildVariantByIndexWith(ctx, parent, start, content);
						} else {
							dataType.createInstanceByIndexWith(ctx, start, content);
						}
					} else if (endOrdinal != null) {
						int end = ctx.getOrdinal(endOrdinal.image);
						if (parent != null) {
							dataType.createChildVariantByIndexWith(ctx, parent, end, content);
						} else {
							dataType.createInstanceByIndexWith(ctx, end, content);
						}
					} else {
						throw new XNScriptError("Can't understand arguments to create");
					}
				} else {
					throw new XNScriptError("Can't understand arguments to create");
				}
			}
			return null;
		}
	};

	private static final Command c_delete = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.isEmpty()) throw new XNScriptError("Can't understand arguments to delete");
			XOMVariant v = interp.evaluateExpression(parameters.get(0)).asValue(ctx);
			if (v.canDelete(ctx)) v.delete(ctx);
			else {
				v = interp.evaluateExpressionString(v.toTextString(ctx)).asValue(ctx);
				if (v.canDelete(ctx)) v.delete(ctx);
				else {
					throw new XNScriptError("Can't delete this");
				}
			}
			return null;
		}
	};

	private static final Command c_divide = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 3) {
				throw new XNScriptError("Can't understand arguments to divide");
			}
			RoundingMode rm = null;
			if (parameters.size() >= 5) {
				String s = interp.evaluateExpression(parameters.get(4)).toTextString(ctx);
				if (s.equalsIgnoreCase("up")) rm = RoundingMode.UP;
				else if (s.equalsIgnoreCase("down")) rm = RoundingMode.DOWN;
				else if (parameters.size() >= 6) {
					s = interp.evaluateExpression(parameters.get(5)).toTextString(ctx);
					if (s.equalsIgnoreCase("zero")) rm = RoundingMode.DOWN;
					else if (s.equalsIgnoreCase("infinity")) rm = RoundingMode.UP;
					else if (s.equalsIgnoreCase("nearest")) rm = RoundingMode.HALF_UP;
					else if (s.equalsIgnoreCase("even")) rm = RoundingMode.HALF_EVEN;
					else if (s.equalsIgnoreCase("ceiling")) rm = RoundingMode.CEILING;
					else if (s.equalsIgnoreCase("floor")) rm = RoundingMode.FLOOR;
				}
			}
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant av = interp.evaluateExpression(parameters.get(0)).asContainer(ctx, false);
			XOMVariant bv = interp.evaluateExpression(parameters.get(2)).asPrimitive(ctx);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.divide(ac, bc, mc, mp);
				if (rm != null) rc = rc.round(rm);
				av.putIntoContents(ctx, rc);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.divide(an, bn, mc, mp);
				if (rm != null) rn = rn.round(rm);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.divide(an, bn, mc, mp);
				if (rm != null) rn = rn.round(rm);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.divide(ac, bc, mc, mp);
				if (rm != null) rc = rc.round(rm);
				av.putIntoContents(ctx, rc);
			}
			else {
				throw new XOMMorphError("number");
			}
			return null;
		}
	};

	private static final Command c_get = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.isEmpty()) throw new XNScriptError("Can't understand arguments to get");
			ctx.getVariableMap("it").setVariable(ctx, "it", interp.evaluateExpression(parameters.get(0)).asPrimitive(ctx));
			return null;
		}
	};

	private static final Command c_let = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 3) throw new XNScriptError("Can't understand arguments to let");
			XOMVariant dest = interp.evaluateExpression(parameters.get(0)).asContainer(ctx, false);
			XOMVariant what = interp.evaluateExpression(parameters.get(2)).asPrimitive(ctx);
			dest.putIntoContents(ctx, what);
			return null;
		}
	};

	private static final Command c_modulo = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 3) {
				throw new XNScriptError("Can't understand arguments to modulo");
			}
			RoundingMode rm = RoundingMode.FLOOR;
			if (parameters.size() >= 5) {
				String s = interp.evaluateExpression(parameters.get(4)).toTextString(ctx);
				if (s.equalsIgnoreCase("up")) rm = RoundingMode.UP;
				else if (s.equalsIgnoreCase("down")) rm = RoundingMode.DOWN;
				else if (parameters.size() >= 6) {
					s = interp.evaluateExpression(parameters.get(5)).toTextString(ctx);
					if (s.equalsIgnoreCase("zero")) rm = RoundingMode.DOWN;
					else if (s.equalsIgnoreCase("infinity")) rm = RoundingMode.UP;
					else if (s.equalsIgnoreCase("nearest")) rm = RoundingMode.HALF_UP;
					else if (s.equalsIgnoreCase("even")) rm = RoundingMode.HALF_EVEN;
					else if (s.equalsIgnoreCase("ceiling")) rm = RoundingMode.CEILING;
					else if (s.equalsIgnoreCase("floor")) rm = RoundingMode.FLOOR;
				}
			}
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant av = interp.evaluateExpression(parameters.get(0)).asContainer(ctx, false);
			XOMVariant bv = interp.evaluateExpression(parameters.get(2)).asPrimitive(ctx);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).round(rm), mc, mp), mc, mp);
				av.putIntoContents(ctx, rc);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).round(rm), mc, mp), mc, mp);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).round(rm), mc, mp), mc, mp);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).round(rm), mc, mp), mc, mp);
				av.putIntoContents(ctx, rc);
			}
			else {
				throw new XOMMorphError("number");
			}
			return null;
		}
	};

	private static final Command c_multiply = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 3) {
				throw new XNScriptError("Can't understand arguments to multiply");
			}
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant av = interp.evaluateExpression(parameters.get(0)).asContainer(ctx, false);
			XOMVariant bv = interp.evaluateExpression(parameters.get(2)).asPrimitive(ctx);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.multiply(ac, bc, mc, mp);
				av.putIntoContents(ctx, rc);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.multiply(an, bn, mc, mp);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.multiply(an, bn, mc, mp);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.multiply(ac, bc, mc, mp);
				av.putIntoContents(ctx, rc);
			}
			else {
				throw new XOMMorphError("number");
			}
			return null;
		}
	};

	private static final Command c_put = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.isEmpty()) throw new XNScriptError("Can't understand arguments to put");
			if (parameters.size() < 3) {
				ctx.getUI().put(interp.evaluateExpression(parameters.get(0)).toTextString(ctx));
			} else if (parameters.size() < 6) {
				XOMVariant what = interp.evaluateExpression(parameters.get(0)).asPrimitive(ctx);
				String prep = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
				XOMVariant dest = interp.evaluateExpression(parameters.get(2)).asContainer(ctx, false);
				if (prep.equalsIgnoreCase("before")) dest.putBeforeContents(ctx, what);
				else if (prep.equalsIgnoreCase("after")) dest.putAfterContents(ctx, what);
				else dest.putIntoContents(ctx, what);
			} else {
				XOMVariant what = interp.evaluateExpression(parameters.get(0)).asPrimitive(ctx);
				String prep = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
				XOMVariant dest = interp.evaluateExpression(parameters.get(2)).asContainer(ctx, false);
				String prop = interp.evaluateExpression(parameters.get(4)).toTextString(ctx);
				XOMVariant pval = interp.evaluateExpression(parameters.get(5)).asValue(ctx);
				if (prep.equalsIgnoreCase("before")) dest.putBeforeContents(ctx, what, prop, pval);
				else if (prep.equalsIgnoreCase("after")) dest.putAfterContents(ctx, what, prop, pval);
				else dest.putIntoContents(ctx, what, prop, pval);
			}
			return null;
		}
	};

	private static final Command c_set = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 5) throw new XNScriptError("Can't understand arguments to set");
			String property = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
			XOMVariant object = interp.evaluateExpression(parameters.get(2)).asValue(ctx);
			XOMVariant value = interp.evaluateExpression(parameters.get(4)).asValue(ctx);
			if (object != null && object.canSetProperty(ctx, property)) {
				object.setProperty(ctx, property, value);
			} else if (
					(object == null || XOMInterpreterType.instance.canMakeInstanceFrom(ctx, object)) &&
					ctx.hasGlobalProperty(property) &&
					ctx.getGlobalProperty(property).canSetProperty(ctx, property)
			) {
				ctx.getGlobalProperty(property).setProperty(ctx, property, value);
			} else {
				throw new XNScriptError("Can't set that property");
			}
			return null;
		}
	};

	private static final Command c_subtract = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 3) {
				throw new XNScriptError("Can't understand arguments to subtract");
			}
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant bv = interp.evaluateExpression(parameters.get(0)).asPrimitive(ctx);
			XOMVariant av = interp.evaluateExpression(parameters.get(2)).asContainer(ctx, false);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.subtract(ac, bc, mc, mp);
				av.putIntoContents(ctx, rc);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.subtract(an, bn, mc, mp);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				XOMNumber rn = XOMNumberMath.subtract(an, bn, mc, mp);
				av.putIntoContents(ctx, rn);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				XOMComplex rc = XOMComplexMath.subtract(ac, bc, mc, mp);
				av.putIntoContents(ctx, rc);
			}
			else {
				throw new XOMMorphError("number");
			}
			return null;
		}
	};

	private static final Command c_beep = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.isEmpty()) {
				ctx.getUI().beep();
			} else {
				int i = XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(0)).asPrimitive(ctx), true).toInt();
				while (i-- > 0) {
					ctx.getUI().beep();
				}
			}
			return null;
		}
	};

	private static final Command c_close = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.isEmpty()) {
				throw new XNScriptError("Can't understand arguments to close");
			}
			XOMVariant object = interp.evaluateExpression(parameters.get(0)).asValue(ctx);
			XOMVariant opener = (parameters.size() > 2) ? interp.evaluateExpression(parameters.get(2)).asValue(ctx) : null;
			if (opener != null) {
				if (!ctx.allow(XNSecurityKey.FILE_LAUNCH, "Operation", "Close", "Object", object.toLanguageString(), "Opener", opener.toLanguageString()))
					throw new XNScriptError("Security settings do not allow close");
				String app = opener.toTextString(ctx);
				String doc = object.toTextString(ctx);
				File a = XIONUtil.locateApplication(ctx, app, true);
				File d = XIONUtil.locateDocument(ctx, doc, true);
				if (a == null) {
					throw new XNScriptError("Failed to locate "+app);
				} else if (d == null) {
					throw new XNScriptError("Failed to locate "+doc);
				} else try {
					XIONUtil.unlaunch(a, d);
				} catch (IOException ioe) {
					throw new XNScriptError("Failed to close "+app);
				}
			} else if (ctx.hasIOManager(object)) {
				ctx.getIOManager(object).close(ctx, object);
			} else {
				if (!ctx.allow(XNSecurityKey.FILE_LAUNCH, "Operation", "Close", "Object", object.toLanguageString()))
					throw new XNScriptError("Security settings do not allow close");
				String name = object.toTextString(ctx);
				File f = XIONUtil.locateApplication(ctx, name, true);
				if (f == null) {
					throw new XNScriptError("Failed to locate "+name);
				} else try {
					XIONUtil.unlaunch(f);
				} catch (IOException ioe) {
					throw new XNScriptError("Failed to close "+name);
				}
			}
			return null;
		}
	};

	private static final Command c_convert = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 9) {
				throw new XNScriptError("Can't understand arguments to convert");
			}
			XOMVariant v = interp.evaluateExpression(parameters.get(0));
			String s = v.toTextString(ctx);
			ParsePosition pos = new ParsePosition(0);
			
			String sf1s = interp.evaluateExpression(parameters.get(2)).toTextString(ctx);
			String sf2s = interp.evaluateExpression(parameters.get(4)).toTextString(ctx);
			String df1s = interp.evaluateExpression(parameters.get(6)).toTextString(ctx);
			String df2s = interp.evaluateExpression(parameters.get(8)).toTextString(ctx);
			
			List<XNDateFormat> sf1 = (sf1s.trim().length() == 0) ? XNDateFormat.allForName("any") : XNDateFormat.allForName(sf1s);
			List<XNDateFormat> sf2 = (sf2s.trim().length() == 0) ? new Vector<XNDateFormat>() : XNDateFormat.allForName(sf2s);
			List<XNDateFormat> df1 = (df1s.trim().length() == 0) ? XNDateFormat.allForName("any") : XNDateFormat.allForName(df1s);
			List<XNDateFormat> df2 = (df2s.trim().length() == 0) ? new Vector<XNDateFormat>() : XNDateFormat.allForName(df2s);
			
			long current = new GregorianCalendar().getTime().getTime();
			current = c_convert_0(current, sf1, s, pos);
			current = c_convert_0(current, sf2, s, pos);
			Date d = new Date(current);
			String output = "";
			if (!df1.isEmpty()) output += df1.get(df1.size()-1).toJavaDateFormat().format(d) + " ";
			if (!df2.isEmpty()) output += df2.get(df2.size()-1).toJavaDateFormat().format(d) + " ";
			
			if (v.canPutContents(ctx)) {
				v.putIntoContents(ctx, new XOMString(output.trim()));
			} else {
				ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(output.trim()));
			}
			return null;
		}
	};
	
	private static final Command c_open = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.isEmpty()) {
				throw new XNScriptError("Can't understand arguments to open");
			}
			XOMVariant object = interp.evaluateExpression(parameters.get(0)).asValue(ctx);
			boolean as = (parameters.size() > 1 && interp.evaluateExpression(parameters.get(1)).toTextString(ctx).equalsIgnoreCase("as"));
			XOMVariant opener = (parameters.size() > 2) ? interp.evaluateExpression(parameters.get(2)).asValue(ctx) : null;
			if (as && opener != null) {
				String type = opener.toTextString(ctx);
				if (ctx.hasIOManager(object)) {
					XNIOManager iomgr = ctx.getIOManager(object);
					if (ctx.hasIOMethod(type)) {
						XNIOMethod iomtd = ctx.getIOMethod(type);
						iomgr.open(ctx, object, iomtd, type);
					} else {
						throw new XNScriptError("Unknown file type");
					}
				} else {
					throw new XNScriptError("Can't open this");
				}
			}
			else if (opener != null) {
				if (!ctx.allow(XNSecurityKey.FILE_LAUNCH, "Operation", "Open", "Object", object.toLanguageString(), "Opener", opener.toLanguageString()))
					throw new XNScriptError("Security settings do not allow open");
				String app = opener.toTextString(ctx);
				String doc = object.toTextString(ctx);
				File a = XIONUtil.locateApplication(ctx, app, true);
				File d = XIONUtil.locateDocument(ctx, doc, true);
				if (a == null) {
					throw new XNScriptError("Failed to locate "+app);
				} else if (d == null) {
					throw new XNScriptError("Failed to locate "+doc);
				} else try {
					XIONUtil.launch(a, d);
				} catch (IOException ioe) {
					throw new XNScriptError("Failed to launch "+app);
				}
			}
			else if (ctx.hasIOManager(object)) {
				ctx.getIOManager(object).open(ctx, object);
			}
			else {
				if (!ctx.allow(XNSecurityKey.FILE_LAUNCH, "Operation", "Open", "Object", object.toLanguageString()))
					throw new XNScriptError("Security settings do not allow open");
				String name = object.toTextString(ctx);
				File f = XIONUtil.locateApplicationOrDocument(ctx, name, true);
				if (f == null) {
					throw new XNScriptError("Failed to locate "+name);
				} else try {
					XIONUtil.launch(f);
				} catch (IOException ioe) {
					throw new XNScriptError("Failed to launch "+name);
				}
			}
			return null;
		}
	};

	private static final Command c_read = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 8) {
				throw new XNScriptError("Can't understand arguments to write");
			}
			XOMVariant obj = interp.evaluateExpression(parameters.get(1)).asValue(ctx);
			XNIOManager io = ctx.getIOManager(obj);
			if (io == null) {
				throw new XNScriptError("Can't read from this");
			}
			boolean usesAt = (interp.evaluateExpression(parameters.get(2)).toTextString(ctx).length() > 0);
			boolean usesFor = (interp.evaluateExpression(parameters.get(4)).toTextString(ctx).length() > 0);
			boolean usesUntil = (interp.evaluateExpression(parameters.get(6)).toTextString(ctx).length() > 0);
			long pos = usesAt ? XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(3)), true).toLong() : -1;
			int len = usesFor ? XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(5)), true).toInt() : -1;
			XOMVariant stop = usesUntil ? interp.evaluateExpression(parameters.get(7)) : XOMEmpty.EMPTY;
			XOMVariant data;
			if (usesAt) {
				if (usesFor) {
					if (usesUntil) {
						data = io.read(ctx, obj, pos, len, stop);
					} else {
						data = io.read(ctx, obj, pos, len);
					}
				} else {
					if (usesUntil) {
						data = io.read(ctx, obj, pos, stop);
					} else {
						data = io.read(ctx, obj, pos);
					}
				}
			} else {
				if (usesFor) {
					if (usesUntil) {
						data = io.read(ctx, obj, len, stop);
					} else {
						data = io.read(ctx, obj, len);
					}
				} else {
					if (usesUntil) {
						data = io.read(ctx, obj, stop);
					} else {
						data = io.read(ctx, obj);
					}
				}
			}
			ctx.getVariableMap("it").setVariable(ctx, "it", data);
			return null;
		}
	};

	private static final Command c_sort = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 3) {
				throw new XNScriptError("Can't understand arguments to wait");
			}
			XOMVariant what = interp.evaluateExpression(parameters.get(0));
			String dir = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
			int dirint = (
					dir.equalsIgnoreCase("ascending") ? XOMComparator.ORDER_ASCENDING :
					dir.equalsIgnoreCase("descending") ? XOMComparator.ORDER_DESCENDING :
					XOMComparator.ORDER_ASCENDING);
			String type = interp.evaluateExpression(parameters.get(2)).toTextString(ctx);
			int typeint = (
					type.equalsIgnoreCase("text") ? XOMComparator.TYPE_TEXT :
					type.equalsIgnoreCase("international") ? XOMComparator.TYPE_INTERNATIONAL :
					type.equalsIgnoreCase("numeric") ? XOMComparator.TYPE_NUMERIC :
					type.equalsIgnoreCase("datetime") ? XOMComparator.TYPE_DATETIME :
					XOMComparator.TYPE_TEXT);
			XNExpression crit = (parameters.size() > 4) ? parameters.get(4) : null;
			XOMComparator cmp = new XOMComparator(ctx, dirint, typeint, crit);
			what.sortContents(ctx, cmp);
			return null;
		}
	};

	private static final Command c_truncate = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.isEmpty()) {
				throw new XNScriptError("Can't understand arguments to truncate");
			}
			XOMVariant obj = interp.evaluateExpression(parameters.get(0)).asValue(ctx);
			XNIOManager io = ctx.getIOManager(obj);
			if (io == null) {
				throw new XNScriptError("Can't truncate this");
			} else if (parameters.size() > 2) {
				long pos = XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(2)), true).toLong();
				io.truncate(ctx, obj, pos);
			} else {
				io.truncate(ctx, obj);
			}
			return null;
		}
	};

	private static final Command c_wait = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 2) {
				throw new XNScriptError("Can't understand arguments to wait");
			}
			String type = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
			if (type.equalsIgnoreCase("while")) {
				XNExpression condition = parameters.get(1);
				while (XOMBooleanType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(condition).asPrimitive(ctx)).toBoolean());
			}
			else if (type.equalsIgnoreCase("until")) {
				XNExpression condition = parameters.get(1);
				while (!XOMBooleanType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(condition).asPrimitive(ctx)).toBoolean());
			}
			else {
				double d = XOMNumberType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(1)), true).toDouble();
				type = (parameters.size() > 2) ? interp.evaluateExpression(parameters.get(2)).toTextString(ctx) : "ticks";
				type = type.toLowerCase();
				if (type.startsWith("sec")) {
					long startTime = System.nanoTime();
					while (System.nanoTime()-startTime < 1000000000L*d);
				} else if (type.startsWith("milli")) {
					long startTime = System.nanoTime();
					while (System.nanoTime()-startTime < 1000000L*d);
				} else if (type.startsWith("micro")) {
					long startTime = System.nanoTime();
					while (System.nanoTime()-startTime < 1000L*d);
				} else if (type.startsWith("nano")) {
					long startTime = System.nanoTime();
					while (System.nanoTime()-startTime < d);
				} else if (type.startsWith("hour")) {
					long startTime = System.nanoTime();
					while (System.nanoTime()-startTime < 3600000000000L*d);
				} else if (type.startsWith("min")) {
					long startTime = System.nanoTime();
					while (System.nanoTime()-startTime < 60000000000L*d);
				} else {
					long startTime = System.nanoTime();
					while (System.nanoTime()-startTime < 16666666.6666666666666666666666666667*d);
				}
			}
			return null;
		}
	};

	private static final Command c_write = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() < 3) {
				throw new XNScriptError("Can't understand arguments to write");
			}
			XOMVariant content = interp.evaluateExpression(parameters.get(0)).asValue(ctx);
			XOMVariant obj = interp.evaluateExpression(parameters.get(2)).asValue(ctx);
			XNIOManager io = ctx.getIOManager(obj);
			if (io == null) {
				throw new XNScriptError("Can't write to this");
			} else if (parameters.size() > 4) {
				long pos = XOMIntegerType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(4)), true).toLong();
				io.write(ctx, obj, content, pos);
			} else {
				io.write(ctx, obj, content);
			}
			return null;
		}
	};
	
	private static void assertEmptyParameter(String functionName, XOMVariant parameter) {
		if (!(parameter == null || parameter instanceof XOMEmpty)) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		}
	}
	
	private static void assertNonEmptyParameter(String functionName, XOMVariant parameter) {
		if (parameter == null || parameter instanceof XOMEmpty) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		}
	}
	
	private static XOMVariant anyNumericParameter(XNContext ctx, String functionName, XOMVariant parameter) {
		if (parameter == null || parameter instanceof XOMEmpty) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		}
		else if (parameter instanceof XOMInteger) {
			return ((XOMInteger)parameter);
		}
		else if (parameter instanceof XOMNumber) {
			return ((XOMNumber)parameter);
		}
		else if (parameter instanceof XOMComplex) {
			return ((XOMComplex)parameter);
		}
		else if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, parameter, true)) {
			return (XOMIntegerType.instance.makeInstanceFrom(ctx, parameter, true));
		}
		else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, parameter, true)) {
			return (XOMNumberType.instance.makeInstanceFrom(ctx, parameter, true));
		}
		else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, parameter, true)) {
			return (XOMComplexType.instance.makeInstanceFrom(ctx, parameter, true));
		}
		else {
			throw new XOMMorphError("number");
		}
	}
	
	private static XOMVariant fpNumericParameter(XNContext ctx, String functionName, XOMVariant parameter) {
		if (parameter == null || parameter instanceof XOMEmpty) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		}
		else if (parameter instanceof XOMNumber) {
			return ((XOMNumber)parameter);
		}
		else if (parameter instanceof XOMComplex) {
			return ((XOMComplex)parameter);
		}
		else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, parameter, true)) {
			return (XOMNumberType.instance.makeInstanceFrom(ctx, parameter, true));
		}
		else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, parameter, true)) {
			return (XOMComplexType.instance.makeInstanceFrom(ctx, parameter, true));
		}
		else {
			throw new XOMMorphError("number");
		}
	}
	
	private static XOMVariant realNumericParameter(XNContext ctx, String functionName, XOMVariant parameter) {
		if (parameter == null || parameter instanceof XOMEmpty) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		}
		else if (parameter instanceof XOMNumber) {
			return ((XOMNumber)parameter);
		}
		else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, parameter, true)) {
			return (XOMNumberType.instance.makeInstanceFrom(ctx, parameter, true));
		}
		else {
			throw new XOMMorphError("number");
		}
	}
	
	private static List<? extends XOMVariant> listParameter(XNContext ctx, String functionName, XOMVariant parameter, boolean primitive) {
		List<? extends XOMVariant> l = (parameter == null) ? new Vector<XOMVariant>() : primitive ? parameter.toPrimitiveList(ctx) : parameter.toVariantList(ctx);
		return l;
	}
	
	private static List<? extends XOMVariant> listParameter(XNContext ctx, String functionName, XOMVariant parameter, int np, boolean primitive) {
		List<? extends XOMVariant> l = (parameter == null) ? new Vector<XOMVariant>() : primitive ? parameter.toPrimitiveList(ctx) : parameter.toVariantList(ctx);
		if (l.size() != np) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		} else {
			return l;
		}
	}
	
	private static List<? extends XOMVariant> listParameter(XNContext ctx, String functionName, XOMVariant parameter, int min, int max, boolean primitive) {
		List<? extends XOMVariant> l = (parameter == null) ? new Vector<XOMVariant>() : primitive ? parameter.toPrimitiveList(ctx) : parameter.toVariantList(ctx);
		if (l.size() < min || l.size() > max) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		} else {
			return l;
		}
	}
	
	private static List<? extends XOMVariant> anyNumericListParameter(XNContext ctx, String functionName, XOMVariant parameter) {
		if (parameter == null || parameter instanceof XOMEmpty) {
			return new Vector<XOMVariant>();
		}
		if (parameter instanceof XOMList) {
			Class<? extends XOMVariant> clazz = ((XOMList)parameter).getElementClass();
			if (clazz.isAssignableFrom(XOMInteger.class)) return (XOMComplexType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
			else if (clazz.isAssignableFrom(XOMNumber.class)) return (XOMNumberType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
			else if (clazz.isAssignableFrom(XOMComplex.class)) return (XOMIntegerType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
		}
		if (XOMIntegerType.listInstance.canMakeInstanceFrom(ctx, parameter)) {
			return (XOMIntegerType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
		}
		else if (XOMNumberType.listInstance.canMakeInstanceFrom(ctx, parameter)) {
			return (XOMNumberType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
		}
		else if (XOMComplexType.listInstance.canMakeInstanceFrom(ctx, parameter)) {
			return (XOMComplexType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
		}
		else {
			throw new XOMMorphError("numbers");
		}
	}
	
	private static List<? extends XOMVariant> fpNumericListParameter(XNContext ctx, String functionName, XOMVariant parameter) {
		if (parameter == null || parameter instanceof XOMEmpty) {
			return new Vector<XOMVariant>();
		}
		if (parameter instanceof XOMList) {
			Class<? extends XOMVariant> clazz = ((XOMList)parameter).getElementClass();
			if (clazz.isAssignableFrom(XOMNumber.class)) return (XOMNumberType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
			else if (clazz.isAssignableFrom(XOMComplex.class)) return (XOMComplexType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
		}
		if (XOMNumberType.listInstance.canMakeInstanceFrom(ctx, parameter)) {
			return (XOMNumberType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
		}
		else if (XOMComplexType.listInstance.canMakeInstanceFrom(ctx, parameter)) {
			return (XOMComplexType.listInstance.makeInstanceFrom(ctx, parameter)).toPrimitiveList(ctx);
		}
		else {
			throw new XOMMorphError("numbers");
		}
	}
	
	private static String rot13(String a) {
		StringBuffer b = new StringBuffer(a.length());
		CharacterIterator i = new StringCharacterIterator(a);
		for (char c = i.first(); c != CharacterIterator.DONE; c = i.next()) {
			if ((c >= 'A' && c <= 'M') || (c >= 'a' && c <= 'm')) b.append((char)(c+13));
			else if ((c >= 'N' && c <= 'Z') || (c >= 'n' && c <= 'z')) b.append((char)(c-13));
			else b.append(c);
		}
		return b.toString();
	}
	
	private static final Function f_abs = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMInteger) return ((XOMInteger)parameter).abs();
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).abs();
			else if (parameter instanceof XOMComplex) return XOMComplexMath.abs((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_acos = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.acos((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.acos((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_acosh = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.acosh((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.acosh((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};

	private static final Function f_acot = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.acot((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.acot((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_acoth = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.acoth((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.acoth((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_acsc = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.acsc((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.acsc((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_acsch = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.acsch((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.acsch((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_agm = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant a = fpNumericParameter(ctx, functionName, l.get(0));
			XOMVariant b = fpNumericParameter(ctx, functionName, l.get(1));
			if (a instanceof XOMComplex || b instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, a, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, b, true);
				return XOMComplexMath.agm(ac, bc, mc, mp);
			} else {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, a, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, b, true);
				return XOMNumberMath.agm(an, bn, mc, mp);
			}
		}
	};
	
	private static final Function f_and = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMBoolean.TRUE;
			List<? extends XOMVariant> booleans = XOMBooleanType.listInstance.makeInstanceFrom(ctx, parameter).toPrimitiveList(ctx);
			boolean finalResult = true;
			for (XOMVariant b : booleans) {
				finalResult = finalResult && XOMBooleanType.instance.makeInstanceFrom(ctx, b).toBoolean();
			}
			return finalResult ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		}
	};
	
	private static final Function f_annuity = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant a = fpNumericParameter(ctx, functionName, l.get(0));
			XOMVariant b = fpNumericParameter(ctx, functionName, l.get(1));
			if (a instanceof XOMComplex || b instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, a, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, b, true);
				return XOMComplexMath.annuity(ac, bc, mc, mp);
			} else {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, a, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, b, true);
				return XOMNumberMath.annuity(an, bn, mc, mp);
			}
		}
	};
	
	private static final Function f_applicationfile = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to applicationFile");
			if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow applicationFile");
			File f = XIONUtil.locateApplication(ctx, parameter.toTextString(ctx), false);
			if (f == null) return XOMEmpty.EMPTY;
			else return new XOMFile(f);
		}
	};
	
	private static final Function f_applicationpath = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to applicationPath");
			if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow applicationPath");
			File f = XIONUtil.locateApplication(ctx, parameter.toTextString(ctx), false);
			if (f == null) return XOMEmpty.EMPTY;
			else return new XOMString(f.getAbsolutePath());
		}
	};
	
	private static final Function f_applicationordocumentfile = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to applicationOrDocumentFile");
			if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow applicationOrDocumentFile");
			File f = XIONUtil.locateApplicationOrDocument(ctx, parameter.toTextString(ctx), false);
			if (f == null) return XOMEmpty.EMPTY;
			else return new XOMFile(f);
		}
	};
	
	private static final Function f_applicationordocumentpath = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to applicationOrDocumentPath");
			if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow applicationOrDocumentPath");
			File f = XIONUtil.locateApplicationOrDocument(ctx, parameter.toTextString(ctx), false);
			if (f == null) return XOMEmpty.EMPTY;
			else return new XOMString(f.getAbsolutePath());
		}
	};
	
	private static final Function f_arg = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) {
				switch (((XOMNumber)parameter).getSign()) {
				case XOMNumber.SIGN_NaN: return XOMNumber.NaN;
				case XOMNumber.SIGN_NEGATIVE: return XOMNumber.PI;
				default: return XOMNumber.ZERO;
				}
			}
			else if (parameter instanceof XOMComplex) return XOMComplexMath.arg((XOMComplex)parameter, mc, mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_asc = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = anyNumericListParameter(ctx, functionName, parameter);
			if (numbers.isEmpty()) return XOMBoolean.TRUE;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMNumber[] prev = ((XOMComplex)numbers.get(0)).toXOMNumbers();
				for (XOMVariant number : numbers) {
					XOMNumber[] num = ((XOMComplex)number).toXOMNumbers();
					if (XOMNumberMath.compare(num[0], prev[0]) < 0) return XOMBoolean.FALSE;
					if (XOMNumberMath.compare(num[1], prev[1]) < 0) return XOMBoolean.FALSE;
					prev = num;
				}
				return XOMBoolean.TRUE;
			} else if (numbers.get(0) instanceof XOMInteger) {
				XOMInteger prev = (XOMInteger)numbers.get(0);
				for (XOMVariant number : numbers) {
					if (XOMIntegerMath.compare((XOMInteger)number, prev) < 0) return XOMBoolean.FALSE;
					prev = (XOMInteger)number;
				}
				return XOMBoolean.TRUE;
			} else {
				XOMNumber prev = (XOMNumber)numbers.get(0);
				for (XOMVariant number : numbers) {
					if (XOMNumberMath.compare((XOMNumber)number, prev) < 0) return XOMBoolean.FALSE;
					prev = (XOMNumber)number;
				}
				return XOMBoolean.TRUE;
			}
		}
	};
	
	private static final Function f_asec = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.asec((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.asec((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_asech = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.asech((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.asech((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_asin = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.asin((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.asin((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_asinh = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.asinh((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.asinh((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_atan = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.atan((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.atan((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_atan2 = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMNumber y = XOMNumberType.instance.makeInstanceFrom(ctx, l.get(0), true);
			XOMNumber x = XOMNumberType.instance.makeInstanceFrom(ctx, l.get(1), true);
			return XOMNumberMath.atan2(y,x,mc,mp);
		}
	};
	
	private static final Function f_atanh = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.atanh((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.atanh((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_aug = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return parameter;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).aug();
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).aug();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_average = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, (XOMComplex)number, mc, mp);
				}
				return XOMComplexMath.divide(sum, new XOMComplex(numbers.size(),0), mc, mp);
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, (XOMNumber)number, mc, mp);
				}
				return XOMNumberMath.divide(sum, new XOMNumber(numbers.size()), mc, mp);
			}
		}
	};
	
	private static final Function f_bc = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String ss = l.get(0).toTextString(ctx);
			int sb = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toBigInteger().intValue();
			int db = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(2), true).toBigInteger().intValue();
			if (sb < 2 || db < 2 || sb > 36 || db > 36) {
				throw new XNScriptError("Expected integer between 2 and 36 here");
			}
			String ds = BaseConvert.bc(ss, sb, db, ctx.getMathContext());
			return new XOMString(ds);
		}
	};
	
	private static final Function f_beta = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant a = fpNumericParameter(ctx, functionName, l.get(0));
			XOMVariant b = fpNumericParameter(ctx, functionName, l.get(1));
			if (a instanceof XOMComplex || b instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, a, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, b, true);
				return XOMComplexMath.beta(ac, bc, mc, mp);
			} else {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, a, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, b, true);
				return XOMNumberMath.beta(an, bn, mc, mp);
			}
		}
	};
	
	private static final Function f_bin = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) {
				XOMInteger i = ((XOMInteger)parameter);
				if (i.isUndefined()) return i;
				return new XOMString(BaseConvert.bc(i.toBigInteger(), 2, ctx.getMathContext()));
			}
			else if (parameter instanceof XOMNumber) {
				XOMNumber n = ((XOMNumber)parameter);
				if (n.isUndefined()) return n;
				return new XOMString(BaseConvert.bc(n.toBigDecimal(), 2, ctx.getMathContext()));
			}
			else if (parameter instanceof XOMComplex) {
				XOMComplex c = ((XOMComplex)parameter);
				if (c.isUndefined()) return c;
				return new XOMString(
						BaseConvert.bc(c.realPart(), 2, ctx.getMathContext()) + "," +
						BaseConvert.bc(c.imaginaryPart(), 2, ctx.getMathContext())
				);
			}
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_bintochar = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to bintochar");
			byte[] data = XOMBinaryType.instance.makeInstanceFrom(ctx, parameter).toByteArray();
			String s;
			try {
				s = new String(data, ctx.getTextEncoding());
			} catch (UnsupportedEncodingException uee) {
				s = new String(data);
			}
			return new XOMString(s);
		}
	};
	
	private static final Function f_bintouni = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to bintouni");
			byte[] data = XOMBinaryType.instance.makeInstanceFrom(ctx, parameter).toByteArray();
			String s;
			try {
				s = new String(data, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				s = new String(data);
			}
			return new XOMString(s);
		}
	};
	
	private static final Function f_cbrt = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.cbrt((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.cbrt((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_ceil = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return parameter;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).ceil();
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).ceil();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_center = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt();
			if (n <= 0) return XOMString.EMPTY_STRING;
			else if (n >= s.length()) return new XOMString(s);
			else return new XOMString(s.substring((s.length()-n+1)/2, (s.length()-n+1)/2+n));
		}
	};
	
	private static final Function f_chartobin = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to chartobin");
			String s = parameter.toTextString(ctx);
			byte[] data;
			try {
				data = s.getBytes(ctx.getTextEncoding());
			} catch (UnsupportedEncodingException uee) {
				data = s.getBytes();
			}
			return new XOMBinary(data);
		}
	};
	
	private static final Function f_chartonum = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to chartonum");
			String s = parameter.toTextString(ctx);
			if (s.length() > 0) {
				int i = s.codePointAt(0);
				s = new String(new int[]{i}, 0, 1);
				byte[] data;
				try {
					data = s.getBytes(ctx.getTextEncoding());
				} catch (UnsupportedEncodingException uee) {
					data = s.getBytes();
				}
				long r = 0;
				for (byte b : data) {
					r = r << 8L | (b & 0xFFL);
				}
				return new XOMInteger(r);
			} else {
				return XOMInteger.ZERO;
			}
		}
	};
	
	private static final Function f_compound = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant a = fpNumericParameter(ctx, functionName, l.get(0));
			XOMVariant b = fpNumericParameter(ctx, functionName, l.get(1));
			if (a instanceof XOMComplex || b instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, a, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, b, true);
				return XOMComplexMath.compound(ac, bc, mc, mp);
			} else {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, a, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, b, true);
				return XOMNumberMath.compound(an, bn, mc, mp);
			}
		}
	};
	
	private static final Function f_concat = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMString.EMPTY_STRING;
			StringBuffer concatenatedString = new StringBuffer();
			for (XOMVariant variant : listParameter(ctx, functionName, parameter, true)) {
				concatenatedString.append(variant.toTextString(ctx));
			}
			return new XOMString(concatenatedString.toString());
		}
	};
	
	private static final Function f_concatsp = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMString.EMPTY_STRING;
			StringBuffer concatenatedString = new StringBuffer();
			for (XOMVariant variant : listParameter(ctx, functionName, parameter, true)) {
				concatenatedString.append(variant.toTextString(ctx));
				concatenatedString.append(" ");
			}
			if (concatenatedString.length() > 0 && concatenatedString.charAt(concatenatedString.length()-1) == ' ') {
				concatenatedString.deleteCharAt(concatenatedString.length()-1);
			}
			return new XOMString(concatenatedString.toString());
		}
	};
	
	private static final Function f_conj = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return ((XOMInteger)parameter);
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter);
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).conj();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_cos = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.cos((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.cos((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_cosh = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.cosh((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.cosh((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_cot = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.cot((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.cot((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_coth = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.coth((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.coth((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_countfields = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMInteger.ZERO;
			String d = "(?i)"+XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			return new XOMInteger(s.split(d).length);
		}
	};
	
	private static final Function f_cpad = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, 3, true);
			String s = l.get(0).toTextString(ctx);
			int maxl = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt();
			String p = (l.size() == 3) ? l.get(2).toTextString(ctx) : " ";
			if (maxl <= 0) return XOMString.EMPTY_STRING;
			while (s.length() < maxl) {
				s = p+s+p;
			}
			return new XOMString(s.substring((s.length()-maxl+1)/2, (s.length()-maxl+1)/2+maxl));
		}
	};
	
	private static final Function f_csc = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.csc((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.csc((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_csch = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.csch((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.csch((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_cscountfields = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMInteger.ZERO;
			String d = XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			return new XOMInteger(s.split(d).length);
		}
	};
	
	private static final Function f_csexplode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMString.EMPTY_STRING;
			String d = XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			String[] flds = s.split(d);
			List<XOMVariant> vlds = new Vector<XOMVariant>();
			for (String fld : flds) {
				vlds.add(new XOMString(fld));
			}
			return new XOMList(vlds);
		}
	};
	
	private static final Function f_csinstr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			return new XOMInteger(l.get(0).toTextString(ctx).indexOf(l.get(1).toTextString(ctx))+1);
		}
	};
	
	private static final Function f_csnthfield = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMString.EMPTY_STRING;
			String d = XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			String[] flds = s.split(d);
			int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(2), true).toInt();
			if (n < 1 || n > flds.length) return XOMString.EMPTY_STRING;
			else return new XOMString(flds[n-1]);
		}
	};
	
	private static final Function f_csoffset = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			return new XOMInteger(l.get(1).toTextString(ctx).indexOf(l.get(0).toTextString(ctx))+1);
		}
	};
	
	private static final Function f_csreplace = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String str = l.get(0).toTextString(ctx);
			String src = XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			String rep = XIONUtil.makeRegexForExactReplace(l.get(2).toTextString(ctx));
			return new XOMString(str.replaceFirst(src, rep));
		}
	};
	
	private static final Function f_csreplaceall = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String str = l.get(0).toTextString(ctx);
			String src = XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			String rep = XIONUtil.makeRegexForExactReplace(l.get(2).toTextString(ctx));
			return new XOMString(str.replaceAll(src, rep));
		}
	};
	
	private static final Function f_csrinstr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			return new XOMInteger(l.get(0).toTextString(ctx).lastIndexOf(l.get(1).toTextString(ctx))+1);
		}
	};
	
	private static final Function f_csstrcmp = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			return new XOMInteger(l.get(1).toTextString(ctx).compareTo(l.get(0).toTextString(ctx)));
		}
	};
	
	private static final Function f_date = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			XNDateFormat fmt = XNDateFormat.SHORT_DATE;
			if (modifier != null) {
				switch (modifier) {
				case SHORT: fmt = XNDateFormat.SHORT_DATE; break;
				case ABBREVIATED: fmt = XNDateFormat.ABBREV_DATE; break;
				case LONG: fmt = XNDateFormat.LONG_DATE; break;
				case ENGLISH: fmt = XNDateFormat.ENGLISH_DATE; break;
				}
			}
			return new XOMDate(fmt);
		}
	};
	
	private static final Function f_dateitems = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			return new XOMDate(XNDateFormat.DATEITEMS);
		}
	};
	
	private static final Function f_dec = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = anyNumericListParameter(ctx, functionName, parameter);
			if (numbers.isEmpty()) return XOMBoolean.TRUE;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMNumber[] prev = ((XOMComplex)numbers.get(0)).toXOMNumbers();
				for (XOMVariant number : numbers) {
					XOMNumber[] num = ((XOMComplex)number).toXOMNumbers();
					if (XOMNumberMath.compare(num[0], prev[0]) > 0) return XOMBoolean.FALSE;
					if (XOMNumberMath.compare(num[1], prev[1]) > 0) return XOMBoolean.FALSE;
					prev = num;
				}
				return XOMBoolean.TRUE;
			} else if (numbers.get(0) instanceof XOMInteger) {
				XOMInteger prev = (XOMInteger)numbers.get(0);
				for (XOMVariant number : numbers) {
					if (XOMIntegerMath.compare((XOMInteger)number, prev) > 0) return XOMBoolean.FALSE;
					prev = (XOMInteger)number;
				}
				return XOMBoolean.TRUE;
			} else {
				XOMNumber prev = (XOMNumber)numbers.get(0);
				for (XOMVariant number : numbers) {
					if (XOMNumberMath.compare((XOMNumber)number, prev) > 0) return XOMBoolean.FALSE;
					prev = (XOMNumber)number;
				}
				return XOMBoolean.TRUE;
			}
		}
	};
	
	private static final Function f_documentfile = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to documentFile");
			if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow documentFile");
			File f = XIONUtil.locateDocument(ctx, parameter.toTextString(ctx), false);
			if (f == null) return XOMEmpty.EMPTY;
			else return new XOMFile(f);
		}
	};
	
	private static final Function f_documentpath = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to documentPath");
			if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow documentPath");
			File f = XIONUtil.locateDocument(ctx, parameter.toTextString(ctx), false);
			if (f == null) return XOMEmpty.EMPTY;
			else return new XOMString(f.getAbsolutePath());
		}
	};
	
	private static final Function f_equal = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMBoolean.TRUE;
			List<? extends XOMVariant> parameters = listParameter(ctx, functionName, parameter, false);
			if (parameters.isEmpty()) return XOMBoolean.TRUE;
			try {
				XNInterpreter interp = new XNInterpreter(ctx);
				XOMVariant first = parameters.get(0);
				for (XOMVariant variant : parameters) {
					if (interp.compareVariants(first,variant) != 0) return XOMBoolean.FALSE;
				}
				return XOMBoolean.TRUE;
			} catch (XNInterpreter.NaNComparisonException nce) {
				return XOMBoolean.FALSE;
			}
		}
	};
	
	private static final Function f_exp = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.exp((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.exp((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_exp1 = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.expm1((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.expm1((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_exp10 = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.exp10((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.exp10((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_exp2 = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.exp2((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.exp2((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_explode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMString.EMPTY_STRING;
			String d = "(?i)"+XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			String[] flds = s.split(d);
			List<XOMVariant> vlds = new Vector<XOMVariant>();
			for (String fld : flds) {
				vlds.add(new XOMString(fld));
			}
			return new XOMList(vlds);
		}
	};
	
	private static final Function f_fact = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.fact((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.fact((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_floor = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return parameter;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).floor();
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).floor();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_frac = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return XOMInteger.ZERO;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).frac();
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).frac();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_gamma = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.gamma((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.gamma((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_gcd = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			XOMInteger xa = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(0), true);
			XOMInteger xb = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true);
			if (xa.isUndefined() || xb.isUndefined()) return XOMInteger.NaN;
			else try {
				BigInteger a = xa.toBigInteger();
				BigInteger b = xb.toBigInteger();
				return new XOMInteger(a.gcd(b));
			} catch (ArithmeticException ae) {
				return XOMInteger.ZERO;
			}
		}
	};
	
	private static final Function f_geom = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex product = XOMComplex.ONE;
				for (XOMVariant number : numbers) {
					product = XOMComplexMath.multiply(product, (XOMComplex)number, mc, mp);
				}
				return XOMComplexMath.pow(product, XOMComplexMath.divide(XOMComplex.ONE, new XOMComplex(numbers.size(), 0), mc, mp), mc, mp);
			} else {
				XOMNumber product = XOMNumber.ONE;
				for (XOMVariant number : numbers) {
					product = XOMNumberMath.multiply(product, (XOMNumber)number, mc, mp);
				}
				return XOMNumberMath.pow(product, XOMNumberMath.divide(XOMNumber.ONE, new XOMNumber(numbers.size()), mc, mp), mc, mp);
			}
		}
	};
	
	private static final Function f_hash = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 1, 2, true);
			byte[] data;
			if (l.get(0) instanceof XOMBinary) {
				data = ((XOMBinary)l.get(0)).toByteArray();
			} else {
				String sdata = l.get(0).toTextString(ctx).toLowerCase();
				try {
					data = sdata.getBytes(ctx.getTextEncoding());
				} catch (UnsupportedEncodingException uee) {
					data = sdata.getBytes();
				}
			}
			String algorithm = (l.size() > 1) ? l.get(1).toTextString(ctx) : "Atkinson";
			if (algorithm.equalsIgnoreCase("Atkinson")) {
				return new XOMInteger(AtkinsonHash.hash(data) & 0xFFFFFFFFL);
			}
			else try {
				MessageDigest md = MessageDigest.getInstance(algorithm);
				data = md.digest(data);
				return new XOMBinary(data);
			}
			catch (NoSuchAlgorithmException nsae) {
				return XOMEmpty.EMPTY;
			}
		}
	};
	
	private static final Function f_head = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMEmpty.EMPTY;
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, false);
			if (l.isEmpty()) return XOMEmpty.EMPTY;
			else return l.get(0);
		}
	};
	
	private static final Function f_hex = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) {
				XOMInteger i = ((XOMInteger)parameter);
				if (i.isUndefined()) return i;
				return new XOMString(BaseConvert.bc(i.toBigInteger(), 16, ctx.getMathContext()));
			}
			else if (parameter instanceof XOMNumber) {
				XOMNumber n = ((XOMNumber)parameter);
				if (n.isUndefined()) return n;
				return new XOMString(BaseConvert.bc(n.toBigDecimal(), 16, ctx.getMathContext()));
			}
			else if (parameter instanceof XOMComplex) {
				XOMComplex c = ((XOMComplex)parameter);
				if (c.isUndefined()) return c;
				return new XOMString(
						BaseConvert.bc(c.realPart(), 16, ctx.getMathContext()) + "," +
						BaseConvert.bc(c.imaginaryPart(), 16, ctx.getMathContext())
				);
			}
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_hypot = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.ZERO;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, XOMComplexMath.multiply((XOMComplex)number,(XOMComplex)number,mc,mp), mc, mp);
				}
				return XOMComplexMath.sqrt(sum, mc, mp);
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, XOMNumberMath.multiply((XOMNumber)number, (XOMNumber)number, mc, mp), mc, mp);
				}
				return XOMNumberMath.sqrt(sum, mc, mp);
			}
		}
	};
	
	private static final Function f_im = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return XOMInteger.ZERO;
			else if (parameter instanceof XOMNumber) return XOMNumber.ZERO;
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).Im();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_implode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			List<? extends XOMVariant> vs = listParameter(ctx, functionName, l.get(0), true);
			String d = l.get(1).toTextString(ctx);
			StringBuffer out = new StringBuffer();
			for (XOMVariant v : vs) {
				out.append(v.toTextString(ctx));
				out.append(d);
			}
			if (out.length() > 0 && out.substring(out.length()-d.length()).equals(d)) {
				out.delete(out.length()-d.length(), out.length());
			}
			return new XOMString(out.toString());
		}
	};
	
	private static final Function f_includefile = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to includeFile");
			if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow includeFile");
			File f = XIONUtil.locateInclude(ctx, parameter.toTextString(ctx), false);
			if (f == null) return XOMEmpty.EMPTY;
			else return new XOMFile(f);
		}
	};
	
	private static final Function f_includepath = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to includePath");
			if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow includePath");
			File f = XIONUtil.locateInclude(ctx, parameter.toTextString(ctx), false);
			if (f == null) return XOMEmpty.EMPTY;
			else return new XOMString(f.getAbsolutePath());
		}
	};
	
	private static final Function f_instr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			return new XOMInteger(l.get(0).toTextString(ctx).toLowerCase().indexOf(l.get(1).toTextString(ctx).toLowerCase())+1);
		}
	};
	
	private static final Function f_isfinite = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return ((XOMInteger)parameter).isUndefined() ? XOMBoolean.FALSE : XOMBoolean.TRUE;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).isUndefined() ? XOMBoolean.FALSE : XOMBoolean.TRUE;
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).isUndefined() ? XOMBoolean.FALSE : XOMBoolean.TRUE;
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_isinfinite = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return ((XOMInteger)parameter).isInfinite() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).isInfinite() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).isInfinite() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_isnan = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return ((XOMInteger)parameter).isNaN() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).isNaN() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).isNaN() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_lcase = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to lcase");
			else return new XOMString(parameter.toTextString(ctx).toLowerCase());
		}
	};
	
	private static final Function f_lcm = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			XOMInteger xa = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(0), true);
			XOMInteger xb = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true);
			if (xa.isUndefined() || xb.isUndefined()) return XOMInteger.NaN;
			else try {
				BigInteger a = xa.toBigInteger();
				BigInteger b = xb.toBigInteger();
				BigInteger gcd = a.gcd(b);
				return new XOMInteger(a.divide(gcd).multiply(b));
			} catch (ArithmeticException ae) {
				return XOMInteger.ZERO;
			}
		}
	};
	
	private static final Function f_lconcat = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMList.EMPTY_LIST;
			List<? extends XOMVariant> parameters = listParameter(ctx, functionName, parameter, true);
			List<XOMVariant> concatenatedList = new Vector<XOMVariant>();
			for (XOMVariant variant : parameters) {
				concatenatedList.addAll(listParameter(ctx, functionName, variant, true));
			}
			return new XOMList(concatenatedList);
		}
	};
	
	private static final Function f_left = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt();
			if (n <= 0) return XOMString.EMPTY_STRING;
			else if (n >= s.length()) return new XOMString(s);
			else return new XOMString(s.substring(0,n));
		}
	};
	
	private static final Function f_len = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to length");
			else return new XOMInteger(parameter.toTextString(ctx).length());
		}
	};
	
	private static final Function f_ln = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.log((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.log((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_ln1 = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.log1p((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.log1p((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_lnbeta = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant a = fpNumericParameter(ctx, functionName, l.get(0));
			XOMVariant b = fpNumericParameter(ctx, functionName, l.get(1));
			if (a instanceof XOMComplex || b instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, a, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, b, true);
				return XOMComplexMath.logbeta(ac, bc, mc, mp);
			} else {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, a, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, b, true);
				return XOMNumberMath.logbeta(an, bn, mc, mp);
			}
		}
	};
	
	private static final Function f_lnfact = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.logfact((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.logfact((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_lngamma = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.loggamma((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.loggamma((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_log = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant a = fpNumericParameter(ctx, functionName, l.get(0));
			XOMVariant b = fpNumericParameter(ctx, functionName, l.get(1));
			if (a instanceof XOMComplex || b instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, a, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, b, true);
				return XOMComplexMath.divide(XOMComplexMath.log(ac,mc,mp), XOMComplexMath.log(bc,mc,mp), mc, mp);
			} else {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, a, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, b, true);
				return XOMNumberMath.divide(XOMNumberMath.log(an,mc,mp), XOMNumberMath.log(bn,mc,mp), mc, mp);
			}
		}
	};
	
	private static final Function f_log10 = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.log10((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.log10((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_log2 = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.log2((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.log2((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_lpad = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, 3, true);
			String s = l.get(0).toTextString(ctx);
			int maxl = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt();
			String p = (l.size() == 3) ? l.get(2).toTextString(ctx) : " ";
			if (maxl <= 0) return XOMString.EMPTY_STRING;
			StringBuffer out = new StringBuffer();
			while (out.length() < maxl) out.append(p);
			out.append(s);
			out.delete(0, out.length()-maxl);
			return new XOMString(out.toString());
		}
	};
	
	private static final Function f_lreverse = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMEmpty.EMPTY;
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, false);
			List<XOMVariant> lr = new Vector<XOMVariant>();
			for (XOMVariant v : l) lr.add(0,v);
			return new XOMList(lr);
		}
	};
	
	private static final Function f_ltrim = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to ltrim");
			String s = parameter.toTextString(ctx);
			int start = 0;
			while (start < s.length() && (s.charAt(start) <= 0x20 || (s.charAt(start) >= 0x7F && s.charAt(start) < 0xA0) || s.charAt(start) == 0x2028 || s.charAt(start) == 0x2029)) {
				start++;
			}
			return new XOMString(s.substring(start));
		}
	};
	
	private static final Function f_max = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = anyNumericListParameter(ctx, functionName, parameter);
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMNumber[] max = ((XOMComplex)numbers.get(0)).toXOMNumbers();
				for (XOMVariant number : numbers) {
					XOMNumber[] num = ((XOMComplex)number).toXOMNumbers();
					if (XOMNumberMath.compare(num[0], max[0]) > 0) max[0] = num[0];
					if (XOMNumberMath.compare(num[1], max[1]) > 0) max[1] = num[1];
				}
				return new XOMComplex(max[0],max[1]);
			} else if (numbers.get(0) instanceof XOMInteger) {
				XOMInteger max = (XOMInteger)numbers.get(0);
				for (XOMVariant number : numbers) {
					if (XOMIntegerMath.compare((XOMInteger)number, max) > 0) max = (XOMInteger)number;
				}
				return max;
			} else {
				XOMNumber max = (XOMNumber)numbers.get(0);
				for (XOMVariant number : numbers) {
					if (XOMNumberMath.compare((XOMNumber)number, max) > 0) max = (XOMNumber)number;
				}
				return max;
			}
		}
	};
	
	private static final Function f_mid = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, 3, true);
			String s = l.get(0).toTextString(ctx);
			if (l.size() == 3) {
				int start = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt()-1;
				int end = start+XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(2), true).toInt();
				if (end < start) return XOMString.EMPTY_STRING;
				if (start < 0) start = 0;
				if (start > s.length()) start = s.length();
				if (end < 0) end = 0;
				if (end > s.length()) end = s.length();
				return new XOMString(s.substring(start,end));
			} else {
				int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt()-1;
				if (n <= 0) return new XOMString(s);
				else if (n >= s.length()) return XOMString.EMPTY_STRING;
				else return new XOMString(s.substring(n));
			}
		}
	};
	
	private static final Function f_min = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = anyNumericListParameter(ctx, functionName, parameter);
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMNumber[] min = ((XOMComplex)numbers.get(0)).toXOMNumbers();
				for (XOMVariant number : numbers) {
					XOMNumber[] num = ((XOMComplex)number).toXOMNumbers();
					if (XOMNumberMath.compare(num[0], min[0]) < 0) min[0] = num[0];
					if (XOMNumberMath.compare(num[1], min[1]) < 0) min[1] = num[1];
				}
				return new XOMComplex(min[0],min[1]);
			} else if (numbers.get(0) instanceof XOMInteger) {
				XOMInteger min = (XOMInteger)numbers.get(0);
				for (XOMVariant number : numbers) {
					if (XOMIntegerMath.compare((XOMInteger)number, min) < 0) min = (XOMInteger)number;
				}
				return min;
			} else {
				XOMNumber min = (XOMNumber)numbers.get(0);
				for (XOMVariant number : numbers) {
					if (XOMNumberMath.compare((XOMNumber)number, min) < 0) min = (XOMNumber)number;
				}
				return min;
			}
		}
	};
	
	private static final Function f_ncr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant a = fpNumericParameter(ctx, functionName, l.get(0));
			XOMVariant b = fpNumericParameter(ctx, functionName, l.get(1));
			if (a instanceof XOMComplex || b instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, a, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, b, true);
				return XOMComplexMath.nCr(ac, bc, mc, mp);
			} else {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, a, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, b, true);
				return XOMNumberMath.nCr(an, bn, mc, mp);
			}
		}
	};
	
	private static final Function f_npr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMVariant a = fpNumericParameter(ctx, functionName, l.get(0));
			XOMVariant b = fpNumericParameter(ctx, functionName, l.get(1));
			if (a instanceof XOMComplex || b instanceof XOMComplex) {
				XOMComplex ac = XOMComplexType.instance.makeInstanceFrom(ctx, a, true);
				XOMComplex bc = XOMComplexType.instance.makeInstanceFrom(ctx, b, true);
				return XOMComplexMath.nPr(ac, bc, mc, mp);
			} else {
				XOMNumber an = XOMNumberType.instance.makeInstanceFrom(ctx, a, true);
				XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(ctx, b, true);
				return XOMNumberMath.nPr(an, bn, mc, mp);
			}
		}
	};
	
	private static final Function f_nthfield = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMString.EMPTY_STRING;
			String d = "(?i)"+XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			String[] flds = s.split(d);
			int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(2), true).toInt();
			if (n < 1 || n > flds.length) return XOMString.EMPTY_STRING;
			else return new XOMString(flds[n-1]);
		}
	};
	
	private static final Function f_number = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to " + functionName);
			return new XOMInteger(listParameter(ctx, functionName, parameter, false).size());
		}
	};
	
	private static final Function f_numtochar = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to numtochar");
			long i = XOMIntegerType.instance.makeInstanceFrom(ctx, parameter, true).toLong();
			byte[] data;
			if (i < 0x100L) {
				data = new byte[]{(byte)(i & 0xFF)};
			} else if (i < 0x10000L) {
				data = new byte[]{(byte)((i >>> 8) & 0xFF),(byte)(i & 0xFF)};
			} else if (i < 0x1000000L) {
				data = new byte[]{(byte)((i >>> 16) & 0xFF),(byte)((i >>> 8) & 0xFF),(byte)(i & 0xFF)};
			} else if (i < 0x100000000L) {
				data = new byte[]{(byte)((i >>> 24) & 0xFF),(byte)((i >>> 16) & 0xFF),(byte)((i >>> 8) & 0xFF),(byte)(i & 0xFF)};
			} else if (i < 0x10000000000L) {
				data = new byte[]{(byte)((i >>> 32) & 0xFF),(byte)((i >>> 24) & 0xFF),(byte)((i >>> 16) & 0xFF),(byte)((i >>> 8) & 0xFF),(byte)(i & 0xFF)};
			} else if (i < 0x1000000000000L) {
				data = new byte[]{(byte)((i >>> 40) & 0xFF),(byte)((i >>> 32) & 0xFF),(byte)((i >>> 24) & 0xFF),(byte)((i >>> 16) & 0xFF),(byte)((i >>> 8) & 0xFF),(byte)(i & 0xFF)};
			} else if (i < 0x100000000000000L) {
				data = new byte[]{(byte)((i >>> 48) & 0xFF),(byte)((i >>> 40) & 0xFF),(byte)((i >>> 32) & 0xFF),(byte)((i >>> 24) & 0xFF),(byte)((i >>> 16) & 0xFF),(byte)((i >>> 8) & 0xFF),(byte)(i & 0xFF)};
			} else {
				data = new byte[]{(byte)((i >>> 56) & 0xFF),(byte)((i >>> 48) & 0xFF),(byte)((i >>> 40) & 0xFF),(byte)((i >>> 32) & 0xFF),(byte)((i >>> 24) & 0xFF),(byte)((i >>> 16) & 0xFF),(byte)((i >>> 8) & 0xFF),(byte)(i & 0xFF)};
			}
			String s;
			try {
				s = new String(data, ctx.getTextEncoding());
			} catch (UnsupportedEncodingException uee) {
				s = new String(data);
			}
			return new XOMString(s);
		}
	};
	
	private static final Function f_numtouni = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to numtouni");
			int i = XOMIntegerType.instance.makeInstanceFrom(ctx, parameter, true).toInt();
			String s = new String(new int[]{i},0,1);
			return new XOMString(s);
		}
	};
	
	private static final Function f_oct = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) {
				XOMInteger i = ((XOMInteger)parameter);
				if (i.isUndefined()) return i;
				return new XOMString(BaseConvert.bc(i.toBigInteger(), 8, ctx.getMathContext()));
			}
			else if (parameter instanceof XOMNumber) {
				XOMNumber n = ((XOMNumber)parameter);
				if (n.isUndefined()) return n;
				return new XOMString(BaseConvert.bc(n.toBigDecimal(), 8, ctx.getMathContext()));
			}
			else if (parameter instanceof XOMComplex) {
				XOMComplex c = ((XOMComplex)parameter);
				if (c.isUndefined()) return c;
				return new XOMString(
						BaseConvert.bc(c.realPart(), 8, ctx.getMathContext()) + "," +
						BaseConvert.bc(c.imaginaryPart(), 8, ctx.getMathContext())
				);
			}
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_offset = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			return new XOMInteger(l.get(1).toTextString(ctx).toLowerCase().indexOf(l.get(0).toTextString(ctx).toLowerCase())+1);
		}
	};
	
	private static final Function f_or = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMBoolean.FALSE;
			List<? extends XOMVariant> booleans = XOMBooleanType.listInstance.makeInstanceFrom(ctx, parameter).toPrimitiveList(ctx);
			boolean finalResult = false;
			for (XOMVariant b : booleans) {
				finalResult = finalResult || XOMBooleanType.instance.makeInstanceFrom(ctx, b).toBoolean();
			}
			return finalResult ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		}
	};
	
	private static final Function f_param = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertNonEmptyParameter(functionName, parameter);
			int index = XOMIntegerType.instance.makeInstanceFrom(ctx, parameter, true).toInt();
			XNStackFrame f = ctx.getCurrentStackFrame();
			if (f == null || index < 0 || index > f.getParameters().size()) return XOMEmpty.EMPTY;
			else if (index == 0) return new XOMString(f.getHandlerName());
			else return f.getParameters().get(index-1);
		}
	};
	
	private static final Function f_paramcount = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			XNStackFrame f = ctx.getCurrentStackFrame();
			if (f == null) return XOMInteger.ZERO;
			else return new XOMInteger(f.getParameters().size());
		}
	};
	
	private static final Function f_params = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			XNStackFrame f = ctx.getCurrentStackFrame();
			if (f == null) return XOMInteger.ZERO;
			else return new XOMList(f.getParameters());
		}
	};
	
	private static final Function f_parent = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertNonEmptyParameter(functionName, parameter);
			XOMVariant p = parameter.getParent(ctx);
			if (p == null) return XOMInterpreter.INTERPRETER;
			else return p;
		}
	};
	
	private static final Function f_pow = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex p = XOMComplex.ONE;
				for (int i = numbers.size()-1; i >= 0; i--) {
					XOMVariant number = numbers.get(i);
					p = XOMComplexMath.pow((XOMComplex)number, p, mc, mp);
				}
				return p;
			} else {
				XOMNumber p = XOMNumber.ONE;
				for (int i = numbers.size()-1; i >= 0; i--) {
					XOMVariant number = numbers.get(i);
					p = XOMNumberMath.pow((XOMNumber)number, p, mc, mp);
				}
				return p;
			}
		}
	};
	
	private static final Function f_prod = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.ONE;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex product = XOMComplex.ONE;
				for (XOMVariant number : numbers) {
					product = XOMComplexMath.multiply(product, (XOMComplex)number, mc, mp);
				}
				return product;
			} else {
				XOMNumber product = XOMNumber.ONE;
				for (XOMVariant number : numbers) {
					product = XOMNumberMath.multiply(product, (XOMNumber)number, mc, mp);
				}
				return product;
			}
		}
	};
	
	private static final Function f_pstddev = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, (XOMComplex)number, mc, mp);
				}
				XOMComplex avg = XOMComplexMath.divide(sum, new XOMComplex(numbers.size(),0), mc, mp);
				XOMComplex sumsqdiff = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					XOMComplex sqdiff = XOMComplexMath.subtract(avg, (XOMComplex)number, mc, mp);
					sqdiff = XOMComplexMath.multiply(sqdiff, sqdiff, mc, mp);
					sumsqdiff = XOMComplexMath.add(sumsqdiff, sqdiff, mc, mp);
				}
				XOMComplex avgsumsqdiff = XOMComplexMath.divide(sumsqdiff, new XOMComplex(numbers.size(),0), mc, mp);
				return XOMComplexMath.sqrt(avgsumsqdiff, mc, mp);
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, (XOMNumber)number, mc, mp);
				}
				XOMNumber avg = XOMNumberMath.divide(sum, new XOMNumber(numbers.size()), mc, mp);
				XOMNumber sumsqdiff = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					XOMNumber sqdiff = XOMNumberMath.subtract(avg, (XOMNumber)number, mc, mp);
					sqdiff = XOMNumberMath.multiply(sqdiff, sqdiff, mc, mp);
					sumsqdiff = XOMNumberMath.add(sumsqdiff, sqdiff, mc, mp);
				}
				XOMNumber avgsumsqdiff = XOMNumberMath.divide(sumsqdiff, new XOMNumber(numbers.size()), mc, mp);
				return XOMNumberMath.sqrt(avgsumsqdiff, mc, mp);
			}
		}
	};
	
	private static final Function f_pvariance = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, (XOMComplex)number, mc, mp);
				}
				XOMComplex avg = XOMComplexMath.divide(sum, new XOMComplex(numbers.size(),0), mc, mp);
				XOMComplex sumsqdiff = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					XOMComplex sqdiff = XOMComplexMath.subtract(avg, (XOMComplex)number, mc, mp);
					sqdiff = XOMComplexMath.multiply(sqdiff, sqdiff, mc, mp);
					sumsqdiff = XOMComplexMath.add(sumsqdiff, sqdiff, mc, mp);
				}
				return XOMComplexMath.divide(sumsqdiff, new XOMComplex(numbers.size(),0), mc, mp);
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, (XOMNumber)number, mc, mp);
				}
				XOMNumber avg = XOMNumberMath.divide(sum, new XOMNumber(numbers.size()), mc, mp);
				XOMNumber sumsqdiff = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					XOMNumber sqdiff = XOMNumberMath.subtract(avg, (XOMNumber)number, mc, mp);
					sqdiff = XOMNumberMath.multiply(sqdiff, sqdiff, mc, mp);
					sumsqdiff = XOMNumberMath.add(sumsqdiff, sqdiff, mc, mp);
				}
				return XOMNumberMath.divide(sumsqdiff, new XOMNumber(numbers.size()), mc, mp);
			}
		}
	};
	
	private static final Function f_random = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertNonEmptyParameter(functionName, parameter);
			int i = XOMIntegerType.instance.makeInstanceFrom(ctx, parameter, true).toInt();
			if (i < 1) {
				return XOMInteger.ONE;
			} else {
				return new XOMInteger(XIONUtil.getRandom().nextInt(i)+1);
			}
		}
	};
	
	private static final Function f_randomdecimal = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			return new XOMNumber(1.0-XIONUtil.getRandom().nextDouble());
		}
	};
	
	private static final Function f_randomrange = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			int begin = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(0), true).toInt();
			int end = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt();
			if (end < begin) {
				return new XOMInteger(begin);
			} else {
				return new XOMInteger(begin+XIONUtil.getRandom().nextInt(end-begin+1));
			}
		}
	};
	
	private static final Function f_re = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return parameter;
			else if (parameter instanceof XOMNumber) return parameter;
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).Re();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_replace = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String str = l.get(0).toTextString(ctx);
			String src = "(?i)"+XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			String rep = XIONUtil.makeRegexForExactReplace(l.get(2).toTextString(ctx));
			return new XOMString(str.replaceFirst(src, rep));
		}
	};
	
	private static final Function f_replaceall = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String str = l.get(0).toTextString(ctx);
			String src = "(?i)"+XIONUtil.makeRegexForExactMatch(l.get(1).toTextString(ctx));
			String rep = XIONUtil.makeRegexForExactReplace(l.get(2).toTextString(ctx));
			return new XOMString(str.replaceAll(src, rep));
		}
	};
	
	private static final Function f_result = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			return ctx.getResult();
		}
	};
	
	private static final Function f_reverse = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to reverse");
			String s = parameter.toTextString(ctx);
			StringBuffer sr = new StringBuffer();
			CharacterIterator ci = new StringCharacterIterator(s);
			for (char ch = ci.last(); ch != CharacterIterator.DONE; ch = ci.previous()) {
				sr.append(ch);
			}
			return new XOMString(sr.toString());
		}
	};
	
	private static final Function f_reversebits = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			XOMInteger xa = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(0), true);
			XOMInteger xb = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true);
			if (xa.isUndefined() || xb.isUndefined() || xb.getSign() == XOMInteger.SIGN_NEGATIVE) return XOMInteger.NaN;
			BigInteger ff = BigInteger.valueOf(0xFF);
			BigInteger srcint = xa.toBigInteger();
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			int len = xb.toInt();
			while (len-- > 0) {
				int i = (Integer.reverse(srcint.and(ff).intValue() & 0xFF) >>> 24) & 0xFF;
				bytes.write(i);
				srcint = srcint.shiftRight(8);
			}
			return new XOMInteger(new BigInteger(bytes.toByteArray()));
		}
	};
	
	private static final Function f_reversebytes = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			XOMInteger xa = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(0), true);
			XOMInteger xb = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true);
			if (xa.isUndefined() || xb.isUndefined() || xb.getSign() == XOMInteger.SIGN_NEGATIVE) return XOMInteger.NaN;
			BigInteger ff = BigInteger.valueOf(0xFF);
			BigInteger srcint = xa.toBigInteger();
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			int len = xb.toInt();
			while (len-- > 0) {
				bytes.write(srcint.and(ff).intValue() & 0xFF);
				srcint = srcint.shiftRight(8);
			}
			return new XOMInteger(new BigInteger(bytes.toByteArray()));
		}
	};
	
	private static final Function f_right = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt();
			if (n <= 0) return XOMString.EMPTY_STRING;
			else if (n >= s.length()) return new XOMString(s);
			else return new XOMString(s.substring(s.length()-n));
		}
	};
	
	private static final Function f_rinstr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			return new XOMInteger(l.get(0).toTextString(ctx).toLowerCase().lastIndexOf(l.get(1).toTextString(ctx).toLowerCase())+1);
		}
	};
	
	private static final Function f_rint = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return parameter;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).rint();
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).rint();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_rms = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, XOMComplexMath.multiply((XOMComplex)number,(XOMComplex)number,mc,mp), mc, mp);
				}
				return XOMComplexMath.sqrt(XOMComplexMath.divide(sum,new XOMComplex(numbers.size(),0),mc,mp),mc,mp);
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, XOMNumberMath.multiply((XOMNumber)number, (XOMNumber)number, mc, mp), mc, mp);
				}
				return XOMNumberMath.sqrt(XOMNumberMath.divide(sum,new XOMNumber(numbers.size()),mc,mp),mc,mp);
			}
		}
	};
	
	private static final Function f_root = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex p = XOMComplex.ONE;
				for (int i = numbers.size()-1; i >= 0; i--) {
					XOMVariant number = numbers.get(i);
					p = XOMComplexMath.pow((XOMComplex)number, XOMComplexMath.divide(XOMComplex.ONE, p, mc, mp), mc, mp);
				}
				return p;
			} else {
				XOMNumber p = XOMNumber.ONE;
				for (int i = numbers.size()-1; i >= 0; i--) {
					XOMVariant number = numbers.get(i);
					p = XOMNumberMath.pow((XOMNumber)number, XOMNumberMath.divide(XOMNumber.ONE, p, mc, mp), mc, mp);
				}
				return p;
			}
		}
	};
	
	private static final Function f_rot13 = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to rot13");
			else return new XOMString(rot13(parameter.toTextString(ctx)));
		}
	};
	
	private static final Function f_round = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return parameter;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).round();
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).round();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_rpad = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, 3, true);
			String s = l.get(0).toTextString(ctx);
			int maxl = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt();
			String p = (l.size() == 3) ? l.get(2).toTextString(ctx) : " ";
			if (maxl <= 0) return XOMString.EMPTY_STRING;
			StringBuffer out = new StringBuffer();
			out.append(s);
			while (out.length() < maxl) out.append(p);
			out.delete(maxl, out.length());
			return new XOMString(out.toString());
		}
	};
	
	private static final Function f_rsr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.ZERO;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, XOMComplexMath.divide(XOMComplex.ONE, (XOMComplex)number, mc, mp), mc, mp);
				}
				return XOMComplexMath.divide(XOMComplex.ONE, sum, mc, mp);
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, XOMNumberMath.divide(XOMNumber.ONE, (XOMNumber)number, mc, mp), mc, mp);
				}
				return XOMNumberMath.divide(XOMNumber.ONE, sum, mc, mp);
			}
		}
	};
	
	private static final Function f_rtrim = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to rtrim");
			String s = parameter.toTextString(ctx);
			int end = s.length();
			while (end > 0 && (s.charAt(end-1) <= 0x20 || (s.charAt(end-1) >= 0x7F && s.charAt(end-1) < 0xA0) || s.charAt(end-1) == 0x2028 || s.charAt(end-1) == 0x2029)) {
				end--;
			}
			return new XOMString(s.substring(0,end));
		}
	};
	
	private static final Function f_sec = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.sec((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.sec((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_sech = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.sech((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.sech((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_secs = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			return new XOMDate(XNDateFormat.SECONDS);
		}
	};
	
	private static final Function f_sgn = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMInteger) return ((XOMInteger)parameter).signum();
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).signum();
			else if (parameter instanceof XOMComplex) return XOMComplexMath.signum((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_sin = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.sin((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.sin((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_sinh = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.sinh((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.sinh((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_sqrt = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.sqrt((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.sqrt((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_sstddev = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, (XOMComplex)number, mc, mp);
				}
				XOMComplex avg = XOMComplexMath.divide(sum, new XOMComplex(numbers.size(),0), mc, mp);
				XOMComplex sumsqdiff = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					XOMComplex sqdiff = XOMComplexMath.subtract(avg, (XOMComplex)number, mc, mp);
					sqdiff = XOMComplexMath.multiply(sqdiff, sqdiff, mc, mp);
					sumsqdiff = XOMComplexMath.add(sumsqdiff, sqdiff, mc, mp);
				}
				XOMComplex avgsumsqdiff = XOMComplexMath.divide(sumsqdiff, new XOMComplex(numbers.size()-1,0), mc, mp);
				return XOMComplexMath.sqrt(avgsumsqdiff, mc, mp);
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, (XOMNumber)number, mc, mp);
				}
				XOMNumber avg = XOMNumberMath.divide(sum, new XOMNumber(numbers.size()), mc, mp);
				XOMNumber sumsqdiff = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					XOMNumber sqdiff = XOMNumberMath.subtract(avg, (XOMNumber)number, mc, mp);
					sqdiff = XOMNumberMath.multiply(sqdiff, sqdiff, mc, mp);
					sumsqdiff = XOMNumberMath.add(sumsqdiff, sqdiff, mc, mp);
				}
				XOMNumber avgsumsqdiff = XOMNumberMath.divide(sumsqdiff, new XOMNumber(numbers.size()-1), mc, mp);
				return XOMNumberMath.sqrt(avgsumsqdiff, mc, mp);
			}
		}
	};
	
	private static final Function f_strcmp = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			return new XOMInteger(l.get(1).toTextString(ctx).compareToIgnoreCase(l.get(0).toTextString(ctx)));
		}
	};
	
	private static final Function f_substr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, 3, true);
			String s = l.get(0).toTextString(ctx);
			if (l.size() == 3) {
				int start = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt()-1;
				int end = start+XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(2), true).toInt();
				if (end < start) return XOMString.EMPTY_STRING;
				if (start < 0) start = 0;
				if (start > s.length()) start = s.length();
				if (end < 0) end = 0;
				if (end > s.length()) end = s.length();
				return new XOMString(s.substring(start,end));
			} else {
				int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt()-1;
				if (n <= 0) return new XOMString(s);
				else if (n >= s.length()) return XOMString.EMPTY_STRING;
				else return new XOMString(s.substring(n));
			}
		}
	};
	
	private static final Function f_substring = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, 3, true);
			String s = l.get(0).toTextString(ctx);
			if (l.size() == 3) {
				int start = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt()-1;
				int end = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(2), true).toInt()-1;
				if (end < start) return XOMString.EMPTY_STRING;
				if (start < 0) start = 0;
				if (start > s.length()) start = s.length();
				if (end < 0) end = 0;
				if (end > s.length()) end = s.length();
				return new XOMString(s.substring(start,end));
			} else {
				int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt()-1;
				if (n <= 0) return new XOMString(s);
				else if (n >= s.length()) return XOMString.EMPTY_STRING;
				else return new XOMString(s.substring(n));
			}
		}
	};
	
	private static final Function f_sum = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.ZERO;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, (XOMComplex)number, mc, mp);
				}
				return sum;
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, (XOMNumber)number, mc, mp);
				}
				return sum;
			}
		}
	};
	
	private static final Function f_svariance = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> numbers = fpNumericListParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (numbers.isEmpty()) return XOMNumber.NaN;
			if (numbers.get(0) instanceof XOMComplex) {
				XOMComplex sum = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMComplexMath.add(sum, (XOMComplex)number, mc, mp);
				}
				XOMComplex avg = XOMComplexMath.divide(sum, new XOMComplex(numbers.size(),0), mc, mp);
				XOMComplex sumsqdiff = XOMComplex.ZERO;
				for (XOMVariant number : numbers) {
					XOMComplex sqdiff = XOMComplexMath.subtract(avg, (XOMComplex)number, mc, mp);
					sqdiff = XOMComplexMath.multiply(sqdiff, sqdiff, mc, mp);
					sumsqdiff = XOMComplexMath.add(sumsqdiff, sqdiff, mc, mp);
				}
				return XOMComplexMath.divide(sumsqdiff, new XOMComplex(numbers.size()-1,0), mc, mp);
			} else {
				XOMNumber sum = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					sum = XOMNumberMath.add(sum, (XOMNumber)number, mc, mp);
				}
				XOMNumber avg = XOMNumberMath.divide(sum, new XOMNumber(numbers.size()), mc, mp);
				XOMNumber sumsqdiff = XOMNumber.ZERO;
				for (XOMVariant number : numbers) {
					XOMNumber sqdiff = XOMNumberMath.subtract(avg, (XOMNumber)number, mc, mp);
					sqdiff = XOMNumberMath.multiply(sqdiff, sqdiff, mc, mp);
					sumsqdiff = XOMNumberMath.add(sumsqdiff, sqdiff, mc, mp);
				}
				return XOMNumberMath.divide(sumsqdiff, new XOMNumber(numbers.size()-1), mc, mp);
			}
		}
	};
	
	private static final Function f_systemname = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName))
				throw new XNScriptError("Security settings do not allow systemName");
			return new XOMString(System.getProperty("os.name"));
		}
	};
	
	private static final Function f_systemversion = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName))
				throw new XNScriptError("Security settings do not allow systemVersion");
			return new XOMString(System.getProperty("os.version"));
		}
	};
	
	private static final Function f_tail = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMEmpty.EMPTY;
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, false);
			if (l.isEmpty()) return XOMEmpty.EMPTY;
			else return new XOMList(l.subList(1,l.size()));
		}
	};
	
	private static final Function f_tan = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.tan((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.tan((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_tanh = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = fpNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.tanh((XOMNumber)parameter,mc,mp);
			else if (parameter instanceof XOMComplex) return XOMComplexMath.tanh((XOMComplex)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_tcase = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to tcase");
			else {
				String oldstr = parameter.toTextString(ctx);
				StringBuffer newstr = new StringBuffer(oldstr.length());
				CharacterIterator ci = new StringCharacterIterator(oldstr);
				for (char pch = ' ', ch = ci.first(); ch != CharacterIterator.DONE; pch = ch, ch = ci.next()) {
					if (!Character.isLetter(pch)) newstr.append(Character.toTitleCase(ch));
					else newstr.append(Character.toLowerCase(ch));
				}
				return new XOMString(newstr.toString());
			}
		}
	};
	
	private static final Function f_theta = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMNumber x = XOMNumberType.instance.makeInstanceFrom(ctx, l.get(0), true);
			XOMNumber y = XOMNumberType.instance.makeInstanceFrom(ctx, l.get(1), true);
			return XOMNumberMath.atan2(y,x,mc,mp);
		}
	};
	
	private static final Function f_ticks = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
			return new XOMInteger(uptime * 60L / 1000L);
		}
	};
	
	private static final Function f_time = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			XNDateFormat fmt = XNDateFormat.SHORT_TIME;
			if (modifier != null) {
				switch (modifier) {
				case SHORT: fmt = XNDateFormat.SHORT_TIME; break;
				case ABBREVIATED: fmt = XNDateFormat.ABBREV_TIME; break;
				case LONG: fmt = XNDateFormat.LONG_TIME; break;
				case ENGLISH: fmt = XNDateFormat.ENGLISH_TIME; break;
				}
			}
			return new XOMDate(fmt);
		}
	};
	
	private static final Function f_todeg = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = realNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.toDeg((XOMNumber)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_torad = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = realNumericParameter(ctx, functionName, parameter);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			if (parameter instanceof XOMNumber) return XOMNumberMath.toRad((XOMNumber)parameter,mc,mp);
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_trim = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to trim");
			String s = parameter.toTextString(ctx);
			int start = 0;
			int end = s.length();
			while (start < end && (s.charAt(start) <= 0x20 || (s.charAt(start) >= 0x7F && s.charAt(start) < 0xA0) || s.charAt(start) == 0x2028 || s.charAt(start) == 0x2029)) {
				start++;
			}
			while (end > start && (s.charAt(end-1) <= 0x20 || (s.charAt(end-1) >= 0x7F && s.charAt(end-1) < 0xA0) || s.charAt(end-1) == 0x2028 || s.charAt(end-1) == 0x2029)) {
				end--;
			}
			return new XOMString(s.substring(start, end));
		}
	};
	
	private static final Function f_trunc = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			parameter = anyNumericParameter(ctx, functionName, parameter);
			if (parameter instanceof XOMInteger) return parameter;
			else if (parameter instanceof XOMNumber) return ((XOMNumber)parameter).trunc();
			else if (parameter instanceof XOMComplex) return ((XOMComplex)parameter).trunc();
			else throw new XOMMorphError("number");
		}
	};
	
	private static final Function f_ucase = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to ucase");
			else return new XOMString(parameter.toTextString(ctx).toUpperCase());
		}
	};
	
	private static final Function f_unitobin = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to unitobin");
			String s = parameter.toTextString(ctx);
			byte[] data;
			try {
				data = s.getBytes("UTF-8");
			} catch (UnsupportedEncodingException uee) {
				data = s.getBytes();
			}
			return new XOMBinary(data);
		}
	};
	
	private static final Function f_unitonum = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to unitonum");
			String s = parameter.toTextString(ctx);
			if (s.length() > 0) {
				return new XOMInteger(s.codePointAt(0));
			} else {
				return XOMInteger.ZERO;
			}
		}
	};
	
	private static final Function f_value = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to value");
			if (!ctx.allow(XNSecurityKey.DO_AND_VALUE, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow value");
			return new XNInterpreter(ctx).evaluateExpressionStringOrLiteral(parameter.toTextString(ctx));
		}
	};
	
	private static final Function f_version = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to version");
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName, "Parameter", parameter.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow version");
			String s = parameter.toTextString(ctx);
			if (ctx.hasVersion(s)) {
				Version v = ctx.getVersion(s);
				if (modifier == null) modifier = XNModifier.ABBREVIATED;
				switch (modifier) {
				case LONG:
				case ENGLISH:
					return new XOMString(v.name() + " " + v.version());
				case SHORT:
					return new XOMString(v.version().trim().split("[^0-9.]",2)[0]);
				case ABBREVIATED:
				default:
					return new XOMString(v.version());
				}
			} else {
				throw new XNScriptError("Can't understand arguments to version");
			}
		}
	};
	
	private static final Function f_xcoord = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMNumber r = XOMNumberType.instance.makeInstanceFrom(ctx, l.get(0), true);
			XOMNumber t = XOMNumberType.instance.makeInstanceFrom(ctx, l.get(1), true);
			return XOMNumberMath.multiply(r,XOMNumberMath.cos(t,mc,mp),mc,mp);
		}
	};
	
	private static final Function f_xionname = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			return new XOMString(XNMain.XION_NAME);
		}
	};
	
	private static final Function f_xionversion = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			return new XOMString(XNMain.XION_VERSION);
		}
	};
	
	private static final Function f_xor = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) return XOMBoolean.FALSE;
			List<? extends XOMVariant> booleans = XOMBooleanType.listInstance.makeInstanceFrom(ctx, parameter).toPrimitiveList(ctx);
			boolean finalResult = false;
			for (XOMVariant b : booleans) {
				if (XOMBooleanType.instance.makeInstanceFrom(ctx, b).toBoolean()) {
					finalResult = !finalResult;
				}
			}
			return finalResult ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		}
	};
	
	private static final Function f_ycoord = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			MathContext mc = ctx.getMathContext();
			MathProcessor mp = ctx.getMathProcessor();
			XOMNumber r = XOMNumberType.instance.makeInstanceFrom(ctx, l.get(0), true);
			XOMNumber t = XOMNumberType.instance.makeInstanceFrom(ctx, l.get(1), true);
			return XOMNumberMath.multiply(r,XOMNumberMath.sin(t,mc,mp),mc,mp);
		}
	};
	
	private static final Property p_username = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return false;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(XIONUtil.getUserName(ctx, modifier));
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			throw new XNScriptError("Can't set that property");
		}
	};
	
	private static final Property p_applicationPaths = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return ctx.allow(XNSecurityKey.SEARCH_PATHS, "Operation", "SetProperty", "Property", propertyName);
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(XIONUtil.getApplicationPaths(ctx));
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (!ctx.allow(XNSecurityKey.SEARCH_PATHS, "Operation", "SetProperty", "Property", propertyName, "Value", value.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow set the applicationPaths");
			ctx.setApplicationPaths(value.toTextString(ctx));
		}
	};
	
	private static final Property p_documentPaths = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return ctx.allow(XNSecurityKey.SEARCH_PATHS, "Operation", "SetProperty", "Property", propertyName);
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(XIONUtil.getDocumentPaths(ctx));
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (!ctx.allow(XNSecurityKey.SEARCH_PATHS, "Operation", "SetProperty", "Property", propertyName, "Value", value.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow set the documentPaths");
			ctx.setDocumentPaths(value.toTextString(ctx));
		}
	};
	
	private static final Property p_includePaths = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return ctx.allow(XNSecurityKey.SEARCH_PATHS, "Operation", "SetProperty", "Property", propertyName);
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(XIONUtil.getIncludePaths(ctx));
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (!ctx.allow(XNSecurityKey.SEARCH_PATHS, "Operation", "SetProperty", "Property", propertyName, "Value", value.toTextString(ctx)))
				throw new XNScriptError("Security settings do not allow set the includePaths");
			ctx.setIncludePaths(value.toTextString(ctx));
		}
	};
	
	private static final Property p_itemdelimiter = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(Character.toString(ctx.getItemDelimiter()));
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			String s = value.toTextString(ctx);
			ctx.setItemDelimiter((s.length() < 1) ? ',' : s.charAt(0));
		}
	};
	
	private static final Property p_columndelimiter = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(Character.toString(ctx.getColumnDelimiter()));
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			String s = value.toTextString(ctx);
			ctx.setColumnDelimiter((s.length() < 1) ? '\uFFF0' : s.charAt(0));
		}
	};
	
	private static final Property p_rowdelimiter = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(Character.toString(ctx.getRowDelimiter()));
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			String s = value.toTextString(ctx);
			ctx.setRowDelimiter((s.length() < 1) ? '\uFFF1' : s.charAt(0));
		}
	};
	
	private static final Property p_littleendian = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return ctx.getLittleEndian() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			ctx.setLittleEndian(XOMBooleanType.instance.makeInstanceFrom(ctx, value).toBoolean());
		}
	};
	
	private static final Property p_unsigned = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return ctx.getUnsigned() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			ctx.setUnsigned(XOMBooleanType.instance.makeInstanceFrom(ctx, value).toBoolean());
		}
	};
	
	private static final Property p_numberformat = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(ctx.getNumberFormat().pattern());
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			ctx.setNumberFormat(value.toTextString(ctx));
		}
	};
	
	private static final Property p_textencoding = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(ctx.getTextEncoding());
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			ctx.setTextEncoding(value.toTextString(ctx));
		}
	};
	
	private static final Property p_lineending = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMString(ctx.getLineEnding());
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			ctx.setLineEnding(value.toTextString(ctx));
		}
	};
	
	private static final Property p_mathprocessor = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			String s = ctx.getMathProcessor().getClass().getCanonicalName();
			if (s.startsWith("com.kreative.openxion.math.") && s.endsWith("Math")) {
				return new XOMString(s.substring(27, s.length()-4).toLowerCase());
			} else {
				return new XOMString(s);
			}
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			String s = value.toTextString(ctx);
			if (!s.equals("") && !s.contains(".")) {
				s = "com.kreative.openxion.math."+s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase()+"Math";
			}
			try {
				MathProcessor mp = (MathProcessor)Class.forName(s).newInstance();
				ctx.setMathProcessor(mp);
				ctx.setMathProcessor(mp);
			} catch (Exception e) {
				throw new XNScriptError(e, "Unsupported math processor "+s);
			}
		}
	};
	
	private static final Property p_precision = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			return new XOMInteger(ctx.getMathContext().getPrecision());
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			int prec = XOMIntegerType.instance.makeInstanceFrom(ctx, value, true).toInt();
			if (prec < 0) prec = 0;
			ctx.setMathContext(new MathContext(prec, ctx.getMathContext().getRoundingMode()));
		}
	};
	
	private static final Property p_roundingmode = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			RoundingMode rm = ctx.getMathContext().getRoundingMode();
			switch (rm) {
			case CEILING: return new XOMString("ceiling");
			case FLOOR: return new XOMString("floor");
			case UP: return new XOMString("up");
			case DOWN: return new XOMString("down");
			case HALF_UP: return new XOMString("nearest");
			case HALF_EVEN: return new XOMString("even");
			case HALF_DOWN: return new XOMString("nearest-down");
			case UNNECESSARY: return new XOMString("exact");
			default: return new XOMString(rm.name().toLowerCase());
			}
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			String s = value.toTextString(ctx).toLowerCase();
			RoundingMode rm;
			if (s.equals("ceiling")) rm = RoundingMode.CEILING;
			else if (s.equals("floor")) rm = RoundingMode.FLOOR;
			else if (s.equals("up")) rm = RoundingMode.UP;
			else if (s.equals("down")) rm = RoundingMode.DOWN;
			else if (s.equals("nearest")) rm = RoundingMode.HALF_UP;
			else if (s.equals("even")) rm = RoundingMode.HALF_EVEN;
			else if (s.equals("nearest-down")) rm = RoundingMode.HALF_DOWN;
			else if (s.equals("exact")) rm = RoundingMode.UNNECESSARY;
			else {
				rm = null;
				for (RoundingMode r : RoundingMode.values()) {
					if (r.name().equalsIgnoreCase(s)) {
						rm = r;
						break;
					}
				}
				if (rm == null) throw new XNScriptError("Unsupported rounding mode "+s);
			}
			ctx.setMathContext(new MathContext(ctx.getMathContext().getPrecision(), rm));
		}
	};
}
