package xsg.book.tools;

public class Participants_people {
    private String EmployeeNumber, Name, Ministry;
    private boolean bo;

    public boolean getBo() {
        return bo;
    }

    public void setBo(boolean bo) {
        this.bo = bo;
    }

    public String getEmployeeNumber() {
        return EmployeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        EmployeeNumber = employeeNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMinistry() {
        return Ministry;
    }

    public void setMinistry(String ministry) {
        Ministry = ministry;
    }

    @Override
    public String toString() {
        return "Participants_people{" +
                "EmployeeNumber='" + EmployeeNumber + '\'' +
                ", Name='" + Name + '\'' +
                ", Ministry='" + Ministry + '\'' +
                '}';
    }
}
