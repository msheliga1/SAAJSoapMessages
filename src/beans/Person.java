package beans;

import java.io.Serializable;

/**
 * 
 * @author Mike Sheliga 5.5.18
 * Standard Person bean class used for SAAJ testing.
 * To simplify testing this model has only 2 instance variables.
 *
 */
public class Person implements Serializable {
	
	static final long serialVersionUID = 1;
	
	// Keep this model minimal as it greatly helps with testing.
	private String firstName;
	private int age;
	
	public Person() {  // needed for bean classes
	}
	
	public Person(String firstName, int age) {
		this.firstName = firstName;
		this.age = age;
	}

	// ------ ------ Standard Methods ------ ------
	
	
	// ------ ------ Standard getters and setters ------ ------

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
} // end class Person
