package edu.ucdenver.ccp.uima_sample.mention_extensions;


import java.util.Collection;
import java.util.ArrayList;

import org.apache.uima.jcas.JCas;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import edu.ucdenver.ccp.uima_sample.mention.ClassMention;
import edu.ucdenver.ccp.uima_sample.mention.SlotMention;
import edu.ucdenver.ccp.uima_sample.mention.StringSlotMention;
import edu.ucdenver.ccp.uima_sample.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.uima_sample.mention.ComplexSlotMention;

// TODO: dream up a way to deal with the (remote) possibility of someone
// else using just plain ClassMention, or even UIMA type compatible,
// types to create a class mention with multiple slot mentions of the
// same name. Preventing it may be a dream or a futile excercise in control,
// but throwing an exception from getSlotMention* may altert folks to
// inconsistencies. (really just a compile-time vs run-time argument)

// TODO: Is this reasonable? a sieve-like API: you can't count on the
// class invariants that might or might not be used/enforced
// in these extensions. It really pushes a lot of checks to run-time.
// It also has to do with progressive disclosure. Some folks learn
// UIMA before this API and will code to that before they learn
// these extensions.

// TODO: consider a full-blown map interface to the class's slots


public class ClassMentionX {


	private static Collection<SlotMention> getSlotMentionsByType(ClassMention cm,Class clazz) {
		Collection<SlotMention> slotList = new ArrayList<SlotMention>();
        FSArray slotMentionsArray = cm.getSlotMentions();
        if (slotMentionsArray != null) {
            for (int i = 0; i < slotMentionsArray.size(); i++) {
                FeatureStructure fs = slotMentionsArray.get(i);
				SlotMention sm = (SlotMention) fs;
				if (clazz.isInstance(fs)) {
                	slotList.add(sm);
				}
            }
		}
		return slotList;
	}

	public static Collection<String> getSlotMentionNames(ClassMention cm) {
		Collection<String> nameList = new ArrayList<String>();

		for (SlotMention sm : getSlotMentionsByType(cm, SlotMention.class)) {
        	nameList.add(sm.getMentionName());
		}

		return nameList;
	}

	public static Collection<String> getPrimitiveSlotMentionNames(ClassMention cm) {
		Collection<String> nameList = new ArrayList<String>();

		for (SlotMention sm : getSlotMentionsByType(cm, PrimitiveSlotMention.class)) {
        	nameList.add(sm.getMentionName());
		}

		return nameList;
	}

	public static Collection<String> getComplexSlotMentionNames(ClassMention cm) {
		Collection<String> nameList = new ArrayList<String>();

		for (SlotMention sm : getSlotMentionsByType(cm, ComplexSlotMention.class)) {
        	nameList.add(sm.getMentionName());
		}

		return nameList;
	}


	/**
	 * Sets a single String value on the named slot, obliterating any
	 * previously present values, throwing if there was more than one.
	 * this will also throw if the named slot isn't of type StringSlotMention.
	 */
	public void setStringSlotMentionValue(JCas jCas, ClassMention cm, String name, String value) 
	throws Exception {

		StringSlotMention sm = (StringSlotMention) getPrimitiveSlotMentionByName(cm, name);
		if (sm == null) {		
			sm = new StringSlotMention(jCas);
			sm.setMentionName(name);
		}
		else {
			// TODO: throw if > 1 value here already
		}
		StringArray values = new StringArray(jCas, 1);
		values.set(0,value);
		sm.setSlotValues(values);

		FSArray slots = cm.getSlotMentions();
		FSArray newSlots = null;
		if (slots == null) {
			newSlots = new FSArray(jCas, 1);
			newSlots.set(0, sm);
		}	
		else {
			newSlots = new FSArray(jCas, slots.size());
			for (int i=0; i<slots.size(); i++) {
				newSlots.set(i, slots.get(i));
			}
			// TODO: check for name repeat
			newSlots.set(slots.size(), sm);
		}
		cm.setSlotMentions(newSlots);
	}

	public String getStringSlotMentionValue(ClassMention cm, String name) {
		StringSlotMention slot = (StringSlotMention) getSlotMentionByName(cm, name);
		return slot.getSlotValues(0);
	}


	/**
	 * returns first SlotMention with given name, null if none.
	 **/
	public static SlotMention getSlotMentionByName(ClassMention cm, String name) {
		
		for (SlotMention sm : getSlotMentionsByType(cm, SlotMention.class)) {
        	if (name.equals(sm.getMentionName())) {
				return sm;
			}
		}

		return null;
    }

	public static PrimitiveSlotMention getPrimitiveSlotMentionByName(ClassMention cm, String name) {

		for (SlotMention sm : getSlotMentionsByType(cm, PrimitiveSlotMention.class)) {
        	if (name.equals(sm.getMentionName())) {
				return (PrimitiveSlotMention) sm;
			}
		}

		return null;
    }

	public static ComplexSlotMention getComplexSlotMentionByName(ClassMention cm, String name) {

		for (SlotMention sm : getSlotMentionsByType(cm, ComplexSlotMention.class)) {
        	if (name.equals(sm.getMentionName())) {
				return (ComplexSlotMention) sm;
			}
		}

		return null;
    }

}
