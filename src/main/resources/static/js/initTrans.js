$(document).ready(function() {
	$('#init-form').validate({
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
		'transferDate' : {
			required : true//,
			//dateFA: true
		}
	},
	// Specify the validation error messages
	messages : {
		'accountId' : {
			required: "Recepient Account number cannot be NULL", 
			digits:	"Please enter valid recepient account number"
		},
		'amount' : {
			required: "Transfer amount cannot be NULL", 
			digits:	"Please enter valid amount"
		},
		'transferDate' : {
			required: "Transfer date cannot be NULL" //, 
			//dateFA:	"Please enter Data in YYYY/MM/DD format"
		}},

	submitHandler : function(form) {
		form.submit();
	}
});
});	
	
$.validator.addMethod( "dateFA", function( value, element ) {
    return this.optional( element ) || /^[1-4]\d{3}\/((0?[1-6]\/((3[0-1])|([1-2][0-9])|(0?[1-9])))|((1[0-2]|(0?[7-9]))\/(30|([1-2][0-9])|(0?[1-9]))))$/.test( value );
}, $.validator.messages.date );
			
		
	
	
	
	