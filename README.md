# Assignment 4

**Due by 11:59pm on Monday, 6/5/2017**

**Demo due by 11:59pm on Friday, 6/16/2017**

In this assignment, you'll add user preferences to our weather app.

This repository provides you with starter code that implements the weather app as we've worked on it to date.  In addition, there is a new activity class `SettingsActivity` that is launched via an action in the main activity.  Your job is to turn that new activity into a user preferences screen by incorporating a `PreferenceFragment`.  The preferences screen should allow the user to set the following preferences:

  * **Weather units** - The user should be allowed to select between "Imperial", "Metric", and "Kelvin", and the currently selected value should be displayed as the summary for the preference.  See the OpenWeatherMap API documentation here for more info on how this preference value will be used: https://openweathermap.org/forecast5#data.

  * **Weather location** - The user should be allowed to enter an arbitrary location for which to fetch weather.  The currently set value should be set as the summary for the preference.  You can specify any default location you'd like.  See the OpenWeatherMap API documentation here for more info on how this preference value will be used: https://openweathermap.org/forecast5#name5.

The settings of these preferences should affect the URL used to query the OpenWeatherMap API.  The app should be hooked up so that any change to the preferences results in the OpenWeatherMap API being re-queried and the new results being displayed.  Importantly, there are a couple places in the UI and elsewhere that will also need to be updated in response to a change in preferences:
  * The weather location displayed at the top of the main activity.
  * The units displayed in the forecast list and the forecast detail view.
  * The location displayed in the map when the corresponding action bar action is triggered.
  * The currently set forecast location should be added to the text shared by the share action.

**NOTE: make sure to add your own API key to make the app work.**

## Submission

As usual, we'll be using GitHub Classroom for this assignment, and you will submit your assignment via GitHub. Make sure your completed files are committed and pushed by the assignment's deadline to the master branch of the GitHub repo that was created for you by GitHub Classroom. A good way to check whether your files are safely submitted is to look at the master branch your assignment repo on the github.com website (i.e. github.com/OSU-CS496-Sp2017/assignment-4-YourGitHubUsername/). If your changes show up there, you can consider your files submitted.

## Grading criteria

This assignment is worth 100 points, broken down as follows:

  * 40 points: preference fragment implementation
    * 10 points: preference fragment is displayed in the settings activity
    * 10 points: a preference exists to allow the user to select units (imperial, metric, or kelvin)
    * 10 points: a preference exists to allow the user to set the forecast location
    * 10 points: the summaries of both preferences reflect the current values of those preferences

  * 60 points: preferences used in the app
    * 20 points: changing preferences results in the forecast being reloaded from the OpenWeatherMap API
    * 10 points: the location in the main activity is updated to reflect the location set in the preferences
    * 10 points: the units displayed in the forecast (e.g. "F", "G", "K") reflect the units selected in the preferences
    * 10 points: the location set in the preferences is displayed when triggering the map action
    * 10 points: the location set in the preferences is added to the text shared by the share action
