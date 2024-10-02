/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model;

import com.ensims.eplus.model.idd.IDD;
import com.ensims.eplus.model.idf.IDF;
import com.ensims.eplus.model.idf.IDFObject;
import com.ensims.eplus.model.idf.PredefinedIDFObject;
//import com.ensims.ep_model.output.EsoDataSet;
//import com.ensims.ep_model.output.OutputDataDictionary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class EPModelFilter {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPModelFilter.class);

    public static void main (String [] args) {

//        // Inject split tags
//        applyRunPeriodSplitTags(
//                "D:\\Dev-Java\\ep_model\\run\\Energy+.idd", 
//                "D:\\Dev-Java\\ep_model\\run\\split_run\\in_annual.idf", 
//                "D:\\Dev-Java\\ep_model\\run\\split_run\\in.idf");
//        System.exit(0);
        
        
        // Filter and condense idf of v89
        IDD idd = IDD.parseIDD("C:\\bin\\EnergyPlusV8-9-0\\Energy+.idd");
        File src1 = new File ("C:\\dev\\java\\ep_model\\run\\173354\\in.idf");
        IDF idf1 = IDF.parseIDF(idd, src1.getAbsolutePath());
        try (PrintWriter fw = new PrintWriter (new FileWriter ("C:\\dev\\java\\ep_model\\run\\173354\\filtered\\"+src1.getName()))) {
            fw.println(idf1.exportModel());
        }catch (Exception ex) {
            logger.error("", ex);
        }
        System.out.println("Merger: " + src1.getAbsolutePath() + " is copied to " + "filtered/" + src1.getName());
        System.exit(0);

        // Merge duplicate objects - not successful
        idd = IDD.parseIDD("Energy+.idd");
        // List idf files in source folder
        // File srcdir = new File ("D:\\Dev-Java\\ep_model\\run\\models\\");
        File srcdir = new File ("D:\\zyyz\\1-jEPlus\\Benchmark Sets\\Large models\\DB-800zonesStar\\");
        File [] srcfiles = srcdir.listFiles();
        for (File src : srcfiles) {
            // System.out.print(idd.toString());
            IDF idf = IDF.parseIDF(idd, src.getAbsolutePath());
            // System.out.print(idf.exportModel());
            int n = mergeObjects (idf, "D:\\Dev-Java\\ep_model\\run\\mergeables.txt", true);
            //int n=0;
            //try (PrintWriter fw = new PrintWriter (new FileWriter ("D:\\Dev-Java\\ep_model\\run\\filtered\\"+src.getName()))) {
            try (PrintWriter fw = new PrintWriter (new FileWriter ("D:\\zyyz\\1-jEPlus\\Benchmark Sets\\Large models\\DB-800zonesStarFiltered\\"+src.getName()))) {
                fw.println(idf.exportModel());
            }catch (Exception ex) {
                logger.error("", ex);
            }
            System.out.println("Merger: " + src.getAbsolutePath() + " is copied to " + "filtered/"+src.getName() + ", " + n + " objects have been removed.");
        }
        System.exit(0);
        
    }
    
    /**
     * 
     * @param idf
     * @param listfile
     * @param removedups
     * @return 
     */
    public static int mergeObjects (IDF idf, String listfile, boolean removedups) {
        int counter = 0;
        HashMap<String, Integer> MergeList = new HashMap<>();
        
        if (listfile != null && new File (listfile).exists()) {
            // Read list of object types to be merged
            try (BufferedReader fr = new BufferedReader (new FileReader (listfile))) {
                String line = fr.readLine();
                while (line != null) {
                    if (line.contains("#")) {
                        line = line.substring(0, line.indexOf('#'));
                    }
                    String [] elements = line.trim().split("\\s*,\\s*");
                    if (elements.length >= 2) {
                        MergeList.put(elements[0], Integer.valueOf(elements[1]));
                    }
                    line = fr.readLine();
                }
            }catch (Exception ex) {
                logger.error("Error reading from list file", ex);
            }
        
            // For each type, find sets of identical objects and create an inverse map for the identifiers
            HashMap<IDFObject, ArrayList<IDFObject>> IdenticalSets = new HashMap<> ();
            HashMap<String, String> IdentifierMap = new HashMap<> ();
            for (String ObjType : MergeList.keySet()) {
                List <IDFObject> objs = idf.getObjs().get(ObjType);
                if (objs != null) {
                    for (IDFObject obj : objs) {
                        // Don't what this does ...? obj.setIdentifier(MergeList.get(ObjType));
                        // If the identifier's length is less than 8, don't merge it
                        if (obj.getId().length() >= 8) { // Why??
                            Set<IDFObject> keys = IdenticalSets.keySet();
                            boolean exist = false;
                            for (IDFObject key : keys) {
                                if (key.isIdenticalTo(obj)) {
                                    IdenticalSets.get(key).add(obj);
                                    IdentifierMap.put(obj.getId(), key.getId());
                                    exist = true;
                                    counter ++;
                                    break;
                                }
                            }
                            if (! exist) {
                                IdenticalSets.put(obj, new ArrayList<IDFObject> ());
                            }
                        }
                    }
                }
            }

            // For each set, replace the reference to duplication objects with the first one
            HashMap <String, List<IDFObject>> ObjectMaps = (HashMap)((HashMap)idf.getObjs()).clone();
            // Exclude types in merge list. This means objects in merge list must not reference to other objs in the list
            for (String ObjType : MergeList.keySet()) {
                ObjectMaps.remove(ObjType);
            }
            // Compare every field in the remainder objects (not very efficient?)
            for (List<IDFObject> list : ObjectMaps.values()) {
                for (IDFObject obj : list) {
                    for (int i=0; i<obj.getFields().size(); i++) {
                        String param = obj.getFields().get(i);
                        if (IdentifierMap.containsKey(param)) {
                            obj.getFields().set(i, IdentifierMap.get(param));
                        }
                    }
                }
            }
            
            // Remove duplicate objects
            if (removedups) {
                for (IDFObject keyobj : IdenticalSets.keySet()) {
                    String ObjType = keyobj.getType();
                    List <IDFObject> objs = idf.getObjs().get(ObjType);
                    for (IDFObject obj : IdenticalSets.get(keyobj)) {
                        objs.remove(obj);
                    }
                }
            }
        }else {
            logger.error("Mergeable list file does not exist.");
        }
        
        return counter;
    }
    
    /**
     * Insert run period split tags into the given idf file, and save it to a new file
     * @param iddfile
     * @param inidf Input idf
     * @param outidf updated idf to save
     */
    public static void applyCsvTabularOutput (String iddfile, String inidf, String outidf) {
        IDD idd = IDD.parseIDD(iddfile);
        IDF idf = IDF.parseIDF(idd, inidf);

        // replace "OutputControl:Table:Style" object while preserving unit conversion
        List <IDFObject> objs = idf.removeObjectsOfType(new IDFObject (idd, PredefinedIDFObject.OutputTableStyleXML).getType());
        IDFObject newobj = new IDFObject (idd, PredefinedIDFObject.OutputTableStyleCSV);
        if (objs != null && objs.size()>0) {
            if (objs.get(0).getFields().size() > 1) {
                newobj.getFields().set(1, objs.get(0).getFields().get(1));
            }
        }
        idf.insertObject(newobj);
        
        // Save file
        try (PrintWriter fw = new PrintWriter (new FileWriter (outidf))) {
            fw.println(idf.exportModel());
        }catch (Exception ex) {
            logger.error("Error save file.", ex);
        }
    }

    /**
     * Insert run period split tags into the given idf file, and save it to a new file
     * @param iddfile
     * @param inidf Input idf
     * @param outidf updated idf to save
     */
    public static void applyRunPeriodSplitTags (String iddfile, String inidf, String outidf) {
        IDD idd = IDD.parseIDD(iddfile);
        IDF idf = IDF.parseIDF(idd, inidf);
        
        // update runperiod object with search tags
        IDFObject rpobj = idf.getObjs().get("RunPeriod").get(0);
        PredefinedIDFObject.updateIDFObjFromString(idd, rpobj, idf.getEPlusVersion().isOrAbove("9.0.0") ? PredefinedIDFObject.RunPeriodJEPlus_V90 : PredefinedIDFObject.RunPeriodJEPlus);
        
        // Add dxf
        idf.removeObjectsOfType(new IDFObject (idd, PredefinedIDFObject.OutputSurfaceDrawing).getType());
        idf.insertObject(new IDFObject (idd, PredefinedIDFObject.OutputSurfaceDrawing));
        
        // Add XML report while preserving unit conversion option
//        idf.removeObjectsOfType(new IDFObject (idd, PredefinedIDFObject.OutputTableStyleXML).getObjType());
//        idf.insertObject(new IDFObject (idd, PredefinedIDFObject.OutputTableStyleXML));
        List <IDFObject> objs = idf.removeObjectsOfType(new IDFObject (idd, PredefinedIDFObject.OutputTableStyleXML).getType());
        IDFObject newobj = new IDFObject (idd, PredefinedIDFObject.OutputTableStyleXML);
        if (objs != null && objs.size()>0) {
            if (objs.get(0).getFields().size() > 1) {
                newobj.getFields().set(1, objs.get(0).getFields().get(1));
            }
        }
        idf.insertObject(newobj);
        
        // Output data dictionary option to regular
        idf.removeObjectsOfType(new IDFObject (idd, PredefinedIDFObject.OutputDataDictionary).getType());
        idf.insertObject(new IDFObject (idd, PredefinedIDFObject.OutputDataDictionary));
        
        // Save file
        try (PrintWriter fw = new PrintWriter (new FileWriter (outidf))) {
            fw.println(idf.exportModel());
        }catch (Exception ex) {
            logger.error("Error save file.", ex);
        }
//        System.out.println("Search tags have been added to " + inidf + " and saved to " + outidf);
    }
}
