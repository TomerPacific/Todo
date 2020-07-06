

const express = require('express')
var bodyParser = require('body-parser')
var firebase = require('firebase')
const app = express()
var port = process.env.PORT || 3000



var firebaseConfig = {
    apiKey: "AIzaSyDit-4pU2GU_m1wii6fRwlZJAaZLwj3vQU",
    authDomain: "todo-tomer.firebaseapp.com",
    databaseURL: "https://todo-tomer.firebaseio.com",
    projectId: "todo-tomer",
    storageBucket: "todo-tomer.appspot.com",
    messagingSenderId: "139032876037",
    appId: "1:139032876037:web:2c847fa4e68c20d162786c"
  };
  
  // Initialize Firebase
firebase.initializeApp(firebaseConfig);

app.use(bodyParser.json())

app.use(function(req, res, next) {
  res.setHeader('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Methods', 'GET, OPTIONS')
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization')
  res.header('Access-Control-Allow-Credentials', true)
  return next()
});


app.get('/todo', function (req, res) {
    var database = firebase.database();
    
    res.status(200).json({ todo_list: {}})
});


app.listen(port, () => console.log(`Example app listening at http://localhost:${port}`))