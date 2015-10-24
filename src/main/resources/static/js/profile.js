$(document).ready(function() {
	$('#profile-form').validate({
		errorClass : 'error',
		rules : {
			'address' : {
				required : true
			},
			'phoneNumber' : {
				required : true,
				digits : true
			},
			'dateOfBirth' : {
				required : true,
				maxDate : true,
				dateFA : true
			},

		},
		// Specify the validation error messages
		messages : {
			'address' : "Address field cannot be NULL",
			'phoneNumber' : {
				required : "phoneNumber cannot be NULL",
				digits : "Please enter a valid phone number"
			},
			'dateOfBirth' : {
				dateFA : "Please enter Date of Birth in mm/dd/yyyy format",
				required : "Date of Birth cannot be NULL",
				maxDate : "Date of Birth cannot be Future Date"
			}
		},

		submitHandler : function(form) {
			form.submit();
		}
	});

	$.validator.addMethod("dateFA", function(value, element) {
		return this.optional(element)
				|| /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/(19|20)\d{2}$/
						.test(value);
	}, $.validator.messages.date);

	$.validator.addMethod("maxDate", function(value, element) {
		var now = new Date();
		var myDate = new Date(value);
		return this.optional(element) || myDate <= now;

	});
});