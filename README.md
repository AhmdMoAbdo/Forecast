# Forecast

Forecast is an android weather app based on kotlin 

Main Features:

1- Current weather condition including temprature, humidity, wind, visibility, cloud and pressure in your current area using GPS or anywhere in the world using the map

2- daily forecast for the upcoming 7 days

3- hourly forecast for the upcoming 48 hours

4- live weather updates from any where around the world

5- Alerts which Notifications about any unstable weather conditions all around the world at the time of your choice

6- support for different temprature units, metric and imperial units as well

7- support for 2 languages English and Arabic 


now let's have a tour around the app

![Group 22](https://user-images.githubusercontent.com/120793640/229963418-6d429685-d7c1-454c-9664-fea970663c72.png)

First the user is greated with a boarding screen showing the main features of the app and briefly explaining them 
which then takes the user to 5 dialogues to initilize the settings and customize the experience



![Group 24](https://user-images.githubusercontent.com/120793640/229963623-465a2852-37ea-49ab-a925-78930c62a86d.png)

The first one is about the tempratue units and the second one is about how to get the home location which is either by GPS or by map
If the user chooses the GPS then the app requests location permissions from the user
And if the user chooses the map then a popup window appear containing a map for the user to choose from




![Group 25](https://user-images.githubusercontent.com/120793640/229963920-b78687bd-8185-4060-9afd-b314f8051382.png)

The Third dialog is about the wind speed unit preferred by the user and then how the user likes keep up with the alerts either by just notifications or by ringing alarms
and for Android 13 and above we request notification permissions as well as you can see in the picture
And the final one is about the language which is either English or Arabic



![Group 23](https://user-images.githubusercontent.com/120793640/229964261-0cdce0a4-31c0-42fb-94c2-0b655ec98877.png)

And then we move on to the home screen showing the weather condition at the moment with hourly forecast of the upcoming 48 hours and daily forecast of the upcoming 7 days
And besides the home we have the saved locations list which shows a list of places of the user's choice for an easy access to the weather condition in these areas as well



![Group 26](https://user-images.githubusercontent.com/120793640/229964636-13281068-e4a0-4707-bd11-f5d042abf3ae.png)

And we have the alerts screens as well which include a list of all the alerts and a floating button which shows a calender, time picker and a map in that order allowing the user to choose the time of his alert and the place as well



![Group 27](https://user-images.githubusercontent.com/120793640/229964874-4a18bba8-68a4-4420-9660-da075439fdf7.png)

and finally we have the settings screen showing the differnet types of settings to customize the experience for the user
the 2nd picture shows the type of notification the user can get on setting an alert which has a dismiss button to stop the alarm and dismiss the notification itself
and then the 3rd picture showing a snipet of the Arabic Version of the app



Technologies used:
1- Android with Kotlin

2- Coroutines

3- Retrofit

4- Room Database

5- Broadcast Reciever

6- Services

7- Alarm Manger

8- View Binding

9- unit&Integration Testing



***********************************************************************************
*                                 Developed By                                    *
*                             Ahmed Mohammed Abdo                                 *
***********************************************************************************
