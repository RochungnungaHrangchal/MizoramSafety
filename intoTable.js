
var dataSet = new Array();


dataReference.collection("InitData").get().then((queryData) => {	
	
	queryData.forEach(function(doci){
			
		dataSet.push([doci.get("District"),doci.get("PAName"),doci.get("OCName"),doci.get("Contact"),doci.get("Latitude"),doci.get("Longitude")]);
		
	});
		 
	 $('#dataTablePS').DataTable( {
            data: dataSet,
            columns: [
			        { title: "District" },
					{ title: "PS Name" }, 
					{ title: "OC Name" },
					{ title: "OC Contact" },
                    { title: "Latitude" },
                    { title: "Longitude" }
            ]
    } );
    
	
 }).catch(function(error) {
	  
  swal({title: "Quota Exceeded !",
  text: "Please Upgrade your Billing Account...",
  icon: "info",
  button: "OK",});
 
 });