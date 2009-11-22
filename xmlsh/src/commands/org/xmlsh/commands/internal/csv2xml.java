/**
 * $Id: xpwd.java 21 2008-07-04 08:33:47Z daldei $
 * $Date: 2008-07-04 04:33:47 -0400 (Fri, 04 Jul 2008) $
 *
 */

package org.xmlsh.commands.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xmlsh.commands.util.CSVParser;
import org.xmlsh.commands.util.CSVRecord;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.util.Util;

/*
 * 
 * Convert CSV files to an XML file
 * Arguments
 * 	-root		root element (default "root")
 *  -row		row	 element (default "row")
 *  -col		col	 element (defauilt "col")
 *  -cols		<seq>   Column names instead of reading from header
 *  -header		read first row for header names
 *  -attr		write in attribute normal format\
 *  -encoding encoding  Read CSV format in the specified encoding, else cp1252 assumed
 *  -quote		quoted by (default ")
*/

public class csv2xml extends XCommand
{

	
	public int run(  List<XValue> args )	throws Exception
	{

		

		Options opts = new Options( "root:,row:,col:,header,attr,encoding:,delim:,quote:,colnames:,tab" , args );
		opts.parse();
		
		// root node
		String root = opts.getOptString("root", "root");
		String row = opts.getOptString("row", "row");
		String col = opts.getOptString("col", "col");
		String delim = opts.getOptString("delim", ",");
		String quote = opts.getOptString("quote", "\"");
		String encoding = opts.getOptString("encoding", "Cp1252");
		boolean bHeader = opts.hasOpt("header");
		boolean bAttr = opts.hasOpt("attr");
		// -tab overrides -delim
		if( opts.hasOpt("tab"))
			delim = "\t";
		
		
		List<XValue> xvargs = opts.getRemainingArgs();
		
		
		
// Output XML

		OutputPort stdout = getStdout();
		XMLStreamWriter writer = stdout.asXMLStreamWriter(getSerializeOpts());
		
		writer.writeStartDocument();
		
		
		writer.writeStartElement(root);
		
// Input is stdin and/or list of commands
		
		InputStream in = null;
		if( xvargs.size() == 0 )
			in = getStdin().asInputStream(getSerializeOpts());
		else
			in = getInputStream( xvargs.get(0) );
		
		
		Reader ir = new InputStreamReader( in , encoding );
		CSVParser parser = new CSVParser( delim.charAt(0), quote.charAt(0) );
		
		CSVRecord header = null ;
		if( bHeader ){
			String line = readLine(ir);
			if( line != null )
				header = parser.parseLine(line);
		} else 
		if( opts.hasOpt("colnames")){
			header = parseCols( opts.getOptValue("colnames"));
			
			
		}
		
		String line;
		while( (line = readLine(ir)) != null ){
			CSVRecord csv = parser.parseLine(line);
			addElement( writer , csv , row , col , bAttr , header );
		}
		writer.writeEndElement();
		writer.writeEndDocument();
		
		ir.close();
		stdout.writeSequenceTerminator();
		
		
		return 0;
		
	}



	private CSVRecord parseCols(XValue cols) {
		
		if( cols.isAtomic() )
			return new CSVRecord( Arrays.asList( cols.toString().split(",")));
		else
			return new CSVRecord(cols.asStringList());
		
		
	}




	private void addElement(
		XMLStreamWriter writer, 
		CSVRecord csv ,
		String row, 
		String col, 
		boolean battr,
		CSVRecord header) throws  XMLStreamException 
	{
		
		writer.writeStartElement(row);
		// Attribute normal format
		if( battr ){
			for( int i = 0 ; i < csv.getNumFields() ; i++ ){
				String name = getAttrName( i , col , header );
				writer.writeAttribute(name,csv.getField(i));
			}
			
			
		} else {


			for( int i = 0 ; i < csv.getNumFields() ; i++ ){
				String name = getColName( i , col , header );
				writer.writeStartElement(name);
				writer.writeCharacters(csv.getField(i));
				writer.writeEndElement();
				
			}
			
		}
		writer.writeEndElement();
		
	}


	private String getColName(int i, String col, CSVRecord header) {
		if( header != null && header.getNumFields() > i )
			return toXmlName( header.getField(i));
		else
			return col ;
	}



	// Get an attribute name 
	private String getAttrName(int i, String col, CSVRecord header) {
		if( header != null && header.getNumFields() > i )
			return toXmlName( header.getField(i));
		else
			return col + (i + 1)  ;
		
	}



	private String toXmlName(String field) {
		return field.replaceAll("[^a-zA-Z0-9_]","-");
	}



	private String readLine(Reader ir) throws IOException {
		return Util.readLine(ir);
	}
	
}

//
//
//Copyright (C) 2008,2009 , David A. Lee.
//
//The contents of this file are subject to the "Simplified BSD License" (the "License");
//you may not use this file except in compliance with the License. You may obtain a copy of the
//License at http://www.opensource.org/licenses/bsd-license.php 
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied.
//See the License for the specific language governing rights and limitations under the License.
//
//The Original Code is: all this file.
//
//The Initial Developer of the Original Code is David A. Lee
//
//Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
//Contributor(s): none.
//
