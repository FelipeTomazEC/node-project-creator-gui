package br.ufop.tomaz.features;

import java.util.Arrays;
import java.util.Collections;

public enum SGBD {
    POSTGRES("PostgreSQL", Arrays.asList("pg", "pg-hstore").toArray(new String[2])),
    MYSQL("MySQL", Collections.singletonList("mysql2").toArray(new String[1])),
    MARIADB("Maria DB", Collections.singletonList("mariadb").toArray(new String[1])),
    SQLITE("SQLite", Collections.singletonList("sqlite3").toArray(new String[1])),
    MICROSOFT_SQL_SERVER("Microsoft SQL Server", Collections.singletonList("tedious").toArray(new String[1]));

    String[] requiredPackagesName;
    String name;

    SGBD(String name, String[] requiredPackagesName) {
        this.name = name;
        this.requiredPackagesName = requiredPackagesName;
    }

    public String[] getRequiredPackagesNames() {
        return this.requiredPackagesName;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
