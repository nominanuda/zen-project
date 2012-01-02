/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xml.XMLLib;
import org.w3c.dom.Node;

public class E4X {
	public static XML node2XML(Node node, Scriptable prototypeScope) {
		XmlNode e4XNode = XmlNode.createElementFromNode(node);
		XMLLibImpl xmlLibImpl = (XMLLibImpl)XMLLib.extractFromScopeOrNull(prototypeScope);
		XML xml = xmlLibImpl.newXML(e4XNode);
		return xml;
	}
}
