package com.s8.io.bohr.lithium.branches;

import static com.s8.api.bohr.BOHR_Keywords.CLOSE_JUMP;
import static com.s8.api.bohr.BOHR_Keywords.CLOSE_SEQUENCE;
import static com.s8.api.bohr.BOHR_Keywords.CREATE_NODE;
import static com.s8.api.bohr.BOHR_Keywords.DECLARE_TYPE;
import static com.s8.api.bohr.BOHR_Keywords.EXPOSE_NODE;
import static com.s8.api.bohr.BOHR_Keywords.FRAME_FOOTER;
import static com.s8.api.bohr.BOHR_Keywords.FRAME_HEADER;
import static com.s8.api.bohr.BOHR_Keywords.OPEN_JUMP;
import static com.s8.api.bohr.BOHR_Keywords.OPEN_SEQUENCE;
import static com.s8.api.bohr.BOHR_Keywords.REMOVE_NODE;
import static com.s8.api.bohr.BOHR_Keywords.UPDATE_NODE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.api.bytes.ByteInflow;
import com.s8.api.exceptions.S8IOException;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.object.CreateLiObjectDelta;
import com.s8.io.bohr.lithium.object.ExposeLiObjectDelta;
import com.s8.io.bohr.lithium.object.RemoveLiObjectDelta;
import com.s8.io.bohr.lithium.object.UpdateLiObjectDelta;
import com.s8.io.bohr.lithium.type.LiType;
import com.s8.io.bohr.lithium.type.LiTypeParser;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public class LiInbound {

	
	






	/**
	 * code base
	 */
	private final LiCodebase codebase;



	/**
	 * 
	 */
	private final Map<Long, LiTypeParser> typeParsersByCode = new HashMap<Long, LiTypeParser>();

	private final Map<String, LiTypeParser> typeParsersById = new HashMap<>();


	/**
	 * 
	 * @param codebase
	 * @param graph
	 * @param isVerbose
	 */
	public LiInbound(LiCodebase codebase) {
		super();
		this.codebase = codebase;
	}


	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public void pullFrame(ByteInflow inflow, LiGraphDeltaConsumer consumer) throws IOException {
		// check opening
		if(!inflow.matches(FRAME_HEADER)) { throw new IOException("DO NOT MATCH HEADER"); }
		parseSequence(inflow, consumer);
		if(!inflow.matches(FRAME_FOOTER)) { throw new IOException("DO NOT MATCH FOOTER"); }
	}
	
	
	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public void parseSequence(ByteInflow inflow, LiGraphDeltaConsumer consumer) throws IOException {

		int code;

		if((code = inflow.getUInt8()) != OPEN_SEQUENCE) {
			throw new IOException("Sequence mist startwith an open sequecne tag");
		}

		while((code = inflow.getUInt8()) != CLOSE_SEQUENCE) {
			switch(code) {

			case OPEN_JUMP: 
				long version = inflow.getUInt64();
				
				LiGraphDelta graphDelta = new LiGraphDelta(version);
				parseGraphDelta(inflow, graphDelta);
				consumer.pushDelta(graphDelta);
				break;

			default : throw new IOException("Unsupported BOHR keyword code: "+Integer.toHexString(code));
			}
		}
	}

	


	/**
	 * 
	 * @param code
	 * @return
	 * @throws S8IOException
	 */
	public LiTypeParser getTypeParserByCode(long code) throws S8IOException {
		LiTypeParser typeParser = typeParsersByCode.get(code);
		if(typeParser == null) {
			throw new S8IOException("Failed to find typeInflow for code: "+Long.toHexString(code));
		}
		return typeParser;
	}	

	
	
	


	/**
	 * 
	 * @param graph
	 * @param inflow
	 * @throws IOException
	 */
	public void parseGraphDelta(ByteInflow inflow, LiGraphDelta delta) throws IOException {


		int code;

		while((code = inflow.getUInt8()) != CLOSE_JUMP) {
			switch(code) {
			
			case DECLARE_TYPE: onDeclareType(inflow);
				break;
			
			case CREATE_NODE: onCreateNode(inflow, delta);
				break;
				
			case UPDATE_NODE: onUpdateNode(inflow, delta);
				break;

			case EXPOSE_NODE: onExposeNode(inflow, delta);
				break;
		
			case REMOVE_NODE: onRemoveNode(inflow, delta);
				break;

			default : throw new IOException("Unsupported BOHR keyword code: "+Integer.toHexString(code));
			}
		}
	}
	
	
	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public LiTypeParser onDeclareType(ByteInflow inflow) throws IOException {

		/* retrieve name of this newly declared type */
		String typeName = inflow.getStringUTF8();

		/* retrieve code assigned to this newly declared type */
		long typeCode = inflow.getUInt7x();

		/* check that this type code has not already been assigned */
		if(typeParsersByCode.containsKey(typeCode)) {
			throw new S8IOException("A type has already defined for code: "+typeCode+"->"+typeName
					+". See "+typeParsersByCode.get(typeCode).print(typeCode));
		}
		
		/* find corresponding type */
		LiType type = codebase.getTypeBySerialName(typeName);
		if(type == null) {
			throw new S8IOException("Failed to find type for name: "+typeName);
		}

		/* create typeInflow*/
		LiTypeParser typeParser = new LiTypeParser(type);

		/* store this typeInflow for later use */
		typeParsersByCode.put(typeCode, typeParser);
		//typeInflowsByClass.put(type.getRuntimeName(), typeInflow);

		return typeParser;
	}
		
	
	public void onCreateNode(ByteInflow inflow, LiGraphDelta branchDelta) throws IOException {

		/* type code */
		long typeCode = inflow.getUInt7x();
		
		/* typeParser */
		LiTypeParser typeParser = getTypeParserByCode(typeCode);
		
		/* index */
		String index = inflow.getStringUTF8();
		
		/* retrieve type */
		LiType type = typeParser.getType();

	
		/* store vertex in graph */
		typeParsersById.put(index, typeParser);

		/* parse fields values */
		List<LiFieldDelta> fieldDeltas = new ArrayList<>();
		typeParser.parse(inflow, fieldDeltas);
		branchDelta.appendObjectDelta(new CreateLiObjectDelta(index, type, fieldDeltas));
		
	}

	
	
	public void onUpdateNode(ByteInflow inflow, LiGraphDelta branchDelta) throws IOException {
		
		String id = inflow.getStringUTF8();
		

		/* retrieve parser from vertex */
		LiTypeParser typeParser = typeParsersById.get(id);

		LiType type = typeParser.getType();
		
		/* parse fields values */
		
		List<LiFieldDelta> fieldDeltas = new ArrayList<>();
		typeParser.parse(inflow, fieldDeltas);
		branchDelta.appendObjectDelta(new UpdateLiObjectDelta(id, type, fieldDeltas));
	}

	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public void onExposeNode(ByteInflow inflow, LiGraphDelta branchDelta) throws IOException {
		String id = inflow.getStringUTF8();
		int slot = inflow.getUInt8();
		branchDelta.appendObjectDelta(new ExposeLiObjectDelta(id, slot));
	}

	
	

	
	public void onRemoveNode(ByteInflow inflow, LiGraphDelta branchDelta) throws IOException {
		
		String id = inflow.getStringUTF8();
		branchDelta.appendObjectDelta(new RemoveLiObjectDelta(id));
	}

	
	public void onDefineComment(String comment, LiGraphDelta branchDelta) throws S8IOException {
		branchDelta.setComment(comment);
	}

	
	public void onTimestamp(long t, LiGraphDelta branchDelta) throws S8IOException {
		branchDelta.setTimestamp(t);
	}
}
