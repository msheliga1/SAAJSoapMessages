This project is used to learn about and test the SAAJ API. MJS 5.18

It contains 3 main packages.  The "simple" package is for web services
that have no input parameters, the "intermediate" package is for web 
services with primitive parameters, and the "complex" package is for 
web services with complex parameters such as nested Collections and POJOs.

All messages are created using SAAJ and the web services are accessed using 
the SOAPConnection class.  Both request messages and the SOAP response messages
are printed using an SAAJ toString method.