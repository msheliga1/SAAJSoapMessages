package soap.saaj.demos.simple;

import javax.jws.WebService;

/* 
 * This class implements a web service with very simple parameters.
 * Both methods have no input parameters and return a String.
 * @Author Mike Sheliga 5.1.15
 */
@WebService(endpointInterface="soap.saaj.demos.simple.SimpleServer")
public class SimpleServerImpl implements SimpleServer {

	@Override
	public String returnHello2() {
		return  "Hello2 Text";
	}

	@Override
	public String returnHello5() {
		return  "Hello5 Text";
	}
	
}
