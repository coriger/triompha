/*
 *
 * @author chenqj
 * Created on 2007-4-20
 *
 */
package com.triompha.common.web.html;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.HTMLEntities;
import org.cyberneko.html.filters.Writer;


public class HtmlWriter extends Writer {
	/**
	 *
	 */
	public HtmlWriter() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws UnsupportedEncodingException
	 */
	public HtmlWriter(OutputStream arg0, String arg1) throws UnsupportedEncodingException {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public HtmlWriter(java.io.Writer arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * 覆盖父类的实现，以支持"<br />
	 * "这种空元素。
	 *
	 * @see org.cyberneko.html.filters.Writer#emptyElement(org.apache.xerces.xni.QName,
	 *      org.apache.xerces.xni.XMLAttributes, org.apache.xerces.xni.Augmentations)
	 */
	@Override
	public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		fSeenRootElement = true;
		printEmptyElement(element, attributes);
		if (fDocumentHandler != null) {
			fDocumentHandler.emptyElement(element, attributes, augs);
		}
	}

	protected void printEmptyElement(QName element, XMLAttributes attributes) {
		// modify META[@http-equiv='content-type']/@content value
		int contentIndex = -1;
		String originalContent = null;
		if (element.rawname.toLowerCase().equals("meta")) {
			String httpEquiv = null;
			int length = attributes.getLength();
			for (int i = 0; i < length; i++) {
				String aname = attributes.getQName(i).toLowerCase();
				if (aname.equals("http-equiv")) {
					httpEquiv = attributes.getValue(i);
				} else if (aname.equals("content")) {
					contentIndex = i;
				}
			}
			if (httpEquiv != null && httpEquiv.toLowerCase().equals("content-type")) {
				fSeenHttpEquiv = true;
				String content = null;
				if (contentIndex != -1) {
					originalContent = attributes.getValue(contentIndex);
					content = originalContent.toLowerCase();
				}
				if (content != null) {
					int charsetIndex = content.indexOf("charset=");
					if (charsetIndex != -1) {
						content = content.substring(0, charsetIndex + 8);
					} else {
						content += ";charset=";
					}
					content += fEncoding;
					attributes.setValue(contentIndex, content);
				}
			}
		}
		// print element
		fPrinter.print('<');
		fPrinter.print(element.rawname);
		int attrCount = attributes != null ? attributes.getLength() : 0;
		for (int i = 0; i < attrCount; i++) {
			String aname = attributes.getQName(i);
			String avalue = attributes.getValue(i);
			fPrinter.print(' ');
			fPrinter.print(aname);
			fPrinter.print("=\"");
			printAttributeValue(avalue);
			fPrinter.print('"');
		}
		fPrinter.print(" /");
		fPrinter.print('>');
		fPrinter.flush();
		// return original META[@http-equiv]/@content value
		if (contentIndex != -1) {
			attributes.setValue(contentIndex, originalContent);
		}
	}

	/**
	 * 覆盖父类的实现，不对"'"转义
	 *
	 * @see org.cyberneko.html.filters.Writer#printCharacters(org.apache.xerces.xni.XMLString, boolean)
	 */
	@Override
	protected void printCharacters(XMLString text, boolean normalize) {
		if (normalize) {
			for (int i = 0; i < text.length; i++) {
				char c = text.ch[text.offset + i];
				if (c != '\n') {
					String entity = null;
					if (c != '\'')
						entity = HTMLEntities.get(c);
					if (entity != null) {
						printEntity(entity);
					} else {
						fPrinter.print(c);
					}
				} else {
					fPrinter.println();
				}
			}
		} else {
			for (int i = 0; i < text.length; i++) {
				char c = text.ch[text.offset + i];
				fPrinter.print(c);
			}
		}
		fPrinter.flush();
	}
}
