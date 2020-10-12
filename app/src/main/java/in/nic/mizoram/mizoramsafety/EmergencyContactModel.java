package in.nic.mizoram.mizoramsafety;

public class EmergencyContactModel {
    int statuss;
    String nameone;
    String onecontact;
    String nametwo;
    String twocontact;

    public int getStatuss() {
        return statuss;
    }

    public void setStatuss(int statuss) {
        this.statuss = statuss;
    }

    public String getNameone() {
        return nameone;
    }

    public void setNameone(String nameone) {
        this.nameone = nameone;
    }

    public String getOnecontact() {
        return onecontact;
    }

    public void setOnecontact(String onecontact) {
        this.onecontact = onecontact;
    }

    public String getNametwo() {
        return nametwo;
    }

    public void setNametwo(String nametwo) {
        this.nametwo = nametwo;
    }

    public String getTwocontact() {
        return twocontact;
    }

    public void setTwocontact(String twocontact) {
        this.twocontact = twocontact;
    }

    public EmergencyContactModel(int statuss,String nameone,String onecontact,String nametwo,String twocontact){
      this.statuss=statuss;
      this.nameone=nameone;
      this.onecontact=onecontact;
      this.nametwo=nametwo;
      this.twocontact=twocontact;

    }
}
