/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.idf;

import com.ensims.eplus.model.idd.IDD;
import com.ensims.eplus.model.idd.IDDObject;
import com.ensims.eplus.model.js.DataIdfOperations;
import com.ensims.eplus.model.js.DataJSTreeItemAttr;
import com.ensims.eplus.model.js.DataJSTreeMin;
import com.ensims.eplus.model.js.DataJSTreeModel;
import com.ensims.eplus.model.VersionInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.LoggerFactory;

/**
 * This class represent a IDF model and provide functions for parsing and exporting
 * the IDF files.
 * @author Yi
 */
public class IDF {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(IDF.class);

    public static final String CommentChar = "!";
    public static final String SeparatorChar = "\\s*,\\s*";
    public static final String DelimiterChar = ";";
    
    public static class ObjIdentifier {
        /** IDD object type */
        String Type = null;
        /** IDF object identifier - name field if available */
        String Id = null;
        /** For citation: id of the field in the source object where this object is cited. For reference: id of the field in this object */ 
        String Field = null;
        /** For citation: index of the field in the source object where this object is cited. For reference: index of the field in this object */
        int FieldIdx = 0;
        public ObjIdentifier () {}
        public ObjIdentifier (String type, String name) { Type = type; Id = name; }
        public ObjIdentifier (String type, String name, String field, int idx) { Type = type; Id = name; Field = field; FieldIdx = idx; }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
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
    
    public static class ModelStats {
        int NZones = 0;
        int NSurfaces = 0;
        int NOpenings = 0;
        int NShading = 0;
        boolean UsingEMS = false;
        boolean UsingPython = false;
        boolean UsingAFN = false;
        boolean HasHVAC = false;
        boolean UsingDetailedHVAC = false;
        
        public ModelStats () {}
        public ModelStats (int nzone, int nsurf, int nwin, int nshade, boolean ems, boolean python, boolean afn, boolean hvac, boolean detailedhvac) {
            NZones = nzone;
            NSurfaces = nsurf;
            NOpenings = nwin;
            NShading = nshade;
            UsingEMS = ems;
            UsingPython = python;
            UsingAFN = afn;
            HasHVAC = hvac;
            UsingDetailedHVAC = detailedhvac;
        }

        public int getNZones() {
            return NZones;
        }

        public void setNZones(int NZones) {
            this.NZones = NZones;
        }

        public int getNSurfaces() {
            return NSurfaces;
        }

        public void setNSurfaces(int NSurfaces) {
            this.NSurfaces = NSurfaces;
        }

        public int getNOpenings() {
            return NOpenings;
        }

        public void setNOpenings(int NOpenings) {
            this.NOpenings = NOpenings;
        }

        public int getNShading() {
            return NShading;
        }

        public void setNShading(int NShading) {
            this.NShading = NShading;
        }

        public boolean isUsingEMS() {
            return UsingEMS;
        }

        public void setUsingEMS(boolean UsingEMS) {
            this.UsingEMS = UsingEMS;
        }

        public boolean isUsingPython() {
            return UsingPython;
        }

        public void setUsingPython(boolean UsingPython) {
            this.UsingPython = UsingPython;
        }

        public boolean isUsingAFN() {
            return UsingAFN;
        }

        public void setUsingAFN(boolean UsingAFN) {
            this.UsingAFN = UsingAFN;
        }

        public boolean isHasHVAC() {
            return HasHVAC;
        }

        public void setHasHVAC(boolean HasHVAC) {
            this.HasHVAC = HasHVAC;
        }

        public boolean isUsingDetailedHVAC() {
            return UsingDetailedHVAC;
        }

        public void setUsingDetailedHVAC(boolean UsingDetailedHVAC) {
            this.UsingDetailedHVAC = UsingDetailedHVAC;
        }
        
    }
    
    public static class IDFInfo {
        String Header = null;
        String Source = null;
        Date LastModified = null;
        String Version = null;
        DataJSTreeModel Tree = null;
        DataJSTreeModel TreeAlt = null;
        ModelStats Stats = null;
        boolean ReadOnly = true;
        
        public IDFInfo () {}
        public IDFInfo (String src, Date last) {
            Source = src;
            LastModified = last;
        }

        public String getSource() {
            return Source;
        }

        public void setSource(String Source) {
            this.Source = Source;
        }

        public Date getLastModified() {
            return LastModified;
        }

        public void setLastModified(Date LastModified) {
            this.LastModified = LastModified;
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
            this.Version = Version;
        }

        public DataJSTreeModel getTree() {
            return Tree;
        }

        public void setTree(DataJSTreeModel Tree) {
            this.Tree = Tree;
        }

        public DataJSTreeModel getTreeAlt() {
            return TreeAlt;
        }

        public void setTreeAlt(DataJSTreeModel TreeAlt) {
            this.TreeAlt = TreeAlt;
        }

        public String getHeader() {
            return Header;
        }

        public void setHeader(String Header) {
            this.Header = Header;
        }

        public ModelStats getStats() {
            return Stats;
        }

        public void setStats(ModelStats Stats) {
            this.Stats = Stats;
        }

        public boolean isReadOnly() {
            return ReadOnly;
        }

        public void setReadOnly(boolean ReadOnly) {
            this.ReadOnly = ReadOnly;
        }
        
    }
    
    /** Reference to the IDD dictionary model */
    @JsonIgnore
    IDD Idd = null;
    /** Source file from which this object is constructed */
    String Src = null;
    /** A map of IDF objects contained in the model. Object Type are used as the keys. Each type can have multiple objects */
    Map <String, List<IDFObject>> Objs = new LinkedHashMap<> ();
    /** Idf info object for front-ends */
    @JsonIgnore
    IDFInfo Info = null;
    
    /**
     * Constructor
     * @param idd
     * @param src 
     */
    public IDF (IDD idd, String src) {
        Idd = idd;
        Src = src;
    }

    @JsonIgnore
    public IDD getIdd() {
        return Idd;
    }

    @JsonIgnore
    public void setIdd(IDD Idd) {
        this.Idd = Idd;
    }

    public String getSrc() {
        return Src;
    }

    public void setSrc(String Src) {
        this.Src = Src;
    }

    public Map <String, List<IDFObject>> getObjs() {
        return Objs;
    }

    public void setObjs(Map <String, List<IDFObject>> Objs) {
        this.Objs = Objs;
    }
    
    /**
     * Parse the given IDF file into an IDF model
     * @param idd
     * @param src
     * @return 
     */
    public static IDF parseIDF (IDD idd, String src) {
        IDF idf = new IDF (idd, src);
        
        if (src != null && new File(src).exists()) {
            idf.Info = new IDFInfo (src, new Date(new File(src).lastModified()));
            try (BufferedReader fr = new BufferedReader (new InputStreamReader(new BOMInputStream(new FileInputStream (src))))) {
                // Start scanning. The following variables are used to mark the current stage
                String curObj = null;

                String line = fr.readLine();
                while (line != null) {
                    line = filterComments (line);
                    if (line.length() > 0){
                        if (curObj == null) {
                            curObj = line;
                        }else {
                            curObj = curObj.concat(line);
                        }
                        if (curObj.endsWith(DelimiterChar)) {
                            IDFObject obj = new IDFObject(idd, curObj);
                            if (! idf.Objs.containsKey(obj.Type)) {
                                idf.Objs.put(obj.Type, new ArrayList<> ());
                            }
                            idf.Objs.get(obj.Type).add(obj);
                            curObj = null;
                        }
                    }
                    line = fr.readLine();
                }
            }catch (Exception ex) {
                logger.error("Error parsing IDF", ex);
            }
            idf.scanReferences();
            idf.Info.setVersion(idf.getEPlusVersion().toMajorDotMinor());
        }
        return idf;
    }
    
    /**
     * Parse the given code and merge the contents into the current IDF model. 
     * Code snippet of IDF can be imported and parsed. If an object with the same
     * ID exists in the current model, it will be replaced by the imported model.
     * "Scope", if set to a type that are present in the IDF, all existing objects
     * of the type are dropped before importing.
     * @param code
     * @param scope "type" means dropping all existing objects of the found types; otherwise replace or append.
     * @return the current IDF model after merging
     */
    public IDF mergeIdfCode (String code, String scope) {
        // Clear objects of the type given by "scope"
        if (scope != null && this.getObjs().containsKey(scope)) {
            this.getObjs().get(scope).clear();
        }
        // Import new objects
        try (BufferedReader fr = new BufferedReader (new StringReader (code))) {
            // Start scanning. The following variables are used to mark the current stage
            String curObj = null;

            String line = fr.readLine();
            while (line != null) {
                line = filterComments (line);
                if (line.length() > 0){
                    if (curObj == null) {
                        curObj = line;
                    }else {
                        curObj = curObj.concat(line);
                    }
                    if (curObj.endsWith(DelimiterChar)) {
                        IDFObject obj = new IDFObject(Idd, curObj);
                        if (! Objs.containsKey(obj.Type)) {
                            Objs.put(obj.Type, new ArrayList<> ());
                        }
                        Objs.get(obj.Type).add(obj);
                        curObj = null;
                    }
                }
                line = fr.readLine();
            }
        }catch (Exception ex) {
            logger.error("Error parsing IDF", ex);
        }
        this.scanReferences();
        this.Info.setVersion(this.getEPlusVersion().toMajorDotMinor());
        return this;
    }
    
    protected void scanReferences () {
        for (String type : Idd.getObjList().keySet()) {
            List <IDFObject> objs = this.Objs.get(type);
            if (objs != null) {
                for (IDFObject obj : objs) {
                    for (int fld_idx : Idd.getObjList().get(type).keySet()) {
                        List<String> refs = Idd.getObjList().get(type).get(fld_idx);
                        if (fld_idx < obj.getFields().size()) {
                            String ref_name = obj.getFields().get(fld_idx);
                            IDFObject reffed = findObject (refs, ref_name);
                            if (reffed != null) {
                                String fld_desc = null;
                                try {
                                    fld_desc = Idd.findObject(type).getFields().get(fld_idx).getDefs().get("field").get(0);
                                }catch (NullPointerException npe) {
                                    logger.warn("Cannot find 'field' definition of IDD object " + type);
                                }
                                obj.addUsingRef(new ObjIdentifier (reffed.getType(), reffed.getId(), fld_desc, fld_idx));
                                reffed.addUsedBy(new ObjIdentifier (obj.getType(), obj.getId(), fld_desc, fld_idx));
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void collectReferences (IDFObject obj) {
        if (Idd.getObjList().containsKey(obj.getType())) {
            for (int fld_idx : Idd.getObjList().get(obj.getType()).keySet()) {
                List<String> refs = Idd.getObjList().get(obj.getType()).get(fld_idx);
                if (fld_idx < obj.getFields().size()) {
                    String ref_name = obj.getFields().get(fld_idx);
                    IDFObject reffed = findObject (refs, ref_name);
                    if (reffed != null) {
                        String fld_desc = null;
                        try {
                            fld_desc = Idd.findObject(obj.getType()).getFields().get(fld_idx).getDefs().get("field").get(0);
                        }catch (NullPointerException npe) {
                            logger.warn("Cannot find 'field' definition of IDD object " + obj.getType());
                        }
                        obj.addUsingRef(new ObjIdentifier (reffed.getType(), reffed.getId(), fld_desc, fld_idx));
                        reffed.addUsedBy(new ObjIdentifier (obj.getType(), obj.getId(), fld_desc, fld_idx));
                    }
                }
            }
        }
    }
    
    /**
     * Find object with the possible referencing types and the object's name
     * @param refs Possible referencing types from the IDD
     * @param name the reference object's name
     * @return The referenced object
     */
    protected IDFObject findObject (List <String> refs, String name) {
        for (String ref : refs) {
            if (Idd.getReferences().containsKey(ref)) {
                for (IDD.ObjIdField obj_id_fld : Idd.getReferences().get(ref)) {
                    if (this.getObjs().containsKey(obj_id_fld.getType())) {
                        for (IDFObject obj : this.getObjs().get(obj_id_fld.getType())) {
                            if (obj.getId().equals(name)) {
                                return obj;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Find object with the given type and id
     * @param type Object type
     * @param id the object's name
     * @return The object, or null if not found
     */
    protected IDFObject findObjectOfType (String type, String id) {
        if (this.getObjs().containsKey(type)) {
            for (IDFObject obj : this.getObjs().get(type)) {
                if (obj.getId().equals(id)) {
                    return obj;
                }
            }
        }
        return null;
    }
    
    /**
     * Utility function for filtering comments out of the text
     * @param input Text to be filtered
     * @return Filtered text
     */
    public static String filterComments (String input) {
        String output;
        int idx = input.indexOf(CommentChar);
        if (idx >= 0) {
            output = input.substring(0, idx).trim();
        }else {
            output = input.trim();
        }
        return output;
    }

    /**
     * Insert an IDF object into the current IDF model. If the object type allows
     * only unique instance in the IDF, the given object will replace the existing
     * one.
     * @param obj 
     */
    public void insertObject (IDFObject obj) {
        List<IDFObject> objlist = Objs.get(obj.getType());
        if (objlist == null) {
            objlist = new ArrayList<> ();
            objlist.add(obj);
            this.getObjs().put(obj.getType(), objlist);
        }else if (this.getIdd().findDefinitionFor(obj.getType()).isUnique()) {
            objlist.set(0, obj);
        }else {
            objlist.add(obj);
        }
    }
    
    /**
     * Remove the IDF objects of the given type from the current IDF model. This method will remove
     * all matching IDF objects found in the IDF model.
     * @param objtype The object type string
     * @return The list of objects that have been removed
     */
    public List<IDFObject> removeObjectsOfType (String objtype) {
        return this.getObjs().remove(objtype);
    }
    
    /**
     * Scan for the E+ version information from the IDF file
     * @param idf_file The IDF file whose version is to be extracted
     * @return Version string, e.g. "4.0", if found. Otherwise, null
     */
    public static String scanVersionString(String idf_file) {
        // Locate "Version,xx.xx;" in the idf/imf file
        try (BufferedReader ins = new BufferedReader(new FileReader(idf_file))) {
            String line = ins.readLine();
            while (line != null) {
                line = (line.contains("!")) ? line.substring(0, line.indexOf("!")).trim() : line.trim();
                if (line.startsWith("Version,")) {
                    String verline = line;
                    while (! line.contains(";")) {
                        line = ins.readLine();
                        if (line != null) {
                            verline = verline.concat((line.contains("!")) ? line.substring(0, line.indexOf("!")).trim() : line.trim());
                        }else {
                            break;
                        }
                    }
                    ins.close();
                    if (verline.contains(";")) {
                        return verline.substring(8, verline.indexOf(";")).trim();
                    }
                }
                line = ins.readLine();
            }
        } catch (Exception e) {
            logger.error("Error scanning version string from " + idf_file, e);
        }
        return null;
    }
    
    /**
     * Get the EPlus version of this model
     * @return VersionInfo object
     */
    @JsonIgnore
    public VersionInfo getEPlusVersion () {
        if (Objs.containsKey("Version")) {
            IDFObject verobj = Objs.get("Version").get(0);
            return new VersionInfo (verobj.getFields().get(0));
        }
        return new VersionInfo();
    }
    
    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder ("IDF model loaded from ");
        buf.append(this.Src).append("\n");
        buf.append("Num of Object Types: ").append(this.Objs.size()).append("\n");
        buf.append("List of Objects    :").append("\n");
        for (String id : this.Objs.keySet()) {
            List <IDFObject> list = Objs.get(id);
            buf.append("\tType [").append(id).append("]: ").append(list.size()).append(" Objects\n");
            for (IDFObject obj : list) {
                buf.append("\t\t").append(obj.toString()).append("\n");
            }
        }
        return buf.toString();
    }
    
    /**
     * Export the model to IDF file content. The resultant text will be in a condensed form
     * @return Text containing the IDF file content represented in this model
     */
    public String exportModel () {
        StringBuilder buf = new StringBuilder ("!- Model exported from DB Model Filter\n");
        buf.append("!-Source file: ").append(this.Src).append("\n");
        buf.append("!-Num of Object Types: ").append(this.Objs.size()).append("\n");
        for (String id : this.Objs.keySet()) {
            List <IDFObject> list = Objs.get(id);
            buf.append("!-[").append(id).append("]: ").append(list.size()).append(" Objects\n");
            for (IDFObject obj : list) {
                buf.append("\t").append(obj.exportObject()).append("\n");
            }
        }
        return buf.toString();
    }
    
    /**
     * Build Idf tree(s) and return the IDFInfo object
     * @return the IDFInfo object constructed from this instance
     */
    public IDFInfo createIDFTreeView () {
        // Get a <group, <objtype, <name, <field, value>>>> map
        Map<String, Map<String, List<IDFObject>>> groupmap = new LinkedHashMap<> ();
        Map<String, Map<String, IDDObject>> idd_groups = this.Idd.getObjects();
        for (String grp_key : idd_groups.keySet()) {
            Map<String, List<IDFObject>>typemap = new LinkedHashMap<>();
            groupmap.put(grp_key, typemap);
            for (String type_key : idd_groups.get(grp_key).keySet()) {
                List<IDFObject>objlist;
                if (Objs.containsKey(type_key)) {
                    objlist = Objs.get(type_key);
                }else {
                    objlist = new ArrayList<>();
                }
                typemap.put(type_key, objlist);
            }
        }
        // Go through the maps and construct tree
        DataJSTreeModel tree = new DataJSTreeModel ("root", "IDF model (v"  + Idd.getVersion() + ")", "IDFRoot", null, true);
        tree.setA_attr(new DataJSTreeItemAttr (
                "",
                "tooltip",
                true,
                "Loaded from <i>" + Src + "</i>"
        ));
        for (String group : groupmap.keySet()) {
            DataJSTreeModel groupnode = new DataJSTreeModel (group, "Group", null);
            for (String objtype : groupmap.get(group).keySet()) {
                DataJSTreeModel typenode = new DataJSTreeModel (objtype, "ObjectType", null);
                for (int i=0; i<groupmap.get(group).get(objtype).size(); i++) {
                    IDFObject idfobj = groupmap.get(group).get(objtype).get(i);
                    DataJSTreeModel objnode = new DataJSTreeModel (objtype + "_" + i, idfobj.toString(), "Object", null);
//                    objnode.setA_attr(new DataJSTreeItemAttr (
//                            "",
//                            "tooltip",
//                            true,
//                            idfobj.toHtml(Idd.findDefinitionFor(objtype))
//                    ));
                    typenode.getChildren().add(objnode);
                }
            
                if (typenode.getChildren().size() > 0) {
                    typenode.setText(typenode.getText() + "[" + typenode.getChildren().size() + "]");
                    IDDObject iddobj = Idd.findDefinitionFor(objtype);
//                    typenode.setA_attr(new DataJSTreeItemAttr (
//                            "",
//                            "tooltip",
//                            true,
//                            "Class contains <b>" + typenode.getChildren().size() + "</b> objects"
//                    ));
                    groupnode.getChildren().add(typenode);
                }
            }
            if (groupnode.getChildren().size() > 0) {
                groupnode.setText(groupnode.getText() + "[" + groupnode.getChildren().size() + "]");
//                groupnode.setA_attr(new DataJSTreeItemAttr (
//                        "",
//                        "tooltip",
//                        true,
//                        "Group contains <b>" + groupnode.getChildren().size() + "</b> non-empty classes"
//                ));
                tree.getChildren().add(groupnode);
            }
        }
        this.Info.setTree(tree);
        this.Info.setStats(gatherModelStats());
        return this.Info;
    }
    
    public IDFInfo createIDFTreeAltView (IDFInfo idf_info, String alt_base) {
        
        if (idf_info == null) {
            idf_info = createIDFTreeView ();
        }
        
        DataJSTreeModel tree = idf_info.getTree();
        DataJSTreeModel tree_alt = null;
        try {
            // Load prepared tree structure
            tree_alt = IDD.Mapper.readValue(new File(alt_base), DataJSTreeModel.class);
            tree_alt.setText(tree.getText());
            if (tree_alt.getState() == null) {
                tree_alt.setState(tree.getState());
            }
            tree_alt.getState().setOpened(true);
            
            // Create a group name - group tree node map of the existing tree
            HashMap <String, DataJSTreeMin> group_map = new HashMap <>();
            for (DataJSTreeMin group : tree.getChildren()) {
                group_map.put(group.getText(), group);
            }
            
            // Go through tree structure and load groups and objects
            for (DataJSTreeMin sub1 : tree_alt.getChildren()) {
                if (sub1.getData() != null) {
                    try {
                        for (String g_name : (ArrayList<String>)sub1.getData()) {
                            if (group_map.containsKey(g_name)) {
                                sub1.getChildren().add(group_map.get(g_name));
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
                                    if (group_map.containsKey(g_name)) {
                                        sub2.getChildren().add(group_map.get(g_name));
                                    }
                                }
                            }catch (Exception ex) {
                                logger.warn("Data contained in " + sub2.getText() + " are not as expected: " + sub2.getData().toString(), ex);
                            }
                        }
                    }
                }
            }
            this.Info.setTreeAlt(tree_alt);
        }catch (IOException ioe) {
            logger.error("Error loading tree structure from " + alt_base, ioe);
        }
        return this.Info;
    }
    
    /**
     * Gather basic stats of the IDF
     * @return 
     */
    public ModelStats gatherModelStats () {
        int nZones = Objs.containsKey("Zone") ? Objs.get("Zone").size() : 0;
        int nSurfaces = (Objs.containsKey("BuildingSurface:Detailed") ? Objs.get("BuildingSurface:Detailed").size() : 0);
        int nOpenings = Objs.containsKey("FenestrationSurface:Detailed") ? Objs.get("FenestrationSurface:Detailed").size() : 0;
        int nShading = Objs.containsKey("Shading:Building:Detailed") ? Objs.get("Shading:Building:Detailed").size() : 0;
        boolean hasEMS = Objs.containsKey("EnergyManagementSystem:ProgramCallingManager");
        boolean hasPython = false;
        boolean hasAFN = Objs.containsKey("AirflowNetwork:SimulationControl");
        boolean hasHVAC =   Objs.containsKey("ZoneControl:Thermostat") || 
                            Objs.containsKey("ZoneControl:Humidistat") || 
                            Objs.containsKey("ZoneControl:Thermostat:OperativeTemperature") || 
                            Objs.containsKey("ZoneControl:Thermostat:ThermalComfort") || 
                            Objs.containsKey("ZoneControl:Thermostat:TemperatureAndHumidity") ||
                            Objs.containsKey("ZoneControl:Thermostat:StagedDualSetpoint") ||
                            Objs.containsKey("ZoneControl:ContaminantController");
        boolean hasDetailedHVAC = hasHVAC && !Objs.containsKey("ZoneHVAC:IdealLoadsAirSystem");
        ModelStats stats = new ModelStats ( nZones,
                                            nSurfaces,
                                            nOpenings,
                                            nShading,
                                            hasEMS,
                                            hasPython,
                                            hasAFN,
                                            hasHVAC,
                                            hasDetailedHVAC
        );
        return stats;
    }
    
    
    /**
     * Apply the given operations to this IDF
     * @param ops 
     * @return model changed flag
     */
    public boolean applyOperations (DataIdfOperations ops) {
        boolean changed = false;
        if (ops != null) {
            if (ops.getCode() != null && ops.getCode().trim().length() > 0) {
                this.mergeIdfCode(ops.getCode(), ops.getScope());
                changed = true;
            }else {
                if (ops.getObjects() != null && ! ops.getObjects().isEmpty()) {
                    for (IDFObject obj : ops.getObjects()) {
                        if (!this.getObjs().containsKey(obj.getType())) {
                            this.getObjs().put(obj.getType(), new ArrayList <>());
                        }
                        if ("new".equals(obj.getId())) {
                            obj.assignId(Idd.findDefinitionFor(obj.getType()), null);
                            this.getObjs().get(obj.getType()).add(obj);
                            this.collectReferences(obj);
                            changed = true;
                        }else {
                            IDFObject current = this.findObjectOfType(obj.getType(), obj.getId());
                            if (current != null) {
                                current.setFields(obj.getFields());
                                current.assignId(Idd.findDefinitionFor(obj.getType()), null);
                                this.collectReferences(current);
                                changed = true;
                            }
                        }
                    }
                }
                
                if (ops.getDeleted() != null && ! ops.getDeleted().isEmpty()) {
                    for (IDFObject obj : ops.getDeleted()) {
                        this.getObjs().get(obj.getType()).remove(this.findObjectOfType(obj.getType(), obj.getId()));
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
    
    
    /**
     * A Tester
     * @param args 
     */
    public static void main (String [] args) {
        IDD idd = IDD.parseIDD("C:\\dev\\java\\ep_model\\run\\idf\\V9-4-0-Energy+.idd");
        IDF idf = IDF.parseIDF(idd, "C:\\dev\\java\\ep_model\\run\\560814\\in.idf");
        
//        ArrayList <IDFObject> objs = idf.removeObjectsOfType(new IDFObject (idd, PredefinedIDFObject.OutputTableStyleXML).getObjType());
//        IDFObject newobj = new IDFObject (idd, PredefinedIDFObject.OutputTableStyleXML);
//        if (objs != null && objs.size()>0) {
//            newobj.getParamList().set(1, objs.get(0).getParamList().get(1));
//        }
//        idf.insertObject(newobj);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
//            mapper.writeValue(new File ("D:\\Dev-Java\\ep_model\\run\\idf_tree.json"), idf.createIDFTreeView());
            mapper.writeValue(new File ("C:\\dev\\java\\ep_model\\run\\idf\\in.idf.json"), idf);
        }catch (Exception ex) {
            logger.error ("Error writing JSON to file.", ex);
        }
    }
}
