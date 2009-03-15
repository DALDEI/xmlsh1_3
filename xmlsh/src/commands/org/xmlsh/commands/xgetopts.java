/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.commands;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.saxon.s9api.XdmNode;
import org.xmlsh.core.Options;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.core.Options.OptionValue;
import org.xmlsh.util.StAXUtils;

public class xgetopts extends XCommand {

	private static final String kOPTION 	= "option";
	private static final String kOPTIONS 	= "options";
	private static final String kROOT 		= "xgetopts";
	private static final String kARG 		= "arg";
	private static final String kARGS 		= "args";
	private static final String kVALUE 	= "value";
	@Override
	public int run(List<XValue> args) throws Exception {
		if( args.isEmpty() ){
			usage();
			return 1;
		}
		
		XMLStreamWriter out = this.getStdout().asXMLStreamWriter(getSerializeOpts());
		
			
		String def =  args.remove(0).toString();
		Options opts = new Options(def,args);
		
		List<OptionValue>  options = opts.parse();
		args = opts.getRemainingArgs();
		
		out.writeStartDocument();
		out.writeStartElement(kROOT);

		out.writeStartElement(kOPTIONS);

		
		for( OptionValue option : options ){
			out.writeStartElement(kOPTION);
			out.writeAttribute("name",option.getOptionDef().name);
			
			if( option.getOptionDef().hasArgs  ){
				for( XValue value : option.getValues() ) {
					out.writeStartElement( kVALUE );
					
					
					if( value.isAtomic())
						out.writeCharacters(value.toString());
					else
						write( out , value.asXdmNode() );
					out.writeEndElement();
					

				}
			}
			out.writeEndElement();
			
		}
		out.writeEndElement();
		out.writeStartElement( kARGS );

		
		for( XValue value : opts.getRemainingArgs() ){
			out.writeStartElement(kARG);
				

			if( value.isAtomic())
				out.writeCharacters( value.toString());
			else
				write( out , value.asXdmNode() );
			
			out.writeEndElement();
		}
		
		out.writeEndDocument();
		out.close();
		
		
		return 0;
	}

	private void write(XMLStreamWriter out, XdmNode node) throws XMLStreamException {
		
		StAXUtils.copy( node.getUnderlyingNode() , out );
		// XMLStreamUtils.copy( node.asSource() , out );

		
	}

	private void usage() {
		this.getEnv().printErr("usage: xargs \"option def\" $*");
		
	}

}



//
//
//Copyright (C) 2008, David A. Lee.
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
