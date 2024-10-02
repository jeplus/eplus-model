/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.js;

import com.ensims.eplus.model.idd.IDDObject;

/**
 *
 * @author yi
 */
public class DataIDDObjectHtml {
    IDDObject Data = null;
    String Html = null;
    
    public DataIDDObjectHtml (IDDObject data) {
        Data = data;
        if (data != null) {
            Html = data.toHtml();
        }
    }

    public IDDObject getData() {
        return Data;
    }

    public void setData(IDDObject Data) {
        this.Data = Data;
    }

    public String getHtml() {
        return Html;
    }

    public void setHtml(String Html) {
        this.Html = Html;
    }
    
}
