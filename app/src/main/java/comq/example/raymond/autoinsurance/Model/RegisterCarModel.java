package comq.example.raymond.autoinsurance.Model;

public class RegisterCarModel {
    private long insuranceDate;
    private String uId;
    private String car_pic;
    private String fName, lName, oName;
    private String occupation;
    private String make;
    private String papers;
    private String model,  value, use, noPlate;
    private String noOfSeat;
    private String status;
    private String policyType;

    public RegisterCarModel() {
    }

    public RegisterCarModel(long insuranceDate, String uId, String car_pic,
                            String fName, String lName, String oName, String occupation,
                            String make, String papers, String model, String value, String use,
                            String noPlate,
                            String noOfSeat, String status, String policyType) {
        this.insuranceDate = insuranceDate;
        this.uId = uId;
        this.car_pic = car_pic;
        this.fName = fName;
        this.lName = lName;
        this.oName = oName;
        this.occupation = occupation;
        this.make = make;
        this.papers = papers;
        this.model = model;
        this.value = value;
        this.use = use;
        this.noPlate = noPlate;
        this.noOfSeat = noOfSeat;
        this.status = status;
        this.policyType = policyType;
    }

    public long getInsuranceDate() {
        return insuranceDate;
    }

    public void setInsuranceDate(long insuranceDate) {
        this.insuranceDate = insuranceDate;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getCar_pic() {
        return car_pic;
    }

    public void setCar_pic(String car_pic) {
        this.car_pic = car_pic;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getoName() {
        return oName;
    }

    public void setoName(String oName) {
        this.oName = oName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getPapers() {
        return papers;
    }

    public void setPapers(String papers) {
        this.papers = papers;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getNoPlate() {
        return noPlate;
    }

    public void setNoPlate(String noPlate) {
        this.noPlate = noPlate;
    }

    public String getNoOfSeat() {
        return noOfSeat;
    }

    public void setNoOfSeat(String noOfSeat) {
        this.noOfSeat = noOfSeat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }
}
