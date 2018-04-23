This is the web server written in Java based on OpenCV and Tensorflow Calculations.

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
  
To create a user we must give 3 types of images: where face is looking left, right and straight.
We can apply many images, and server will pick mean values of them.

![alt text](http://ksassets.timeincuk.net/wp/uploads/sites/55/2016/07/2015JustinBieber_8_DC_131115-1-920x610.jpg)

Server created json representation of images (based on 128 face features) that has been passed through Convolutional Neural Network FaceNet and disperses them into 3 categories: left, center and right.

Example of JSON: 
{ left {"features":[0.017909864, ...]}, center{ "features":[-0.014903864,...]} right {"features":[0.4253545443, ....], 
facelabel: "example_user" }

Then we can send login request, get a token and after than we can send POST query to
  <host_url>/api/identify_users
    
Server proceeds an image and spawns new image with face labels that qualify if this user has been recognized.
Example of sending justin bieber another photo:

Server crops and alignes a face so that eyes are always in the same position

![alt text](https://raw.githubusercontent.com/sanstorik/server_face_recognition/master/example/jb_highlight.jpg)


Based on eyes and center of face positions, we calculate a proper face type for them whether they're looking left, right or straight.  

For example this image is qualified as LOOKING_LEFT, because the distance between the left eye and the face center is much bigger than the distance between the face center and the right eye.

![alt text](https://raw.githubusercontent.com/sanstorik/server_face_recognition/master/example/jb-cropped.jpg)

Results of these calculations are proceeded in a neural network and compared with other user/users needed face type.

![alt text](https://raw.githubusercontent.com/sanstorik/server_face_recognition/master/example/57be9738d93a4bf8a9b2d7ebba4fbaf7.jpg)


We've qualified our user successfully. This template is applicable to all API methods.
