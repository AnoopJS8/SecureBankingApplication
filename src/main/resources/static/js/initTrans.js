$(document).ready(function() {
	$('#init-form').validate({
		errorClass : 'error',
		rules : {
			'accountId' : {
				required : true
			},
			'amount' : {
				required : true
			},
			'comment' : {
				required : false
			},
			'transferDate' : {
				required : true,
				dateFA : true,
				maxDate : true,
				minDate : true
			}
		},
		// Specify the validation error messages
		messages : {
			'accountId' : {
				required : "Recepient Account number cannot be NULL"
			},
			'amount' : {
				required : "Transfer amount cannot be NULL"
			},
			'transferDate' : {
				required : "Transfer date cannot be NULL",
				dateFA : "Please enter Data in MM/dd/yyyy format",
				minDate : "Please enter future date",
				maxDate : "Please enter a future date in this century"
			}
		},

		submitHandler : function(form) {
			form.submit();
		}
	});
});

$.validator.addMethod("dateFA", function(value, element) {
	return this.optional(element)
			|| /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/([0-9][0-9])\d{2}$/
					.test(value);
}, $.validator.messages.date);

$.validator.addMethod("minDate", function(value, element) {
	var now = new Date();
	var myDate = new Date(value);
	return this.optional(element) || myDate > now;
});

$.validator.addMethod("maxDate", function(value, element) {
	var now = new Date('December 31, 2099');
	var myDate = new Date(value);
	return this.optional(element) || (myDate.getFullYear() < now);

});
