package org.restcomm.connect.interpreter.rcml;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public interface AttributeValidator {
		
	public boolean Validators (Tag tag);
}

 class RecordValidator implements AttributeValidator{
	   Map <String, String> MyValidators = new TreeMap<String, String>();  
	   private static Logger logger = Logger.getLogger(Parser.class);
	   
	  public RecordValidator() {
		  MyValidators.put("maxLength","100");
		  MyValidators.put("maxLengthParse","^([0-9]{1,3})$");
		  MyValidators.put("timeout","5");
		  MyValidators.put("timeoutParse","^([0-9]{1,3})$");
		  MyValidators.put("action","action");
		  MyValidators.put("transcribeCallback","transcribeCallback");
		  MyValidators.put("urlParse", "^(((http://|https://|/))?(([\\w!~*'().&=+$%-]+: )?[\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([\\w!~*'()-]+\\.)*([\\w^-][\\w-]{0,61})?[\\w]\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\w!~*'().;?:@&=+$,%#-]+)+/*))|$");
		 
		  MyValidators.put("FinishOnKey","FinishOnKey"); 
		  MyValidators.put("FinishOnKeyParse","^\\+|([0-9]{1})|\\#|-1$");
		  

		  
	  }		  
		    
	public boolean Validators (Tag tag) {
		List<Attribute> mapKeys;
		mapKeys=tag.attributes();
		boolean FinalValidationState=true; 
		//if after run the validations is still true, it's ok. if Not, means there is something wrong with the input
		
		for (Attribute e : mapKeys) { ///Repeat for all attributes
			
			switch (e.name()) {
			 case "maxLength":
				 
				 /////////////getting from the validator map //////////
				 double MaxLeghtValue=Double.parseDouble(MyValidators.get("maxLength")); 	 
				 
				 ////////////////getting from tag value /////////////
				 double eValue=Double.parseDouble(e.value()); 
				 Pattern pattern = Pattern.compile(MyValidators.get("maxLengthParse"));
				 Matcher mather = pattern.matcher(e.value()); 
				 
				 if (mather.matches() == true) {
					 if ( (eValue<0) ||(eValue>MaxLeghtValue) ) {
						 // this set maxLength between 0 and 100 sec
						 //if is set one of those values the process will show errors
						  FinalValidationState=false;
						 if(logger.isInfoEnabled()){
							
				                logger.info("The Value is exceeding the maximum length allowed on Record");
				            }
					 } 
				 }else {
					 if(logger.isInfoEnabled()){
						 FinalValidationState=false;
			                logger.info("The Value is too high or is not a numeric Value on Record");
			            }
			 	}
				 
				 break;
			 case "timeout":
				 
				 /////////////getting from the validator map //////////
				 double timeOutValue=Double.parseDouble(MyValidators.get("timeout")); 	 
				 
				 ////////////////getting from tag value /////////////
				 double timeOut=Double.parseDouble(e.value()); 
				 Pattern patternTimeOut = Pattern.compile(MyValidators.get("timeoutParse"));
				 Matcher matherTimeOut = patternTimeOut.matcher(e.value()); 
				 
				 if (matherTimeOut.matches() == true) {
					 if ( (timeOut<0) ||(timeOut>timeOutValue) ) { // this set time out according the map
						  FinalValidationState=false;
					 	 if(logger.isInfoEnabled()){
							
				                logger.info("The Value is exceeding the timeout allowed on Record");
				            }
					 }
				 }else {
					 FinalValidationState=false;
					 if(logger.isInfoEnabled()){
						 
			                logger.info("The Value is too high or is not a numeric Value on Record");
			            }
			 	}
				 
				 break;
			 case "action":
		/////////////getting from the validator map //////////
				// System.out.println(MyValidators.get("urlParse"));
				 //System.out.println(e.value());
				 
				 Pattern pattern_url = Pattern.compile(MyValidators.get("urlParse"));
	////////////////getting from tag value /////////////
				 Matcher mather_url = pattern_url.matcher(e.value());
				
				 if (mather_url.matches() == false) {
					 FinalValidationState=false;
				 	 if(logger.isInfoEnabled()){
				 //		 System.out.println("NOTOK");
			                logger.info("The url in action on Record is not the right format");
			            }
					 }
				 	 
				 
				 break;
			 case "transcribeCallback":
	/////////////getting from the validator map //////////
				 
				 Pattern pattern_url_transcribeCallback = Pattern.compile(MyValidators.get("urlParse"));
	////////////////getting from tag value /////////////
				 Matcher mather_url_transcribeCallback = pattern_url_transcribeCallback.matcher(e.value());
		 
				 if (mather_url_transcribeCallback.matches() == false) {
					 FinalValidationState=false;
				 	 if(logger.isInfoEnabled()){
			                logger.info("The url in transcribeCallback on Record is not the right format");
			            }
					 }
					 
				 break;
			 case "FinishOnKey":
				 
				 Pattern patternFinishOnKey = Pattern.compile(MyValidators.get("FinishOnKeyParse"));
				 Matcher matherFinishOnKey = patternFinishOnKey.matcher(e.value());
				 if (matherFinishOnKey.matches() == true) {
						
					 FinalValidationState=false;
				 	 if(logger.isInfoEnabled()){
			                logger.info("The entry in FinishOnKey on Record is not valid");
			            }
					 
					
				  }
				 break;
			
			 
			 }
		}	 
		

		return FinalValidationState;
	}
}

class DialValidator implements AttributeValidator {
	
	 Map <String, String> MyValidators = new TreeMap<String, String>();  
	   private static Logger logger = Logger.getLogger(Parser.class);
	   
	  public DialValidator() {
		  MyValidators.put("timeLimit","14400");
		  MyValidators.put("timeLimitParse","^([0-9]{1,5})$");
		  MyValidators.put("timeout","5");
		  MyValidators.put("timeoutParse","^([0-9]{1,2})$");
		  MyValidators.put("action","action"); 
		  MyValidators.put("urlParse", "^(((http://|https://|/))?(([\\w!~*'().&=+$%-]+: )?[\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([\\w!~*'()-]+\\.)*([\\w^-][\\w-]{0,61})?[\\w]\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\w!~*'().;?:@&=+$,%#-]+)+/*))|$");

	  }		  
	
	
	public boolean Validators (Tag tag) {
		List<Attribute> mapKeys;
		mapKeys=tag.attributes();
		boolean FinalValidationState=true; 
		
		//if after run the validations is still true, it's ok. if Not, means there is something wrong with the input
		
		for (Attribute e : mapKeys) { ///Repeat for all mapkeys on attributte
			
			switch (e.name()) {
			case "timeLimit":
				/////////////getting from the validator map //////////
						 
						 Pattern pattern_timeLimit = Pattern.compile(MyValidators.get("timeLimitParse"));
						 double eValue=Double.parseDouble(e.value());
			////////////////getting from tag value /////////////
						 Matcher mather_timeLimit = pattern_timeLimit.matcher(e.value());
				 
						 if (mather_timeLimit.matches() == false) {
							 FinalValidationState=false;
						 	 if(logger.isInfoEnabled()){
					                logger.info("The time limit on Dial is too high or is not a numeric value");
					            }
						}else {
							if ( (eValue<0) ||(eValue>Double.parseDouble(MyValidators.get("timeLimit")))) {
	    						 
								FinalValidationState=false;
								if(logger.isInfoEnabled()){
					                logger.info("The time limit on Dial is too high");
					            }
								
	    					 }	 
						}
							 
						 
			break;
			case "timeout":
				 
				 /////////////getting from the validator map //////////
				 double timeOutValue=Double.parseDouble(MyValidators.get("timeout")); 	 
				 
				 ////////////////getting from tag value /////////////
				 double timeOut=Double.parseDouble(e.value()); 
				 Pattern patternTimeOut = Pattern.compile(MyValidators.get("timeoutParse"));
				 Matcher matherTimeOut = patternTimeOut.matcher(e.value()); 
				 
				 if (matherTimeOut.matches() == true) {
					 if ( (timeOut<0) ||(timeOut>timeOutValue) ) { // this set time out according the map
						  FinalValidationState=false;
					 	 if(logger.isInfoEnabled()){
							
				                logger.info("The Value is exceeding the timeout allowed on Dial");
				            }
					 } 
				 }else {
					 FinalValidationState=false;
					 if(logger.isInfoEnabled()){
						 
			                logger.info("The Value is too high or is not a numeric Value on Dial");
			            }
			 	}
				 
				 break;
			
			case "action":
				/////////////getting from the validator map //////////
						 
						 Pattern pattern_url = Pattern.compile(MyValidators.get("urlParse"));
			////////////////getting from tag value /////////////
						 Matcher mather_url = pattern_url.matcher(e.value());
				 
						 if (mather_url.matches() == false) {
							 FinalValidationState=false;
						 	 if(logger.isInfoEnabled()){
					                logger.info("The url in action on Dial is not the right format");
					            }
							 }
							 
						 
			break;
			}
			
		}
		
		return FinalValidationState;
	}
}

class EmailValidator implements AttributeValidator {
	 Map <String, String> MyValidators = new TreeMap<String, String>();  
	   private static Logger logger = Logger.getLogger(Parser.class);
	   
	   public EmailValidator() {
			  MyValidators.put("EmailParse","^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

		  }		
	   
	public boolean Validators (Tag tag) {
		List<Attribute> mapKeys;
		mapKeys=tag.attributes();
		boolean FinalValidationState=true;
		
		Pattern pattern = Pattern.compile((MyValidators.get("EmailParse")));
		for (Attribute e : mapKeys) {
			 String eName=e.name();
			 
			 if (eName.equals("from") || eName.equals("to") || eName.equals("cc") || eName.equals("bcc")) {
				 String eValue=e.value();
				 Matcher mather = pattern.matcher(eValue);
				 
		        if (mather.matches() == false) {
		        	FinalValidationState=false;
		        	if(logger.isInfoEnabled()){
		                logger.info("The field "+eName+" on Email is incorrect");
		            }
		        } 
			 
			 }
		}	
		
		return FinalValidationState;	
	}
	
}

class PauseValidator implements AttributeValidator {
	 Map <String, String> MyValidators = new TreeMap<String, String>();  
	   private static Logger logger = Logger.getLogger(Parser.class);
	   
	   public PauseValidator() {
		      MyValidators.put("length","3600");
			  MyValidators.put("lengthParse","^([0-9]{1,4})$");

		  }	
	   
	public boolean Validators (Tag tag) {
		List<Attribute> mapKeys;
		mapKeys=tag.attributes();
		boolean FinalValidationState=true;
		
		Pattern pattern_pause = Pattern.compile((MyValidators.get("lengthParse")));

		for (Attribute e : mapKeys) {
			String eName=e.name();
			if (eName.equals("length")){
			//
				Matcher mather_pause = pattern_pause.matcher(e.value());
				
				if( (mather_pause.matches() == true) ) {
					double eValue=Double.parseDouble(e.value());
					if ( (eValue<0) ||(eValue>Double.parseDouble(MyValidators.get("length")))) {
						
						FinalValidationState=false;
						if(logger.isInfoEnabled()){
			                logger.info("The length value on Pause is too high, it must to be a value below to "+MyValidators.get("length"));
			            }
					} 
				} else {
					FinalValidationState=false;
		        	if(logger.isInfoEnabled()){
		                logger.info("The length value on Pause is incorrect, it must to be a numeric value below to "+MyValidators.get("length"));
		            }
				}
			}
		}
		
		return FinalValidationState;
	}
}



class SmsAndFaxValidator implements AttributeValidator {
	 Map <String, String> MyValidators = new TreeMap<String, String>();  
	   private static Logger logger = Logger.getLogger(Parser.class);
	   
	   public SmsAndFaxValidator() {
		      MyValidators.put("urlparse", "^(((http://|https://|/))?(([\\w!~*'().&=+$%-]+: )?[\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([\\w!~*'()-]+\\.)*([\\w^-][\\w-]{0,61})?[\\w]\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\w!~*'().;?:@&=+$,%#-]+)+/*))|$");
			  MyValidators.put("PhoneParse","^\\+?([0-9]{1,14})$");

		  }		
	   
	
	public boolean Validators (Tag tag) {
		List<Attribute> mapKeys;
		mapKeys=tag.attributes();
		boolean FinalValidationState=true;
		
		

		for (Attribute e : mapKeys) {
			String eName=e.name();
			 String eValue=e.value();
			 
			 
			 switch (e.name()) {
			 case "statusCallback":
				 Pattern patternUrl = Pattern.compile(MyValidators.get("urlparse"));
			    	//this admit: http https and / (local directory)	
				 Matcher matherUrl = patternUrl.matcher(eValue);
					 /// note the attributes to and from was not validated because can admit numbers or characters
				 if (matherUrl.matches() == false) {
					 FinalValidationState=false;
					 if(logger.isInfoEnabled()){
			                logger.info("The url value on "+eName+" from "+tag.name()+" is not valid");
			            }
				 }	

				 break;
			 case "action":
				 Pattern patternUrlAction = Pattern.compile(MyValidators.get("urlparse"));
				 Matcher matherUrlAction = patternUrlAction.matcher(eValue);
				 
				 if (matherUrlAction.matches() == false) {
					 FinalValidationState=false;
					 if(logger.isInfoEnabled()){
			                logger.info("The url value on "+eName+" from "+tag.name()+" is not valid");
			            }
				 }	

 
				 break;
			 case "to":
				 Pattern patternPhone = Pattern.compile(MyValidators.get("PhoneParse"));
				 Matcher matherPhone = patternPhone.matcher(eValue);
				 
				 if (matherPhone.matches() == false) {
					 FinalValidationState=false;
					 if(logger.isInfoEnabled()){
			                logger.info("The value on "+eName+" from "+tag.name()+" is not valid");
			            }
				 }
				 break;
			 case "from":
				 Pattern patternPhoneFrom = Pattern.compile(MyValidators.get("PhoneParse"));
				 Matcher matherPhoneFrom = patternPhoneFrom.matcher(eValue);
				 
				 if (matherPhoneFrom.matches() == false) {
					 FinalValidationState=false;
					 if(logger.isInfoEnabled()){
			                logger.info("The value on "+eName+" from "+tag.name()+" is not valid");
			            }
				 }
				 break;
				 
			 }
			  		
		}
		return FinalValidationState;
	}
}

class GatherValidator implements AttributeValidator {
	
	 Map <String, String> MyValidators = new TreeMap<String, String>();  
	   private static Logger logger = Logger.getLogger(Parser.class);
	   
	   public GatherValidator() {
		   
		    MyValidators.put("numDigit","3");
		   	MyValidators.put("numDigitParse","^([0-9]{1,2})$");
		   	MyValidators.put("timeOut","20");
		   	MyValidators.put("timeOutParse","^([0-9]{1,2})$");
		    MyValidators.put("urlparse", "^(((http://|https://|/))?(([\\w!~*'().&=+$%-]+: )?[\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([\\w!~*'()-]+\\.)*([\\w^-][\\w-]{0,61})?[\\w]\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\w!~*'().;?:@&=+$,%#-]+)+/*))|$");
			 

		  }		
	   

	
	public boolean Validators (Tag tag) {
		List<Attribute> mapKeys;
		mapKeys=tag.attributes();
		boolean FinalValidationState=true;

		for (Attribute e : mapKeys) {
			String eName=e.name();
	
			 switch (e.name()) {
			 case "numDigits":
				 double numDigits=Double.parseDouble(e.value());
 				
				 Pattern patternNumDigits = Pattern.compile(MyValidators.get("numDigitParse"));
				 Matcher mathernumDigits = patternNumDigits.matcher(e.value());
				 
				 if (mathernumDigits.matches() == true)  {
					 if ( (numDigits<0) ||(numDigits>Double.parseDouble(MyValidators.get("numDigit")) )) {
						 
						 FinalValidationState=false;
						 if(logger.isInfoEnabled()){
				                logger.info("The value on field "+eName+" from "+ tag.name() +" is incorrect, it must be below of "+MyValidators.get("numDigit"));
				            }
					 }
					 
				 }else {
					 FinalValidationState=false;
			        	if(logger.isInfoEnabled()){
			                logger.info("The value on field "+eName+" from "+ tag.name() +" is incorrect, it must be a numeric value below of "+MyValidators.get("numDigit"));
			            }
				 }
				 
				 break;
			 case "timeout":
				 double timeout=Double.parseDouble(e.value());
	 				
				 Pattern patternTimeout = Pattern.compile(MyValidators.get("numDigitParse"));
				 Matcher matherTimeout = patternTimeout.matcher(e.value());
				 
				 if (matherTimeout.matches() == true)  {
					 if ( (timeout<0) ||(timeout>Double.parseDouble(MyValidators.get("timeOut")) )) {
						 
						 FinalValidationState=false;
						 if(logger.isInfoEnabled()){
				                logger.info("The value on field "+eName+" from "+ tag.name() +" is incorrect, it must be below of "+MyValidators.get("timeOut"));
				            }
					 }
					 
				 }else {
					 FinalValidationState=false;
			        	if(logger.isInfoEnabled()){
			                logger.info("The value on field "+eName+" from "+ tag.name() +" is incorrect, it must be a numeric value below of "+MyValidators.get("timeOut"));
			            }
				 }
				 break;
			 case "action":
				 Pattern pattern_urlAction = Pattern.compile(MyValidators.get("urlparse")) ;
  				 Matcher mather_urlAction = pattern_urlAction.matcher(e.value());
  				if (mather_urlAction.matches() == false) {
  					FinalValidationState=false;
  					if(logger.isInfoEnabled()){
		                logger.info("The url value on "+eName+" from "+tag.name()+" is not valid");
		            }
				 }
				 break;
			 case "partialResultCallback":
				 Pattern pattern_partialResultCallback = Pattern.compile(MyValidators.get("urlparse")) ;
  				 Matcher mather_partialResultCallback = pattern_partialResultCallback.matcher(e.value());
  				if (mather_partialResultCallback.matches() == false) {
  					FinalValidationState=false;
  					if(logger.isInfoEnabled()){
		                logger.info("The url value on "+eName+" from "+tag.name()+" is not valid");
		            }
				 }
				 break;
				 
			 
			 }
		}

		
		return FinalValidationState;
	}
}

class PlayAndSayValidator implements AttributeValidator {
	 Map <String, String> MyValidators = new TreeMap<String, String>();  
	   private static Logger logger = Logger.getLogger(Parser.class);
	   
	   public PlayAndSayValidator() {
		      MyValidators.put("loop","3");
			  MyValidators.put("loopParse","^([0-9]{1,3})$");

		  }		
	   
	
	public boolean Validators (Tag tag) {
		List<Attribute> mapKeys;
		mapKeys=tag.attributes();
		boolean FinalValidationState=true;
		
		Pattern pattern_loop = Pattern.compile((MyValidators.get("loopParse")));

		for (Attribute e : mapKeys) {
			String eName=e.name();
			
			if (eName.equals("loop")){
				double eValue=Double.parseDouble(e.value());
				Matcher mather_loop = pattern_loop.matcher(e.value());
				
				if( (mather_loop.matches() == true) ) {
					
					if ((eValue<0) ||(eValue>Double.parseDouble(MyValidators.get("loop")))) {
						FinalValidationState=false;
						if(logger.isInfoEnabled()){
			                logger.info("The loop value on field "+eName+" from "+ tag.name() +" is too high, it must  be a value below of "+MyValidators.get("loop"));
			            }
						
					}
					
				} else {
					FinalValidationState=false;
		        	if(logger.isInfoEnabled()){
		                logger.info("The loop value on field "+eName+" from "+ tag.name() +" is incorrect, it must be a numeric value below of "+MyValidators.get("loop"));
		            }
				}
			}
		}

		
		return FinalValidationState;	
	}
}


