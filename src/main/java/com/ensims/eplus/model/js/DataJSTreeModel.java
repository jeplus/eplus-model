/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.js;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Yi
 */

public class DataJSTreeModel extends DataJSTreeMin {
    
    String ID = null;
    public static class NodeState {
        boolean Opened = false;
        boolean Disabled = false;
        boolean Selected = false;
        public NodeState () {}

        public boolean isOpened() {
            return Opened;
        }

        public void setOpened(boolean Opened) {
            this.Opened = Opened;
        }

        public boolean isDisabled() {
            return Disabled;
        }

        public void setDisabled(boolean Disabled) {
            this.Disabled = Disabled;
        }

        public boolean isSelected() {
            return Selected;
        }

        public void setSelected(boolean Selected) {
            this.Selected = Selected;
        }
    }
    NodeState State = null;
    IF_JSTreeItemAttr Li_attr = null;
    IF_JSTreeItemAttr A_attr = null;
    
    
    public DataJSTreeModel () { }

    /**
     * Constructor with auto ID converted from the text field
     * @param text The text field, also used to specify ID
     * @param type Type field
     * @param data Data object
     */
    public DataJSTreeModel (String text, String type, Object data) {
        super (text, type, data);
        ID = text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
        // State = new NodeState();
    }

    /**
     * Explicity specifies ID. If the specified id is null, JS tree will create one automatically. this is necessary for ESO, for example
     * @param id
     * @param text
     * @param type
     * @param data 
     */
    public DataJSTreeModel (String id, String text, String type, Object data) {
        super (text, type, data);
        // ID = id == null ? text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_") : id;
        ID = id;    // Allow ID to be assigned by JS Tree if id is null
        // State = new NodeState();
    }

    /**
     * With an additional flag to create an expanded node
     * @param id
     * @param text
     * @param type
     * @param data
     * @param open 
     */
    public DataJSTreeModel (String id, String text, String type, Object data, boolean open) {
        super (text, type, data);
        // ID = id == null ? text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_") : id;
        ID = id;    // Allow ID to be assigned by JS Tree if id is null
        if (open) {
            State = new NodeState();
            State.setOpened(true);
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public NodeState getState() {
        return State;
    }

    public void setState(NodeState State) {
        this.State = State;
    }


    public IF_JSTreeItemAttr getLi_attr() {
        return Li_attr;
    }

    public void setLi_attr(IF_JSTreeItemAttr Li_attr) {
        this.Li_attr = Li_attr;
    }

    public IF_JSTreeItemAttr getA_attr() {
        return A_attr;
    }

    public void setA_attr(IF_JSTreeItemAttr A_attr) {
        this.A_attr = A_attr;
    }

    @JsonIgnore
    public void setNodeExpanded (boolean opened) {
        if (State == null) {
            State = new NodeState();
        }
        State.setOpened(opened);
    }
}
