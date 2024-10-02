/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
@Embeddable
@XmlRootElement
public class VersionInfo implements Comparable, Serializable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(VersionInfo.class);
    
    /** Major version number */
    Integer Major = 0;
    /** Minor version number */
    Integer Minor = 0;
    /** Revision number */
    Integer Revision = null;
    /** UpdateNum number */
    Integer UpdateNum = null;
    
    public VersionInfo () {}
    public VersionInfo (String verstr) {
        parseVersion (verstr);
    }

    public Integer getMajor() {
        return Major;
    }

    public void setMajor(Integer Major) {
        this.Major = Major;
    }

    public Integer getMinor() {
        return Minor;
    }

    public void setMinor(Integer Minor) {
        this.Minor = Minor;
    }

    public Integer getRevision() {
        return Revision;
    }

    public void setRevision(Integer Revision) {
        this.Revision = Revision;
    }

    public Integer getUpdateNum() {
        return UpdateNum;
    }

    public void setUpdateNum(Integer UpdateNum) {
        this.UpdateNum = UpdateNum;
    }

    final public void parseVersion (String verstr) {
        if (verstr != null && verstr.trim().length() > 0) {
            String [] parts = verstr.trim().split("\\.");
            try {
                Major = new Integer (parts[0]);
                if (parts.length > 1) {
                    Minor = new Integer (parts[1]);
                }
                if (parts.length > 2) {
                    Revision = new Integer (parts[2]);
                }
                if (parts.length > 3) {
                    UpdateNum = new Integer (parts[3]);
                }
            }catch (NumberFormatException nfe) {
                logger.error ("Version string \"" + verstr + "\" is not recognized! Version is set to 0.0.0 ");
                Major = Minor = Revision = 0;
            }
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.Major);
        hash = 97 * hash + Objects.hashCode(this.Minor);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VersionInfo other = (VersionInfo) obj;
        if (!Objects.equals(this.Major, other.Major)) {
            return false;
        }
        if (!Objects.equals(this.Minor, other.Minor)) {
            return false;
        }
        return true;
    }
    
    
    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder();
        buf.append(Major).append(".").append(Minor);
        if (Revision != null) {
            buf.append(".").append(Revision);
            if (UpdateNum != null) {
                buf.append(".").append(UpdateNum);
            }
        }
        return buf.toString();
    }

    public String toMajorDotMinor () {
        StringBuilder buf = new StringBuilder();
        buf.append(Major).append(".").append(Minor);
        return buf.toString();
    }

    @Override
    public int compareTo(Object obj) {
        if (obj == null) {
            logger.warn("Comparing current version to NULL.");
            return 1;
        }
        if (! (obj instanceof VersionInfo)) {
            logger.warn("Comparing current version to non-version-info object: " + obj.toString());
            return 1;
        }
        final VersionInfo other = (VersionInfo) obj;
        if (this.Major.compareTo(other.Major) > 0) {
            return 1;
        }else if (this.Major.compareTo(other.Major) == 0) {
            return this.Minor.compareTo(other.Minor);
        }else {
            return -1;
        }
    }
    
    public boolean isOrAbove (String ver) {
        return this.compareTo(new VersionInfo(ver)) >= 0;
    }
   
    public boolean isOrBelow (String ver) {
        return this.compareTo(new VersionInfo(ver)) <= 0;
    }
   
}
