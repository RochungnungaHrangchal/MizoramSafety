
var dataSetA = new Array();


dataReference.collection("IncomingHelp").get().then((queryData) => {	
	
	queryData.forEach(function(doci){
			
		dataSetA.push([doci.get("datetimereport"),doci.get("sendernos"),doci.get("sendersemergencyno"),doci.get("smscontent"),doci.get("latitudein"),doci.get("longitudein")]);//,doci.get("Contact"),doci.get("Latitude"),doci.get("Longitude")]);
		
	});
		 
	 $('#dataTableIC').DataTable( {
            data: dataSetA,
            columns: [
			        { title: "Date and Time" },
					{ title: "Sender Nos" },
					{ title: "Sender Emergency Contact Nos" },
					{ title: "SMS Content" },
					
					{ title: "Latitude " }, 
					{ title: "Longitude " }
					
            ]
    } );
    
	
 }).catch(function(error) {
	  
  swal({title: "Quota Exceeded !",
  text: "Please Upgrade your Billing Account...",
  icon: "info",
  button: "OK",});
 
 });