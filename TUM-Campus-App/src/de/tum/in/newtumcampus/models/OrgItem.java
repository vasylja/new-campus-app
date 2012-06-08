package de.tum.in.newtumcampus.models;

/** An Element of the Organisation Tree. In the App a List of those Elements is showed ({@link OrgItemList}). The shown
 * Elements are for Navigation to an Element without child-Element, whose details are then shown.
 * <p>
 * 
 * @author Thomas Behrens
 * @review Daniel G. Mayr, Vincenz Doelle */

public class OrgItem {

	/** Organisation ID -> to identify */
	private String id;

	/** Organisation ID of the parent Organisation */
	private String parentId;

	/** German Description of the Organisation */
	private String nameDe;

	/** English Description of the Organisation */
	private String nameEn;

	// Getter and Setter Functions
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String id) {
		this.parentId = id;
	}

	public String getNameDe() {
		return nameDe;
	}

	public void setNameDe(String name) {
		this.nameDe = name;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}
}
