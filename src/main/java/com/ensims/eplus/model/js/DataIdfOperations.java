/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ensims.eplus.model.js;

import com.ensims.eplus.model.idf.IDFObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 *
 * @author yi
 */
public class DataIdfOperations {
    String Source = null;
    String Target = null;
    List<IDFObject> Objects = null;
    List<IDFObject> Deleted = null;
    String Code = null;     // Code segment from the text editor
    String Scope = "na";    // IDF object type to mark that code replaces all objects of the given type
    
    public DataIdfOperations () {}

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public String getTarget() {
        return Target;
    }

    public void setTarget(String Target) {
        this.Target = Target;
    }

    public List<IDFObject> getObjects() {
        return Objects;
    }

    public void setObjects(List<IDFObject> Objects) {
        this.Objects = Objects;
    }

    public List<IDFObject> getDeleted() {
        return Deleted;
    }

    public void setDeleted(List<IDFObject> Deleted) {
        this.Deleted = Deleted;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getScope() {
        return Scope;
    }

    public void setScope(String Scope) {
        this.Scope = Scope;
    }
    
    @JsonIgnore
    public boolean isCopyOnly () {
        return Objects == null && Deleted == null && Code == null;
    }
}
