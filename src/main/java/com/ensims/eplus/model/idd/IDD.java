/*
 * To change this license header, choose License Headers in Project Defs.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.idd;

import com.ensims.eplus.model.js.DataJSTreeMin;
import com.ensims.eplus.model.js.DataJSTreeModel;
import com.ensims.eplus.model.EPlusVersionInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

/**
 * This class maps the IDF object definitions from the E+ IDD file. Its main data
 * set is the map between object types and IDDObject instances.
 * @author Yi
 */
public class IDD {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(IDD.class);
    
    public static final ObjectMapper Mapper = new ObjectMapper();
    static {
        // Don't throw an exception when json has extra fields you are
        // not serializing on. This is useful when you want to use a pojo
        // for deserialization and only care about a portion of the json
        Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Ignore null values when writing json.
        Mapper.setSerializationInclusion(Include.NON_NULL);

        Mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Write times as a String instead of a Long so its human readable.
//        Mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
    
    public static final String CommentChar = "!";
    public static final String PropertyChar = "\\";
    public static final String SeparatorChar = ",";
    public static final String DelimiterChar = ";";
    
    /** Static fields used until v8.7 */
    static String [] StaticFields = {
        "Lead Input;",
        "Simulation Data;"
    };
    /** Base url of online docs */
    static String OnlineDocURLBase = "https://bigladdersoftware.com/epx/docs/";
    /** Mid part of the url of online docs */
    static String OnlineDocURLMid = "/input-output-reference/";
    /** Map between group name and group docs file name */
    @JsonIgnore
    Map<String, String> GroupDocNameMap = null;
    
    /** The url part of online docs index page */
    static String IndexPage = "index.html";
    /** Map between object names and the url of the doc page extracted from the IndexPage */
    //@JsonIgnore
    Map<String, String> ObjectUrlMap = null;
    
    /** The source IDD file from which this object is created */
    String SourceFile = null;
    /** IDD version as found in the source file */
    EPlusVersionInfo Version = null;
    /** Map of groups of maps of object types and definitions */
    Map <String, Map <String, IDDObject>> Objects = new LinkedHashMap<> ();
    
    /** Object identifier defined by object type and field id */
    public static class ObjIdField {
        String Type = null;
        String Field = null;
        int FieldIdx = 0;
        
        public ObjIdField () {}
        
        public ObjIdField (String type, String field, int idx) {
            Type = type;
            Field = field;
            FieldIdx = idx;
        }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getField() {
            return Field;
        }

        public void setField(String Field) {
            this.Field = Field;
        }

        public int getFieldIdx() {
            return FieldIdx;
        }

        public void setFieldIdx(int FieldIdx) {
            this.FieldIdx = FieldIdx;
        }
    }
    /** Map of cross-references reference -> object & field/index (ObjIdField) */
    // @JsonIgnore
    Map <String, List<ObjIdField>> References = new HashMap<> ();
    /** Map of object types -> object-list fields -> list of reference names */
    // @JsonIgnore
    Map <String, Map <Integer, List<String>>> ObjList = new HashMap<> ();

    // ========= Getters and setters ==============
    
    public String getSourceFile() {
        return SourceFile;
    }

    public void setSourceFile(String SourceFile) {
        this.SourceFile = SourceFile;
    }

    public EPlusVersionInfo getVersion() {
        return Version;
    }

    public void setVersion(EPlusVersionInfo Version) {
        this.Version = Version;
    }

    public Map<String, Map<String, IDDObject>> getObjects() {
        return Objects;
    }

    public void setObjects(Map<String, Map<String, IDDObject>> Objects) {
        this.Objects = Objects;
    }

    public Map<String, List<ObjIdField>> getReferences() {
        return References;
    }

    public void setReferences(Map<String, List<ObjIdField>> References) {
        this.References = References;
    }

    public Map <String, Map <Integer, List<String>>> getObjList() {
        return ObjList;
    }

    public void setObjList(Map <String, Map <Integer, List<String>>> ObjList) {
        this.ObjList = ObjList;
    }

    @JsonIgnore
    public Map<String, String> getGroupDocNameMap() {
        return GroupDocNameMap;
    }

    @JsonIgnore
    public void setGroupDocNameMap(Map<String, String> GroupDocNameMap) {
        this.GroupDocNameMap = GroupDocNameMap;
    }
    
    //@JsonIgnore
    public Map<String, String> getObjectUrlMap() {
        return ObjectUrlMap;
    }

    //@JsonIgnore
    public void setObjectUrlMap(Map<String, String> ObjectUrlMap) {
        this.ObjectUrlMap = ObjectUrlMap;
    }

    // ========= End getters and setters ==============

    
    /** 
     * Create an IDD object by parsing the given IDD file 
     * @param src Paths to the IDD file to be parsed
     * @return The resultant IDD object
     */
    public static IDD parseIDD (String src) {
        IDD idd = new IDD();
        
        if (src != null && new File(src).exists()) {
            idd.SourceFile = src;
            try (BufferedReader fr = new BufferedReader (new FileReader (src))) {
                // Start scanning. The following variables are used to mark the current stage
                boolean scanningObj = false;
                String curGroupName = null;
                Map<String, IDDObject> curGroup = null;
                IDDObject curObj = null;
                ArrayList<IDDObjectField> curParams = null;
                
                int nextIdx = 0;
                String line = fr.readLine();
                while (line != null) {
                    line = line.trim();
                    if (! scanningObj) {
                        // the header part
                        if (line.startsWith("!IDD_Version")) {
                            idd.Version = new EPlusVersionInfo("IDD", line);
                        }else if (line.startsWith("!IDD_BUILD")) {
                            if (idd.Version != null) {
                                idd.Version.setBuildStr(line.substring(11));
                            }
                        }else if (line.length() > 0 && ! line.startsWith(CommentChar)) {
                            if (StaticFields[1].equals(line)) {
                                scanningObj = true;
                            }else if (line.startsWith("\\group ")) {
                                curGroupName = line.substring(7).trim();
                                curGroup = new LinkedHashMap<> ();
                                idd.Objects.put(curGroupName, curGroup);
                                scanningObj = true;
                            }
                        }
                    }else {
                        // Now the objects
                        if (line.length()>0 && ! line.startsWith(CommentChar)) {
                            String fieldtext;
                            String proptext;
                            // Split field text and field notes
                            int pci = line.indexOf(PropertyChar);
                            if (pci > 0) {
                                fieldtext = line.substring(0, pci).trim();
                                proptext = line.substring(pci).trim();
                            }else if (pci == 0) {
                                fieldtext = null;
                                proptext = line;
                            }else {
                                fieldtext = line;
                                proptext = null;
                            }
                            // Assign to object
                            if (fieldtext != null) {
                                // field text can only be a object name or a field name
                                boolean terminates = fieldtext.endsWith(DelimiterChar);
                                fieldtext = fieldtext.substring(0, fieldtext.length()-1).trim(); // remove the last , or ; first
                                if (curObj == null) {
                                    curObj = new IDDObject (idd, curGroupName, fieldtext);  
                                    curGroup.put(fieldtext, curObj);
                                    // set current parameter to null
                                    curParams = null;
                                    nextIdx = 0;
                                }else {
                                    String [] pieces = fieldtext.split("\\s*,\\s*");
                                    curParams = new ArrayList <>();
                                    for (String piece : pieces) {
                                        IDDObjectField fieldobj = new IDDObjectField (piece, nextIdx);
                                        curObj.addField(fieldobj);
                                        curParams.add(fieldobj);
                                        nextIdx ++;
                                    }
                                    // close obj if field text ends with ;
                                    if (terminates) {
                                        curObj = null;
                                    }
                                }
                            }
                            if (proptext != null) {
                                // Check if it is a group note
                                if (proptext.startsWith("\\group ")) {
                                    curGroupName = line.substring(7).trim();
                                    curGroup = new LinkedHashMap<> ();
                                    idd.Objects.put(curGroupName, curGroup);
                                }else if (curParams == null) {
                                    // Check if it belongs object
                                    String [] parsed = parseDefString(proptext);
                                    curObj.addDef(parsed[0], parsed[1]);
                                }else {
                                    // Otherwise it is to field
                                    for (IDDObjectField fieldobj : curParams) {
                                        String [] parsed = parseDefString(proptext);
                                        fieldobj.addDef(parsed[0], parsed[1]);
                                    }
                                }
                            }
                        }
                    }
                    line = fr.readLine();
                }
            }catch (Exception ex) {
                logger.error ("Error parsing IDD model from " + src, ex);
            }
        }
        if (idd.Version != null) {
            idd.ObjectUrlMap = extractObjectUrlMap (idd.Version);
        }
        idd.gatherReferences();
        return idd;
    }
    
    /**
     * Parse the given text into an 2-piece string array
     * @param text IDD snippet to be parsed
     * @return a 2-element String array whose first element is the Id, and the second is the value
     */
    public static String [] parseDefString (String text) {
        String [] elements = text.split("\\s+", 2);
        return new String [] {
            elements[0].substring(1), 
            elements.length > 1 ? elements[1] : ""
        };
    }
    
    public IDDObject findObject (String type) {
        IDDObject obj = null;
        for (Map <String, IDDObject> group : Objects.values()) {
            if (group.containsKey(type)) {
                obj = group.get(type);
                break;
            }
        }
        return obj;
    }
    
    public static Map<String, String> extractObjectUrlMap (EPlusVersionInfo version) {
        Map<String, String> map = new HashMap <> ();
        String verstr = version.getMajor() + "-" + version.getMinor();
        String idxurl = OnlineDocURLBase + verstr + OnlineDocURLMid + IndexPage;
        try {
            Document doc = Jsoup.connect(idxurl).get();
            Elements links = doc.select("li a");
            for (Element link : links) {
              map.put(link.text(), link.absUrl("href"));
            }        
        }catch (IOException ioe) {
            logger.error("Error retrieving doc index from " + idxurl, ioe);
        }
        return map;
    }
    
    public void gatherReferences () {
        this.References = new HashMap<> ();
        for (Map <String, IDDObject> group: this.Objects.values()) {
            for (IDDObject object : group.values()) {
                for (IDDObjectField field : object.Fields) {
                    if (field.getDefs().containsKey("reference")) {
                        for (String ref : field.getDefs().get("reference")) {
                            if (! References.containsKey(ref)) {
                                References.put(ref, new ArrayList<> ());
                            }
                            References.get(ref).add (new ObjIdField (object.getType(), field.getId(), field.getIdx()));
                        }
                    }
                    if (field.getDefs().containsKey("object-list")) {
                        if (! ObjList.containsKey(object.getType())) {
                            ObjList.put(object.getType(), new HashMap<Integer, List<String>>());
                        }
                        Map<Integer, List<String>> flds = ObjList.get(object.getType());
                        if (! flds.containsKey(object.getType())) {
                            flds.put(field.getIdx(), new ArrayList<>());
                        }
                        for (String ref : field.getDefs().get("object-list")) {
                            flds.get(field.getIdx()).add(ref);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Retrieve the IDD object for the given model object type
     * @param objtype Model object type
     * @return IDD object containing the definition of this type
     */
    public IDDObject findDefinitionFor (String objtype) {
        for (Map<String, IDDObject> group: Objects.values()) {
            if (group.containsKey(objtype)) {
                return group.get(objtype);
            }
        }
        // If no key was found, scan keys will case-insensitive match
        for (Map<String, IDDObject> group: Objects.values()) {
            for (String key : group.keySet()) {
                if (key.equalsIgnoreCase(objtype)) {
                    return group.get(key);
                }
            }
        }
        // If none, log warning and return null
        logger.warn("The given object type [" + objtype + "] is not found in the IDD");
        return null;
    }
    
    /**
     * Write IDD object info to a string
     * @return 
     */
    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder ("IDD model loaded from ");
        buf.append(this.SourceFile).append("\n");
        buf.append("IDD Version: ").append(this.Version.toString()).append("\n");
        buf.append("IDD BUILD  : ").append(this.Version.getBuildStr()).append("\n");
        buf.append("Num of Objs: ").append(this.Objects.size()).append("\n");
        buf.append("List of Objs:").append("\n");
        for (String key : Objects.keySet()) {
            Map<String, IDDObject> group = Objects.get(key);
            buf.append("\t").append(key).append(": ").append(group.size()).append(" objects\n");
            for (String id : group.keySet()) {
                buf.append("\t\t").append(id).append(": ").append(group.get(id).Fields.size()).append(" fields\n");
            }
        }
        return buf.toString();
    }
    
    /**
     * Export this IDD object to JSON format
     * @return 
     */
    public String toJSON () {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String output = null;
        try {
            output = objectMapper.writeValueAsString(this);
        } catch (JsonGenerationException e) {
            logger.error ("Error generating JSON", e);
        } catch (JsonMappingException e) {
            logger.error ("Error generating JSON", e);
        } catch (IOException e) {
            logger.error ("Error generating JSON", e);
        }
        return output;
    }
    
    public DataJSTreeModel createIDDTreeView () {
        DataJSTreeModel tree = new DataJSTreeModel ("root", "IDD v" + Version + (Version.getBuildStr() == null ? "" : " (build " + Version.getBuildStr() + ")"), "IDDRoot", null, true);
        // Go through the maps and construct tree
        for (String group : Objects.keySet()) {
            DataJSTreeModel groupnode = new DataJSTreeModel (group, "Group", null);
            for (String objtype : Objects.get(group).keySet()) {
                DataJSTreeModel namenode = new DataJSTreeModel (objtype, "ObjectType", null);
                // namenode.setData(Objects.get(group).get(objtype).toHtml(objtype, group));
                groupnode.getChildren().add(namenode);
            }
            tree.getChildren().add(groupnode);
        }
        return tree;
    }
    
    public DataJSTreeModel createIDDTreeViewAlt (String basetree) {
        DataJSTreeModel tree = null;
        try {
            // Load prepared tree structure
            tree = Mapper.readValue(new File(basetree), DataJSTreeModel.class);
            tree.setText("IDD v" + Version);
            if (tree.getState() == null) {
                tree.setState(new DataJSTreeModel.NodeState());
            }
            tree.getState().setOpened(true);
            // Go through tree structure and load groups and objects
            for (DataJSTreeMin sub1 : tree.getChildren()) {
                if (sub1.getData() != null) {
                    try {
                        for (String g_name : (ArrayList<String>)sub1.getData()) {
                            if (Objects.containsKey(g_name)) {
                                DataJSTreeModel groupnode = new DataJSTreeModel (g_name, "Group", null);
                                for (String objtype : Objects.get(g_name).keySet()) {
                                    DataJSTreeModel namenode = new DataJSTreeModel (objtype, "ObjectType", null);
                                    // namenode.setData(Objects.get(group).get(objtype).toHtml(objtype, group));
                                    groupnode.getChildren().add(namenode);
                                }
                                sub1.getChildren().add(groupnode);
                            }
                        }
                    }catch (Exception ex) {
                        logger.warn("Data contained in " + sub1.getText() + " are not as expected: " + sub1.getData().toString(), ex);
                    }
                }else if (! sub1.getChildren().isEmpty()) {     // No data, may contain sub tags
                    for (DataJSTreeMin sub2 : sub1.getChildren()) {
                        if (sub2.getData() != null) {
                            try {
                                for (String g_name : (ArrayList<String>)sub2.getData()) {
                                    if (Objects.containsKey(g_name)) {
                                        DataJSTreeModel groupnode = new DataJSTreeModel (g_name, "Group", null);
                                        for (String objtype : Objects.get(g_name).keySet()) {
                                            DataJSTreeModel namenode = new DataJSTreeModel (objtype, "ObjectType", null);
                                            // namenode.setData(Objects.get(group).get(objtype).toHtml(objtype, group));
                                            groupnode.getChildren().add(namenode);
                                        }
                                        sub2.getChildren().add(groupnode);
                                    }
                                }
                            }catch (Exception ex) {
                                logger.warn("Data contained in " + sub2.getText() + " are not as expected: " + sub2.getData().toString(), ex);
                            }
                        }
                    }
                }
            }
        }catch (IOException ioe) {
            logger.error("Error loading tree structure from " + basetree, ioe);
            tree = createIDDTreeView ();
        }
        return tree;
    }
    
    @JsonIgnore
    public String getOnlineDocURL (String version, String group, String objtype) {
        String [] verstr = version.split("\\.");
        String groupStr = mapGroupToOnlineDocName(group);
        return OnlineDocURLBase + verstr[0] + "-" + verstr[1] + OnlineDocURLMid + (groupStr == null ? "" : groupStr + "#" + objtype);
    }
    
    public void loadGroupDocNameMap (String fn) {
        this.GroupDocNameMap = new HashMap<>();
        try (BufferedReader fr = new BufferedReader (new FileReader(fn))) {
            String line = fr.readLine();
            while (line != null) {
                if (line.trim().length()>0) {
                    String [] ab = line.split("\\s+");
                    if (ab.length >= 2) {
                        this.GroupDocNameMap.put(ab[0], ab[1]);
                    }else {
                        logger.warn("Problematic line in " + fn + " is ignored: " + line);
                    }
                }
                line = fr.readLine();
            }
        }catch (Exception ex) {
            logger.error("Error loading group file name map file.", ex);
        }
    }
    
    public String mapGroupToOnlineDocName (String Group) {
        if (GroupDocNameMap != null) {
            String groupid = Group.replaceAll("[ ,/]+", "-").replaceAll("[\\(\\)]", "").toLowerCase();
            if (GroupDocNameMap.containsKey(groupid)) {
                return GroupDocNameMap.get(groupid);
            }
            return "group-" + groupid + ".html";
        }
        return null;
    }
    

    /**
     * A Tester
     * @param args 
     */
    public static void main (String [] args) {

        // Parse IDDs to JsTree json
//        Collection<File> idd_list = FileUtils.listFiles(new File("C:\\bin\\EnergyPlusVersionUpdater\\"), new String[]{"idd"}, false);
//        
//        idd_list.forEach((idd_file) -> {
//            IDD idd = IDD.parseIDD(idd_file.getAbsolutePath());
//            try {
//                mapper.writeValue(new File ("C:\\dev\\java\\ep_model\\run\\idd\\" + idd_file.getName() + ".json"), idd.createIDDTreeView());
//            }catch (IOException ex) {
//                logger.error ("Error writing JSON to file.", ex);
//            }
//        });
        
        try {
            DataJSTreeModel tree = Mapper.readValue(new File("C:\\data\\jess\\EnergyPlus\\IDFVersionUpdater\\idd_tree_alt.json"), DataJSTreeModel.class);
            int a = 1;
        }catch (Exception ex) {
            logger.error ("Error reading JSON from file.", ex);
        }

        // Save IDDs to JSON format
        IDD idd = IDD.parseIDD("C:\\dev\\java\\ep_model\\run\\idd\\V9-1-0-Energy+.idd");
        idd.loadGroupDocNameMap("C:\\data\\jess\\EnergyPlus\\IDFVersionUpdater\\group_name_map.txt");
//        try {
//            Mapper.writeValue(new File ("C:\\dev\\java\\ep_model\\run\\idd\\V9-1-0-Energy+.json"), idd);
//        }catch (IOException ex) {
//            logger.error ("Error writing JSON to file.", ex);
//        }

        try {
            Mapper.writeValue(new File ("C:\\dev\\java\\ep_model\\run\\idd\\V91_Tree.json"), idd.createIDDTreeViewAlt("C:\\data\\jess\\EnergyPlus\\IDFVersionUpdater\\idd_tree_alt.json"));
            Mapper.writeValue(new File ("C:\\dev\\java\\ep_model\\run\\idd\\V91_IDD_dump.json"), idd);
        }catch (IOException ex) {
            logger.error ("Error writing JSON to file.", ex);
        }
    }
}
