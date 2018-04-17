This is the web server on Java based on OpenCV and Tensorflow Calculations.

This server accepts GET/POST queries with images and returns outcome with 
identifying prediction based on face features (FaceNet neural network).

Alongside with simple identification it provides finding all registered 
users on photo and putting labels on them with square on their faces.

Registration is based on jdbc SQL connection. (PostgreSQL, MySQL etc.).
Every non-login query requires token authentication based on RSA256.

Server supports updating avatars, returning simple image with all
faces marked, face coordinates of all found faces, eyes coorinates etc.