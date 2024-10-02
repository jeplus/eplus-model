/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.idf;

import com.ensims.eplus.model.idd.IDD;

/**
 * This class contains a set of predefined IDF objects (neutral to IDD versions). 
 * These objects are accessible through the correspondent static method getIDFObjFromString()
 * @author Yi
 */
public class PredefinedIDFObject {
    public static final String OutputSQLite = "Output:SQLite,SimpleAndTabular;";
    public static final String OutputTableStyleXML = "OutputControl:Table:Style,XMLAndHTML,JtoKWH;";
    public static final String OutputTableStyleCSV = "OutputControl:Table:Style,CommaAndHTML,JtoKWH;";
    public static final String OutputDataDictionary = "Output:VariableDictionary,regular;";
    public static final String OutputSurfaceDrawing = "Output:Surfaces:Drawing,DXF,Triangulate3DFace;";

    public static final String SimulationControl = "SimulationControl,No,No,No,Yes,Yes;";
    public static final String RunPeriodControl = "RunPeriod,Run Period,1,1,12,31,UseWeatherFile,Yes,Yes,No,Yes,Yes,1;";
    public static final String RunPeriodJEPlus = "RunPeriod,<KEEP>,@@StartMonth@@,@@StartDate@@,@@EndMonth@@,@@EndDate@@,<KEEP>,<KEEP>,<KEEP>,<KEEP>,<KEEP>,<KEEP>,<KEEP>;";
    public static final String RunPeriodJEPlus_V90 = "RunPeriod,<KEEP>,@@StartMonth@@,@@StartDate@@,<KEEP>,@@EndMonth@@,@@EndDate@@,<KEEP>,<KEEP>,<KEEP>,<KEEP>,<KEEP>,<KEEP>,<KEEP>;";

    /**
     * Factory method for constructing an IDF object from raw string
     * @param idd
     * @param raw
     * @return 
     */
    public static IDFObject getIDFObjFromString (IDD idd, String raw) {
        return new IDFObject (idd, raw);
    }

    /**
     * Factory method for constructing an IDF object from raw string
     * @param idd
     * @param obj The existing object
     * @param raw Raw object string containing updates to the given object. Fields to keep are marked as 
     * @return 
     */
    public static IDFObject updateIDFObjFromString (IDD idd, IDFObject obj, String raw) {
        IDFObject patch = new IDFObject (idd, raw);
        if (obj.getType().equals(patch.getType())) {
            for (int i=0; i<obj.getFields().size(); i++) {
                if (i < patch.getFields().size() && ! "<KEEP>".equals(patch.getFields().get(i))) {
                    obj.getFields().set(i, patch.getFields().get(i));
                }
            }
        }
        return obj;
    }
}
