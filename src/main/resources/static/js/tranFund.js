$(document).ready(function() {
	$('#tF-form').validate({
		errorClass : 'error',
		rules : {
			'accountId' : {
				required : true,
				digits : true
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
			'toAccount.accId' : {
				required : "Recepient Account number cannot be NULL",
				digits : "Please enter valid recepient account number"
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
