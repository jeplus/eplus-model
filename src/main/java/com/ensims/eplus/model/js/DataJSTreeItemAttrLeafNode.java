/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.js;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Yi
 */
public class DataJSTreeItemAttrLeafNode implements IF_JSTreeItemAttr {
    
    String Attr_class = null;
    
    public DataJSTreeItemAttrLeafNode () {}

    public DataJSTreeItemAttrLeafNode (String cls) {
        Attr_class = cls;
    }
    
    @Override
    public String getAttr_class() {
        return Attr_class;
    }

    public void setAttr_class(String Attr_class) {
        this.Attr_class = Attr_class;
    }

}
