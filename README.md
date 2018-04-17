This is the web server on Java based on OpenCV and Tensorflow Calculations.

This server accepts GET/POST queries with images and returns outcome with 
identifying prediction based on face features (FaceNet neural network).

Alongside with simple identification it provides finding all registered 
users on photo and putting labels on them with square on their faces.

Registration is based on jdbc SQL connection. (PostgreSQL, MySQL etc.).
Every non-login query requires token authentication based on RSA256.

Server supports updating avatars, returning simple image with all
faces marked, face coordinates of all found faces, eyes coorinates etc.

------------------------------EXAMPLE-----------------------------------

First we register new user using API.
POST query <host url>/api/register
  <p><input type="text" name="username" value=jb">
  <p><input type="text" name="username" value=jb">
  <p><input type="file" name="image">

![alt text](http://ksassets.timeincuk.net/wp/uploads/sites/55/2016/07/2015JustinBieber_8_DC_131115-1-920x610.jpg)

Server created json representation of image (based on 128 face features) that has been passed throught Convolutional Neural Network FaceNet.
Example: 
{ "features":[0.017909864,-0.09039427,-0.11307395,0.094229214,-0.0062726582,0.0854959,0.0112353945,0.13639219,0.04221405,0.088752426,0.045691364,0.107265346,0.23955862,0.021586264,-0.17147852,0.07574615,0.014535043,-0.06156437,0.22773448,1.8782669E, .....],
"faceLabel":"jb", identifier: 0 }

Then we can work login, get token and after than we can send POST query to
  <host url>/api/identify_users
  <p><input type="file" name="image">
    
Server proceeds image and spawns new image with face labels that qualify if this user has been recognized.
Example of sending justin bieber another photo:


![alt text](https://raw.githubusercontent.com/sanstorik/server_face_recognition/master/example/57be9738d93a4bf8a9b2d7ebba4fbaf7.jpg)


We've qualified our user successfully. This template is applicable to all API methods.
