/*
 * To change this license header, choose License Headers in Project Defs.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.idd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class IDDObject {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(IDDObject.class);
    
    public static enum CommentType {
//	memo,           // Memo describing the object. If multiple lines, start each line
//			// with \memo.
//			// Limit line length to 100 characters.
//	unique-object,	// To flag objects which should appear only once in an idf
//			// (this comment has no "value")
//	required-object,	// To flag objects which are required in every idf
//			// (this comment has no "value")
//	min-fields,	// Minimum number of fields that should be included in the
//			// object.  If appropriate, the Input Processor will fill
//			// any missing fields with defaults (for numeric fields).
//			// It will also supply that number of fields to the "get"
//			// routines using blanks for alpha fields (note -- blanks
//			// may not be allowable for some alpha fields).
//	obsolete,	// This object has been replaced though is kept (and is read)
//			// in the current version.  Please refer to documentation as
//			// to the dispersal of the object.  If this object is
//			// encountered in an IDF, the InputProcessor will post an
//			// appropriate message to the error file.
//			// usage:  \obsolete New=>[New object name]
//	extensible:<#>,	// This object is dynamically extensible -- meaning, if you
//			// change the IDD appropriately (if the object has a simple list
//			// structure -- just add items to the list arguments (i.e. BRANCH
//			// LIST). These will be automatically redimensioned and used during
//			// the simulation. <#> should be entered by the developer to signify
//			// how many of the last fields are needed to be extended (and EnergyPlus
//			// will attempt to auto-extend the object).  The first field of the first
//			// instance of the extensible field set is marked with \begin-extensible.
//	begin-extensible,	// See previous item, marks beginning of extensible fields in
//			// an object.
//	format,         // The object should have a special format when saved in
//			// the IDF Editor with the special format option enabled.
//			// The options include SingleLine, Vertices, CompactSchedule,
//			// FluidProperties, ViewFactors, and Spectral.
//			// The SingleLine option puts all the fields for the object
//			// on a single line. The Vertices option is used in objects
//			// that use X, Y and Z fields to format those three fields
//			// on a single line.
//			// The CompactSchedule formats that specific object.
//			// The FluidProperty option formats long lists of fluid
//			// properties to ten values per line.
//			// The ViewFactor option formats three fields related to
//			// view factors per line.
//			// The Spectral option formats the four fields related to
//			// window glass spectral data per line.
//	reference-class-name	// Adds the name of the class to the reference list
//			// similar to \reference.
    }
    
    @JsonIgnore
    IDD Dict = null;
    @JsonIgnore
    String Group = null;
    @JsonIgnore
    String Type = null;
    
    List <IDDObjectField> Fields = new ArrayList<>();
    HashMap <String, List<String>> Defs = new LinkedHashMap <> ();
    
    public IDDObject (IDD idd, String group, String type) {
        Dict = idd;
        Group = group;
        Type = type;
    }

    public List <IDDObjectField> getFields() {
        return Fields;
    }

    public void setFields(List <IDDObjectField> Fields) {
        this.Fields = Fields;
    }

    public HashMap<String, List<String>> getDefs() {
        return Defs;
    }

    public void setDefs(HashMap<String, List<String>> Defs) {
        this.Defs = Defs;
    }

    @JsonIgnore
    public String getGroup() {
        return Group;
    }

    @JsonIgnore
    public String getType() {
        return Type;
    }
    
    
    public void addField (IDDObjectField field) {
        Fields.add(field);
    }
    
    public void addDef (String id, String val) {
        List<String> cur = Defs.get(id);
        if (cur == null) {
            cur = new ArrayList<> ();
            cur.add(val);
            Defs.put(id, cur);
        }else {
            cur.add(val);
        }
    }
    
    /**
     * Test if this object definition has "unique-object" property
     * @return true if the definition contains unique-object property
     */
    public boolean isUnique () {
        return this.getDefs().containsKey("unique-object");
    }
    
    /**
     * Test if this object definition has "unique-object" property
     * @return true if the definition contains unique-object property
     */
    public boolean isRequired () {
        return this.getDefs().containsKey("required-object");
    }
    
    /**
     * Test if this object definition has a field of a given name
     * @param field_name The field's name to be checked
     * @return ID of the field if found; -1 if not found.
     */
    public int lookUpIdOfField (String field_name) {
        int id = -1;
        int counter = 0;
        for (IDDObjectField field : Fields) {
            if (field.getDefs().containsKey("field")) {
                if (field_name.equalsIgnoreCase(field.getDefs().get("field").get(0))) {
                    id = counter;
                    break;
                }
            }
            counter ++;
        }
        return id;
    }
    
    @Override
    public String toString () {
        // Not used
        return "";
    }
    
    @JsonIgnore
    public String getDocUrl () {
        if (Dict.getVersion().isOrAbove("8.0.0")) {
            String url;
            if (Dict.getObjectUrlMap().containsKey(Type)) {
                url = Dict.getObjectUrlMap().get(Type);
            }else {
                url = Dict.getOnlineDocURL(
                    Dict.getVersion().toString(), 
                    Group, 
                    Type.replaceAll(":", "").toLowerCase());
            }
            return url;
        }
        return null;
    }
    
    public String toHtml () {
//        <div class="panel panel-default">
//          <!-- Default panel contents -->
//          <div class="panel-heading">Panel heading</div>
//          <div class="panel-body">
//            <p>...</p>
//          </div>
//
//          <!-- Table -->
//          <table class="table">
//            ...
//          </table>
//        </div>
//      Style changed to BS4
//
        StringBuilder buf = new StringBuilder ("<div class='p-3'>");
        buf.append("<div class=''>");
        buf.append("<h3>").append(Type).append(" ");
        if (this.isUnique()) buf.append("<sup><span class='badge badge-primary'>Unique</span></sup>");
        buf.append(" ");
        if (this.isRequired()) buf.append("<sup><span class='badge badge-success'>Required</span></sup>");
        buf.append("</h3>");
        String url = getDocUrl ();
        if (url != null) {
            buf.append("<a target='_blank' href='");
            buf.append(url).append("' title='").append(url);
            buf.append("'>Go to EnergyPlus Reference <i class='fas fa-globe'></i></a>");
        }
        buf.append("</div>");
        
        buf.append("<div class='alert alert-info mt-3'>");
        buf.append("<ul>");
        for (String id : Defs.keySet()) {
            buf.append("<li>").append("<b>").append(id).append("</b>");
            if (! ("unique-object".equals(id) || "required-object".equals(id))) {
                buf.append(" : ");
                for (String line : Defs.get(id)) {
                    buf.append(line).append(" ");
                }
            }
            buf.append("</li>");
        }
        buf.append("</ul>");
        buf.append("</div>");
        
        buf.append("<table class='table table-striped table-hover'>");
        buf.append("<tr><th>ID</th><th>Field</th><th>Property</th><th>Information</th></tr>");
        for (IDDObjectField param : Fields) {
            Map <String, List<String>> props = param.getDefs();
            // int proprows = param.hasProperty("note") ? Math.max(1, props.size()-1) : props.size();
            int proprows = props.size();
            boolean addRow = false;
            if ((proprows + 1) % 2 == 0) {
                proprows ++;
                addRow = true;
            }
            
            // ID
            buf.append("<tr>");
            buf.append("<td ").append("rowspan='").append(proprows).append("'>");
            if (param.isRequired()) buf.append("<b>");
            buf.append(param.Id);
            if (param.isRequired()) buf.append("</b>");
            buf.append("</td>");
            // Field name
            buf.append("<td ").append("rowspan='").append(proprows).append("'>");
            if (param.isRequired()) buf.append("<b>");
            buf.append(param.hasProperty("field") ? param.getDefs().get("field").get(0) : "-");
            if (param.isRequired()) buf.append("</b>");
            buf.append("</td>");
            buf.append("</tr>");
            // Note
            if (param.hasProperty("note")) {
                buf.append("<tr>");
                buf.append("<td>");
                buf.append("<i>").append("note").append("</i>");
                buf.append("</td>");
                buf.append("<td>");
                if (param.hasProperty("note")) {
                    param.getDefs().get("note").forEach((val) -> {
                        buf.append(val).append("<br>");
                    });
                }
                buf.append("</td>");
                buf.append("</tr>");
            }
            // Other properties
            for (String prop : props.keySet()) {
                if (! (prop.equalsIgnoreCase("field") || prop.equalsIgnoreCase("note"))) {
                    buf.append("<tr>");
                    buf.append("<td>").append("<i>").append(prop).append("</i>").append("</td>");
                    buf.append("<td>");
                        param.getDefs().get(prop).forEach((val) -> {
                            if (prop.equalsIgnoreCase("default")) buf.append("<b>");
                            buf.append(val);
                            if (prop.equalsIgnoreCase("default")) buf.append("</b>");
                            buf.append("<br>");
                        });
                    buf.append("</td>");
                    buf.append("</tr>");
                }
            }
            if (addRow) {
                buf.append("<tr></tr>");
            }
        }
        buf.append("</table>");
        buf.append("</div>");
        return buf.toString();
    }

}
