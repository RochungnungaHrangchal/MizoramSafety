 dataReference.collection("Counters").doc("1").get().then((doci) => {
	
	document.getElementById("users").innerHTML=doci.get("LoginCounts");
	document.getElementById("reportsnos").innerHTML=doci.get("IncomingHelpCounts")
	document.getElementById("psnos").innerHTML=doci.get("PoliceStationCounts");
	
	
 }).catch(function(error) {
	  
  swal({title: "Quota Exceeded !",
  text: "Please Upgrade your Billing Account...",
  icon: "info",
  button: "OK",});
 
 });