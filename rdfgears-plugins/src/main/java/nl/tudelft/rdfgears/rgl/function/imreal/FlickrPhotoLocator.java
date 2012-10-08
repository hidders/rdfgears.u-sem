package nl.tudelft.rdfgears.rgl.function.imreal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.ListBackedBagValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;

import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

import java.util.*; 
import java.io.*;
import java.net.*;


/**
 * Given a flickr username, use Claudia's C++ based service to fetch the photo data
 * and estimate the geo-long/lat. 
 * 
 * 
 * @author Jasper / Eric
 *
 */
public class FlickrPhotoLocator  extends SimplyTypedRGLFunction  {
	/* named inputs */ 
	public static final String INPUT_USERNAME = "flickrUser";
	public static final String INPUT_HOSTNAME = "serviceHost";
	public static final String INPUT_PORT     = "servicePort";

	/* fieldnames in output records */ 
	public static final String FIELD_LONGITUDE = "longitude";
	public static final String FIELD_LATITUDE = "latitude";
	public static final String FIELD_ESTIM_LONGITUDE = "estimatedLong";
	public static final String FIELD_ESTIM_LATITUDE = "estimatedLat";
	public static final String FIELD_ESTIM_ERROR = "estimatedErrorKm";
	public static final String FIELD_DATE_TAKEN= "dateTaken";
	
	private static final Map<String, RGLValue> cachedResults = new HashMap<String, RGLValue>(); 

	
	FieldIndexMap fiMap = FieldIndexMapFactory.create(	FIELD_LONGITUDE, 
														FIELD_LATITUDE, 
														FIELD_ESTIM_LONGITUDE, 
														FIELD_ESTIM_LATITUDE, 
														FIELD_ESTIM_ERROR, 
														FIELD_DATE_TAKEN);
	
	public FlickrPhotoLocator(){
		/* required input type is always RDFType */ 
		requireInputType(INPUT_USERNAME, RDFType.getInstance());
		requireInputType(INPUT_HOSTNAME, RDFType.getInstance());
		requireInputType(INPUT_PORT, RDFType.getInstance());
	}
	
	@Override
	public RGLType getOutputType() {
		/* this function returns a type Bag(Record(< .... >)) */
		TypeRow typeRow = new TypeRow();
		typeRow.put(FIELD_LONGITUDE, RDFType.getInstance());
		typeRow.put(FIELD_LATITUDE, RDFType.getInstance());
		typeRow.put(FIELD_ESTIM_LONGITUDE, RDFType.getInstance());
		typeRow.put(FIELD_ESTIM_LATITUDE, RDFType.getInstance());
		typeRow.put(FIELD_ESTIM_ERROR, RDFType.getInstance());
		typeRow.put(FIELD_DATE_TAKEN, RDFType.getInstance());
		return BagType.getInstance(RecordType.getInstance(typeRow));
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		/* input values are guaranteed to be non-null, as this is a SimplyTypedRGLFunction */
		RGLValue inputUser = inputRow.get(INPUT_USERNAME);
		RGLValue inputHost = inputRow.get(INPUT_HOSTNAME);
		RGLValue inputPort = inputRow.get(INPUT_PORT);
		
		if (! (inputUser.isLiteral() && inputHost.isLiteral() && inputPort.isLiteral())){
			return ValueFactory.createNull("input to "+getFullName()+" must be all literals");
		}
		/* ok, all literals */
		
		
		try { 
			int port; 
			try { 
				port = (int) inputPort.asLiteral().getValueDouble();
			} catch (NumberFormatException e){
				e.printStackTrace();
				return ValueFactory.createNull("Cannot format number "+inputPort.asLiteral().getValueString()+": "+e.getMessage()); 
			}  
			
			String username = inputUser.asLiteral().getValueString();
			String host     = inputHost.asLiteral().getValueString();
			return getPhotos(username, host, port);
			
			
		} catch (Exception e){
			return ValueFactory.createNull("Exception in "+getFullName()+": "+e.getMessage());
		}
	}
		
	
	 
	private RGLValue getPhotos(String username, String host, int port) throws IOException {
		
		String cacheKey = username + " "+ host + " "+ port;
		if (! cachedResults.containsKey(cacheKey)){
			
			/* access remote service */ 
			ArrayList<Location> photoLocations = getPhotosFromRemoteService(username, host, port);
			
			/* convert to rgl value */
			List<RGLValue> bagList = ValueFactory.createBagBackingList();
			
			for (Location loc : photoLocations ) {
				ModifiableRecord locRecord = ValueFactory.createModifiableRecordValue(fiMap);
				locRecord.put(FIELD_LONGITUDE, ValueFactory.createLiteralDouble(loc.longitude));  
				locRecord.put(FIELD_LATITUDE, ValueFactory.createLiteralDouble(loc.latitude));  
				locRecord.put(FIELD_ESTIM_LATITUDE, ValueFactory.createLiteralDouble(loc.estLatitude));  
				locRecord.put(FIELD_ESTIM_LONGITUDE, ValueFactory.createLiteralDouble(loc.estLongitude));
				locRecord.put(FIELD_ESTIM_ERROR, ValueFactory.createLiteralDouble(loc.errorKM));
				locRecord.put(FIELD_DATE_TAKEN, ValueFactory.createLiteralPlain(loc.date, null));

				bagList.add(locRecord);
			}
			
			cachedResults.put(cacheKey, new ListBackedBagValue(bagList));
		}
		
		return cachedResults.get(cacheKey);		
	}

	private ArrayList<Location> getPhotosFromRemoteService(String flickrID, String hostname, int port) throws IOException {
		
		Engine.getLogger().warn(("getting enriched photos for flickr user "+flickrID));
		Socket kkSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
 
		kkSocket = new Socket(hostname, port);
		out = new PrintWriter(kkSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String fromServer;

		out.println(flickrID);
		
		Vector<String> inputVec = new Vector<String>();
	 
		while ((fromServer = in.readLine()) != null){
			inputVec.add(fromServer);
        }
		
        out.close();
        in.close();
        stdIn.close();
        kkSocket.close();

		String markerDate = " datetaken=\"";
		String markerLat = " latitude=\"";
		String markerLng = " longitude=\"";
		String markerEstLat=" estLat=\"";
		String markerEstLng=" estLng=\"";
		String markerError=" errorDist=\"";


		ArrayList<Location> results = new ArrayList<Location>();

		for(int i=0; i<inputVec.size(); i++)
		{
			String line = inputVec.elementAt(i);
			if(line.contains("<photo id=")==false)
				continue;

			
			double lat=0, lng=0, estLat=0, estLng=0, error=0; 
			String sEstLat, sEstLng, sError, dateTaken="", sLat, sLng; 
			
			
			try { 
				/* this is a try/catch because the parsing mechanism isn't robust. Goldplate later.  */ 
				dateTaken 	= line.substring( line.indexOf(markerDate)+markerDate.length(), line.indexOf(" ",line.indexOf(markerDate)+5));
				sLat		= line.substring( line.indexOf(markerLat)+markerLat.length(), line.indexOf("\"",line.indexOf(markerLat)+markerLat.length()));
				sLng		= line.substring( line.indexOf(markerLng)+markerLng.length(), line.indexOf("\"",line.indexOf(markerLng)+markerLng.length()));
				
				lat = Double.parseDouble(sLat);
				lng = Double.parseDouble(sLng);
					
				sEstLat		= line.substring( line.indexOf(markerEstLat)+markerEstLat.length(), line.indexOf("\"", line.indexOf(markerEstLat)+markerEstLat.length()));
				sEstLng		= line.substring( line.indexOf(markerEstLng)+markerEstLng.length(), line.indexOf("\"", line.indexOf(markerEstLng)+markerEstLng.length()));
				sError		= line.substring( line.indexOf(markerError)+markerError.length(), line.indexOf("\"", line.indexOf(markerError)+markerError.length()));		
				
				estLat = Double.parseDouble(sEstLat);
				estLng = Double.parseDouble(sEstLng);
				error = Double.parseDouble(sError);
			} catch (Exception e){
				// ignore 
				Engine.getLogger().warn("Parsing the XML from Claudia's photo locater failed (no problem, assuming value 0.0). "); 
			}

			Location loc = new Location();
			loc.latitude = lat;
			loc.longitude = lng;
			loc.estLatitude = estLat;
			loc.estLongitude = estLng;
			loc.errorKM = error;
			loc.date = dateTaken;

			results.add(loc);

		}
		return results; 
    }
	 
	 
	 

	class Location
	{
		public double latitude;
		public double longitude;
		
		public double estLatitude;
		public double estLongitude;
	
		public double errorKM;
	
		String date;
	}

	

}
