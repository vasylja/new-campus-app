package de.tum.in.tumcampus.models;

import java.util.ArrayList;
import java.util.List;

/**
 * An List of Elements of the Organisation Tree.
 * One element is the ({@link OrgItem}).
 * <p>
 * 
 * @author Thomas Behrens
 * @review Vincez Doelle, Daniel G. Mayr
 */

public class OrgItemList {

	/** List of OrganisationItems */
	private List<OrgItem> groups = new ArrayList<OrgItem>();

	/** getter */
	public List<OrgItem> getGroups() {
		return groups;
	}

	/** setter */
	public void setGroups(List<OrgItem> groups) {
		this.groups = groups;
	}

	/** add an element to the list */
	public void add(OrgItem o) {
		this.groups.add(o);
	}

}