/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.idf;

import com.ensims.eplus.model.idd.IDD;
import com.ensims.eplus.model.idd.IDDObject;
import com.ensims.eplus.model.idd.IDDObjectField;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * General class for all IDF objects. It has an Object type string, an index of
 * the parameter that can help uniquely identify this object, and a list of 
 * parameter values. Information about each parameter should come from the IDD file
 * @author Yi
 */
public class IDFObject {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(IDFObject.class);
    static MessageDigest md = null;
    static {
        try {
            md = MessageDigest.getInstance("MD5");
        }catch (NoSuchAlgorithmException nsae) {
            logger.error("Cannot initiate MD5", nsae);
        }
    }
    
    /** Object type string from IDF. This corresponds to the definitions in IDD */
    String Type = null;
    /** ID String of the object. If the type has the unique flag, ID string is "Unique"; otherwise if name is not available, the default id is "Object" */
    String Id = null;
    /** The list of values for the parameter from the IDF */
    List<String> Fields = new ArrayList<> ();
    /** Used-by lists all incoming refs */
    List<IDF.ObjIdentifier> UsedBy = null;
    /** List of outgoing refs */
    List<IDF.ObjIdentifier> Using = null;
    
    /** 
     * Default constructor
     */
    public IDFObject () {}
    
    /**
     * Construct a new object from the given text. An example of the text is: "Version: 8.2;"
     * @param idd
     * @param rawstring 
     */
    public IDFObject (IDD idd, String rawstring) {
        if (rawstring.endsWith(IDF.DelimiterChar)) {
            rawstring = rawstring.substring(0, rawstring.length()-1);
        }
        String [] elements = rawstring.split(IDF.SeparatorChar);
        Type = elements[0];
        for (int i=1; i<elements.length; i++) {
            Fields.add(elements[i]);
        }
        IDDObject iddobj = idd.findDefinitionFor(Type);
        // Update object type according to the IDD object
        if (iddobj != null) {
            Type = iddobj.getType();
        }
        // Assign object id
        assignId (iddobj, rawstring);
    }

    final protected void assignId (IDDObject def, String raw) {
        if (def != null) {
            int idx = def.lookUpIdOfField("Name");
            if (idx >= 0 && Fields.get(idx).trim().length() > 0) {
                Id = Fields.get(idx);
            }else if (def.isUnique()) {
                Id = "Unique";
            }else if (md != null) {
                String code = (raw == null ? this.exportObject() : raw);
                byte [] digest = md.digest(code.getBytes());
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    buf.append(String.format("%02X", digest[i]));		
                }
                Id = buf.toString().toLowerCase();
            }else {
                Id = "Object";
            }
        }
    }
    
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

    public List<String> getFields() {
        return Fields;
    }

    public void setFields(List<String> Fields) {
        this.Fields = Fields;
    }

    public List<IDF.ObjIdentifier> getUsedBy() {
        return UsedBy;
    }

    public void setUsedBy(List<IDF.ObjIdentifier> UsedBy) {
        this.UsedBy = UsedBy;
    }

    public List<IDF.ObjIdentifier> getUsing() {
        return Using;
    }

    public void setUsing(List<IDF.ObjIdentifier> Using) {
        this.Using = Using;
    }
    
    public void addUsedBy (IDF.ObjIdentifier obj) {
        if (UsedBy == null) {
            UsedBy = new ArrayList<> ();
        }
        UsedBy.add(obj);
    }
    
    public void addUsingRef (IDF.ObjIdentifier obj) {
        if (Using == null) {
            Using = new ArrayList<> ();
        }
        Using.add(obj);
    }
    
    /**
     * Test if an object is identical to another. The objects are considered 
     * identical if they only differ in the identifier field
     * @param other The object to be tested against this
     * @return True if the two objects deffer only in the identifier field
     */
    public boolean isIdenticalTo (Object other) {
        if (other instanceof IDFObject) {
            IDFObject idf = (IDFObject)other;
            if (! Type.equals(idf.Type)) return false;
            for (int i=0; i<Fields.size(); i++) {
                if (! (Fields.get(i).equals(idf.Fields.get(i)) || Fields.get(i).equals(Id))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder ();
        if (Id.equals("Unique") || Id.equals("Object") || Id.matches("^[a-f0-9]{32}$")) {
            for (String ele : Fields) {
                if(buf.length() > 50) {
                    buf.append(",...");
                    break;
                }
                buf.append(",").append(ele);
            }
            buf.replace(0, 1, "=>");
        }else {
            buf.append(Id);
        }
        return buf.toString();
    }
    
    public String toHtml (IDDObject iddobj) {
        StringBuilder buf = new StringBuilder ("<table>");
        buf.append("<tr><td colspan=\"2\">").append(Type).append(",</td></tr>");
        for (int i=0; i<Fields.size(); i++) {
            String val = Fields.get(i);
            String comment = "";
            if (i < iddobj.getFields().size()) {
                IDDObjectField param = iddobj.getFields().get(i);
                comment = param.hasProperty("field") ? param.getDefs().get("field").get(0) : "-";
            }
            buf.append("<tr><td>").append(val).append(i==Fields.size()-1 ? ";" : ",").append("</td><td>!- ").append(comment).append("</td></tr>");
        }
        buf.append("</table>");
        return buf.toString();
    }
    
    /**
     * Export this object to IDF text form
     * @return 
     */
    public String exportObject () {
        StringBuilder buf = new StringBuilder ();
        buf.append(Type);
        for (String ele : Fields) {
            buf.append(",").append(ele);
        }
        buf.append(";");
        if (buf.length() > 300) {
            int start = 200;
            while (buf.length() > start) {
                buf.insert(buf.indexOf(",", start) + 1, "\n");
                start += 200;
            }
        }
        return buf.toString();
    }
    
}
