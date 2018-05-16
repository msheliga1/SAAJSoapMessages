package beans;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Mike Sheliga 5.5.18
 * Standard Family bean class used for SAAJ SOAPMessage testing.
 * To simplify testing this model has only 3 instance variables, 
 * two of which are Collections (lists in this case).
 *
 */
public class Family implements Serializable {

	// beans are serializable, have no-arg constructor and standard getters and setters.
	static final long serialVersionUID = 1;
	
	private String surName;
	private List<Person> parents;
	private List<Person> children;
	
	public Family() {
	}	
	
	public Family(String surName, List<Person> parents, List<Person> children) {
		this.surName = surName;
		this.parents = parents;
		this.children = children;
	}



	// ------ ------ Standard Methods ------ ------
	
	
	// ------ ------ Standard getters and setters ------ ------

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public List<Person> getParents() {
		return parents;
	}

	public void setParents(List<Person> parents) {
		this.parents = parents;
	}

	public List<Person> getChildren() {
		return children;
	}

	public void setChildren(List<Person> children) {
		this.children = children;
	}

} // end class Family
