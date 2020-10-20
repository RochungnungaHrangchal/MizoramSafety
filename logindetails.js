var dataSetA = new Array();

//dataReference.collection("Counters").doc("1").get().then((doci) => {
dataReference.collection("LoginDetails").get().then((queryData) => {	
	
	queryData.forEach(function(doci){
			
		dataSetA.push([doci.get("loginname"),doci.get("loginaddress"),doci.get("logincontact")]);//,doci.get("smscontent"),doci.get("latitudein"),doci.get("longitudein")]);//,doci.get("Contact"),doci.get("Latitude"),doci.get("Longitude")]);
		
	});
		 
	 $('#dataTableLogin').DataTable( {
            data: dataSetA,
            columns: [
			        { title: "Name" },
					{ title: "Address" },
					{ title: "Contact" },
					{ title: "Emergency Contact 1" },
					{ title: "Emergency Contact 2" }
					
					
            ]
     });
    
	
    }).catch(function(error) {
		 swal({title: "Quota Exceeded !",
  text: "Please Upgrade your Billing Account...",
  icon: "info",
  button: "OK",});
	  
        //document.getElementById("demo").innerHTML=error;
        x.style.display = "none";
});