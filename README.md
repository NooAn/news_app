# This is a sample News Reader app as test app for agoda
News Reader app that is supposed to display news list and the details. The first page displays news list, when one of the items is clicked, it is supposed to show the detail of the selected news. Unfortunately, the app is full of bugs and it crashes as soon as it is launched. Also, the code is not properly written and there are no unit tests. 

Basic unit test
Unit tests for NewsViewModel

Notes
It is possible that some of the stories do not have images.
It is possible that the link to the full story might not work as it is controlled by New York Times.

There is MVVM arch with ViewState components.
Every NewsState inheritance from BaseState and use three main fields like loading,error,init.
ViewModel has observeState which help us to have only one observer in view with one ViewState
