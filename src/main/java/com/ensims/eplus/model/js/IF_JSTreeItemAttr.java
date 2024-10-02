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
public interface IF_JSTreeItemAttr {

    @JsonProperty(value = "class")
    String getAttr_class();
    
}
