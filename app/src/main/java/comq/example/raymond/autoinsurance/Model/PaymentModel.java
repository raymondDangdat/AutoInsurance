package comq.example.raymond.autoinsurance.Model;

public class PaymentModel {
    private long insuranceDate, paymentDate;
    private String uId;
    private String car_pic;
    private String papers;
    private String name;
    private String make;
    private String model;
    private double   value;
    private double amountPaid;
    private String use, noPlate;
    private String status;
    private String policyType;

    public PaymentModel() {
    }

    public PaymentModel(long insuranceDate, long paymentDate, String uId, String car_pic, String papers, String name, String make, String model, double value, double amountPaid, String use, String noPlate, String status, String policyType) {
        this.insuranceDate = insuranceDate;
        this.paymentDate = paymentDate;
        this.uId = uId;
        this.car_pic = car_pic;
        this.papers = papers;
        this.name = name;
        this.make = make;
        this.model = model;
        this.value = value;
        this.amountPaid = amountPaid;
        this.use = use;
        this.noPlate = noPlate;
        this.status = status;
        this.policyType = policyType;
    }

    public long getInsuranceDate() {
        return insuranceDate;
    }

    public void setInsuranceDate(long insuranceDate) {
        this.insuranceDate = insuranceDate;
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(long paymentDate) {
        this.paymentDate = paymentDate;
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

    public String getPapers() {
        return papers;
    }

    public void setPapers(String papers) {
        this.papers = papers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
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
