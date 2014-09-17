MockServer
==========

A mock server application, Using for mobile develop, simulate server response.


<h2>QuickStart:</h2>

1.execute run.bat<br>
2.open IE browser and input URL: http://localhost:8080/test<br>
3.you can see "hello"<br>
<p></p>

<h2>How to work:</h2>

When you request a url, this app will load mocks folder's certain file, <br>
and return the file's content.<br>
<br>
e.g.<br>
You prepare a file named "calc", its content is "123". create a folder named "service"<br>
put service folder into mocks folder, and put calc file into service folder.<br>
like : mocks/service/calc<br>
<br>
Then you can access the URL like:<br>
http://localhost:8080/service/calc<br>
"123" will return to browser(or app).<br>

If your request has parameter such as http://localhost:8080/service/calc?cmd=a&id=1<br>
then you need to prepare file like:<br>
mock/test/service/calc_cmd=a&id=1<br>
(just replace ? with _ )<br>
<br>
<br>
<h2>usage:</h2>
java -jar MockServer.jar [mock file path] [port]<br>
e.g.<br>
java -jar MockServer.jar d:\\MockServer 808<br>
<br>
<br>
License:<br>
MIT<br>

Author:<br>
amushen@gmail.com<br>
<br>
2014/09/17<br>
