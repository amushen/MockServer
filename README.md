MockServer
==========

A mock server application, Using for mobile develop, simulate server response.


QuickStart:

1.execute run.bat
2.open IE browser and input URL: http://localhost:8080/test
3.you can see "hello"

How to work:

When you request a url, this app will load mocks folder's certain file, 
and return the file's content.

e.g.
You prepare a file named "calc", its content is "123". create a folder named "service"
put service folder into mocks folder, and put calc file into service folder.
like : mocks/service/calc

Then you can access the URL like:
http://localhost:8080/service/calc
"123" will return to browser(or app).

If your request has parameter such as http://localhost:8080/service/calc?cmd=a&id=1
then you need to prepare file like:
mock/test/service/calc_cmd=a&id=1
(just replace ? with _ )


usage:
java -jar MockServer.jar [mock file path] [port]
e.g.
java -jar MockServer.jar d:\\MockServer 808


License:
MIT

Author:
amushen@gmail.com

2014/09/17
