package com.s8.io.bohr.lithium.branches;

import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.s8.api.exceptions.S8BuildException;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.object.ExposeLiObjectDelta;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.ResolveScope;

public class LiGraph {


	/**
	 * 
	 */
	public final static int EXPOSURE_RANGE = 8;


	public final LiBranch branch;


	/**
	 * The interior mapping
	 */
	public final Map<String, LiVertex> vertices;


	final LiVertex[] exposure;


	private boolean hasUnpublishedChanges = false;

	private final Deque<LiVertex> unpublishedVertices = new LinkedList<LiVertex>();


	private final Set<Integer> unpublishedSlotExposure = new HashSet<>();




	/**
	 * Stateful var
	 */
	long version;



	/**
	 * 
	 * @param branch
	 */
	public LiGraph(LiBranch branch) {

		super();
		this.branch = branch;

		// exposure
		exposure = new LiVertex[EXPOSURE_RANGE];

		vertices = new HashMap<String, LiVertex>();
	}


	/**
	 * 
	 * @return
	 */
	public LiCodebase getCodebase() {
		return branch.codebase;
	}


	public LiVertex getVertex(String id) {
		return vertices.get(id);
	}


	public void removeVertex(String id) {
		vertices.remove(id);
	}


	public void expose(int slot, SpaceS8Object object) throws S8IOException {
		if(object != null) {
			LiVertex vertex = resolveVertex(object);
			exposure[slot] = vertex;	
		}
		else {
			exposure[slot] = null;
		}
		reportExpose(slot);
	}


	public SpaceS8Object retrieveObject(String index) {
		return vertices.get(index).object;
	}




	public BuildScope createBuildScope() {
		return new BuildScope() {
			@Override
			public SpaceS8Object retrieveObject(String index) {
				return vertices.get(index).object;
			}
		};
	}



	public final ResolveScope resolveScope = new ResolveScope() {

		@Override
		public String resolveId(SpaceS8Object object) throws S8IOException {
			if(object != null) {
				return append(null, object).id;
			}
			else {
				return null;
			}
		}
	};




	public LiVertex resolveVertex(SpaceS8Object object) throws S8IOException {
		if(object == null) throw new S8IOException("Cannot resolve null object vertex");
		return append(null, object);
	}



	public LiVertex append(String id, SpaceS8Object object) throws S8IOException {

		if(object == null) { throw new S8IOException("Cannot append null obejct"); }

		/* retrieve object vertex */
		LiVertex vertex = (LiVertex) object.S8_vertex;

		if(vertex == null) {

			/* if index is null, assigned a newly generated one */
			boolean isCreating;
			if(isCreating = (id == null)){
				id = branch.createNewIndex();
			}

			/* create vertex */
			vertex = new LiVertex(this, id, object);

			/* assign newly created vertex */
			object.S8_vertex = vertex;

			/* newly created vertex, so report activity */
			if(isCreating) { reportCreate(vertex); }

			/* register vertex */
			vertices.put(id, vertex);
		}

		return vertex;

	}




	public void reportExpose(int slot) {
		unpublishedSlotExposure.add(slot);
		hasUnpublishedChanges = true;
	}


	public void reportCreate(LiVertex vertex) {
		unpublishedVertices.add(vertex);
		hasUnpublishedChanges = true;
	}


	public void reportUpdate(LiVertex vertex) {
		hasUnpublishedChanges = true;
		unpublishedVertices.add(vertex);
	}



	public boolean hasUnpublishedChanges() {
		return hasUnpublishedChanges;
	}


	/**
	 * 
	 * @param outflow
	 * @throws IOException 
	 * @throws S8IOException 
	 * @throws S8BuildException 
	 */
	public LiGraphDelta produceDiff() throws S8BuildException, S8IOException, IOException {

		if(!hasUnpublishedChanges) {
			// TODO
		}

		LiGraphDelta branchDelta = new LiGraphDelta(version+1);
		version++;


		LiVertex vertex;
		while((vertex = unpublishedVertices.poll()) != null) {
			vertex.publish(branchDelta.objectDeltas, resolveScope);
		}


		// expose if necessary
		if(!unpublishedSlotExposure.isEmpty()) {
			unpublishedSlotExposure.forEach(slot -> {
				
				LiVertex exposedVertex = exposure[slot];

				branchDelta.appendObjectDelta(
						new ExposeLiObjectDelta(exposedVertex != null ? exposedVertex.id : null, slot));		

			});
		}



		hasUnpublishedChanges = false;

		return branchDelta;

	}


}
