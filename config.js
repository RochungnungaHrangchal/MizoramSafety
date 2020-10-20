var firebaseConfig = {
    apiKey: "AIzaSyDQtn1SWiQaa-yY1RGgHq01F9k4A4AEGGA",
    authDomain: "mizoramsafety.firebaseapp.com",
    databaseURL: "https://mizoramsafety.firebaseio.com",
    projectId: "mizoramsafety",
    storageBucket: "mizoramsafety.appspot.com",
    messagingSenderId: "827060174507",
    appId: "1:827060174507:web:5ae64ed7d0798abbed01ee"
  };
  // Initialize Firebase
  var dashBoard=firebase.initializeApp(firebaseConfig);
  var dataReference=dashBoard.firestore();
  