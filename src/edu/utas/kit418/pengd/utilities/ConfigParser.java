package edu.utas.kit418.pengd.utilities;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigParser {

	private static int row1, col1row2, col2;
	private static String mode;
	private static String errMsg;
	
	public static String getMode() {
		return mode;
	}

	public static int getRow1() {
		return row1;
	}

	public static int getCol1row2() {
		return col1row2;
	}

	public static int getCol2() {
		return col2;
	}

	public static String getErrMsg() {
		return errMsg;
	}

	public enum State {
		SUCCESS,
		FAIL
	}

	public static ConfigParser.State parse(File confFile) {
		State state = State.SUCCESS;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(confFile);
			doc.getDocumentElement().normalize();
			NodeList modeNodeList = doc.getElementsByTagName("mode");
			for(int i =0;i<modeNodeList.getLength();i++){
				Node node = modeNodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					mode = node.getTextContent().trim();
					break;
				}
			}
			modeNodeList = doc.getElementsByTagName("row1");
			for(int i = 0;i<modeNodeList.getLength();i++){
				Node node = modeNodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					row1 = Integer.parseInt(node.getTextContent().trim());
					break;
				}
			}
			modeNodeList = doc.getElementsByTagName("col1row2");
			for(int i = 0;i<modeNodeList.getLength();i++){
				Node node = modeNodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					col1row2 = Integer.parseInt(node.getTextContent().trim());
					break;
				}
			}
			modeNodeList = doc.getElementsByTagName("col2");
			for(int i = 0;i<modeNodeList.getLength();i++){
				Node node = modeNodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					col2 = Integer.parseInt(node.getTextContent().trim());
					break;
				}
			}
		} catch (Exception e){
			errMsg = e.getMessage();
			state = State.FAIL;
		}
		return state;
	}

}
