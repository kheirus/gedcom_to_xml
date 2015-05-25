package com.gedcom.parser;

public class TestParsor {

	public static void main(String[] args) {
		GedcomToXML g= new GedcomToXML("complet.ged");
		g.parseGedcom();
	}

}
