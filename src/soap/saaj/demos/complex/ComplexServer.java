package soap.saaj.demos.complex;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import beans.Family;
import beans.Person;

import java.util.List;
import javax.jws.WebMethod;

/* This interface is for testing SAAJ and SOAP methods with complex parameters.
 * Parameters include nested Collections and POJOs.
 * @Author Mike Sheliga 5.10.15
 */
@WebService
// Wrapped is the default parameter style so this is redundant
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ComplexServer {
	// Methods are webMethods by default . . .
	public String addIntList(List<Integer> ints);  // Collection parameter
	
	@WebMethod // ... so this is also redundant
	public int getPersonAge(Person p1);  // POJO parameter
	
	@WebMethod 
	public int addChildAges(Family family);  // POJO with Collections of other POJOs
	
	@WebMethod(exclude=false)  // false is default anyways
	// Parameter is Collection of POJO that contains Collections of other POJOs.
	public String getOldestFamily(List<Family> familiyList); 
	
}
