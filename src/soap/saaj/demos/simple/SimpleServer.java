package soap.saaj.demos.simple;

import javax.jws.WebService;
import javax.jws.WebMethod;


/* 
 * This interface is for testing SAAJ and SOAP messages with very simple parameters/return values.
 * No input parameters are used and only String outputs are used..
 * @Author Mike Sheliga 5.1.18
 */
@WebService
public interface SimpleServer {
	@WebMethod  // Default is for all methods to be web methods so this is redundant.
	public String returnHello2();
	
	@WebMethod
	public String returnHello5();

}
