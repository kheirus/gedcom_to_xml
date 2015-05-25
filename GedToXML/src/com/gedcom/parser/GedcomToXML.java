/**
 * The GedcomToXML class allows to parse a GEDCOM v 5.5.1 file and generate a valid XML (validation with local DTD).
 * See the user manual for more details.
 *  © Copyright 2014 K. Ouelaa, R. Oubraim 
 * **/

package com.gedcom.parser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.gedcom4j.model.Family;
import org.gedcom4j.model.FileReference;
import org.gedcom4j.model.Header;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.Multimedia;
import org.gedcom4j.model.Place;
import org.gedcom4j.model.StringWithCustomTags;
import org.gedcom4j.model.Submitter;
import org.gedcom4j.model.Trailer;
import org.gedcom4j.parser.GedcomParser;
import org.gedcom4j.parser.GedcomParserException;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;



/**  
 * The <b>GedcomToXML</b> class represents a tool that allows to parse a <i>GEDCOM v 5.5.1</i> file and generate a valid <i>XML</i> .
 *   @author Kheireddine Ouelaa and Rachid Oubraim 
 *   @version 0.1  
 */
public class GedcomToXML {
	/**
	 * the XML document 
	 */
	private Document document;

	/**
	 * An <i>XML</i> element generated from a <i>GEDCOM</i> tag
	 *  
	 */
	private Element gedcom, head, indi, fam, name, firstname, lastname, sex, fams, famc, wife, husb ,chil,
	trlr, obje, file, form, titl, marr, div, plac, date, birt, deat, buri, chr, sour, dest, charr,
	addr , cont, phon, subm, sname ;

	/**
	 * An <i>XML</i> attribute generated from a <i>GEDCOM</i> tag
	 */
	private Attribute idIndi, idFam, idSubm, refFams, refFamc, refChil, 
	refHusb, refWife, type, isDiv ;

	/**
	 * date's event
	 */
	private  StringWithCustomTags dateEvent;

	/**
	 * type of the event (MARRIAGE, DIVORCE...)
	 */
	private  String eventType;

	/**
	 * place of the event
	 */
	private  Place eventPlace;

	/**
	 * multimedia attached to an individual or a family
	 */
	private  Multimedia multimedia;

	/**
	 * GEDCOM file name
	 * */
	private  String gedcomFileName;

	/**
	 * XML file name
	 * */
	private  String xmlFileName;

	/**
	 * 
	 */
	private DocType docType;
	/**
	 * TODO
	 * */
	boolean bool =true;

	/**  
	 * Constructor of the <b>GedcomToXML</b> class, here we introduce the DTD file wich will be used to validate the XML generated file.
	 *   @param gedcomFileName : name of the GEDCOM v 5.5.1 file, must be a <b>valide</b> 
	 *   file with <b>".ged"</b> extension
	 *     
	 */
	public GedcomToXML(String gedcomFileName) {
		docType= new DocType("gedcom", "gedcom.dtd");
		gedcom = new Element("gedcom");
		document = new Document(gedcom,docType);
		this.gedcomFileName=gedcomFileName;
	}



	/**
	 * Parse the GEDCOM file and generate the appropriate <i>XML</i> file 
	 */
	public void parseGedcom(){  
		GedcomParser gp = new GedcomParser();
		
		try{
			gp.load(gedcomFileName);

		}catch (GedcomParserException e) {
			bool=false;
			System.out.println("ALERT : Probleme with parseur");
			//e.printStackTrace();

		}catch (IOException e) {
			bool=false;
			System.out.println("ALERT : Check the name of the file");
		}
		/**---------------------------- CHECK HEADER -------------------------------**/
		Header h = gp.gedcom.header;
		if (h!=null){
			setHeader(h);
		}

		/**---------------------------- CHECK INDIVIDUALS -------------------------------**/
		for (Submitter s : gp.gedcom.submitters.values()) {
			if(s!=null){
				setSumbitter(s);
			}
		}
		/**---------------------------- CHECK INDIVIDUALS -------------------------------**/

		for (Individual i : gp.gedcom.individuals.values()) {
			if(i!=null){
				setIndividuals(i);
			}
		}
		/**---------------------------- CHECK FAMILIES -------------------------------**/

		for (Family f : gp.gedcom.families.values()) {
			if(f!=null){
				setFamilyIndividuals(f);
			}
		}
		/**---------------------------- CHECK TRAILER -------------------------------**/
		Trailer t = gp.gedcom.trailer;
		if (t!=null){
			trlr=new Element("trlr");
			gedcom.addContent(trlr);
		}
		/**---------------------------- VALIDATION -------------------------------**/
		//Save and validate the XML file 
		
// Small waiting animation		
				
		if(bool){
			try {
				System.out.print("Parse..");
				String chars = "....";
				int idx = 0;
				for (int i=0; i < 20; i++) {
					System.out.print("\b" + chars.charAt(idx));
					Thread.sleep(50);
					idx = ++idx % chars.length();
				}

			} catch (InterruptedException e) {
			}

		xmlFileName=saveXML(gedcomFileName);
		validateWithDTD(xmlFileName);
		printXML();
		}
		


	}
	/*---------------------------------------------------------------------------------*/
	/*---------------------------------- METHODS --------------------------------------*/
	/*---------------------------------------------------------------------------------*/

	/** 
	 * Shows the <i>XML</i> output in the console
	 */

	private void printXML(){
		try{
			XMLOutputter outFile = new XMLOutputter(Format.getPrettyFormat());
			outFile.output(document, System.out);
		}catch (java.io.IOException e){}
	}

	/**
	 *  Save the <i>XML</i> generated file
	 *  @param file : Name of GEDCOM file.
	 *  @return s : Name of the generated XML file.
	 */
	private String saveXML(String file){
		String s=null;
		try{
			XMLOutputter outFile = new XMLOutputter(Format.getPrettyFormat());
			s=file.substring(0, file.length()-3)+"xml";
			outFile.output(document, new FileOutputStream(s));


		}
		catch (java.io.IOException e){}
		return s;
	}

	/**
	 * Manage validation of the XML generated with a local DTD
	 * @param file : File name.
	 */
	private void validateWithDTD(String file){
		
		SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
		Document jdoc;

		try {
			jdoc = builder.build(new File(file));
			
			System.out.println("Succeeded!");
			System.out.println(jdoc.getBaseURI()+"............Generated");


		} catch (JDOMException e) {
			System.out.println("JDOM EXCEPTION : Check the DTD file");
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO EXCPETION : Can't create a new file");
			//e.printStackTrace();
		}

	}

	/**
	 *   Manage date of events. </br>
	 *   Generate parent <i>XML</i> element <i>date</i> and his children :</br>
	 *   <i>d</i> : for day </br> 
	 *   <i>m</i> : for mouth </br> 
	 *   <i>y</i> : for year
	 *  @param el : Parent element of date.
	 *  @param dateEvent  : References the event's date.
	 */
	private void setEventDate(Element el, StringWithCustomTags dateEvent){
		String dValue=null,mValue=null,yValue=null;
		try{
			date = new Element("date");
			el.addContent(date);
			String [] dateValue;
			/* Note : remove spaces at beginning and the end with trim() */
			dateValue=dateEvent.value.trim().split(" ");
			Element d = new Element("d");
			Element m = new Element("m");
			Element y = new Element("y");
			Attribute spec = new Attribute("spec", "Not exact");

			if(dateValue!=null){

				if(dateValue[0].matches("[0-9]{1,2}")){
					dValue= dateValue[0];
					date.addContent(d);
					d.setText(dValue);
					if (dateValue[1]!=null && dateValue[1].matches("[AAA-ZZZ]*")){
						mValue=dateValue[1];
						date.addContent(m);
						m.setText(mValue);
						if(dateValue[2]!=null && dateValue[2].matches("[0-9]{4}")){
							yValue= dateValue[2];
							date.addContent(y);
							y.setText(yValue);
						}
					}
				}
				else if (dateValue[0].matches("[AAA-ZZZ]*")){
					if(dateValue[0].matches("ABT") || dateValue[0].matches("BEF") || dateValue[0].matches("AFT") ){
						date.setAttribute(spec);
						/*TODO 
						 * in an ideal world still display date of the event
						 * */ 
					}
					else {
						mValue= dateValue[0];

						date.addContent(m);
						m.setText(mValue);
						if(dateValue[1].matches("[0-9]{4}")){
							yValue= dateValue[1];
							date.addContent(y);
							y.setText(yValue);
						}
					}
				}
				else if(dateValue[0].matches("[0-9]{4}")){
					yValue= dateValue[0];
					date.addContent(y);
					y.setText(yValue);
				}
			}
		}catch (ArrayIndexOutOfBoundsException e){
			//it could be that there are some tags that are not present in GEDCOM file
		}
	}

	/** 
	 *  Manage place of events. </br>
	 *  Generate XML element : <i>plac</i>  
	 *  @param el : Parent element of place.
	 *  @param eventPlace  : References the event's place.
	 */
	private void setEventPlace (Element el, Place eventPlace){
		try{
			plac = new Element("plac");
			el.addContent(plac);
			String placeValue;
			placeValue=eventPlace.placeName;

			if(placeValue!=null){
				plac.setText(placeValue);
			}
		}catch (Exception e){}
	}

	/**
	 * Manage the header of the document, give information about GEDCOM file in more details : <i>source, destination, version, name of the file...</i> </br>
	 * @param h : Header information about the GEDCOM file.
	 * @see #setEventDate(Element, StringWithCustomTags)
	 */
	private void setHeader(Header h){
		head = new Element("head");
		gedcom.addContent(head);
		if(h.sourceSystem.systemId!=null){
			sour= new Element("sour");
			head.addContent(sour);
			sour.setText(h.sourceSystem.systemId);
		}
		if(h.destinationSystem!=null){
			dest= new Element("dest");
			head.addContent(dest);
			dest.setText(h.destinationSystem.value);
		}
		if(h.date!=null){
			setEventDate(head, h.date);
		}
		if(h.fileName!=null){
			file= new Element("file");
			head.addContent(file);
			file.setText(h.fileName.value);
		}
		if(h.characterSet.characterSetName!=null){
			charr=new Element("char");
			head.addContent(charr);
			charr.setText(h.characterSet.characterSetName.value);
		}
	}

	/**
	 * Manage informations about submitter of the GEDCOM document : <i>name</i>,<i>adresse</i>,<i>emails</i>...  
	 * @param s : A submitter. Corresponds to the SUBMITTER_RECORD structure in the GEDCOM standard.
	 */
	private void setSumbitter(Submitter s){
		String rawXref;
		subm = new Element("subm");
		idSubm = new Attribute("id", "");
		gedcom.addContent(subm);
		rawXref=s.xref;
		//remove @
		if(rawXref!=null){
			idSubm.setValue(rawXref.substring(1, rawXref.length()-1));
			subm.setAttribute(idSubm);
		}
		if(s.name!=null){
			sname=new Element("sname");
			subm.addContent(sname);
			sname.setText(s.name.value);
		}
		if(s.address!=null){
			addr=new Element("addr");
			subm.addContent(addr);
			List<String> contTable=s.address.lines;
			//toString().split(", ");
			addr.setText(contTable.get(0));
			for(int i=1;i<contTable.size();i++){
				if(contTable.get(i)!=null){
					cont=new Element("cont");
					addr.addContent(cont);
					cont.setText(contTable.get(i));
				}
			}
		}
		if(s.phoneNumbers!=null){
			phon=new Element("phon");
			subm.addContent(phon);
			phon.setText(s.phoneNumbers.get(0).value);
		}
		//comments can't be parsed
	}

	/** 
	 *  Manage multimedia of individuals persons and families. </br>
	 *  Generate parent <i>XML</i> element <i>obje</i> and his children : </br> 
	 *   <i>file</i> : Location of the multimedia </br>
	 *   <i>form</i> : Format of the multimedia </br>
	 *   <i>titl</i> : Title of multimedia
	 *  @param el : Parent element of place.
	 *  @param multimedia : References the event's place.
	 */
	private void setMultimedia(Element el, Multimedia multimedia){
		obje= new Element("obje");
		el.addContent(obje);
		FileReference fileReferece;
		fileReferece=multimedia.fileReferences.get(0);
		/* (INDI || FAM) /OBJE/FILE */
		if(fileReferece.referenceToFile != null){
			file= new Element("file");
			obje.addContent(file);
			file.setText(fileReferece.referenceToFile.value);
		}
		/* (INDI || FAM)/OBJE/FORM */
		if(fileReferece.format != null){
			form = new Element("form");
			obje.addContent(form);
			form.setText(fileReferece.format.value);
		}
		if(fileReferece.title!= null){
			titl = new Element("titl");
			obje.addContent(titl);
			titl.setText(fileReferece.title.value);
		}
		/* (INDI || FAM)/OBJE/TITL */
		if(multimedia.embeddedTitle!= null){
			titl = new Element("titl");
			obje.addContent(titl);
			titl.setText(multimedia.embeddedTitle.value);
		}

	}

	/**
	 * Manage an individual person : <i>names, titles, sex, family's reference to which he belongs</i>
	 *     @param i : An individual person. Corresponds to the INDIVIDUAL_RECORD structure in the GEDCOM specification.
	 */
	private void setIndividuals(Individual i){
		String rawXref=null;
		indi = new Element("indi");
		idIndi = new Attribute("id","");
		gedcom.addContent(indi);
		indi.setAttribute(idIndi);
		rawXref=i.xref;
		idIndi.setValue(rawXref.substring(1, rawXref.length()-1));

		try {
			String names [];
			if(i.names.get(0)!=null){
				name = new Element("name");
				indi.addContent(name);
				names=i.names.get(0).toString().split("/");
				//ignore spaces
				if(!names[0].trim().isEmpty()){
					firstname=new Element("firstname");
					name.addContent(firstname);
					firstname.setText(names[0]);
				}
				//ignore spaces
				if(!names[1].trim().isEmpty()){
					lastname=new Element("lastname");
					name.addContent(lastname);
					lastname.setText(names[1]);
				}
			}

			/* INDI/TITL */

			if(!i.attributes.isEmpty()){
				String titleOfIndi =i.attributes.get(0).description.value;
				titl = new Element("titl");
				indi.addContent(titl);
				titl.setText(titleOfIndi);
			}

			/* INDI/SEX */
			if(i.sex!=null){

				sex= new Element("sex");
				indi.addContent(sex);
				type= new Attribute("type", "");
				sex.setAttribute(type);
				type.setValue(i.sex.value);
			}

			/* INDI/FAMS */
			if(!i.familiesWhereSpouse.isEmpty()){
				fams = new Element("fams");
				indi.addContent(fams);
				refFams= new Attribute("ref", "");
				fams.setAttribute(refFams);
				rawXref = i.familiesWhereSpouse.get(0).family.xref;
				//remove @
				refFams.setValue(rawXref.substring(1, rawXref.length()-1));
			}

			/* INDI/FAMC */
			if(!i.familiesWhereChild.isEmpty()){
				famc = new Element("famc");
				indi.addContent(famc);
				refFamc= new Attribute("ref", "");
				famc.setAttribute(refFamc);
				rawXref =i.familiesWhereChild.get(0).family.xref;
				//remove @
				refFamc.setValue(rawXref.substring(1, rawXref.length()-1));
			}

			/**---------CHECK FAMILIES EVENTS-----------**/
			/* INDI/OBJE */
			/**TODO
			 * BUG : manage form when it's child of file tag
			 * **/

			if(!i.multimedia.isEmpty()){
				multimedia=i.multimedia.get(0);

				/* INDI/OBJE/FILE */
				setMultimedia(indi, multimedia);

			}
			/**---------CHECK INDIVIDUES EVENTS-----------**/

			if(!i.events.isEmpty()){
				setIndivividualEvents(i);
			}
		}catch (IndexOutOfBoundsException ioobe){

		}catch (NullPointerException npe){

		}
	}

	/**
	 * Manage individuals persons of a family : <i>husband, wife and children</i>.</br>
	 * Generate individuals XML elements : </br>
	 * <i>fam</i>  : Family's tag to which the individual person belongs. Ans his attribute <i>id</i> </br>
	 * <i>husb</i> : Husband's tag with his attribute <i>ref</i> reference an existing individual person </br>
	 * <i>wife</i> : Wife's tag  with his attribute <i>ref</i> reference an existing individual person </br>
	 * <i>chil</i> : Child's tag with his attribute <i>ref</i> reference an existing individual person</br>
	 * @param f : A family record. Corresponds to FAM_RECORD in the GEDCOM spec.
	 * @see #setFamilyEvents(Family)
	 * @see #setMultimedia(Element, Multimedia)
	 */
	private void setFamilyIndividuals(Family f){
		String rawXref;
		try{
			/* FAM */

			fam = new Element("fam");
			idFam = new Attribute("id","");
			gedcom.addContent(fam);
			fam.setAttribute(idFam);
			rawXref=f.xref;
			idFam.setValue(rawXref.substring(1, rawXref.length()-1));

			if(f.husband!=null){
				husb = new Element("husb");
				fam.addContent(husb);
				refHusb = new Attribute("ref", "");
				husb.setAttribute(refHusb);
				rawXref=f.husband.xref;
				//remove @
				refHusb.setValue(rawXref.substring(1, rawXref.length()-1));
			}

			/* FAM/WIFE */
			if(f.wife!=null){
				wife = new Element("wife");
				fam.addContent(wife);
				refWife = new Attribute("ref", "");
				wife.setAttribute(refWife);
				rawXref=f.wife.xref;
				//remove @
				refWife.setValue(rawXref.substring(1, rawXref.length()-1));
			}

			/* FAM/CHIL */
			if(!f.children.isEmpty()){
				for(int j=0;j<f.children.size();j++){
					chil = new Element("chil");
					fam.addContent(chil);
					refChil = new Attribute("ref", "");
					chil.setAttribute(refChil);
					rawXref=f.children.get(j).xref;
					//remove @
					refChil.setValue(rawXref.substring(1, rawXref.length()-1));
				}
			}
			if(!f.events.isEmpty()){
				setFamilyEvents(f);
			}


			if(!f.multimedia.isEmpty()){
				multimedia=f.multimedia.get(0);
				setMultimedia(fam, multimedia);
			}

		}catch (ArrayIndexOutOfBoundsException aoobe){
			//aoobe.printStackTrace();
		}catch (NullPointerException npe){
			//npe.printStackTrace();
		}
	}


	/**
	 * Manage events of an individual person : <i>Birthday, Death, Burial, Christening</i> .</br>
	 * Generate individuals XML elements : </br>
	 * <i>birt</i> : Birthday's tag </br>
	 * <i>deat</i> : Death's tag </br>
	 * <i>buri</i> : Burial's tag </br>
	 * <i>chr</i>  : Christening's tag </br>
	 * @param i : An individual person. Corresponds to the INDIVIDUAL_RECORD structure in the GEDCOM specification.
	 * @see #setEventDate(Element, StringWithCustomTags)
	 * @see #setEventPlace(Element, Place)
	 */
	private void setIndivividualEvents(Individual i){

		for(int j=0;j<i.events.size();j++){
			eventType=i.events.get(j).type.display;
			eventPlace=i.events.get(j).place;
			dateEvent=i.events.get(j).date;
			/* BIRTH */
			if(eventType.contains("Birth")){
				birt = new Element("birt");
				indi.addContent(birt);
				/* INDI/BIRT/DATE*/
				if(dateEvent!=null){
					setEventDate(birt, dateEvent);
				}
				/* INDI/BIRT/PLAC */
				if(eventPlace!=null){
					setEventPlace(birt, eventPlace);
				}
			}
			/* DEATH */
			else if(eventType.contains("Death")){
				deat = new Element("deat");
				indi.addContent(deat);

				/* INDI/DEAT/DATE*/
				if(dateEvent!=null){
					setEventDate(deat, dateEvent);
				}

				/* INDI/DEAT/PLAC */
				if(eventPlace!=null){
					setEventPlace(deat, eventPlace);
				}
			}
			/* BURIAL */
			else if(eventType.contains("Burial")){
				buri = new Element("buri");
				indi.addContent(buri);

				/* INDI/BURI/DATE*/
				if(dateEvent!=null){
					setEventDate(buri, dateEvent);
				}

				/* INDI/DEAT/PLAC */
				if(eventPlace!=null){
					setEventPlace(buri, eventPlace);
				}
			}
			/* CHRISTENING */
			else if(eventType.contains("Christening")){
				chr = new Element("chr");
				indi.addContent(chr);

				/* INDI/CHR/DATE*/
				if(dateEvent!=null){
					setEventDate(chr, dateEvent);
				}

				/* INDI/CHR/PLAC */
				if(eventPlace!=null){
					setEventPlace(chr, eventPlace);
				}
			}
		}
	}

	/**
	 * Manage family's events : <i>Marriage</i> and <i>Divorce</i>.</br>
	 * Generate family's events XML elements : </br>
	 * <i>marr</i> : Marriage </br>
	 * <i>div</i>  : Divorce and his attribute <i>isDiv(Y or N)</i> 
	 * @see #setEventDate(Element, StringWithCustomTags)
	 * @see #setEventPlace(Element, Place)
	 */
	private void setFamilyEvents(Family f){
		for(int j=0;j<f.events.size();j++){
			eventType=f.events.get(j).type.display;
			dateEvent=f.events.get(j).date;
			eventPlace=f.events.get(j).place;

			/* MARRIAGE */
			if(eventType.contains("Marriage")){
				marr = new Element("marr");
				fam.addContent(marr);

				/* FAM/MARR/DATE*/
				if(dateEvent!=null){
					setEventDate(marr, dateEvent);
				}

				/* FAM/MARR/PLAC */
				if(eventPlace!=null){
					setEventPlace(marr, eventPlace);
				}
			}

			/* DIVORCE */
			else if (eventType.contains("Divorce")){
				div= new Element("div");
				fam.addContent(div);

				/* DIV N */
				isDiv = new Attribute("isdiv", "N");
				div.setAttribute(isDiv);

				/* DIV Y */

				if(f.events.get(j).yNull.equals("Y")){
					isDiv.setValue("Y");

					/* DIV/DATE */
					dateEvent=f.events.get(j).date;
					if(dateEvent!=null){
						setEventDate(div, dateEvent);
					}

					/* DIV/PLAC */
					eventPlace=f.events.get(j).place;
					if(eventPlace!=null){
						isDiv.setValue("Y");
						setEventPlace(div, eventPlace);
					}
				}
			}
		}
	}
}
