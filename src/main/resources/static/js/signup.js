$(document).ready(function() {
	$('#signup-form').validate({
		errorClass : 'error',
		rules : {
			'user.username' : {
				required : true
			},
			'user.email' : {
				required : true,
				email : true
			},
			'user.password' : {
				required : true,
				minlength : 6
			},
			'user.securityQuestion' : {
				required : true
			},
			'user.securityAnswer' : {
				required : true
			}
		},
		// Specify the validation error messages
		messages : {
			'user.username' : "Please enter your user name",
			'user.email' : "Please enter a valid email address",
			'user.password': {
				required: "Please enter a password",
				minlength: "Password should be atleast 6 characters long"
			},
			'user.securityQuestion' : "Please enter a security question",
			'user.securityAnswer' : "Please enter a security answer"
		},

		submitHandler : function(form) {
			form.submit();
		}
	});
});