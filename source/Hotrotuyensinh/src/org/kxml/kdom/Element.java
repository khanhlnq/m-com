/* kXML
 *
 * The contents of this file are subject to the Enhydra Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License
 * on the Enhydra web site ( http://www.enhydra.org/ ).
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * The Initial Developer of kXML is Stefan Haustein. Copyright (C)
 * 2000, 2001 Stefan Haustein, D-46045 Oberhausen (Rhld.),
 * Germany. All Rights Reserved.
 *
 * Contributor(s): Paul Palaszewski, Wilhelm Fitzpatrick, 
 *                 Eric Foster-Johnson
 *
 * */

package org.kxml.kdom;

import java.io.IOException;
import java.util.Vector;

import org.kxml.Attribute;
import org.kxml.PrefixMap;
import org.kxml.Xml;
import org.kxml.io.AbstractXmlWriter;
import org.kxml.parser.AbstractXmlParser;
import org.kxml.parser.StartTag;

/**
 * In order to create an element, please use the createElement method instead of
 * invoking the constructor directly. The right place to add user defined
 * initialization code is the init method.
 */

public class Element extends Node {

    protected String namespace;
    protected String name;
    protected Vector attributes;
    protected Node parent;
    protected PrefixMap prefixMap;

    public Element() {
    }

    /** removes all children and attributes */

    public void clear() {
        setAttributes(new Vector());

        for (int i = getChildCount() - 1; i >= 0; i--)
            removeChild(i);
    }

    /**
     * Forwards creation request to parent if any, otherwise calls
     * super.createElement. Please note: For no namespace, please use
     * Xml.NO_NAMESPACE, null is not a legal value. Currently, null is converted
     * to Xml.NO_NAMESPACE, but future versions may throw an exception.
     */

    public Element createElement(String namespace, String name) {

        return (this.parent == null) ? super.createElement(namespace, name)
                : this.parent.createElement(namespace, name);
    }

    /** Returns the attribute at the given index. */

    public Attribute getAttribute(int index) {
        return (Attribute) attributes.elementAt(index);
    }

    /** convenience method for getAttribute (Xml.NO_NAMESPACE, name) */

    public Attribute getAttribute(String name) {
        return getAttribute(Xml.NO_NAMESPACE, name);
    }

    /**
     * returns the attribute with the given namespace and name. Please use null
     * as placeholder for any namespace or Xml.NO_NAMESPACE for no namespace.
     */

    public Attribute getAttribute(String namespace, String name) {

        int len = getAttributeCount();

        for (int i = 0; i < len; i++) {
            Attribute attr = getAttribute(i);
            if (name.equals(attr.getName())
                    && (namespace == null || namespace.equals(attr
                            .getNamespace())))

                return attr;
        }
        return null;
    }

    /** Returns the number of attributes of this element. */

    public int getAttributeCount() {
        return attributes == null ? 0 : attributes.size();
    }

    /**
     * Returns a Vector containing all Attributes. The Vector is not copied.
     * Modification is not allowed.
     */

    public Vector getAttributes() {
        return attributes;
    }

    /**
     * Returns the document this element is a member of. The document is
     * determined by ascending to the parent of the root element. If the element
     * is not contained in a document, null is returned.
     */

    public Document getDocument() {

        if (parent instanceof Document)
            return (Document) parent;

        if (parent instanceof Element)
            return ((Element) parent).getDocument();

        return null;
    }

    /** returns the (local) name of the element */

    public String getName() {
        return name;
    }

    /** returns the namespace of the element */

    public String getNamespace() {
        return namespace;
    }

    /** Returns the parent node of this element */

    public Node getParent() {
        return parent;
    }

    /** Returns the parent element if available, null otherwise */

    public Element getParentElement() {
        return (parent instanceof Element) ? ((Element) parent) : null;
    }

    /** Returns the namespace prefix map of this Element. */

    public PrefixMap getPrefixMap() {
        return prefixMap;
    }

    /**
     * Returns the value of the given attribute. If the attribute does not
     * exist, an exception is thrown. If a null value shall be returned for not
     * existing attributes, please use getValueDefault (name, null) instead.
     */

    public String getValue(String name) {
        Attribute attr = getAttribute(Xml.NO_NAMESPACE, name);
        return attr == null ? null : attr.getValue();
    }

    /**
     * Returns the value of the given attribute, or the given default value if
     * the desired attribute does not exist.
     */

    public String getValueDefault(String name, String dflt) {
        Attribute attr = getAttribute(Xml.NO_NAMESPACE, name);
        return attr == null ? dflt : attr.getValue();
    }

    /**
     * @deprecated The init method is invoked by createElement setParent .
     */

    public Element init(Node parent, String namespace, String name,
            Vector attributes) {
        this.parent = parent;
        return this;
    }

    /**
     * Builds the child elements from the given Parser. By overwriting parse, an
     * element can take complete control over parsing its subtree.
     */

    public void parse(AbstractXmlParser parser) throws IOException {

        StartTag startTag = (StartTag) parser.read();

        name = startTag.getName();
        namespace = startTag.getNamespace();
        attributes = startTag.getAttributes();
        setPrefixMap(startTag.getPrefixMap());

        super.parse(parser);

        if (startTag != null && !startTag.getDegenerated()
                && getChildCount() == 0)

            addChild(Xml.WHITESPACE, "");

        parser.read(Xml.END_TAG, startTag.getNamespace(), startTag.getName());
    }

    /** Removes the attribute at the given index */

    public void removeAttribute(int index) {
        attributes.removeElementAt(index);
    }

    /** sets the given attribute */

    public void setAttribute(Attribute attribute) {
        if (attributes == null)
            attributes = new Vector();
        else
            for (int i = attributes.size() - 1; i >= 0; i--) {
                Attribute attr = (Attribute) attributes.elementAt(i);
                if (attr.getName().equals(attribute.getName())
                        && attr.getNamespace().equals(attribute.getNamespace())) {
                    attributes.setElementAt(attribute, i);
                    return;
                }
            }

        attributes.addElement(attribute);
    }

    /**
     * Replaces all attributes by the given Vector. Caution: The Vector is not
     * copied.
     */

    public void setAttributes(Vector attributes) {
        this.attributes = attributes;
    }

    /** sets the name of the element */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets the namespace of the element. Please note: For no namespace, please
     * use Xml.NO_NAMESPACE, null is not a legal value. Currently, null is
     * converted to Xml.NO_NAMESPACE, but future versions may throw an
     * exception.
     */

    public void setNamespace(String namespace) {
        this.namespace = namespace == null ? Xml.NO_NAMESPACE : namespace;
    }

    /**
     * Sets the Parent of this element. Automatically called from the add
     * method. The right place for replacements of the deprectated init method.
     * Please use with care, you can simply create inconsitencies in the
     * document tree structure using this method!
     */

    protected void setParent(Node parent) {
        this.parent = parent;
        if (init(parent, namespace, name, attributes) != this)
            throw new RuntimeException("init must return this");
    }

    /** Sets the namespace prefix map of this Node. */

    public void setPrefixMap(PrefixMap prefixMap) {
        this.prefixMap = prefixMap;
    }

    /**
     * sets the value of the given attribute to the given string. Convenience
     * method for setAttribute (new Attribute (name, value));
     */

    public void setValue(String name, String value) {
        setAttribute(new Attribute(name, value));
    }

    /** Writes this element and all children to the given XmlWriter. */

    public void write(AbstractXmlWriter writer) throws IOException {

        writer.startTag(getPrefixMap(), getNamespace(), getName());

        int len = getAttributeCount();

        for (int i = 0; i < len; i++) {
            Attribute attr = getAttribute(i);
            writer.attribute(attr.getNamespace(), attr.getName(), attr
                    .getValue());
        }

        writeChildren(writer);

        writer.endTag();
    }
}
