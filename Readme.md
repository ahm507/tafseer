
Publishing Books Android App
=============================


What is this?
-------------
* This program is used to display 12 islamic text books of the prophet mohamed sayings. These usually 
 used by specialized scholars and students.
* This is a complete rewrite to books-android Cordova application to use standard/native Android development.  
 


Why publishing books this way?
-------------------------------
I converted them to ePub I had these problem:
 * The books actually huge in size. When converted the ePub became huge file and readers usually stuck or freeze when opening it. 
 In my Mac pro, the iBooks was unable to open the ePub book.
 * The Arabic was not showing properly as the arabic diacritics was misplaced.
 * One of the important customer features is to search with Arabic that has no vowels inside 
 the text that has vowels. This actually impossible to achieve in epub and standard readers. 
 


Technology
----------

* This is a standard Android Studoip application. If some file is missing, please contact me.
* Data is stored in sqlite application.
* full text indexing feature is used to search inside the sqlite.


Future:
--------
* Moving data to a fulltext index with ranked results using tools such as Lucene. T
his will enable me to save the text once with diacritics instead of double as of now.
It will make it possible to keep data compressed on Android device and save a lot of storage.
* generalize the application, so any book can be viewed with it.  


License
--------
* The code currently has me only as a creator and contributor, however, usage and contributions are welcomed.
* The text and code is license as GNU 3.0 located at: https://www.gnu.org/copyleft/gpl.html

