$(document).ready(function() {
	$('#tF-form').validate({
		errorClass : 'error',
		rules : {
			'accountId' : {
				required : true
			},
			'amount' : {
				required : true,
				digits : true
			},
			'comment' : {
				required : false
			},

		},
		// Specify the validation error messages
		messages : {
			'accountId' : {
				required : "Recipient account number cannot be empty"
			},
			'amount' : {
				required : "Transfer amount cannot be empty",
				digits : "Please enter valid amount"
			}
		},

		submitHandler : function(form) {
			form.submit();
		}
	});
});