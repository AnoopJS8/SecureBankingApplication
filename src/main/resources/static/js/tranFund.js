$(document).ready(function() {
	$('#tF-form').validate({
		errorClass : 'error',
		rules : {
			'accountId' : {
				required : true,
				digits : true
			},
			'amount' : {
				required : true
			},
			'comment' : {
				required : false
			},

		},
		// Specify the validation error messages
		messages : {
			'amount' : {
				required : "Recepient Account number cannot be NULL
			},
			'amount' : {
				required : "Transfer amount cannot be NULL",
				digits : "Please enter valid amount"
			}
		},

		submitHandler : function(form) {
			form.submit();
		}
	});
});