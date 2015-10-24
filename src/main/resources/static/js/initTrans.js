<<<<<<< HEAD
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
			required : true,
			dateFA: true,
			minDate: true
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
			required: "Transfer date cannot be NULL" , 
			dateFA:	"Please enter Data in MM/dd/yyyy format",
			minDate: "Please enter future date"
		}},

	submitHandler : function(form) {
		form.submit();
	}
});
});	
	
$.validator.addMethod( "dateFA", function( value, element ) {
    return this.optional( element ) || /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/(19|20)\d{2}$/.test( value );
}, $.validator.messages.date );


$.validator.addMethod("minDate", function (value, element) {
   var now = new Date();
   var myDate = new Date(value);
   return this.optional(element) || myDate > now;

   
});
	
	
	
=======
$(document).ready(function() {
	$('#init-form').validate({
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
		'transferDate' : {
			required : true,
			dateFA: true,
			minDate: true
		}
	},
	// Specify the validation error messages
	messages : {
		'accountId' : {
			required: "Recepient Account number cannot be NULL"
		},
		'amount' : {
			required: "Transfer amount cannot be NULL", 
			digits:	"Please enter valid amount"
		},
		'transferDate' : {
			required: "Transfer date cannot be NULL" , 
			dateFA:	"Please enter Data in MM/dd/yyyy format",
			minDate: "Please enter future date"
		}},

	submitHandler : function(form) {
		form.submit();
	}
});
});	
	
$.validator.addMethod( "dateFA", function( value, element ) {
    return this.optional( element ) || /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/(19|20)\d{2}$/.test( value );
}, $.validator.messages.date );


$.validator.addMethod("minDate", function (value, element) {
   var now = new Date();
   var myDate = new Date(value);
   return this.optional(element) || myDate > now;

   
});
	
	
	
>>>>>>> c725d54c1e8fbc90529bd9f00b904c13213453f8
	