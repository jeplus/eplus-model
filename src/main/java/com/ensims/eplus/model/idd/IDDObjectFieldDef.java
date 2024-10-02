/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.idd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;

/**
 *
 * @author Yi
 */
public class IDDObjectFieldDef {
    public static String [] DefType = new String [] {
        "field",
        "note",
        "required-field",
        "begin-extensible",
        "units",
        "ip-units",
        "unitsBasedOnField",
        "minimum",
        "minimum>",
        "maximum",
        "maximum<",
        "default",
        "deprecated",
        "autosizable",
        "autocalculatable",
        "type",
        "retaincase",
        "key",
        "object-list",
        "external-list",
        "reference"
    };
    
    ArrayList<String> Vals = new ArrayList<>();

    IDDObjectFieldDef(String text) {
        Vals.add(text);
    }

    public ArrayList<String> getVals() {
        return Vals;
    }

    public void setVals(ArrayList<String> Vals) {
        this.Vals = Vals;
    }
    
    @JsonIgnore
    public String getText () {
        StringBuilder buf = new StringBuilder();
        Vals.forEach((line) -> {
            buf.append(line).append("  ");
        });
        return buf.toString();
    }
}
