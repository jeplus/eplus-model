/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.js;

import java.util.ArrayList;

/**
 *
 * @author Yi
 */
public class DataJSTreeMin {
    
    String Text = null;
    String Type = null;
    Object Data = null;
    ArrayList<DataJSTreeMin> Children = new ArrayList<> ();

    public DataJSTreeMin() {
    }

    public DataJSTreeMin (String text, String type, Object data) {
        Text = text;
        Type = type;
        Data = data;
    }
    
    public String getText() {
        return Text;
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public ArrayList<DataJSTreeMin> getChildren() {
        return Children;
    }

    public void setChildren(ArrayList<DataJSTreeMin> Children) {
        this.Children = Children;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object Data) {
        this.Data = Data;
    }

}
