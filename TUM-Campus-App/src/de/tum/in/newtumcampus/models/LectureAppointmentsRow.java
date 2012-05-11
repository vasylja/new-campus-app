package de.tum.in.newtumcampus.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * This class is dealing with the deserialization of the output of TUMOnline to the method "TermineLehrveranstaltungen".
 * 
 * @author Daniel Mayr
 * 
 * @see http://simple.sourceforge.net/download/stream/doc/tutorial/tutorial.php
 * 
 */
@Root(name = "row")
public class LectureAppointmentsRow {

	@Element(required = false)
	private String ort;

	@Element(required = false)
	private String art;

	@Element
	private String beginn_datum_zeitpunkt;

	@Element
	private String ende_datum_zeitpunkt;

	@Element(required = false)
	private String termin_betreff;

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getArt() {
		return art;
	}

	public void setArt(String art) {
		this.art = art;
	}

	public String getTermin_betreff() {
		return termin_betreff;
	}

	public void setTermin_betreff(String termin_betreff) {
		this.termin_betreff = termin_betreff;
	}

	public String getBeginn_datum_zeitpunkt() {
		return beginn_datum_zeitpunkt;
	}

	public void setBeginn_datum_zeitpunkt(String beginn_datum_zeitpunkt) {
		this.beginn_datum_zeitpunkt = beginn_datum_zeitpunkt;
	}

	public String getEnde_datum_zeitpunkt() {
		return ende_datum_zeitpunkt;
	}

	public void setEnde_datum_zeitpunkt(String ende_datum_zeitpunkt) {
		this.ende_datum_zeitpunkt = ende_datum_zeitpunkt;
	}

}