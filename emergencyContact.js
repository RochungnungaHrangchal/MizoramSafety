function searchEmergencyContact()
{
	
var searchTxt = document.getElementById("phoneToView").value;
var dataSetEmergency= new Array();
var x=document.getElementById("emergencydiv");
// awm chuan show ang a nih loh chuan a ni mai

if(isNaN(searchTxt))
{
     swal({title: "Incorrect Phone Number Format",
  text: "Please check the Number and try again...",
  icon: "warning",
  button: "OK",});
     x.style.display = "none";
     dataSetEmergency.length=0;

}

else if(searchTxt.length != 10)
{
	swal({title: "Incorrect Phone Number",
  text: "Please check the Number and try again...",
  icon: "warning",
  button: "OK",});
	x.style.display = "none";
	dataSetEmergency.length=0;
	
}
else
{
	 //document.getElementById("demo").innerHTML="";
	 x.style.display = "block";
	 var searchNumber="+91" + searchTxt;
	 
	 dataReference.collection("Users").doc(searchNumber).collection("EmergencyContact").doc("1").get().then((doci) => {
	    	
	   
	   if(doci.exists)
	   { dataSetEmergency.push([doci.get("nameone"),doci.get("onecontact"),doci.get("nametwo"),doci.get("twocontact")]);
	 
		$('#dataTableEmergency').show();
	 
		$('#dataTableEmergency').DataTable( {
		    destroy: true,
            data: dataSetEmergency,
            columns: [
			        { title: "First Contact Name" },
					{ title: "Phone" },
					{ title: "Second Contact Name" },
					{ title: "Phone" }
					
            ]
    } );
    
	   }	
	   else
	   {
		  swal({title: "Emergency Contact not Found!",
  text: "Please check the Number and try again...",
  icon: "warning",
  button: "OK",});
		  //document.getElementById("demo").innerHTML="No Records Found"; 
		  x.style.display = "none";
		  dataSetEmergency.length=0;
	   }
	
 }).catch(function(error) {
	  
   swal({title: "Quota Exceeded !",
  text: "Please Upgrade your Billing Account...",
  icon: "info",
  button: "OK",});
   //document.getElementById("demo").innerHTML=error;
   x.style.display = "none";
});
  
  // DataTable a dah luh
  
	
	
}


}
