package soap.saaj.demos.intermediate;

import javax.jws.WebService;

/* 
 * This class implements a web service with very moderately complex parameters.
 * Methods have varying input parameters and return types, but all are primitives.
 * @Author Mike Sheliga 5.5.15
 */
@WebService(endpointInterface="soap.saaj.demos.intermediate.IntermediateServer")
public class IntermediateServerImpl implements IntermediateServer {

	@Override
	public String returnHelloName(String name) {
		return "Hello " + name;
	}

	@Override
	public int add2Ints(int int1, int int2) {
		return int1 + int2;
	}

	@Override
	// could not pass in a character MJS 5.14.18
	public long addBSILD(byte byte1, short short1, int int1, long long1, double double1) {
		// System.out.println(" Byte: " + byte1 + " Short: " + short1);
		// System.out.println("int " + int1 + " Long: " + long1 + " Double: " + (long) double1);
		return (long) ((long) (byte1 + short1 + int1) + long1 + (long) double1);
	}


	
}
