/*
 * To change this license header, choose License Headers in Project Defs.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ensims.eplus.model.idd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Yi
 */
public class IDDObjectField {
    
    public static enum CommentType {
	field,          // Name of field
			// (should be succinct and readable, blanks are encouraged)
	note,           // Note describing the field and its valid values. If multiple lines,
			// start each line with \note. Limit line length to 100 characters.
//	required-field,	// To flag fields which must have a value. If the idf input is blank and
//			// there is a \default, then the default will be used. However, as of v8.6.0
//			// the use of \required-field and \default on the same field is discouraged
//			// and instances with both have been changed.
//			// (this comment has no "value")
//	begin-extensible,	// Marks the first field at which the object accepts an extensible
//			// field set.  A fixed number of fields from this marker define the
//			// extensible field set, see the object code \extensible for
//			// more information.
//	units,          // Units (must be from EnergyPlus standard units list)
//			// EnergyPlus units are standard SI units
//	ip-units,	// IP-Units (for use by input processors with IP units)
//			// This is only used if the default conversion is not
//			// appropriate.
//	unitsBasedOnField,	// For fields that may have multiple possible units, indicates
//			// the field in the object that can be used to determine
//			// the units. The field reference is in the A2 form.
//	minimum,	// Minimum that includes the following value
//	minimum>,	// Minimum that must be > than the following value
//	maximum,	// Maximum that includes the following value
//	maximum<,	// Maximum that must be < than the following value
//	default,	// Default for the field (if N/A then omit entire line). If a default is
//			// added to an existing field, then \required-field should be removed if present.
//			// Defaults are filled in only if the field is within \min-fields, or the actual
//			// object is longer than this field.
//	deprecated,	// This field is not really used and will be deleted from the object.
//			// The required information is gotten internally or
//			// not needed by the program.
//	autosizable,	// Flag to indicate that this field can be used with the Auto
//			// Sizing routines to produce calculated results for the
//			// field.  If a value follows this, then that will be used
//			// when the "Autosize" feature is flagged.  To trigger
//			// autosizing for a field, enter Autosize as the field's
//			// value.  Only applicable to numeric fields.
//	autocalculatable,	// Flag to indicate that this field can be automatically
//			// calculated. To trigger auto calculation for a field, enter
//			// Autocalculate as the field's value.  Only applicable to
//			// numeric fields.
//	type,           // Type of data for the field -
//			// integer
//			// real
//			// alpha       (arbitrary string),
//			// choice      (alpha with specific list of choices, see key)
//			// object-list (link to a list of objects defined elsewhere,
//			// see \object-list and \reference)
//			// external-list (uses a special list from an external source,
//			// see \external-list)
//			// node        (name used in connecting HVAC components)
//	retaincase,	// Retains the alphabetic case for alpha type fields
//	key,            // Possible value for "\type choice" (blanks are significant)
//			// use multiple \key lines to indicate all valid choices
//	object-list,	// Name of a list of user-provided object names that are valid
//			// entries for this field (used with "\reference")
//			// see Zone and BuildingSurface:Detailed objects below for
//			// examples.
//			// ** Note that a field may have multiple \object-list commands.
//	external-list,	// The values for this field should be selected from a special
//			// list generated outside of the IDD file. The choices for the
//			// special lists are:
//			// autoRDDvariable
//			// autoRDDmeter
//			// autoRDDvariableMeter
//			// When one of these are selected the options for the field
//			// are taken from the RDD or MDD file or both.
	reference	// Name of a list of names to which this object belongs
			// used with "\type object-list" and with "\object-list"
			// see Zone and BuildingSurface:Detailed objects below for
			// examples:
			//	 Zone,
			//	 A1,	\field Name
			//			\type alpha
			//			\reference ZoneNames
			//	 BuildingSurface:Detailed,
			//	 A4,	\field Zone Name
			//			\note Zone the surface is a part of
			//			\type object-list
			//			\object-list ZoneNames
			// For each zone, the field "Name" may be referenced
			// by other objects, such as BuildingSurface:Detailed, so it is
			// commented with "\reference ZoneNames"
			// Fields that reference a zone name, such as BuildingSurface:Detailed's
			// "Zone Name", are commented as
			// "\type object-list" and "\object-list ZoneNames"
			// ** Note that a field may have multiple \reference commands.
			// ** This is useful if the object belongs to a small specific
			// object-list as well as a larger more general object-list.
    }
    
    String Id = null;
    int Idx = 0;
    Map <String, List<String>> Defs = new LinkedHashMap <> ();

    public IDDObjectField (String name, int idx) {
        Id = name;
        Idx = idx;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public int getIdx() {
        return Idx;
    }

    public void setIdx(int Idx) {
        this.Idx = Idx;
    }

    public Map<String, List<String>> getDefs() {
        return Defs;
    }

    public void setDefs(Map<String, List<String>> defs) {
        this.Defs = defs;
    }
    
    public void addDef (String id, String val) {
        List<String> cur = Defs.get(id);
        if (cur == null) {
            cur = new ArrayList<> ();
            cur.add(val);
            Defs.put(id, cur);
        }else {
            cur.add(val);
        }
    }
    
    /**
     * Test if this field definition has a "required-field" property
     * @return true if the definition contains required-field property
     */
    public boolean isRequired () {
        return this.getDefs().containsKey("required-field");
    }
    
    public boolean hasProperty (String prop) {
        return this.getDefs().containsKey(prop);
    }
    
    public String listProperties(List <String> exclude) {
        StringBuilder buf = new StringBuilder ();
        for (String key : getDefs().keySet()) {
            if (! exclude.contains(key)) {
                buf.append("<i>").append(key).append("</i> : ");
                getDefs().get(key).forEach((val) -> {
                    buf.append("").append(val).append("<br />");
                });
            }
        }
        return buf.toString();
    }
}
