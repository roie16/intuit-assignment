# Intuit home assigment 

## swagger endpoint:
you can see and test the api using the following link(instead of postman): http://localhost:8080/swagger-ui.html

## run:
* pre requisite :
  * Java 17 + 
  * Docker (Optional)
  * node npm\yarn
    
run the Attached dockerfile (publish port 8080 & 8080(rsocket port ) or using intellij start the backend app

please note that I have used spring reactor with reactive programing for this project :) 

* please note do to the size of the home assigment I am only supporting loading the frond using the webpack dev server using the following command:
``` "start": "react-scripts start"```
and not serving the front via the spring server, apologies, I just didn't have the time

* also while I did unitest as again the size of this project is huge
they are there for more of concept understanding of my skill and not coverage :) 

* normally  I am also deploying the app to heroku but because of the rsocket second netty server it is also not as smooth as I would like 

