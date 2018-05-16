package soap.saaj.demos.intermediate;

import javax.jws.WebService;
import javax.jws.WebMethod;

/* 
 * This interface is for testing SAAJ and SOAP messages with parameters of intermediate complexity.
 * Multiple input parameters are allowed but no Collections or POJOs are used.
 * @Author Mike Sheliga 5.4.18
 */
@WebService
public interface IntermediateServer {
	@WebMethod  // Default is for all methods to be web methods so this is redundant.
	public String returnHelloName(String name);
	
	@WebMethod
	public int add2Ints(int int1, int int2);
	
	@WebMethod 
	public long addBSILD(byte byte1, short short1, int int1, long long1, double double1);

}
