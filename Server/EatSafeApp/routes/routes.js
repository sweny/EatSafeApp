var register = require('config/register');
var login = require('config/login');
var upcDecoder = require('config/upcDecoder');

module.exports = function(app) {
	
	app.get('/', function(req, res) {
		res.end("Eat Safe App"); 
	});

	app.post('/login',function(req,res){
		var email = req.body.email;
		console.log ("req body is "+JSON.stringify(req.body));

		console.log("email is "+email);
        var password = req.body.password;
        console.log("password is "+password);
		login.login(email, password, function (found) {
			console.log(found);
			res.json(found);
	});
	});
	
	app.post('/register',function(req,res){
		var fName = req.body.fName;
		var lName = req.body.lName;
		var email = req.body.username;
        var password = req.body.password;
        register.register(fName, lName, email, password, function (found) {
			console.log(found);
			res.json(found);
	});
	});	
	
	app.post('/decodeUpc',function(req,res){
		console.log("in decodeUpc route");
		var msg = req.body.upc_code;
		var username = req.body.username;
		console.log("username in routes.js "+username);
        upcDecoder.upcDecoder(msg, username, function (found) {
			console.log("in decode UPC func "+found);
			res.json(found);
	});
	});	
};