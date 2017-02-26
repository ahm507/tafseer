
Publishing Books Android App
=============================



What is this?
-------------
* This program is used to display 12 Islamic text books of the prophet Mohamed sayings. These usually 
 studied by specialized scholars and students.
* This is a complete rewrite to books-android Cordova application to use standard/native Android development.
* You can download the app from: https://play.google.com/store/apps/details?id=org.sonna.www.sonna&hl=en  
 

Why publishing books this way?
-------------------------------
I converted them to ePub but had these problems:
 * The ePub became huge file and readers usually stuck or freeze when open it. 
 As example, in my Mac pro, the Apple iBooks was unable to open the ePub book.
 * The Arabic was not showing properly as the arabic diacritics was misplaced.
 * One of the important customer features is to search with Arabic that has no vowels inside 
 the text that has vowels. This actually impossible to achieve in epub and standard readers. 
 


Technology
----------

* This is a standard Android Studio application. If some file is missing, please contact me.
* Data is stored in Sqlite file. As the file is really huge, it is not included currently. 
* full text indexing feature is used to search inside the Sqlite file.


Data base Structure:
----------------------
Here is a lost of fields:
* book_code: Stores a code to distinguish the book, so you can store multiple books.
* page_id:   Stores a unique id for within some book_code. it must be unique. 
* parent_id: Stores parent id, so a table of contents can be showed.
* title:     Store contents title
* page:      Store contents or paragraphs that has vowels for display purposes. 
* page_fts:  Stores contents with no vowels, so that sqlite full text index can work on it.

Note: page_fts is indexed as shown in the schema. In order to have your own data, sonna_empty.sqlite 
should be filled with data as well as rename teh file to be sonna.sqlite  

Future:
--------
* Moving data to a fulltext index with ranked results using libraries such as Lucene. 
This will enable me to save the text once with diacritics only.
Also, it will make it possible to keep data compressed on Android device and save a lot of storage.
* Generalize the application, so any book can be viewed with it.  


License
--------
* Usage and contributions are welcomed.
* The text and code is license as GNU 3.0 located at: https://www.gnu.org/copyleft/gpl.html

