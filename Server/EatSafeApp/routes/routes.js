var register = require('config/register');
var login = require('config/login');

module.exports = function(app) {
	
	app.get('/', function(req, res) {
		res.end("Eat Safe App"); 
	});

	app.post('/login',function(req,res){
		var email = req.body.username;
		console.log(email);
        var password = req.body.password;
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
};