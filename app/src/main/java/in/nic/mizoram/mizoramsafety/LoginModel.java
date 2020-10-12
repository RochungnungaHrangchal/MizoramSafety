package in.nic.mizoram.mizoramsafety;

public class LoginModel {
    String loginname;
    String loginaddress;
    String logincontact;

    public LoginModel(String loginname,String loginaddress,String logincontact)
    {
        this.loginaddress=loginaddress;
        this.logincontact=logincontact;
        this.loginname=loginname;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getLoginaddress() {
        return loginaddress;
    }

    public void setLoginaddress(String loginaddress) {
        this.loginaddress = loginaddress;
    }

    public String getLogincontact() {
        return logincontact;
    }

    public void setLogincontact(String logincontact) {
        this.logincontact = logincontact;
    }
}
