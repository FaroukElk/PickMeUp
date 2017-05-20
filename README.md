# PickMeUp

PickMeUp is an android app where users can find nearby pick up games for various sports. I decided to create this app because I enjoyed playing pick up soccer in college, but once I graduated and moved to a new area I had trouble finding a regular group to play with.

# Introduction

When you launch the app, the home page features a Google Map Fragment, a button to switch the map to full screen mode, and a button to create a new event (plus sign button in bottom right corner).

# Features

Clicking the create event button will launch a new activity where the user can input the event information including location, sport, skill level, date, and time. The location box uses the Google Places API to make it easy for users to find their location, and to get that location's coordinates. 

Once the user clicks the button to create the new event; the user will be taken back to the home page where their event information will be added to a recyclerview, and a location marker will be added to the map fragment. As more events are added, the user can click each item in the recyclerview to be taken to that event's marker on the map.

After there is at least one event added, a new button will appear on the homepage that will allow the user to filter the results shown on the map and in the recyclerview. Once clicked, the user will be taken to a new activity where they can choose which sports, skill level, distance, and date the user would like to filter the results. All choices are set to "Any" by default.

# Acknowledgements 

Soccer, basketball, hockey, volleyball, lacrosse, and fullscreen icons made by Freepik from www.flaticon.com
Tennis, and baseball/softball icons made by Madebyoliver from www.flaticon.com
