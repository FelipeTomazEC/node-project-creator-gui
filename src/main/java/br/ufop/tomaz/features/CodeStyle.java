package br.ufop.tomaz.features;

public enum CodeStyle {
    AIRBNB("AirBnB",
           "airbnb-base",
           "eslint-config-airbnb-base"
    ),
    GOOGLE("Google","google","eslint-config-google"),
    STANDARD("Standard",
            "prettier-standard",
            "eslint-config-standard",
            "eslint-plugin-standard",
            "eslint-plugin-promise",
            "eslint-plugin-node",
            "eslint-config-prettier-standard",
            "prettier-config-standard"
    );

    private String name;
    private String nameReference;
    private String[] requiredPackages;


    CodeStyle(String name, String nameReference, String ... requiredPackages){
        this.name = name;
        this.nameReference = nameReference;
        this.requiredPackages = requiredPackages;
    }

    public String getExtendName(){
        return this.nameReference;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String[] getRequiredPackages() {
        return requiredPackages;
    }
}
