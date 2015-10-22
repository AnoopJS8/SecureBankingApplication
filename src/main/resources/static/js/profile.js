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
				dateFA: true
			},
			
		},
		// Specify the validation error messages
		messages : {
			'address' : "Please enter valid address",
			'phoneNumber' : {
			  required : "phoneNumber cannot be null",
			  digits : "Please enter a valid phone number"
			},
			'dateOfBirth' : "Please enter Date of Birth in mm/dd/yyyy format" 
		},
	
		submitHandler : function(form) {
			form.submit();
		}
	});
	
	$.validator.addMethod( "dateFA", function( value, element ) {
		return this.optional( element ) || /((0?[1-6]\/((3[0-1])|([1-2][0-9])|(0?[1-9])))|((1[0-2]|(0?[7-9]))\/(30|([1-2][0-9])|(0[1-9]))))\/^[1-4]\d{3}$/.test( value );
	} , $.validator.messages.date );
});	


	

			
		
	
	
	
	