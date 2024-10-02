/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;

/**
 * Encapsulating the version info from various EnergyPlus files, e.g.
 * ESO: "Program Version,EnergyPlus, Version 8.9.0-eba93e8e1b, YMD=2019.02.20 18:42"
 * RDD/MDD "! Program Version,EnergyPlus, Version 8.9.0-eba93e8e1b, YMD=2019.02.20 18:42,"
 * IDD "!IDD_Version 8.9.0\n !IDD_BUILD eba93e8e1b"
 * @author yi
 */
@Embeddable
public class EPlusVersionInfo extends VersionInfo {
    /** EPlus BuildStr string */
    String BuildStr = null;
    /** EPlus build timestamp */
    @Temporal(javax.persistence.TemporalType.DATE)    Date BuildTimestamp = null;

    public EPlusVersionInfo() {
    }
    
    public EPlusVersionInfo (String type, String infostr) {
        switch (type) {
            case "IDD":
                parseVersion (infostr.substring(13));
                break;
            case "RDD":
            case "MDD":
            case "ESO":
            default:
                String [] parts = infostr.split("\\s*,\\s*");
                String [] ver_build = parts[2].substring(8).split("-");
                parseVersion (ver_build[0]);
                BuildStr = ver_build.length > 1 ? ver_build[1] : "NA";
                try {
                    BuildTimestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm").parse(parts[3].substring(4));
                } catch (ParseException ex) {
                    logger.error("Error parsing simulation timestamp.", ex);
                } catch (ArrayIndexOutOfBoundsException ae) {
                    logger.error("Problem with the version info string: " + infostr, ae);
                }
                break;
        }
    }
    
    public String getBuildStr() { return BuildStr; }
    public void setBuildStr(String BuildStr) { this.BuildStr = BuildStr; }
    public Date getTimeStamp () { return BuildTimestamp; }
    public String toString (String type) {
        StringBuilder buf = new StringBuilder();
        switch (type) {
            case "IDD":
                buf.append("!IDD_Version ").append(this.toString());
                break;
            case "RDD":
            case "MDD":
                buf.append("! ");
            case "ESO":
            default:
                buf.append("Program Version,EnergyPlus, Version ");
                buf.append(this.toString()).append("-").append(this.BuildStr).append(", YMD=");
                buf.append(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(BuildTimestamp));
        }
        return buf.toString();
    }
    public int getNMetaRows () {
        return isOrAbove("8.9") ? 6 : 5;
    }
}
