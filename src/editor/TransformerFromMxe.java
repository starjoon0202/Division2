package editor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import element.Geometry;
import element.MxeEdgeData;
import element.MxeNodeData;

public class TransformerFromMxe {

	static String exMxeFile = "./data-misoo/1.mxe";
	static ArrayList<MxeEdgeData> edgeList = new ArrayList<MxeEdgeData>();
	static HashMap<Integer, MxeNodeData> nodeMap = new HashMap<Integer, MxeNodeData>();;
	static boolean startFlag = false;

	private static void init(){
		idAndNum = new HashMap<Integer, Integer>();
		numAndID = new HashMap<Integer, Integer>();

		postCond = new HashMap<Integer, Integer>();
		preCond = new HashMap<Integer, Integer>();
		softgoalCont = new HashMap<Integer, HashMap<Integer, Double>>();

		sgNameMap = new HashMap<Integer, String>();		
		queue = new ArrayDeque<>();
		
		goalIdAndID = new HashMap<Integer, String>();

		v = -1;
		edgeList = new ArrayList<MxeEdgeData>();
		nodeMap = new HashMap<Integer, MxeNodeData>();;
		startFlag = false;
	}


	public static void overwriteMxe(String path) throws ParserConfigurationException, SAXException, IOException {
		exMxeFile = path;
		File fXmlFile = new File(exMxeFile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();
//		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("mxCell");

		parseXML(nList);

		int topGoalID = searchTop();

		// DFS for Parse Goal ID
		search(nodeMap, edgeList);

		BufferedReader br = new BufferedReader(new FileReader(exMxeFile));
		String mxeText = "";
		String str;
		while ((str = br.readLine()) != null) {
			mxeText = mxeText + str + "\n";
		}
		br.close();
		BufferedWriter bw = new BufferedWriter(new FileWriter(exMxeFile));
		bw.write(mxeText);
		bw.write("<STARTJSON>\n");
		bw.write("{\"goals\":[\n");

		MxeNodeData topNode = nodeMap.get(topGoalID);
		String fileName = exMxeFile.split("/")[exMxeFile.split("/").length - 1].replace(".mxe", "");
		fileName = fileName.split("\\\\")[fileName.split("\\\\").length - 1];
		writeNodeInfo(bw, topNode, true, fileName);
		bw.write(",\n\n");

		// Write Bottom Goals except SoftGoals
		Iterator<Integer> iter = goalIdAndID.keySet().iterator();
		int num = 0;
		while (iter.hasNext()) {
			int key = iter.next();
			num++;
			if (key == idAndNum.get(topGoalID))
				continue;

			MxeNodeData node = nodeMap.get(numAndID.get(key));
			writeNodeInfo(bw, node, false, fileName);
			if (num != goalIdAndID.size() - 1)
				bw.write(",\n\n");
			else if (sgNameMap.size() != 0)
				bw.write(",\n\n");
			else
				bw.write("\n\n");
		}

		// Write Softgoal
		iter = sgNameMap.keySet().iterator();
		num = 0;
		while (iter.hasNext()) {
			int key = iter.next();
			MxeNodeData node = nodeMap.get(numAndID.get(key));
			writeNodeInfo(bw, node, false, fileName);
			if (num != sgNameMap.size() - 1)
				bw.write(",\n\n");
			else
				bw.write("\n\n");
			num++;
		}

		bw.write("\n]\n}");
		bw.write("<ENDJSON>\n");
		bw.close();
	}

	public static void parseJson(String path) throws ParserConfigurationException, SAXException, IOException {
		init();
		exMxeFile = path;
		File fXmlFile = new File(exMxeFile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("mxCell");

		parseXML(nList);

		int topGoalID = searchTop();

		// DFS for Parse Goal ID
		search(nodeMap, edgeList);

		BufferedWriter bw = new BufferedWriter(new FileWriter(exMxeFile.replace(".mxe", ".json")));
		bw.write("{\"goals\":[\n");

		// Write Root Goal
		MxeNodeData topNode = nodeMap.get(topGoalID);
		String fileName = exMxeFile.split("/")[exMxeFile.split("/").length - 1].replace(".mxe", "");
		fileName = fXmlFile.getName().replace(".mxe", "");
		writeNodeInfo(bw, topNode, true, fileName);
		bw.write(",\n\n");

		// Write Bottom Goals except SoftGoals
		Iterator<Integer> iter = goalIdAndID.keySet().iterator();
		int num = 0;
		while (iter.hasNext()) {
			int key = iter.next();
			num++;
			if (key == idAndNum.get(topGoalID))
				continue;

			MxeNodeData node = nodeMap.get(numAndID.get(key));
			writeNodeInfo(bw, node, false, fileName);
			if (num != goalIdAndID.size() - 1)
				bw.write(",\n\n");
			else if (sgNameMap.size() != 0)
				bw.write(",\n\n");
			else
				bw.write("\n\n");
		}

		// Write Softgoal
		iter = sgNameMap.keySet().iterator();
		num = 0;
		while (iter.hasNext()) {
			int key = iter.next();
			MxeNodeData node = nodeMap.get(numAndID.get(key));
			writeNodeInfo(bw, node, false, fileName);
			if (num != sgNameMap.size() - 1)
				bw.write(",\n\n");
			else
				bw.write("\n\n");
			num++;
		}

		bw.write("\n]\n}");
		bw.close();
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(exMxeFile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("mxCell");

		parseXML(nList);

		int topGoalID = searchTop();

		// DFS for Parse Goal ID
		search(nodeMap, edgeList);

		BufferedWriter bw = new BufferedWriter(new FileWriter(exMxeFile + ".json"));
		bw.write("{\"goals\":[\n");

		MxeNodeData topNode = nodeMap.get(topGoalID);
		String fileName = exMxeFile.split("/")[exMxeFile.split("/").length - 1].replace(".mxe", "");
		writeNodeInfo(bw, topNode, true, fileName);
		bw.write(",\n\n");

		// Write Bottom Goals except SoftGoals
		Iterator<Integer> iter = goalIdAndID.keySet().iterator();
		int num = 0;
		while (iter.hasNext()) {
			int key = iter.next();
			num++;
			if (key == idAndNum.get(topGoalID))
				continue;

			MxeNodeData node = nodeMap.get(numAndID.get(key));
			writeNodeInfo(bw, node, false, fileName);
			if (num != goalIdAndID.size() - 1)
				bw.write(",\n\n");
			else if (sgNameMap.size() != 0)
				bw.write(",\n\n");
			else
				bw.write("\n\n");
		}

		// Write Softgoal
		iter = sgNameMap.keySet().iterator();
		num = 0;
		while (iter.hasNext()) {
			int key = iter.next();
			MxeNodeData node = nodeMap.get(numAndID.get(key));
			writeNodeInfo(bw, node, false, fileName);
			if (num != sgNameMap.size() - 1)
				bw.write(",\n\n");
			else
				bw.write("\n\n");
			num++;
		}

		bw.write("\n]\n}");
		bw.close();
	}

	private static int searchTop() {
		int topGoalID = -1;
		for (int i = 0; i < edgeList.size(); i++) {
			MxeEdgeData edgeData = edgeList.get(i);
			int sourceID = Integer.parseInt(edgeData.getSource());
			int targetID = Integer.parseInt(edgeData.getTarget());
			if (topGoalID == -1) {
				topGoalID = targetID;
			}

			MxeNodeData sourceNode = nodeMap.get(sourceID);
			MxeNodeData targetNode = nodeMap.get(targetID);
			// System.out.println(sourceNode.getNodeName() + " -> " +
			// targetNode.getNodeName() + " : "
			// + edgeData.getEdgeType() + " ( " + edgeData.getValue() + " )");

			if (topGoalID == sourceID) {
				topGoalID = targetID;
			}
		}
//		System.out.println(topGoalID + " " + nodeMap.get(topGoalID).getNodeName());
		return topGoalID;
	}

	private static void parseXML(NodeList nList) {
		// TODO Auto-generated method stub
		// Read MXE File
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.getAttributes().getLength() > 2) {
				Element eElement = (Element) nNode;
				if (nNode.getAttributes().getNamedItem("edge") == null) {
					MxeNodeData nodeData = new MxeNodeData();
					String id = eElement.getAttribute("id");
					nodeData.setNodeID(id);
					nodeData.setNodeName(eElement.getAttribute("value").replace("\n", " "));
					String style = eElement.getAttribute("style");
					// System.out.println(style);
					if (style.contains("shape=hexagon")) {
						nodeData.setNodeType("Task");
						nodeData.setNodeInfo(style.replace("shape=hexagon;fillColor=yellow;;", ""));
					} else if (style.contains("ellipse;shape=cloud")) {
						nodeData.setNodeType("SoftGoal");
						nodeData.setNodeInfo(style.replace("ellipse;shape=cloud;fillColor=skyblue;;", ""));
					} else if (style.contains("ellipse")) {
						nodeData.setNodeType("HardGoal");
						nodeData.setNodeInfo(style.replace("ellipse", ""));
					} else if (style.contains("rectangle;;resourcename=")) {
						nodeData.setNodeType("Resource");
						nodeData.setNodeInfo(style.replace("rectangle;", ""));
					} else if (style.contains("rectangle;shape=doubleRectangle")) {
						nodeData.setNodeType("Condition");
						nodeData.setNodeInfo(style.replace("rectangle;shape=doubleRectangle;", ""));
					} else {
						System.err.println("UN-SUITABLE NODE TYPE : " + nodeData.getNodeID() + " "
								+ nodeData.getNodeName() + " " + style);
					}

					Geometry geo = new Geometry();
					NodeList geoList = eElement.getElementsByTagName("mxGeometry");
					for (int i = 0; i < geoList.getLength(); i++) {
						Node geoNode = geoList.item(i);
						if (geoNode.getNodeType() == Node.ELEMENT_NODE) {
							Element geoEle = (Element) geoNode;

							geo.setHeight(geoEle.getAttribute("height"));
							geo.setWidth(geoEle.getAttribute("width"));
							geo.setX(geoEle.getAttribute("x"));
							geo.setY(geoEle.getAttribute("y"));
						}
					}

					nodeData.setGeo(geo);
					nodeMap.put(Integer.parseInt(id), nodeData);
				} else {
					MxeEdgeData edgeData = new MxeEdgeData();
					edgeData.setEdgeID(eElement.getAttribute("id"));
					edgeData.setSource(eElement.getAttribute("source"));
					edgeData.setTarget(eElement.getAttribute("target"));
					if (!eElement.getAttribute("value").equals(""))
						edgeData.setValue(eElement.getAttribute("value"));
					else
						edgeData.setValue("1.0");

					String style = eElement.getAttribute("style");
					if (style.contains("dashed"))
						edgeData.setEdgeType("OR");
					else
						edgeData.setEdgeType("AND");

					try {
						double num = Double.parseDouble(eElement.getAttribute("value"));
					} catch (Exception e) {
						if (eElement.getAttribute("value").contains("post")) {
							edgeData.setEdgeType("COND");
						} else if (eElement.getAttribute("value").contains("+")
								|| eElement.getAttribute("value").contains("-")
								|| eElement.getAttribute("value").contains("=")
								|| eElement.getAttribute("value").contains("?")) {
							edgeData.setEdgeType("CONT");
						}
					}

					Geometry geo = new Geometry();
					NodeList geoList = eElement.getElementsByTagName("mxGeometry");
					for (int i = 0; i < geoList.getLength(); i++) {
						Node geoNode = geoList.item(i);
						if (geoNode.getNodeType() == Node.ELEMENT_NODE) {
							Element geoEle = (Element) geoNode;

							geo.setHeight(geoEle.getAttribute("height"));
							geo.setWidth(geoEle.getAttribute("width"));

							NodeList pointList = geoEle.getElementsByTagName("mxPoint");
							for (int j = 0; j < pointList.getLength(); j++) {
								Node pointNode = pointList.item(j);
								if (pointNode.getNodeType() == Node.ELEMENT_NODE) {
									Element pointEle = (Element) pointNode;
									if (geo.getSourceX() == null) {
										geo.setSourceX(pointEle.getAttribute("x"));
										geo.setSourceY(pointEle.getAttribute("y"));
									} else {
										geo.setTargetX(pointEle.getAttribute("x"));
										geo.setTargetY(pointEle.getAttribute("y"));
									}
								}
							}

						}
					}

					edgeData.setGeo(geo);
					edgeList.add(edgeData);
				}
			}
		}
	}

	private static void writeNodeInfo(BufferedWriter bw, MxeNodeData node, boolean top, String domainName)
			throws IOException {

		boolean cond = false;
		// System.out.println(idAndNum);
		// System.out.println(goalIdAndID);
		if (node.getNodeType().toLowerCase().equals("softgoal")) {
			bw.write(
					"\t\t{\"NodeID\" : \"" + sgNameMap.get(idAndNum.get(Integer.parseInt(node.getNodeID()))) + "\",\n");
		} else {
			bw.write("\t\t{\"NodeID\" : \"" + goalIdAndID.get(idAndNum.get(Integer.parseInt(node.getNodeID())))
					+ "\",\n");
		}
		bw.write("\t\t\"NodeName\" : \"" + node.getNodeName() + "\",\n");

		if (top) {
			bw.write("\t\t\"ParentNode\" : \"\",\n");
		} else if (node.getNodeType().toLowerCase().equals("softgoal")) {
			bw.write("\t\t\"ParentNode\" : \"G\",\n");
		} else {
			for (int i = 0; i < edgeList.size(); i++) {
				MxeEdgeData edgeData = edgeList.get(i);
				int sourceID = Integer.parseInt(edgeData.getSource());
				int targetID = Integer.parseInt(edgeData.getTarget());

				if (sourceID != Integer.parseInt(node.getNodeID()))
					continue;

				if (nodeMap.get(targetID).getNodeType().toLowerCase().equals("softgoal"))
					continue;

				MxeNodeData parentNode = nodeMap.get(targetID);
				String goalID = goalIdAndID.get(idAndNum.get(Integer.parseInt(parentNode.getNodeID())));

				bw.write("\t\t\"ParentNode\" : \"" + goalID + "\",\n");
				break;
			}
		}

		bw.write("\t\t\"NodeDomain\" : \"" + domainName + "\",\n");
		bw.write("\t\t\"NodeType\" : \"" + node.getNodeType() + "\",\n");
		bw.write("\t\t\"DetailedNodeInfo\" : \"" + node.getNodeInfo()+ "\",\n");
		bw.write("\t\t\"RELATION\" : {\n");
		if (node.getNodeType().toLowerCase().equals("softgoal")) {
			bw.write("\t\t\t\"RelType\" : \"AND\",\n");
			bw.write("\t\t\t\"SubNodes\" : [],\n");
			bw.write("\t\t\t\"Weight\" : [],\n");
			bw.write("\t\t\t\"Contribution\" : []\n");
		} else {
			ArrayList<MxeNodeData> subNodes = new ArrayList<MxeNodeData>();
			ArrayList<String> subNodeTypes = new ArrayList<String>();
			ArrayList<Double> subNodeValues = new ArrayList<Double>();
			for (int i = 0; i < edgeList.size(); i++) {
				MxeEdgeData edgeData = edgeList.get(i);
				int sourceID = Integer.parseInt(edgeData.getSource());
				int targetID = Integer.parseInt(edgeData.getTarget());
				if (targetID != Integer.parseInt(node.getNodeID()))
					continue;

				if (edgeData.getValue().equals("post")) {
					cond = true;
					continue;
				}
				MxeNodeData subNode = nodeMap.get(sourceID);
				subNodes.add(subNode);
				subNodeTypes.add(edgeData.getEdgeType());

				try {
					subNodeValues.add(Double.parseDouble(edgeData.getValue()));
				} catch (NumberFormatException e) {
					double contValue = 0.0;
					switch (edgeData.getValue()) {
					case "++":
						contValue = 1.0;
						break;
					case "+":
						contValue = 0.5;
						break;
					case "?":
						contValue = 0.0;
						break;
					case "=":
						contValue = 0.0;
						break;
					case "-":
						contValue = -0.5;
						break;
					case "--":
						contValue = -1.0;
						break;
					default:
						System.err.println("SG CONT VALUE ERROR !");
						break;
					}
					subNodeValues.add(contValue);
				}

			}

			if (subNodes.size() == 0) {
				bw.write("\t\t\t\"RelType\" : \"AND\",\n");
				bw.write("\t\t\t\"SubNodes\" : [],\n");
				bw.write("\t\t\t\"Weight\" : [],\n");

				if (softgoalCont.containsKey(Integer.parseInt(node.getNodeID()))) {
					HashMap<Integer, Double> data = softgoalCont.get(Integer.parseInt(node.getNodeID()));
					Iterator<Integer> iter = data.keySet().iterator();
					bw.write("\t\t\t\"Contribution\" : [");
					int num = 0;
					while (iter.hasNext()) {
						num++;
						int key = iter.next();
						double value = data.get(key);
						bw.write("[\"" + sgNameMap.get(idAndNum.get(key)) + "\"," + value);
						if (num == data.keySet().size()) {
							bw.write("]");
						} else {
							bw.write("],");
						}
					}
					bw.write("]");
				} else {
					bw.write("\t\t\t\"Contribution\" : []");
				}
			} else {
				String type = "";
				boolean choice = false;
				for (int i = 0; i < subNodeTypes.size(); i++) {
					if (i != 0 && !type.equals(subNodeTypes.get(i))) {
						choice = true;
//						System.out.println(subNodes.get(i));
						break;
					}
					type = subNodeTypes.get(i);
				}
				if (choice) {
					System.err.println("IGNORE CHOICE CASES!!! ");
					type = "AND";
				}

				bw.write("\t\t\t\"RelType\" : \"" + type + "\",\n");
				bw.write("\t\t\t\"SubNodes\" : [");
				for (int i = 0; i < subNodes.size() - 1; i++) {
					String goalID = goalIdAndID.get(idAndNum.get(Integer.parseInt(subNodes.get(i).getNodeID())));
					bw.write("\"" + goalID + "\",");
				}
				if (top) {
					Iterator<Integer> iter = sgNameMap.keySet().iterator();
					while (iter.hasNext()) {
						int key = iter.next();
						bw.write("\"" + sgNameMap.get(key) + "\",");
					}
				}
				String goalID = goalIdAndID
						.get(idAndNum.get(Integer.parseInt(subNodes.get(subNodes.size() - 1).getNodeID())));
				bw.write("\"" + goalID + "\"],\n");

				bw.write("\t\t\t\"Weight\" : [");
				for (int i = 0; i < subNodeValues.size() - 1; i++) {
					bw.write(subNodeValues.get(i) + ",");
				}
				if (top) {
					Iterator<Integer> iter = sgNameMap.keySet().iterator();
					while (iter.hasNext()) {
						int key = iter.next();
						bw.write("-1.0,");
					}
				}

				bw.write(subNodeValues.get(subNodeValues.size() - 1) + "],\n");

				if (softgoalCont.containsKey(Integer.parseInt(node.getNodeID()))) {
					HashMap<Integer, Double> data = softgoalCont.get(Integer.parseInt(node.getNodeID()));
					Iterator<Integer> iter = data.keySet().iterator();
					bw.write("\t\t\t\"Contribution\" : [");
					int num = 0;
					while (iter.hasNext()) {
						num++;
						int key = iter.next();
						double value = data.get(key);
						bw.write("[\"" + sgNameMap.get(idAndNum.get(key)) + "\"," + value);
						if (num == data.keySet().size()) {
							bw.write("]");
						} else {
							bw.write("],");
						}
					}
					bw.write("]");
				} else {
					bw.write("\t\t\t\"Contribution\" : []");
				}
			}
		}
		bw.write("\t\t},\n");

		bw.write("\t\t\"Condition\" : {\n");
		boolean preFlag = false;
		boolean postFlag = false;
		if (preCond.containsKey(Integer.parseInt(node.getNodeID()))) {
			bw.write("\t\t\t\"PreCond\" : [\""
					+ goalIdAndID.get(idAndNum.get(preCond.get(Integer.parseInt(node.getNodeID())))) + "\"],\n");
			preFlag = true;
		} else {
			bw.write("\t\t\t\"PreCond\" : [],\n");
		}

		if (postCond.containsKey(Integer.parseInt(node.getNodeID()))) {
			bw.write("\t\t\t\"PostCond\" : [\""
					+ goalIdAndID.get(idAndNum.get(postCond.get(Integer.parseInt(node.getNodeID())))) + "\"]\n");
			postFlag = true;
		} else {
			bw.write("\t\t\t\"PostCond\" : []\n");
		}

		bw.write("\t\t},\n");

		bw.write("\t\t\"Evaluation\" : 0.0 }");

	}

	static HashMap<Integer, String> goalIdAndID = new HashMap<Integer, String>();

	static int v = -1;
	static HashMap<Integer, Integer> idAndNum = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> numAndID = new HashMap<Integer, Integer>();

	static HashMap<Integer, Integer> postCond = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> preCond = new HashMap<Integer, Integer>();
	static HashMap<Integer, HashMap<Integer, Double>> softgoalCont = new HashMap<Integer, HashMap<Integer, Double>>();

	static HashMap<Integer, String> sgNameMap = new HashMap<Integer, String>();

	public static void search(HashMap<Integer, MxeNodeData> nodeMap, ArrayList<MxeEdgeData> edgeList) {
		v = nodeMap.size();

		// Sequence Num and Node ID Mapping
		int num = 0;
		Iterator<Integer> iter = nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			int key = iter.next();
			idAndNum.put(key, num);
			numAndID.put(num, key);
			num++;
		}
		//
		// System.out.println(idAndNum + " " + v);
		// System.out.println(numAndID + " " + v);

		// Parse the Neighbor Node
		// Find Top Goal ID
		int topGoalID = -1;
		int sgID = 0;
		for (int i = 0; i < edgeList.size(); i++) {
			MxeEdgeData edgeData = edgeList.get(i);
			int sourceID = Integer.parseInt(edgeData.getSource());
			int targetID = Integer.parseInt(edgeData.getTarget());

			if (edgeData.getEdgeType().equals("COND")) {
				// System.out.println(sourceID+" "+targetID);
				postCond.put(sourceID, targetID);
				preCond.put(targetID, sourceID);
				continue;
			}

			if (topGoalID == -1) {
				topGoalID = targetID;
			}

			MxeNodeData sourceNode = nodeMap.get(sourceID);
			MxeNodeData targetNode = nodeMap.get(targetID);

//			System.out.println(targetNode);
//			System.out.println(sourceNode);

			if (targetNode.getNodeType().equals("softgoal")) {

				if (!sgNameMap.containsKey(idAndNum.get(Integer.parseInt(targetNode.getNodeID())))) {
					sgNameMap.put(idAndNum.get(Integer.parseInt(targetNode.getNodeID())), "SG" + sgID);
					sgID++;
				}
				double contValue = 0.0;
				switch (edgeData.getValue()) {
				case "++":
					contValue = 1.0;
					break;
				case "+":
					contValue = 0.5;
					break;
				case "?":
					contValue = 0.0;
					break;
				case "=":
					contValue = 0.0;
					break;
				case "-":
					contValue = -0.5;
					break;
				case "--":
					contValue = -1.0;
					break;
				default:
					System.err
							.println("SG CONT VALUE ERROR !" + edgeData + " " + contValue + " " + edgeData.getValue());
					break;
				}

				if (softgoalCont.containsKey(Integer.parseInt(sourceNode.getNodeID()))) {
					HashMap<Integer, Double> data = softgoalCont.get(Integer.parseInt(sourceNode.getNodeID()));
					data.put(Integer.parseInt(targetNode.getNodeID()), contValue);
					softgoalCont.replace(Integer.parseInt(sourceNode.getNodeID()),
							(HashMap<Integer, Double>) data.clone());
				} else {
					HashMap<Integer, Double> data = new HashMap<Integer, Double>();
					data.put(Integer.parseInt(targetNode.getNodeID()), contValue);
					softgoalCont.put(Integer.parseInt(sourceNode.getNodeID()), (HashMap<Integer, Double>) data.clone());
				}

				continue;
			}
//			System.out.println(sourceNode.getNodeName() + " -> " + targetNode.getNodeName() + " : "
//					+ edgeData.getEdgeType() + " ( " + edgeData.getValue() + " )");

			if (topGoalID == sourceID) {
				topGoalID = targetID;
			}
		}

		// Depth First Search
		initSearch(edgeList, nodeMap);
		// System.out.println(idAndNum.get(topGoalID)+" Top Goal");
		goalIdAndID.put(idAndNum.get(topGoalID), "G");
		dfs(idAndNum.get(topGoalID));
		// bfs(idAndNum.get(topGoalID));

//		System.out.println(goalIdAndID);

	}

	// 인접 리스트
	static ArrayList<Integer>[] adjList;

	private static void initSearch(ArrayList<MxeEdgeData> edgeList, HashMap<Integer, MxeNodeData> nodeMap) {
//		if (visited == null) {
			visited = new int[v];
			adjList = new ArrayList[v];
			for (int i = 0; i < edgeList.size(); i++) {
				MxeEdgeData edgeData = edgeList.get(i);

				if (edgeData.getEdgeType().equals("COND"))
					continue;

				int sourceID = Integer.parseInt(edgeData.getSource());
				int targetID = Integer.parseInt(edgeData.getTarget());

				MxeNodeData sourceNode = nodeMap.get(sourceID);
				MxeNodeData targetNode = nodeMap.get(targetID);
//				System.out.println(sourceNode + " " + targetNode);
				if (sourceNode.getNodeType().equals("softgoal") || targetNode.getNodeType().equals("softgoal"))
					continue;

//				System.out.println(sourceNode.getNodeName() + " ->" + targetNode.getNodeName() + " : "
//						+ edgeData.getEdgeType() + " ( " + edgeData.getValue() + " )");

				// Reverse connect direction (-> : <- for searching)
				if (adjList[idAndNum.get(targetID)] == null)
					adjList[idAndNum.get(targetID)] = new ArrayList<>();
				adjList[idAndNum.get(targetID)].add(idAndNum.get(sourceID));
//				System.out.println(idAndNum.get(targetID) + "" + adjList[idAndNum.get(targetID)]);
			}
//		}
	}

	// 방문한 정점을 표시하는 배열
	static int[] visited;

	public static void dfs(int node) {

		// 방문 정점 표시
		visited[node] = 1;

		// System.out.println(node);

		int num = 0;
		if (adjList[node] != null) {
			// 모든 인접한 정점들을 방문
			for (int adjacent : adjList[node]) {
				// 방문한 정점이면 건너 뜀
				num++;
				if (visited[adjacent] == 1) {
					// System.out.println(adjacent+" (parent = "+node+")");
					continue;
				} else {
					// System.out.println(adjacent+" parent = "+node+" "+num);
					if (goalIdAndID.containsKey(node)) {
						goalIdAndID.put(adjacent, goalIdAndID.get(node) + "." + num);
					}
				}

				// 재귀호출
				dfs(adjacent);
			}
		}
	}

	static ArrayDeque<Integer> queue = new ArrayDeque<>();
	static int[] discovered;

	public static void bfs(int start) {
		discovered = new int[v];
		int node;

		// 큐에 시작지점 추가 및 발견 표시
		queue.add(start);
		discovered[start] = 1;

		while (queue.size() > 0) {
			// 큐에 방문할 정점을 poll
			node = queue.pollFirst();

//			System.out.println(node);

			if (adjList[node] != null) {
				// 인접정점들을 발견
				for (int adjacent : adjList[node]) {
					// 이미 발견된 정점이 아니라면
					if (discovered[adjacent] == 0) {
						// 큐에 추가 및 발견 표시
						queue.add(adjacent);
						discovered[adjacent] = 1;
					}
				}
			}
		}
	}

	
}
