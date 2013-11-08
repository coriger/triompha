package com.triompha.common.web.html;

import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.cyberneko.html.filters.ElementRemover;

/**
 * 继承ElementRemover的实现，增加对属性内容的过滤，并提供通过构造函数直接创建acceptedElements和removedElements的方式
 *
 *
 */
public class HtmlElementFilter extends ElementRemover {
	/**
	 * 构造函数
	 *
	 * @param acceptedElements
	 *            可以接受的html elements，所有的元素名称和属性名称必须要小写
	 * @param removedElements
	 *            需要删除的html elements，所有的元素名称必须要小写
	 */
	public HtmlElementFilter(Hashtable acceptedElements, Hashtable removedElements) {
		this.fAcceptedElements = acceptedElements;
		this.fRemovedElements = removedElements;
	}

	/**
	 * 继承ElementRemover的实现，增加对属性内容的过滤
	 *
	 * @see org.cyberneko.html.filters.ElementRemover#handleOpenTag(org.apache.xerces.xni.QName,
	 *      org.apache.xerces.xni.XMLAttributes)
	 */
	@Override
	protected boolean handleOpenTag(QName element, XMLAttributes attributes) {
		if (elementAccepted(element.rawname)) {
			int maxAttributeLength = 200;
			Object key = element.rawname.toLowerCase();
			Object value = fAcceptedElements.get(key);
			if (value != NULL) {
				String[] anames = (String[]) value;
				int attributeCount = attributes.getLength();
				LOOP: for (int i = 0; i < attributeCount; i++) {
					String aname = attributes.getQName(i).toLowerCase();
					int len = anames.length;
					for (int j = 0; j < len; j++) {
						if (anames[j].equals(aname)) {
							String avalue = attributes.getValue(i);
							if (avalue != null) {
								if (("href".equals(aname) || "src".equals(aname)) || "style".equals(aname)) {
									if (avalue.length() > maxAttributeLength) {
										attributes.setValue(i, "#");
									} else {
										avalue = StringUtils.deleteWhitespace(avalue).toLowerCase();
										avalue = deleteComments(avalue);
										if (avalue.indexOf("script:") > -1 || avalue.indexOf("expression(") > -1
												|| avalue.indexOf("fromcharcode") > -1
												|| avalue.indexOf("position:") > -1) {
											attributes.setValue(i, "#");
										} else if (avalue.indexOf("&#") > -1) {
											attributes.setValue(i, StringUtils.replace(avalue, "&#", "&amp;#", -1));
										} else if (avalue.indexOf("\\00") > -1) {
											attributes.setValue(i, StringUtils.replace(avalue, "\\", "/", -1));
										}
									}
								}
							}
							continue LOOP;
						}
					}
					attributes.removeAttributeAt(i--);
					attributeCount--;
				}
			} else {
				attributes.removeAllAttributes();
			}
			return true;
		} else if (elementRemoved(element.rawname)) {
			fRemovalElementDepth = fElementDepth;
		}
		return false;
	}

	/**
	 * 删除属性中的注释
	 *
	 * @param str
	 * @return
	 */
	private String deleteComments(String str) {
		if (str == null) {
			return str;
		}
		return str.replaceAll("/\\*[^*]*?\\*/", "");
	}
}
