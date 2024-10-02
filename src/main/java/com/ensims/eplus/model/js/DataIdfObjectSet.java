/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.js;

import com.ensims.eplus.model.idd.IDDObject;
import com.ensims.eplus.model.idd.IDDObjectField;
import com.ensims.eplus.model.idf.IDFObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 *
 * @author yi
 */
public class DataIdfObjectSet {
    
    public static enum TextFormat {
        Compact,    // compact format, one object in one line of text
        Indented,   // formatted with indentation and white spaces
        Full        // Indented format with comments from the associated IDD object
    }
    
    String Comment = null;
    List<IDFObject> Objects = null;
    IDDObject Type = null;
    String Text = null;
    
    public DataIdfObjectSet (List<IDFObject> objects, IDDObject type, String comment) {
        Comment = comment;
        Objects = objects;
        Type = type;
        Text = toFormattedText (TextFormat.Full);
    }
    
    // ===========

    public String getComment() {
        return Comment;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    public List<IDFObject> getObjects() {
        return Objects;
    }

    public void setObjects(List<IDFObject> Objects) {
        this.Objects = Objects;
    }

    public String getText() {
        return Text;
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public IDDObject getType() {
        return Type;
    }

    public void setType(IDDObject Type) {
        this.Type = Type;
    }

    // ========
    
    public final String toFormattedText (TextFormat style) {
        StringBuilder buf = new StringBuilder (Comment);
        switch (style) {
            case Compact:
                if (Objects != null) {
                    buf.append("\n");
                    Objects.forEach((obj) -> {
                        buf.append("\n").append(obj.exportObject());
                    });
                }else {
                    buf.append("\n\n\tNo object is selected");
                }
            case Indented:
                if (Objects != null) {
                    buf.append("\n");
                    Objects.forEach((obj) -> {
                        buf.append("\n").append(obj.getType());
                        for (String ele : obj.getFields()) {
                            buf.append(",\n\t").append(ele);
                        }
                        buf.append(";\n");
                    });
                }else {
                    buf.append("\n\n\tNo object is selected");
                }
            case Full:
            default:
                if (Objects != null && Type != null) {
                    buf.append("\n");
                    Objects.forEach((obj) -> {
                        buf.append("\n").append(obj.getType()).append(",\n");
                        for (int i=0; i<obj.getFields().size(); i++) {
                            String ele = obj.getFields().get(i);
                            buf.append("\t").append(ele).append(i == obj.getFields().size()-1 ? ";" : ",");
                            List<String> field = Type.getFields().get(i).getDefs().get("field");
                            if (field != null && ! field.isEmpty()) {
                                buf.append("\t\t!- ").append(field.get(0));
                            }
                            buf.append("\n");
                        }
                    });
                }else {
                    buf.append("\n\n\tObject or Type information is missing");
                }
                
        }

        
        return buf.toString();
    }
}
