package soap.saaj.demos.complex;

import java.util.List;

import javax.jws.WebService;

import beans.Family;
import beans.Person;

/* 
 * This class implements a web service with complex Collection and POJO parameters.
 * Methods included parameters that are Collections (Lists), POJOs, POJOs that contain 
 * list of other POJOs and Lists that contain complex POJOs.  These are used for 
 * testing SAAJ modifications to SOAPMessages.
 * @Author Mike Sheliga 5.11.15
 */
@WebService(endpointInterface="soap.saaj.demos.complex.ComplexServer")
public class ComplexServerImpl implements ComplexServer {
	private static final String NEW_LINE = System.lineSeparator();

	@Override
	public String addIntList(List<Integer> ints) {
		int sum = 0;
		for (Integer intValue: ints) {
			sum += intValue;
		}
		return "Sum is " + sum;
	} // end addIntList

	@Override
	public int getPersonAge(Person p1) {
		return p1.getAge();
	} // end getPersonAge
	
	@Override
	/* Sum the ages of all children in a family. 
	 */
	public int addChildAges(Family family) {
		int result = 0;
		List<Person> children = family.getChildren();
		for (Person child : children) {
			result += child.getAge();
		}
		return result;
	} // end addChildAges
		
	@Override
	/* Sums the ages of all family members and returns the maximum total.
	 * Returns a string showing the names and ages of all family members, 
	 * along with a sum of the ages for each family, and the name 
	 * and total years for the family with the greatest sum of ages.
	 */
	public String getOldestFamily(List<Family> families) {
		String result = "Finding oldest family among: " + NEW_LINE;
		int oldestSum = 0; // initial value
		Family oldestFamily = null;
		for (Family family: families) {
			result += " " + family.getSurName();
			int parentSum = 0;
			result += ": (Parents: ";
			for (Person parent: family.getParents()) {
				parentSum += parent.getAge();
				result += " " + parent.getFirstName() + " (" + parent.getAge() + ") ";
			}
			result += " ==> " + parentSum + ", " + NEW_LINE;
			int childSum = 0;
			result += "          + Children: ";
			for (Person child: family.getChildren()) {
				childSum += child.getAge();
				result += " " + child.getFirstName() + " (" + child.getAge() + ") ";
			}
			result += " ==> " + childSum + ", " + NEW_LINE;
			int totalSum = parentSum + childSum;
			result += "        ==> " + totalSum + NEW_LINE;
			if (oldestFamily == null || totalSum > oldestSum) {
				oldestFamily = family;
				oldestSum = totalSum;
			} // end if new oldest family found
		} // end for all families
		return result += " The family with the greatest sum of ages is the " + oldestFamily.getSurName() + 
				" family (" + oldestSum + " years).";
	} // end getOldestFamily
	
} // end ComplexServerImpl
