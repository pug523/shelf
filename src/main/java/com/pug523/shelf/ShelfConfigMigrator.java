package com.pug523.shelf;

import com.pug523.shelf.config.Migrator;

public class ShelfConfigMigrator {
    // @formatter:off
    public static final Migrator migrator = new Migrator().addRenameRule("someOldName", "brandNewName")
            .addRenameRule("configuWizuTypo", "configWithoutTypo")
            .addRenameRule("oldCaseStyle", "new_case_style");
    // @formatter:on
}
