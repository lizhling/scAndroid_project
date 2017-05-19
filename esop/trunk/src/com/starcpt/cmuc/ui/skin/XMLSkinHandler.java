package com.starcpt.cmuc.ui.skin;

import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLSkinHandler extends DefaultHandler {

	private String elementName;	
	private String content = null;
	private HashMap<String, String> mMap;
	private String currTag = null;
	private String tagName = "name";
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		content = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if (qName.equals(currTag)) {
			mMap.put(tagName, content);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		mMap = new HashMap<String, String>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(elementName)) {
			currTag = qName;
			tagName = attributes.getValue("name");
		}
		
	}

	public HashMap<String, String> getMap(){
		return mMap;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	
}
