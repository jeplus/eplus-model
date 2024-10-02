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
public class DataJSTreeItemAttr implements IF_JSTreeItemAttr {
    
    String Attr_class = null;
    String Data_toggle = null;
    boolean Data_html = false;
    String Data_title = null;
//    @JsonProperty("class") String Attr_class = null;
//    @JsonProperty("data-toggle") String Data_toggle = null;
//    @JsonProperty("data-html") boolean Data_html = false;
//    @JsonProperty("data-title") String Data_title = null;
    
    public DataJSTreeItemAttr () {}

    public DataJSTreeItemAttr (String cls, String tgl, boolean htm, String title) {
        Attr_class = cls;
        Data_toggle = tgl;
        Data_html = htm;
        Data_title = title;
    }
    
    @JsonProperty("rel")
    @Override
    public String getAttr_class() {
        return Attr_class;
    }

    public void setAttr_class(String Attr_class) {
        this.Attr_class = Attr_class;
    }

    @JsonProperty("data-toggle")
    public String getData_toggle() {
        return Data_toggle;
    }

    public void setData_toggle(String Data_toggle) {
        this.Data_toggle = Data_toggle;
    }

    @JsonProperty("data-html")
    public boolean isData_html() {
        return Data_html;
    }

    public void setData_html(boolean Data_html) {
        this.Data_html = Data_html;
    }

    @JsonProperty("data-title")
    public String getData_title() {
        return Data_title;
    }

    public void setData_title(String Data_title) {
        this.Data_title = Data_title;
    }
    
    
}
