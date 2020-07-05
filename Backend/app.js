

const express = require('express')
var bodyParser = require('body-parser')
const app = express()
var port = process.env.PORT || 3000

app.use(bodyParser.json())

app.use(function(req, res, next) {
  res.setHeader('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Methods', 'GET, OPTIONS')
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization')
  res.header('Access-Control-Allow-Credentials', true)
  return next()
});


app.get('/todo', function (req, res) {
    let data = "{}";
    res.status(200).json({ todo_list: data})
});


app.listen(port, () => console.log(`Example app listening at http://localhost:${port}`))