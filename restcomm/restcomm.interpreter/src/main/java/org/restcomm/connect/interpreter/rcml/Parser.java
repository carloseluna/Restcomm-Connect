/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */
package org.restcomm.connect.interpreter.rcml;

import akka.actor.ActorRef;
import org.apache.log4j.Logger;
import org.restcomm.connect.commons.faulttolerance.RestcommUntypedActor;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher; //added
import java.util.regex.Pattern; //added
 

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
public final class Parser extends RestcommUntypedActor {
    private static Logger logger = Logger.getLogger(Parser.class);
    private Tag document;
    private Iterator<Tag> iterator;
    private String xml;
    private ActorRef sender;

    private Tag current;

    public Parser(final InputStream input, final String xml, final ActorRef sender) throws IOException {
        this(new InputStreamReader(input), xml, sender);
    }

    public Parser(final Reader reader, final String xml, final ActorRef sender) throws IOException {
        super();
        if(logger.isDebugEnabled()){
            logger.debug("About to create new Parser for xml: "+xml);
        }
        this.xml = xml;
        this.sender = sender;
        final XMLInputFactory inputs = XMLInputFactory.newInstance();
        inputs.setProperty("javax.xml.stream.isCoalescing", true);
        XMLStreamReader stream = null;
        try {
            stream = inputs.createXMLStreamReader(reader);
            document = parse(stream);
            if (document == null) {
                throw new IOException("There was an error parsing the RCML.");
            }
            iterator = document.iterator();
        } catch (final XMLStreamException exception) {
            if(logger.isInfoEnabled()) {
                logger.info("There was an error parsing the RCML for xml: "+xml+" excpetion: ", exception);
            }
            sender.tell(new ParserFailed(exception,xml), null);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (final XMLStreamException nested) {
                    throw new IOException(nested);
                }
            }
        }
    }

    public Parser(final String xml, final ActorRef sender) throws IOException {
        this(new StringReader(xml.trim().replaceAll("&([^;]+(?!(?:\\w|;)))", "&amp;$1")), xml, sender);
    }

    private void end(final Stack<Tag.Builder> builders, final XMLStreamReader stream) {
        if (builders.size() > 1) {
            final Tag.Builder builder = builders.pop();
            final Tag tag = builder.build();
            builders.peek().addChild(tag);
        }
    }

    private void start(final Stack<Tag.Builder> builders, final XMLStreamReader stream) {
        final Tag.Builder builder = Tag.builder();
        // Read the next tag.
        builder.setName(stream.getLocalName());
        // Read the attributes.
        final int limit = stream.getAttributeCount();
        for (int index = 0; index < limit; index++) {
            final String name = stream.getAttributeLocalName(index);
            final String value = stream.getAttributeValue(index).trim();
            final Attribute attribute = new Attribute(name, value);
            builder.addAttribute(attribute);
        }
        builders.push(builder);
    }

    private Tag next() {
    	boolean shouldContinue; // new variable

        if (iterator != null) {
            while (iterator.hasNext()) {
                final Tag tag = iterator.next();
                if (Verbs.isVerb(tag)) {
                          
                	shouldContinue=validation(tag); // add new validation, invoke to class
               		
                    if (current != null && current.hasChildren() && shouldContinue) {
                        final List<Tag> children = current.children();
                        if (children.contains(tag)) {
                            continue;
                        }
                    }
                    current = tag;
                    return current;
                }
            }
        } else {
            if(logger.isInfoEnabled()){
                logger.info("iterator is null");
            }
        }
        return null;
    }
///////////////////////////////////////////// new class -- validate
    private boolean validation(Tag tag) {
    	boolean shouldContinue=true;
    	List<Attribute> mapKeys;

    	
       	if (tag.hasAttributes()) {
    		
    		mapKeys=tag.attributes();
    		
    		if (tag.name().equals("Record")) {
    			for (Attribute e : mapKeys) {
    				 String eName=e.name();
    				 double eValue=Double.parseDouble(e.value());
    				
    				 Pattern pattern = Pattern.compile("^([0-9]{1,3})$");
    				 Matcher mather = pattern.matcher(e.value());
    				 
    				 if(eName.equals("maxLength") && (mather.find() == true) ) {
    					 if ( (eValue<0) ||(eValue>100) ) {
    						 // this set maxLength between 0 and 100 sec
    						 //if is set one of those values the process will not go
    						shouldContinue=false;
    					 }
    				  }
    				 
    				 if(eName.equals("timeout") && (mather.find() == true) ) {
    					 if ( (eValue<0) ||(eValue>5) ) {
    						 // this set timeout in f
    						
    						shouldContinue=false;
    					 }
    				  }
    				 
    				 Pattern pattern_url = Pattern.compile("^((http://|https://|/))?(([\\\\w!~*'().&=+$%-]+: )?[\\\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\\\.){3}[0-9]{1,3}|([\\\\w!~*'()-]+\\\\.)*([\\\\w^-][\\\\w-]{0,61})?[\\\\w]\\\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\\\w!~*'().;?:@&=+$,%#-]+)+/*)$");
      				//this admit: http https and / (local directory)	
      				 Matcher mather_url = pattern_url.matcher(e.value());
      				if(eName.equals("action") || eName.equals("transcribeCallback") ) {
   					 if (mather_url.find() == false) {
   						shouldContinue=false;
   					 }
   				  }
    				
      			 Pattern patternFinishOnKey = Pattern.compile("^\\+|([0-9]{1})|\\#|-1$");
   				 Matcher matherFinishOnKey = patternFinishOnKey.matcher(e.value());
   				 
   				 if(eName.equals("finishOnKey") && (matherFinishOnKey.find() == true) ) {
					
						shouldContinue=false;
					
				  }
    				 
    			}	
    		}
    		
    		
    		if (tag.name().equals("Dial")) {
    			for (Attribute e : mapKeys) {
    				 String eName=e.name();
    				 double eValue=Double.parseDouble(e.value());
    				
    				 Pattern patterntimeLimit = Pattern.compile("^([0-9]{1,5})$");
    				 Matcher mathertimeLimit = patterntimeLimit.matcher(e.value());
    				 
    				 if(eName.equals("timeLimit") && (mathertimeLimit.find() == true) ) {
    					 if ( (eValue<0) ||(eValue>14400) ) {
    						 // this set maxLength between 0 and 14400  sec
    						 //if is set one of those values the process will not go
    						shouldContinue=false;
    					 }
    				  }
    				 
    				 Pattern patterntimeout = Pattern.compile("^([0-9]{1,2})$");
    				 Matcher mathertimeout = patterntimeout.matcher(e.value());
    				 
    				 if(eName.equals("timeout") && (mathertimeout.find() == true) ) {
    					 if ( (eValue<0) ||(eValue>30) ) {
    						 // this set timeout in f
    						
    						shouldContinue=false;
    					 }
    				  }
    				 
    				 Pattern pattern_url = Pattern.compile("^((http://|https://|/))?(([\\\\w!~*'().&=+$%-]+: )?[\\\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\\\.){3}[0-9]{1,3}|([\\\\w!~*'()-]+\\\\.)*([\\\\w^-][\\\\w-]{0,61})?[\\\\w]\\\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\\\w!~*'().;?:@&=+$,%#-]+)+/*)$");
      				//this admit: http https and / (local directory)	
      				 Matcher mather_url = pattern_url.matcher(e.value());
      				if(eName.equals("action")) {
   					 if (mather_url.find() == false) {
   						shouldContinue=false;
   					 }
   				  }
    				
      		
    				 
    			}	
    		}
    		
    		
    		if (tag.name().equals("Email")) {
    			//pattern to validate
    			Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    			for (Attribute e : mapKeys) {
    				 String eName=e.name();
    				 String eValue=e.value();
    				 
    				
    				 
    				 if (eName.equals("from") || eName.equals("to") || eName.equals("cc") || eName.equals("bcc")) {
    				  
    			        Matcher mather = pattern.matcher(eValue);
    			 
    			        if (mather.find() == false) {
    			        		shouldContinue=false;
    			        } 
    				 
    				 }
    			}	
    		}
    		
    		if (tag.name().equals("Pause")) {
    			for (Attribute e : mapKeys) {
    				 String eName=e.name();
    				 double eValue=Double.parseDouble(e.value());
    				
    				 Pattern pattern = Pattern.compile("^([0-9]{1,3})$");
    				 Matcher mather = pattern.matcher(e.value());
    				 
    				 if(eName.equals("length") && (mather.find() == true) ) {
    					 if ( (eValue<0) ||(eValue>3600) ) {
    						 // this set maxLength between 0 and 3600 sec
    						 //if is set tour of those values the process will not go
    						shouldContinue=false;
    					 }
    				  }
    				 
    			}	
    		}
    		
    		if (tag.name().equals("Sms") || tag.name().equals("Fax") ) {
    			//as far I can see the sms and fax has the same variables
    			for (Attribute e : mapKeys) {
    				 String eName=e.name();
    				 String eValue=e.value();
    				 Pattern pattern = Pattern.compile("^((http://|https://|/))?(([\\\\w!~*'().&=+$%-]+: )?[\\\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\\\.){3}[0-9]{1,3}|([\\\\w!~*'()-]+\\\\.)*([\\\\w^-][\\\\w-]{0,61})?[\\\\w]\\\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\\\w!~*'().;?:@&=+$,%#-]+)+/*)$");
    		    	//this admit: http https and / (local directory)	
    				 Matcher mather = pattern.matcher(eValue);
    				 /// note the attributes to and from was not validated because can admit numbers or characters
    				
    				 Pattern patternPhone = Pattern.compile("^\\+?([0-9]{1,14})$");
    				 Matcher matherPhone = patternPhone.matcher(eValue);
    				 
    				 if(eName.equals("statusCallback") ||eName.equals("action") ) {
    					 if (mather.find() == false) {
    						shouldContinue=false;
    					 }
    				  }
    				 
    				 if(eName.equals("to") ||eName.equals("from") ) {
    					 if (matherPhone.find() == false) {
    						shouldContinue=false;
    					 }
    				  }
    				 
    			}	
    		}
    		
    		
    		if (tag.name().equals("Gather")) {
    			for (Attribute e : mapKeys) {
    				 String eName=e.name();
    				 double eValue=Double.parseDouble(e.value());
    				
    				 Pattern pattern = Pattern.compile("^([0-9]{1,3})$");
    				 Matcher mather = pattern.matcher(e.value());
    				 
    				 Pattern pattern_url = Pattern.compile("^((http://|https://|/))?(([\\\\w!~*'().&=+$%-]+: )?[\\\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\\\.){3}[0-9]{1,3}|([\\\\w!~*'()-]+\\\\.)*([\\\\w^-][\\\\w-]{0,61})?[\\\\w]\\\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\\\w!~*'().;?:@&=+$,%#-]+)+/*)$");
     				//this admit: http https and / (local directory)	
     				 Matcher mather_url = pattern_url.matcher(e.value());
     				 
    				 if( eName.equals("numDigits") && (mather.find() == true) ) {
    					 if ( (eValue<0) ||(eValue>3) ) {
    						 // this allows only 3 digits
    						shouldContinue=false;
    					 }
    				  }
    				 
    				 if( eName.equals("timeout") && (mather.find() == true) ) {
    					 if ( (eValue<0) ||(eValue>20) ) {
    						 // this set a timeout of 20 sec
    						shouldContinue=false;
    					 }
    				  }
    				 
    				 if(eName.equals("action") || eName.equals("partialResultCallback") ) {
    					 if (mather_url.find() == false) {
    						shouldContinue=false;
    					 }
    				  }
    				 
    				 
    			}	
    		}
    		
    		if ((tag.name().equals("Play"))||(tag.name().equals("Say"))) {
    			for (Attribute e : mapKeys) {
    				 String eName=e.name();
    				 double eValue=Double.parseDouble(e.value());
 
    				 Pattern pattern_repeat = Pattern.compile("^([0-9]{1,3})$");
    				 Matcher mather_repeat = pattern_repeat.matcher(e.value());
    				 
    				 if( eName.equals("loop") && (mather_repeat.find() == true) ) {
    					 if ( (eValue<0) ||(eValue>3) ) {
    						 // this set a repeat 3 times
    						shouldContinue=false;
    					 }
    				  }
    			}	
    		}
    		
    		
    		
    } //if tag has no attributtes then continue		
    
       	return shouldContinue;
    	
    }
///////////////// end new class    
    private Tag parse(final XMLStreamReader stream) throws IOException, XMLStreamException {
        final Stack<Tag.Builder> builders = new Stack<Tag.Builder>();
        while (stream.hasNext()) {
            switch (stream.next()) {
                case START_ELEMENT: {
                    start(builders, stream);
                    continue;
                }
                case CHARACTERS: {
                    text(builders, stream);
                    continue;
                }
                case END_ELEMENT: {
                    end(builders, stream);
                    continue;
                }
                case END_DOCUMENT: {
                    if (!builders.isEmpty()) {
                        return builders.pop().build();
                    }
                }
            }
        }
        return null;
    }

   
    
    @Override
    public void onReceive(final Object message) throws Exception {
        final Class<?> klass = message.getClass();
        final ActorRef self = self();
        final ActorRef sender = sender();
        if (GetNextVerb.class.equals(klass)) {
            final Tag verb = next();
            if (verb != null) {
                sender.tell(verb, self);
                if(logger.isDebugEnabled()){
                    logger.debug("Parser, next verb: "+verb.toString());
                }
            } else {
                final End end = new End();
                sender.tell(end, sender);
                if(logger.isDebugEnabled()) {
                    logger.debug("Parser, next verb: "+end.toString());
                }
            }
        }
    }

    private void text(final Stack<Tag.Builder> builders, final XMLStreamReader stream) {
        if (!stream.isWhiteSpace()) {
            // Read the text.
            final Tag.Builder builder = builders.peek();
            final String text = stream.getText().trim();
            builder.setText(text);
        }
    }
}
