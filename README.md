For Generating Token 
use "/token" api end point 
url:- http://localhost:8090/token   
Json input :- {
    "userName":"Mohit",
    "password":"Mohit@123"
}
For anyother username and password it will get the authentication error 

For running the api :-
use "api/devices" endpoint
url:- http://localhost:8090/api/devices
json input:-
{
    "deviceID": "12362",
    "deviceName": "Device K",
    "site": "Site X",
    "content": {
        "meterReading": 100,
        "meterActiveDuration": 30
    }
}

can change the values in given in the json file for diffrent set of outptut
