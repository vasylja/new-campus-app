package de.tum.in.newtumcampus.models;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * This class is dealing with the deserialization of the output of TUMOnline to the method "sucheLehrveranstaltungen" or
 * "eigeneLehrveranstaltungen".
 * 
 * @author Daniel Mayr
 * @see FindLecturesRow
 * @see http://simple.sourceforge.net/download/stream/doc/tutorial/tutorial.php
 * @review Thomas Behrens
 */
@Root(name = "rowset")
public class FindLecturesRowSet {

	@ElementList(inline = true)
	private List<FindLecturesRow> lehrveranstaltungen;

	public List<FindLecturesRow> getLehrveranstaltungen() {
		return lehrveranstaltungen;
	}

	public void setLehrveranstaltungen(List<FindLecturesRow> lehrveranstaltungen) {
		this.lehrveranstaltungen = lehrveranstaltungen;
	}

}
