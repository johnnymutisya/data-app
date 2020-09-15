package org.ichooselifeafrica.mydata.models;

public class Shujaa {
    private String name,county,sub_county,school, gender;

    public Shujaa(String name, String county, String sub_county, String school, String gender) {
        this.name = name;
        this.county = county;
        this.sub_county = sub_county;
        this.school = school;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getSub_county() {
        return sub_county;
    }

    public void setSub_county(String sub_county) {
        this.sub_county = sub_county;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

