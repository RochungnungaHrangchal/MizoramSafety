package in.nic.mizoram.mizoramsafety;

public class IncomingHelpModel {

    String ocname;
    String occontact;
    String smscontent;
    String actiontaken;
    String status;
    String sendernos;
    String sendersemergencyno;
    String datetimereport;
    String psname;
    double latitudein;
    double longitudein;

    public String getOcname() {
        return ocname;
    }

    public void setOcname(String ocname) {
        this.ocname = ocname;
    }

    public String getOccontact() {
        return occontact;
    }

    public void setOccontact(String occontact) {
        this.occontact = occontact;
    }

    public String getSmscontent() {
        return smscontent;
    }

    public void setSmscontent(String smscontent) {
        this.smscontent = smscontent;
    }

    public String getActiontaken() {
        return actiontaken;
    }

    public void setActiontaken(String actiontaken) {
        this.actiontaken = actiontaken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendernos() {
        return sendernos;
    }

    public void setSendernos(String sendernos) {
        this.sendernos = sendernos;
    }

    public String getSendersemergencyno() {
        return sendersemergencyno;
    }

    public void setSendersemergencyno(String sendersemergencyno) {
        this.sendersemergencyno = sendersemergencyno;
    }

    public String getDatetimereport() {
        return datetimereport;
    }

    public void setDatetimereport(String datetimereport) {
        this.datetimereport = datetimereport;
    }

    public String getPsname() {
        return psname;
    }

    public void setPsname(String psname) {
        this.psname = psname;
    }

    public double getLatitudein() {
        return latitudein;
    }

    public void setLatitudein(double latitudein) {
        this.latitudein = latitudein;
    }

    public double getLongitudein() {
        return longitudein;
    }

    public void setLongitudein(double longitudein) {
        this.longitudein = longitudein;
    }

    public IncomingHelpModel(String ocname,
                             String occontact,
                             String smscontent,
                             String actiontaken,
                             String status,
                             String sendernos,
                             String sendersemergencyno,
                             String datetimereport,
                             String psname,
                             double latitudein,
                             double longitudein)
    {
        this.ocname=ocname;
        this.occontact=occontact;
        this.smscontent=smscontent;
        this.actiontaken=actiontaken;
        this.status=status;
        this.sendernos=sendernos;
        this.sendersemergencyno=sendersemergencyno;
        this.datetimereport=datetimereport;
        this.psname=psname;
        this.latitudein=latitudein;
        this.longitudein=longitudein;
    }

}
